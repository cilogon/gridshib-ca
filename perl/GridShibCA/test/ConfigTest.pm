######################################################################
#
# $Id$
#
# GridShibCA Unit tests for Config.pm
#
######################################################################
package GridShibCA::test::ConfigTest;

use base qw(Test::Unit::TestCase);

use GridShibCA::Config;

# Configuration file to use
my $configFilename = "conf/gridshib-ca.conf";

# Bogus, non-existant parameter for failure tests
my $bogusParam = "ThisParamDoesNotExist";

sub new
{
    my $self = shift()->SUPER::new(@_);
    return $self;
}

sub set_up
{
    my $self = shift;
    GridShibCA::Config->setConfigFilename($configFilename);
    $self->{config} = GridShibCA::Config->new(useStderrOnError => 0);
    $self->assert_not_null($self->{config});
}

sub tear_down
{
    # Fixture cleanup
}

sub test_getConfigFilename
{
    my $self = shift;
    $self->assert_str_equals($configFilename,
			     $self->{config}->getConfigFilename());
}

sub test_getParam
{
    my $self = shift;
    $self->assert_not_null($self->{config}->getParam("ConfigVersion"));
}

sub test_getParamShouldFail
{
    my $self = shift;
    $self->assert_null($self->{config}->getParam($bogusParam));
}

sub test_getParamBoolean
{
    my $self = shift;
    $self->assert_not_null($self->{config}->getParamBoolean("debug"));
}

sub test_getParamBooleanShouldFaile
{
    my $self = shift;
    $self->assert_null($self->{config}->getParamBoolean($bogusParam));
}

# Return true for import/use
1;

### Local Variables: ***
### mode:perl ***
### End: ***
######################################################################
