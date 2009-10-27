######################################################################
#
# $Id$
#
# GridShibCA Unit tests for CGISession.pm
#
######################################################################

package GridShibCA::test::CGISessionTest;

use base qw(Test::Unit::TestCase);

use GridShibCA::CGISession;

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
    # Fixture cleanup
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

# Return true for import/use
1;

### Local Variables: ***
### mode:perl ***
### End: ***
######################################################################
