######################################################################
#
# Post-install test suite for GridShib-CA PERL modules.
#
# These tests require root privileges to run.
#
######################################################################

package GridShibCA::test::PostInstallTestSuite;

use base qw(Test::Unit::TestSuite);

sub name { 'GridShib-CA Post-install Test Suite' } 

sub include_tests
{
    qw(
GridShibCA::test::CGISessionTest
)
}

# Return true for import/use
1;

### Local Variables: ***
### mode:perl ***
### End: ***
######################################################################
