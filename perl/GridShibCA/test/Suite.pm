######################################################################
#
# $Id$
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
GridShibCA::test::CGISessionTest
GridShibCA::test::ConfigTest
GridShibCA::test::CommandTest
GridShibCA::test::FakeSignOnTest
GridShibCA::test::ExceptionTest
GridShibCA::test::HTMLTemplateTest
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
