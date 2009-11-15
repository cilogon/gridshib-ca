######################################################################
#
# This file is part of the GriShib-CA distribution.  Copyright
# 2006-2009 The Board of Trustees of the University of
# Illinois. Please see LICENSE at the root of the distribution.
#
######################################################################

=head1 Globus::Config

An interface to Globus configuration.

=cut

package Globus::Config;

use File::Spec;

=head2 Methods

=over 4

=item getCertPath()

Return the path to the default certificate, as a string.

=cut

sub getCertPath
{
    if (defined($ENV{X509_USER_PROXY}))
    {
	return $ENV{X509_USER_PROXY};
    }
    if (defined($ENV{X509_USER_CERT}))
    {
	return $ENV{X509_USER_CERT};
    }
    my $proxyPath = getProxyPath();
    if ( -f $proxyPath )
    {
	return $proxyPath;
    }
    return File::Spec->catfile(getHome(), ".globus", "usercert.pem");
}

=item getKeyPath()

Return the path to the default key, as a string.

=cut

sub getKeyPath
{
    if (defined($ENV{X509_USER_PROXY}))
    {
	return $ENV{X509_USER_PROXY};
    }
    if (defined($ENV{X509_USER_KEY}))
    {
	return $ENV{X509_USER_KEY};
    }
    my $proxyPath = getProxyPath();
    if ( -f $proxyPath )
    {
	return $proxyPath;
    }
    return File::Spec->catfile(getHome(), ".globus", "userkey.pem");
}


=item getProxyPath()

Return the path to the default Proxy file, as a string.

=cut

sub getProxyPath
{
    if (defined($ENV{X509_USER_PROXY}))
    {
	return $ENV{X509_USER_PROXY};
    }
    return File::Spec->catfile(File::Spec->tmpdir(),
			       "x509up_u" . $<)
}

=item getHome()

Return the user's home directory as a string.

=cut

sub getHome
{
    return $ENV{HOME};
}

=item getCAPath()

Return path to trusted CA certificates directory.

=cut

sub getCAPath
{
    if (defined($ENV{X509_CERT_PATH}))
    {
	return $ENV{X509_CERT_PATH};
    }
    return "/etc/grid-security/certificates";
}

=back

=head2 Version

Verion 1.1

=cut

# Return true for import/use

1;

### Local Variables: ***
### mode:perl ***
### End: ***
######################################################################
