######################################################################
#
# GridShibCA Unit tests for Logger.pm
#
######################################################################
package GridShibCA::test::LoggerTest;

use base qw(Test::Unit::TestCase);

use GridShibCA::Exception;

sub new
{
    my $self = shift()->SUPER::new(@_);
    return $self;
}

sub set_up
{
    my $self = shift;
    $self->{logger} = GridShibCA::Logger->new();
    $self->assert_not_null($self->{logger});
}

sub tear_down
{
    # Fixture cleanup
}

sub test_log
{
    my $self = shift;
    $self->{logger}->log("info", "Logger test: test_log(%s)", "hello world");
}

sub test_info
{
    my $self = shift;
    $self->{logger}->info("Logger test: test_info(%s)", "hello world");
}

sub test_warn
{
    my $self = shift;
    $self->{logger}->warn("Logger test: test_warn(%s)", "hello world");
}

sub test_debug
{
    my $self = shift;
    $self->{logger}->debug("Logger test: test_debug(%s)", "hello world");
}

sub test_err
{
    my $self = shift;
    $self->{logger}->err("Logger test: test_err(%s)", "hello world");
}

sub test_error
{
    my $self = shift;
    $self->{logger}->error("Logger test: test_error(%s)", "hello world");
}

sub test_logException
{
    my $self = shift;
    my $ex = GridShibCA::Exception->new("Test exception");
    $self->{logger}->logException($ex);
}

# Return true for import/use
1;

### Local Variables: ***
### mode:perl ***
### End: ***
######################################################################
