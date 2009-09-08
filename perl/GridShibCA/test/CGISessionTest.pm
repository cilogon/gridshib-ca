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

}

sub tear_down
{
    # Fixture cleanup
}

sub test_new
{
    my $self = shift;
    $self->assert_not_null($self->{session});
}

sub test_sessionException
{
    my $self = shift;
    $self->assert_raises(GridShibCA::SessionException,
			 sub { throw GridShibCA::SessionException("test"); });
}

# Return true for import/use
1;

### Local Variables: ***
### mode:perl ***
### End: ***
######################################################################
