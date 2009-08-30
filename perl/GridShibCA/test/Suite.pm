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
    qw(GridShibCA::test::ConfigTest)
}

# Return true for import/use
1;

### Local Variables: ***
### mode:perl ***
### End: ***
######################################################################
