######################################################################
#
# $Id$
#
# GridShibCA Unit tests for Command.pm
#
######################################################################

package GridShibCA::test::CommandTest;

use base qw(Test::Unit::TestCase);

use GridShibCA::Config;
use GridShibCA::Command;
use GridShibCA::Constants qw(%BINARY);

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

sub test_basic
{
    my $self = shift;
    my $message = "hello world";
    $self->{command} = GridShibCA::Command->new($BINARY{"echo"}, $message);
    $self->assert_not_null($self->{command});
    $self->assert_equals(1, $self->{command}->exec());
    my $output = $self->{command}->getOutput();
    chomp($output);
    $self->assert_str_equals($message, $output);
    $self->assert_equals(0, $self->{command}->getStatus());
}

sub test_nonzero_status
{
    my $self = shift;
    $self->{command} = GridShibCA::Command->new($BINARY{"false"});
    $self->assert_not_null($self->{command});
    $self->assert_equals(0, $self->{command}->exec());
    $self->assert_equals(1, $self->{command}->getStatus());
}

sub test_null_command
{
    my $self = shift;
    $self->assert_raises(GridShibCA::CommandException,
			 sub { GridShibCA::Command->new() });
}

sub test_command_openssl
{
    my $self = shift;
    my $config = GridShibCA::Config->new();
    $self->assert_not_null($config);
    my $openssl = $config->getParam("Binaries", "OpenSSL");
    $self->assert_not_null($openssl);
    my $command = GridShibCA::Command->new($openssl);
    $self->assert_not_null($command);
}

# Return true for import/use
1;

### Local Variables: ***
### mode:perl ***
### End: ***
######################################################################
