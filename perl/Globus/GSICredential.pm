######################################################################
#
# This file is part of the GriShib-CA distribution.  Copyright
# 2006-2009 The Board of Trustees of the University of
# Illinois. Please see LICENSE at the root of the distribution.
#
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

Version 1.1

=cut

# Return true for import/use

1;

### Local Variables: ***
### mode:perl ***
### End: ***
######################################################################
