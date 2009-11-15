######################################################################
#
# GridShibCA Unit tests for Exception.pm
#
######################################################################

package GridShibCA::test::ExceptionTest;

use base qw(Test::Unit::TestCase);

use GridShibCA::Exception qw(:try);

sub new
{
    my $self = shift()->SUPER::new(@_);
    return $self;
}

sub set_up
{
    my $self = shift;
}

sub tear_down
{
    # Fixture cleanup
}

sub test_raise
{
    my $self = shift;
    $self->assert_raises(GridShibCA::Exception,
			 sub { throw GridShibCA::Exception("test"); });
}

sub test_catch
{
    my $self = shift;
    my $message = "Hello World!";
    my $ex = undef;
    try
    {
	throw GridShibCA::Exception($message);
    }
    catch GridShibCA::Exception with
    {
	$ex = shift;
	$self->assert_str_equals($message, $ex->text());
    };
    $self->assert_not_null($ex, "Did not catch exception");
}

sub test_logtext
{
    my $self = shift;
    my $message = "Hello World!";
    my $logText = "Secret";
    my $ex = undef;
    try
    {
	throw GridShibCA::Exception($message, -logText=>$logText);
    }
    catch GridShibCA::Exception with
    {
	$ex = shift;
	$self->assert_str_equals($message, $ex->text());
	$self->assert_str_equals($logText, $ex->logText());
    };
    $self->assert_not_null($ex, "Did not catch exception");
}

sub test_internalException
{
    my $self = shift;
    $self->assert_raises(GridShibCA::InternalException,
			 sub { throw GridShibCA::InternalException("test"); });
}

# Return true for import/use
1;

### Local Variables: ***
### mode:perl ***
### End: ***
######################################################################
