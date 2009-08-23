######################################################################
#
# $Id$
#
# GridShib configuration and class loader.
#
######################################################################
# Copyright 2006 The Board of Trustees of the University of Illinois.
# All rights reserved.

# Developed by:

#   The GridShib Project
#   National Center for Supercomputing Applications
#   University of Illinois
#   http://gridshib.globus.org/

# Permission is hereby granted, free of charge, to any person obtaining
# a copy of this software and associated documentation files (the
# "Software"), to deal with the Software without restriction, including
# without limitation the rights to use, copy, modify, merge, publish,
# distribute, sublicense, and/or sell copies of the Software, and to
# permit persons to whom the Software is furnished to do so, subject to
# the following conditions:

#   Redistributions of source code must retain the above copyright
#   notice, this list of conditions and the following disclaimers.

#   Redistributions in binary form must reproduce the above copyright
#   notice, this list of conditions and the following disclaimers in the
#   documentation and/or other materials provided with the distribution.

#   Neither the names of the National Center for Supercomputing
#   Applications, the University of Illinois, nor the names of its
#   contributors may be used to endorse or promote products derived from
#   this Software without specific prior written permission.

# THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
# EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
# MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
# IN NO EVENT SHALL THE CONTRIBUTORS OR COPYRIGHT HOLDERS BE LIABLE FOR
# ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF
# CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
# WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS WITH THE SOFTWARE.
######################################################################

=head1 Globus::GSISocket

A GSI protected socket connection implemented as a subclass of
IO::Socket::SSL.

=cut

package Globus::GSISocket;

use Globus::Config;
use Globus::GSICredential;

use IO::Socket::SSL;

@ISA = qw(IO::Socket::SSL);

# Enabling debugging if non-zero
$DEBUG = 0;

# Turn on debugging in IO::Socket:SSL
#$IO::Socket::SSL::DEBUG = 1;

my $ERRSTR = undef;

# Static values

# Don't do delegation
my $NO_DELEGATION_FLAG = "0";

# A NULL byte
my $NULL = chr(0);

=head2 Methods

=over 4

=item new()

Create a new GSISocket object. Takes the same options as
    IO::Socket:SSL. The following options are set by default if not
provided:

B<Variable>            B<Value>
SSL_use_cert        Forced to 1
SSL_key_file        Globus::Config::getKeyPath()
SSL_cert_file       Globus::Config::getCertPath()
SSL_ca_path         Globus::Config::getCAPath()
SSL_version         Forced to "SSLv3"
Proto               Forced to "tcp"

=cut

sub new
{
    my $class = shift;
    my $arg_hash = (ref($_[0]) eq 'HASH') ? $_[0] : {@_};
    $arg_hash->{SSL_use_cert} = 1;
    $arg_hash->{SSL_version} = "SSLv3";
    $arg_hash->{Proto} = "tcp";
    if (!defined($arg_hash->{SSL_key_file}))
    {
	$arg_hash->{SSL_key_file} = Globus::Config::getKeyPath();
	_debug("Setting key path to %s", $arg_hash->{SSL_key_file});
    }
    if (!defined($arg_hash->{SSL_cert_file}))
    {
	$arg_hash->{SSL_cert_file} = Globus::Config::getCertPath();
	_debug("Setting cert path to %s", $arg_hash->{SSL_cert_file});
    }
    if (!defined($arg_hash->{SSL_ca_path}))
    {
	$arg_hash->{SSL_ca_path} = Globus::Config::getCAPath();
	_debug("Setting trusted CA path to %s",
	       $arg_hash->{SSL_ca_path});
    }
    my $self = $class->SUPER::new(%$arg_hash);
    if (!defined($self))
    {
	$ERRSTR = IO::Socket::SSL::errstr();
	if ($ERRSTR =~ /error:00000000:lib\(0\):func\(0\):reason\(0\)/)
	{
	    # Unfortunately this error string can mean lots of things
	    # like "connection refused" and "client certificate expired"
	    # so we can't return a decent error message.
	    $ERRSTR = sprintf("Error connecting to %s:%s",
			      $arg_hash->{PeerHost},
			      $arg_hash->{PeerPort});
	}
	return undef;
    }
    ${*$self}{errstr} = undef;
    bless($self, $class);

    if (!$self->_authorize())
    {
	# Error already set
	$self->close();
	# Save error string to class state
	$ERRSTR = $self->errstr();
	return undef;
    }

    # We don't support delegation here yet
    $self->write($NO_DELEGATION_FLAG);

    return $self;
}


=item acceptDelegation()

Accept a delegated credential from the peer.

B<Arguments:>

certReq           String containing certificate request to use.

certReqFormat     Either "PEM" or "DER". Default if PEM if not provided.

B<Returns:> a Globus::GSICredential object or undef on error. (

=cut

sub acceptDelegation
{
    my $self = shift;
    my $arg_hash = (ref($_[0]) eq 'HASH') ? $_[0] : {@_};

    my $req = $arg_hash->{"certReq"};
    my $reqFormat = $arg_hash->{"certReqFormat"} || "PEM";

    if ($reqFormat eq "PEM")
    {
	$req = $self->_convertReqToDER($req);
    }

    # Write NULL-terminated certificate request
    if (!$self->write($req))
    {
	$self->_error("Error writing certificate request");
	return undef;
    }
    if (!$self->write($NULL, 1))
    {
	$self->_error("Error writing certificate request (NULL byte");
	return undef;
    }

    # HACK: See MyProxy Bug 359:
    # http://bugzilla.ncsa.uiuc.edu/show_bug.cgi?id=359
    # We may get an Myproxy error message here instead of a certificate chain.
    if ($self->myProxyResponsePending())
    {
	$self->_error("Got response from server instead of certificate chain");
	return undef;
    }

    # Read number of certificates being delegated
    my $numCerts;
    if (!$self->read($numCerts, 1))
    {
	$self->_error("Error reading certificate chain (numCerts)");
	return undef;
    }
    $numCerts = ord($numCerts);
    $self->_debug("Preparing to read %d certificates", $numCerts);

    my $cred = Globus::GSICredential->new();

    for (my $count = 0; $count < $numCerts; $count++)
    {
	my $cert = $self->readDERCertificate();
	if (!defined($cert))
	{
	    # $self->_error() already called.
	    return undef;
	}
	$cred->addDERCert($cert);
    }

    return $cred;
}



=item readDERCertificate()

Read and return a DER encoded certificate.

B<Arguments:> None

B<Returns:> String containing a DER encoded certificate, or undef on error.

=cut

sub readDERCertificate
{
    my $self = shift;
    my $headerLen = 4;

    my $cert;
    # Read 4 byte header
    if (!$self->read($cert, $headerLen))
    {
	$self->_error("Could not reader DER certificate header");
	return undef;
    }

    my @headerData = unpack("C4", $cert);
    # Make sure we have what looks like a header
    if (($headerData[0] != 0x30) or ($headerData[1] != 0x82))
    {
	$self->_error("Bad DER certificate header (0x%x 0x%x)",
		     $headerData[0], $headerData[1]);
	return undef;
    }
    my $len = $headerData[2] * 256 + $headerData[3];
    $self->_debug("Certificate length is %d", $len);
    if (!$self->read($cert, $len, $headerLen))
    {
	$self->_error("Could not read DER certificate of length %d",
		     $len);
	return undef;
    }
    return $cert;
}

=item myProxyResponsePending

This function is a hack to suport workaround described in MyProxy bug 359:
http://bugzilla.ncsa.uiuc.edu/show_bug.cgi?id=359

Return true if there is a MyProxy response waiting on the socket.

B<Arguments:> None

B<Returns:> 1 if MyProxy reponse pending on socket, 0 otherwise.

=cut
sub myProxyResponsePending
{
    my $self = shift;

    my $peekBuf;
    # This will be the string waiting in the buffer indicating a response
    my $verString = "VERSION";
    if (!$self->peek($peekBuf, length($verString)))
    {
	$self->_error("Error checking for error reading certificate chain");
	return 0;
    }
    if ($verString eq $peekBuf)
    {
	return 1;
    }
    return 0;
}

=item _authorize()

Authorize the peer we've connected to.

Currently assumes host authorization based on DNS name.

B<Arguments:> None

B<Returns:> 1 on success, 0 on error calling _error()

=cut
sub _authorize
{
    my $self = shift;
    my $peerDN = $self->peer_certificate("subject");
    $self->_debug("Peer DN is: %s", $peerDN);
    $peerDN =~ /CN=(.*)/;
    if (!defined($1))
    {
	$self->_error("Authorization failed. Could not extract hostname from peer DN (%d).",
		      $peerDN);
	return 0;
    }
    my $peerHostname = $1;
    # Remove "host/" prefix if present
    $peerHostname =~ s/^host\///;
    my $expectedHostname = $self->_getpeername();
    if (!defined($expectedHostname))
    {
	# error already set
	return 0;
    }
    $self->_debug("Expected peer hostname is: %s", $expectedHostname);
    if ($peerHostname ne $expectedHostname)
    {
	$self->_error("Host authorization failed. Expected %s got %s.",
	    $expectedHostname, $peerHostname);
	return 0;
    }
    # Success
    $self->_debug("Host authorization success.");
    return 1;
}

=item _getpeername()

Return the peer hostname for authorization.

B<Arguments:> None

B<Returns:> Hostname as string, undef on error

=cut

sub _getpeername()
{
    use Net::hostent;
    use Net::Domain;
    use Socket;
    my $self = shift;
    my $peerAddr = $self->peeraddr();
    my $peername = undef;
    # Are we connected to localhost?
    if (inet_ntoa($peerAddr) eq inet_ntoa(INADDR_LOOPBACK))
    {
	# Yes, localhost
	$peername = Net::Domain::hostfqdn();
	if (!defined($peername))
	{
	    $self->_error("Authorization failed. Could not resolve local hostname.");
	    return undef;
	}
    }
    else
    {
	# No, some other host
	my $host = gethostbyaddr($peerAddr);
	if (!defined($host))
	{
	    $self->_error("Authorization failed. Could not resolve host address %s",
			  $self->peerhost());
	    return undef;
	}
	$peername = $host->name
    }
    return $peername;
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
    my $message .= sprintf($format, @_);
    ${*$self}{errstr} = $message;
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
    if (defined($self) && defined(${*$self}{errstr}))
    {
	return ${*$self}{errstr};
    }
    else
    {
	return $ERRSTR || IO::Socket::SSL::errstr();
    }
}

=back

=head2 Version

$Id$

=cut

# Return true for import/use

1;

### Local Variables: ***
### mode:perl ***
### End: ***
######################################################################
