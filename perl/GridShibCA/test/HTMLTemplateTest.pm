######################################################################
#
# GridShibCA Unit tests for HTMLTemplate.pm
#
######################################################################
package GridShibCA::test::HTMLTemplateTest;

use base qw(Test::Unit::TestCase);

use GridShibCA::HTMLTemplate;

sub new
{
    my $self = shift()->SUPER::new(@_);
    return $self;
}

sub set_up
{
    # Fixture setup
}

sub tear_down
{
    # Fixture cleanup
}

sub test_new
{
    my $self = shift;
    my $template = GridShibCA::HTMLTemplate->new("test/template.html");
    $self->assert_not_null($template);
}

# Return true for import/use
1;

### Local Variables: ***
### mode:perl ***
### End: ***
######################################################################
