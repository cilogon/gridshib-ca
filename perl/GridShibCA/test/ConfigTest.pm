######################################################################
#
# GridShibCA Unit tests for Config.pm
#
######################################################################
package GridShibCA::test::ConfigTest;

use base qw(Test::Unit::TestCase);

use GridShibCA::Config;

# Bogus, non-existant parameter for failure tests
my $bogusParam = "ThisParamDoesNotExist";

# Bogus, non-existant section for failure tests
my $bogusSection = "ThisSectionDoesNotExist";

sub new
{
    my $self = shift()->SUPER::new(@_);
    return $self;
}

sub set_up
{
    my $self = shift;
    $self->{config} = GridShibCA::Config->new(debug=>0);
    $self->assert_not_null($self->{config});
}

sub tear_down
{
    # Fixture cleanup
}

sub test_getConfigFilename
{
    my $self = shift;
    $self->assert_not_null($self->{config}->getConfigFilename());
}

sub test_getParam
{
    my $self = shift;
    $self->assert_not_null($self->{config}->getParam("ConfigVersion"));
}

sub test_getParamShouldFail
{
    my $self = shift;
    $self->assert_raises(GridShibCA::ConfigException,
			 sub { $self->{config}->getParam($bogusParam) });
    # Get a section, which should fail
    $self->assert_raises(GridShibCA::ConfigException,
			 sub { $self->{config}->getParam("CA") });
}

sub test_getParamBoolean
{
    my $self = shift;
    $self->assert_not_null($self->{config}->getParamBoolean("debug"));
}

sub test_getParamBooleanShouldFail
{
    my $self = shift;
    $self->assert_raises(GridShibCA::ConfigException,
			 sub { $self->{config}->getParamBoolean($bogusParam) });
}

sub test_raiseConfigException
{
    my $self = shift;
    $self->assert_raises(GridShibCA::ConfigException,
			 sub { throw GridShibCA::ConfigException("test"); });

}

sub test_raiseModuleException
{
    my $self = shift;
    $self->assert_raises(GridShibCA::ModuleException,
			 sub { throw GridShibCA::ModuleException("test"); });

}

sub test_getSection
{
    my $self = shift;
    my %section = $self->{config}->getSection("Commands"); 
    $self->assert_not_null($section{DEFAULT});
}

sub test_getSubSection
{
    my $self = shift;
    %section = $self->{config}->getSection("Commands", "IssueCert");
    $self->assert_not_null($section{class});
}

sub test_getSectionShouldFail
{
    my $self = shift;
    # Need at least one argument
    $self->assert_raises(GridShibCA::ConfigException,
			 sub { $self->{config}->getSection(); });
    # Bogus section name
    $self->assert_raises(GridShibCA::ConfigException,
			 sub { $self->{config}->getSection($bogusSection); });
}

sub test_getSectionparam
{
    my $self = shift;
    $self->assert_not_null($self->{config}->getParam("Modules", "CA"));
}

sub test_getParamURI
{
    my $self = shift;
    $self->assert_not_null($self->{config}->getParamURI("URLs", "WebApp"));
}

sub test_getParamURL
{
    my $self = shift;
    $self->assert_not_null($self->{config}->getParamURL("URLs", "WebApp"));
}

sub test_getSectionparamShouldFail
{
    my $self = shift;
    $self->assert_raises(GridShibCA::ConfigException,
			 sub { $self->{config}->getParam($bogusSection, "CA");} );
    $self->assert_raises(GridShibCA::ConfigException,
			 sub { $self->{config}->getParam("Modules", $bogusParam);} );

}

sub test_getCA
{
    my $self = shift;
    $self->assert_not_null($self->{config}->getCA());
}

sub test_getCommand
{
    my $self = shift;
    my $openssl = $self->{config}->getParam("Binaries", "OpenSSL");
    $self->assert_not_null($openssl);
    $command = $self->{config}->getCommand($openssl);
    $self->assert_not_null($command);
}

sub test_getCommandModuleDEFAULT
{
    my $self = shift;
    my $module = $self->{config}->getCommandModule("DEFAULT");
    $self->assert_not_null($module);
}


sub test_getCommandModule
{
    my $self = shift;
    my $module = $self->{config}->getCommandModule("IssueCert");
    $self->assert_not_null($module);
}

# This test fails if GSCA not installed because templates will not be
# installed (e.g. in buildbot tests).
#sub test_getHTMLTemplate
#{
#    my $self = shift;
#    my $template = $self->{config}->getHTMLTemplate("ErrorTemplate");
#    $self->assert_not_null($template);
#}

# Return true for import/use
1;

### Local Variables: ***
### mode:perl ***
### End: ***
######################################################################
