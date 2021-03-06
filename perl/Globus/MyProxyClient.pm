######################################################################
#
# This file is part of the GriShib-CA distribution.  Copyright
# 2006-2009 The Board of Trustees of the University of
# Illinois. Please see LICENSE at the root of the distribution.
#
######################################################################

=head1 Globus::MyProxyClient

An interface to a MyProxy Client.

=cut

package Globus::MyProxyClient;

use Globus::GSISocket;

# Enabling debugging if non-zero
$DEBUG = 0;

# Constants
my $MYPROXY_VERSION_STRING = "MYPROXYv2";

my $MYPROXY_GET_COMMAND = 0;

my $MYPROXY_RESPONSE_SUCCESS = 0;

=head2 Methods

=over 4

=item new()

Create a new MyProxy Client interface.

B<Arguments:> 

ServerHost        Name of MyProxy server host ("localhost" by default)
ServerPort        Port for MyProxy server (7512 by default)
ClientCert        Path to certificate to use to authenticate.
ClientKey         Path to key to use to authenticate.

B<Returns:> A Globus::MyProxyClient object

=cut

sub new
{
    my $class = shift;
    my $self = {};
    my $arg_hash = (ref($_[0]) eq 'HASH') ? $_[0] : {@_};
    $self->{host} = $arg_hash->{ServerHost} || "localhost";
    $self->{port} = $arg_hash->{ServerPort} || 7512;
    $self->{cert} = $arg_hash->{ClientCert};
    $self->{key} = $arg_hash->{ClientKey};
    bless($self, $class);
    return $self;
}

=item getCred()

Request a credential from the MyProxy server.

B<Arguments:>

CertReq               Certificate request to use.
CertReqFormat         Certificate request format (PEM or DER)
Lifetime              Requested certificate lifetime in seconds.
Username              Username
Passphrase            Password to use

B<Returns:> A Globus:GSICredential object or undef on error.

=cut

sub getCred
{
    my $self = shift;
    my $arg_hash = (ref($_[0]) eq 'HASH') ? $_[0] : {@_};
    my $sock = Globus::GSISocket->new(
        PeerHost => $self->{host},
        PeerPort => $self->{port},
        SSL_cert_file => $self->{cert},
        SSL_key_file => $self->{key}
        );
    if (!defined($sock))
    {
        $self->_error(Globus::GSISocket::errstr());
        return undef;
    }

    $self->_debug("Sending request to MyProxy server");
    if (!$sock->write(sprintf(
                          "VERSION=%s\n" .
                          "COMMAND=%d\n" .
                          "USERNAME=%s\n" .
                          "PASSPHRASE=%s\n" .
                          "LIFETIME=%d\n",
                          $MYPROXY_VERSION_STRING,
                          $MYPROXY_GET_COMMAND,
                          $arg_hash->{Username},
                          $arg_hash->{Passphrase},
                          $arg_hash->{Lifetime})))
    {
        $self->_error("Error writing MyProxy request: %s",
                      $sock->errstr());
        return undef;
    }

    $self->_debug("Reading response from MyProxy server");
    my $responseCode = $self->readResponse($sock);
    if (!defined($responseCode))
    {
        return undef;
    }
    $self->_debug("Response code is %d", $responseCode);
    if ($responseCode != $MYPROXY_RESPONSE_SUCCESS)
    {
        $self->_error("MyProxy server denied request: %s",
                      $self->{responseError});
        return undef;
    }

    $self->_debug("Accepting delegated credential from MyProxy server");
    my $cred =  $sock->acceptDelegation(
        certReq => $arg_hash->{CertReq},
        certReqFormat => $arg_hash->{CertReqFormat});

    if (!defined($cred))
    {
        if ($sock->myProxyResponsePending())
        {
            $self->readResponse($sock);
            if (defined($self->{responseError}))
            {
                $self->_error("Error from MyProxy server (more details in ".
                              "serrver log): %s", $self->{responseError});
            }
            else
            {
                $self->_error("Got error from MyProxy server but " . 
                              "unable to parse. See server logs.");
            }
        }
        else
        {
            $self->_error("Error reading credential: %s",
                          $sock->errstr());
        }
        return undef;
    }
    $sock->close();
    return $cred;
}

=item readResponse()

Read a response from the MyProxy server.

B<Arguments:> C<socket>

B<Returns:> Response code. Undef on error, setting
    $self->{responseError}.

=cut

sub readResponse
{
    my $self = shift;
    my $sock = shift;
    my $versionString = $sock->readline();
    if (!defined($versionString))
    {
        $self->_error("Error reading response: %s",
                      $sock->errstr());
        return undef;
    }
    my $responseString = $sock->readline();
    if (!defined($responseString))
    {
        $self->_error("Error reading response: %s",
                      $sock->errstr());
        return undef;
    }
    my $expectString = sprintf("VERSION=%s", $MYPROXY_VERSION_STRING);
    chomp($versionString);
    if ($versionString ne $expectString)
    {
        $self->_error(
            "Bad version string received from MyProxy Server: %s != %s",
            $versionString, $expectString);
        return undef;
    }
    chomp($responseString);
    if ($responseString !~ /RESPONSE=(\d+)/)
    {
        $self->_error("Bad response string received from MyProxy Server: %s",
                      $responseString);
        return undef;
    }
    my $responseCode = int($1);
    if ($responseCode != 0)
    {
        # Read error strings
        $self->{responseError} = "";
        while ($sock->pending())
        {
            my $errorString = $sock->readline();
            if ($errorString =~ /ERROR=(.*)/)
            {
                $errorString = $1;
                chomp($errorString);
                $self->{responseError} .= $errorString . "\n";
            }
        }
    }
    # Read and discard any leftover stuff
    my $extra;
    my $pending = $sock->pending();
    $self->_debug("Reading and discarding %d byte(s) after response",
                  $pending);
    $sock->read($extra, $pending);
    return $responseCode;
}


=item _error()

Set our error string

B<Arguments:> As to sprintf()

B<Returns:> Nothing

=cut
sub _error
{
    my $self = shift;
    my $format = shift;
    $self->{errstr} = sprintf($format, @_);
}


=item _debug()

Print a message if we are in debug mode.

B<Arguments:> As to sprintf()

B<Returns:> Nothing

=cut
sub _debug
{
    my $format = shift;
    if (ref($format))
    {
        # We were called as a instance method instead of a static method
        $format = shift;
    }
    if ($DEBUG and defined($format))
    {
        chomp($format);
        printf($format . "\n", @_);
    }
}

=item errstr()

Return our error string.

B<Arguments:> None

B<Returns:> Error as string, undef if no error set.

=cut
sub errstr
{
    my $self = shift;
    if (defined($self))
    {
        return $self->{errstr};
    }
    else
    {
        # Shouldn't get here
        return "No Error.";
    }
}

=back

=head2 Version

Version 1.2

=cut

# Return true for import/use

1;

### Local Variables: ***
### mode:perl ***
### End: ***
######################################################################
