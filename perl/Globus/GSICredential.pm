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

=head1 Globus::GSICredential

An interface to a GSI Credential.

=cut

package Globus::GSICredential;


=head2 Methods

=over 4

=item new()

Create a new GSICredential object.

B<Arguments:> None

B<Returns:> A Globus::GSICredential object

=cut

sub new
{
    my $class = shift;
    my $self = {};
    $self->{certs} = [];
    $self->{key} = undef;
    bless($self, $class);
    return $self;
}

=item addCert()

Append a DER-encoded certificate to the certificate chain.

=cut

sub addDERCert
{
    my $self = shift;
    my $cert = shift;
    push(@{$self->{certs}}, $cert);
}

=item getCerts()

Return the array of DEF-encoded certificates.

=cut

sub getDERCerts
{
    my $self = shift;
    return @{$self->{certs}};
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
