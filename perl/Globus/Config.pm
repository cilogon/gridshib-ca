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

$Id$

=cut

# Return true for import/use

1;

### Local Variables: ***
### mode:perl ***
### End: ***
######################################################################
