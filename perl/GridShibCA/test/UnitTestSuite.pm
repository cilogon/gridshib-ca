######################################################################
#
# Test suite for unittests for GridShib-CA PERL modules.
#
# These tests can be run prior to install as a normal user.
#
######################################################################

package GridShibCA::test::UnitTestSuite;

use base qw(Test::Unit::TestSuite);

sub name { 'GridShib-CA Unit Test Suite' } 

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
