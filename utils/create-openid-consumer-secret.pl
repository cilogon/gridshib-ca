#!/usr/bin/perl -I /usr/local/gridshib-ca//perl
######################################################################
#
# This file is part of the GriShib-CA distribution.  Copyright
# 2006-2009 The Board of Trustees of the University of
# Illinois. Please see LICENSE at the root of the distribution.
#
######################################################################

=head1 create-openid-consumer-secret.pl

Create a file with an OpenId consumer secret.

=head2 Usage

Run 'create-openid-consumer-secret.pl -h' for usage.

=head2 Return Value

Zero on success, one on error.

=head2 Version

Gridshib-CA version 2.0.0-preview

=cut

######################################################################

use FileHandle;
use GridShibCA::Config;

my $config = GridShibCA::Config->new();

# Characyer set to generate secret from
my @characterSet = (a..z, A..Z, 0..9);

######################################################################

use Getopt::Std;

# Output filename
my $outputFilename = $config->getParam("OpenId", "ConsumerSecretFilename");

# Secret length in characters
my $length = 24;

my $usage = <<"EOF";
Usage: $0 [<options>]

Options are:
 -h                  Print help
 -l <length>         Secret length [Default is $length]
 -o <path>           Output filename [Default is $outputFilename]
EOF

my %options;
getopts('hl:o:', \%options);

if ($options{h})
{
    print $usage;
    exit(0);
}

$outputFilename = $options{o} || $outputFilename;
$length = $options{l} || $length;

print "Writing OpenId consumer secret ($length characters) to $outputFilename...\n";

# Generate secret
my $secret = "";
foreach (1..$length)
{
    $secret .= $characterSet[rand(@characterSet)];
}

# Write out
my $file = FileHandle->new($outputFilename, "w") ||
    die "Failed to out $outputFilename: $!";

print $file $secret . "\n";
$file->close();

print "Success.\n";
exit(0);

### Local Variables: ***
### mode:perl ***
### End: ***
######################################################################
