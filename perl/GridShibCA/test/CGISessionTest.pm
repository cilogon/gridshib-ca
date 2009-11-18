######################################################################
#
# GridShibCA Unit tests for CGISession.pm
#
######################################################################

package GridShibCA::test::CGISessionTest;

use base qw(Test::Unit::TestCase);

use GridShibCA::CGISession;
use GridShibCA::UserIdentity;

sub new
{
    my $self = shift()->SUPER::new(@_);
    return $self;
}

sub set_up
{
    my $self = shift;
    $self->{session} = GridShibCA::CGISession->new();
    $self->assert_not_null($self->{session});
    $self->{session}->createNew();
    $self->assert_not_null($self->{session}->id());
    $self->{session}->param("Variable1", "Value1");
    $self->{session}->param("Variable2", "Value2");
    $self->{session}->flush();
}

sub tear_down
{
    my $self = shift;
    if ($self->{session}->established())
    {
	$self->{session}->destroy();
    }
}

sub test_sessionException
{
    my $self = shift;
    $self->assert_raises(GridShibCA::SessionException,
			 sub { throw GridShibCA::SessionException("test"); });
}

#
# Check and make sure unititialized session throws an error if we try
# to use it.
#
sub test_invalidSession
{
    my $self = shift;
    my $session = GridShibCA::CGISession->new();
    $self->assert_raises(GridShibCA::SessionException,
			 sub { $session->id(); });
}

sub test_fromId
{
    my $self = shift;
    my $id = $self->{session}->id();
    my $session2 = GridShibCA::CGISession->new();
    $self->assert_not_null($session2);
    $session2->fromId($id);
    $session2->destroy();
}

sub test_param
{
    my $self = shift;

    $self->assert_equals("Value1", $self->{session}->param("Variable1"));
    $self->assert_equals("Value2", $self->{session}->param("Variable2"));
}

sub test_params
{
    my $self = shift;
    my $params = $self->{session}->params();
    $self->assert_not_null($params);
    $self->assert_equals("Value1", $params->{Variable1});
    $self->assert_equals("Value2", $params->{Variable2});
}

sub test_destroy
{
    my $self = shift;
    $self->{session}->destroy();
    # Should not be invalid to access
    $self->assert_raises(GridShibCA::SessionException,
			 sub { $self->{session}->id(); });
}

sub test_established
{
    my $self = shift;
    my $session = GridShibCA::CGISession->new();
    $self->assert_equals(0, $session->established());
    $session->createNew();
    $self->assert_equals(1, $session->established());
    $session->destroy();
    $self->assert_equals(0, $session->established());
}

sub test_clientSession
{
    my $self = shift;
    my $userId = GridShibCA::UserIdentity->new(
	-authMethod => "AuthMethod",
	-userId => "Joe User",
	-idpId => "Jane Idp",
	-clientHost => "localhost");
    $self->{session}->fromUserIdentity($userId);
    my $clientSession = $self->{session}->createClientSession();
    $self->assert_not_null($clientSession);
    $self->assert_not_equals($clientSession->id(), $self->{session}->id());
    my $clientUserId = $clientSession->userIdentity();
    $self->assert_equals($clientUserId->authMethod(), $userId->authMethod());
    $self->assert_equals($clientUserId->userId(), $userId->userId());
    $self->assert_equals($clientUserId->idpId(), $userId->idpId());
    $self->assert_equals($clientUserId->clientHost(), $userId->clientHost());
}

# Return true for import/use
1;

### Local Variables: ***
### mode:perl ***
### End: ***
######################################################################
