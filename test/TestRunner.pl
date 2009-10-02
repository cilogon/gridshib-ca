#!/usr/bin/perl -w

use strict;

use Test::Unit::Debug qw(debug_pkgs);
use Test::Unit::TestRunner;

# Uncomment and edit to debug individual packages.
#debug_pkgs(qw/Test::Unit::TestCase/);

my $testrunner = Test::Unit::TestRunner->new();
my $success = $testrunner->start(@ARGV);

if (!$success)
{
    exit(1);
}

exit(0);


