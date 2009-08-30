######################################################################
#
# $Id$
#
# GridShibCA Unit tests for Command.pm
#
######################################################################

package GridShibCA::test::CommandTest;

use base qw(Test::Unit::TestCase);

use GridShibCA::Command;

# Binaries to use. Need to use absolute paths.
my %binary = (
    "bogus" => "nonexistantBinary",
    "cat" => "/bin/cat",
    "echo" => "/bin/echo",
);

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
    $self->{command} = GridShibCA::Command->new($binary{"echo"}, $message);
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
    $self->{command} = GridShibCA::Command->new("false");
    $self->assert_not_null($self->{command});
    $self->assert_equals(0, $self->{command}->exec());
    $self->assert_equals(1, $self->{command}->getStatus());
}

# Return true for import/use
1;

### Local Variables: ***
### mode:perl ***
### End: ***
######################################################################
