######################################################################
#
# $Id$
#
# GridShibCA Unit tests for FakeSignOn.pm
#
######################################################################
package GridShibCA::test::FakeSignOnTest;

use base qw(Test::Unit::TestCase);

use GridShibCA::FakeSignOn;

sub new
{
    my $self = shift()->SUPER::new(@_);
    return $self;
}

sub set_up
{
    my $self = shift;
    # No-Op
}

sub tear_down
{
    # No-Op
}

sub test_default
{
    my $self = shift;
    my $signon = GridShibCA::FakeSignOn->new();
    $self->assert_equals("FAKE", $signon->methodName());
    $self->assert_equals("FakeUser", $signon->getUserId());
    $self->assert_equals("urn:gridshib:FakeSignOn:FakeIdp", $signon->getIdP());
}

sub test_settingValues
{
    my $self = shift;
    my $method = "ReallyFake";
    my $userId = "testuser\@fakeidp.org";
    my $idp = "urn:mace:incommon:fakeidp";
    my $signon = GridShibCA::FakeSignOn->new((method => $method,
					      userId => $userId,
					      idpId => $idp,
					     ));
    $self->assert_equals($method, $signon->methodName());
    $self->assert_equals($userId, $signon->getUserId());
    $self->assert_equals($idp, $signon->getIdP());
}

# Return true for import/use
1;

### Local Variables: ***
### mode:perl ***
### End: ***
######################################################################
