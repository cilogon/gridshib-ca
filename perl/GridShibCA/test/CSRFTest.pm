######################################################################
#
# GridShibCA Unit tests for Config.pm
#
######################################################################
package GridShibCA::test::CSRFTest;

use base qw(Test::Unit::TestCase);

use GridShibCA::CSRF;

sub new
{
    my $self = shift()->SUPER::new(@_);
    return $self;
}

sub set_up
{
    my $self = shift;
    $self->{csrf} = GridShibCA::CSRF->new();
    $self->assert_not_null($self->{csrf});
}

sub tear_down
{
    # Fixture cleanup
}

sub test_createCookie
{
    my $self = shift;
    $self->assert_not_null($self->{csrf}->createCookie());
}

sub test_getToken
{
    my $self = shift;
    $self->assert_not_null($self->{csrf}->getToken());
}

sub test_expiredCookie
{
    my $self = shift;
    $self->assert_not_null($self->{csrf}->expiredCookie());
}

sub test_getFormElement
{
    my $self = shift;
    $self->assert_not_null($self->{csrf}->getFormElement());
}

sub test_createToken
{
    my $self = shift;
    $self->assert_not_null($self->{csrf}->createToken());
}

sub test_raiseCSRFException
{
    my $self = shift;
    $self->assert_raises(GridShibCA::CSRFException,
			 sub { throw GridShibCA::CSRFException("test"); });

}

sub test_doubleCreate
{
    my $self = shift;
    # Two CSRF instances created in same process should return same token
    my $csrf = GridShibCA::CSRF->new();
    $self->assert_equals($csrf->getToken(), $self->{csrf}->getToken());
}

# Return true for import/use
1;

### Local Variables: ***
### mode:perl ***
### End: ***
######################################################################
