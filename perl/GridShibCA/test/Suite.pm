######################################################################
#
# Test suite for GridShib-CA PERL modules.
#
######################################################################

package GridShibCA::test::Suite;

use base qw(Test::Unit::TestSuite);

sub name { 'GridShib-CA Test Suite' } 

sub include_tests
{
    qw(
GridShibCA::test::ConfigTest
GridShibCA::test::CommandTest
GridShibCA::test::ExceptionTest
GridShibCA::test::HTMLTemplateTest
GridShibCA::test::LoggerTest
GridShibCA::test::ShibLogonTest
GridShibCA::test::UserIdentityTest
)
}

# Return true for import/use
1;

### Local Variables: ***
### mode:perl ***
### End: ***
######################################################################
