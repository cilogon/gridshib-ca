#!/usr/bin/perl
######################################################################
#
# $Id$
#
# Create a CA cert and key for GridShib CA to use.
#
######################################################################
# Copyright 2006 The Board of Trustees of the University of Illinois.
# All rights reserved.

# Developed by:

#   The GridShib Project
#   National Center for Supercomputing Applications
#   University of Illinois
#   http://gridshib.globus.org/

# Permission is hereby granted, free of charge, to any person obtaining
# a copy of this software and associated documentation files (the
# "Software"), to deal with the Software without restriction, including
# without limitation the rights to use, copy, modify, merge, publish,
# distribute, sublicense, and/or sell copies of the Software, and to
# permit persons to whom the Software is furnished to do so, subject to
# the following conditions:

#   Redistributions of source code must retain the above copyright
#   notice, this list of conditions and the following disclaimers.

#   Redistributions in binary form must reproduce the above copyright
#   notice, this list of conditions and the following disclaimers in the
#   documentation and/or other materials provided with the distribution.

#   Neither the names of the National Center for Supercomputing
#   Applications, the University of Illinois, nor the names of its
#   contributors may be used to endorse or promote products derived from
#   this Software without specific prior written permission.

# THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
# EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
# MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
# IN NO EVENT SHALL THE CONTRIBUTORS OR COPYRIGHT HOLDERS BE LIABLE FOR
# ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF
# CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
# WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS WITH THE SOFTWARE.
######################################################################

my $openssl = "/sw/bin/openssl";
my $certFile = "/tmp/gridshib-ca/gridshib-ca-cert.pem";
my $keyFile = "/tmp/gridshib-ca/gridshib-ca-key.pem";
my $confPath = "/tmp/gridshib-ca";

# Default CA lifetime is 10 years
my $lifetime = sprintf("%d", 10 * 365);

# CA subject
my $subject = "/C=US/O=TEST ORG/OU=TEST OU/CN=Test CA";

# Key length in bits
my $keyLength = 2048;

######################################################################

print "Creating GridShib CA\n";
print " Keyfile is $keyFile\n";
print " Certificate is $certFile\n";

my @args = ( $openssl );
push(@args, "req");
push(@args, "-x509");
push(@args, "-newkey", "rsa:$keyLength");
push(@args, "-out", $certFile);
push(@args, "-keyout", $keyFile);
push(@args, "-days", $lifetime);
push(@args, "-nodes");
push(@args, "-subj", $subject);

if (system(@args) != 0)
{
    print "Execution of " . join(' ', @args) . " failed: $?\n";
    exit(1);
}

######################################################################

print "Getting hash of GridShib CA\n";

@args = ( $openssl );
push(@args, "x509");
push(@args, "-in", $certFile);
push(@args, "-hash");
push(@args, "-noout");

if (open(fromOpenSSL, join(' ', @args) . "|") == 0)
{
    print "Execution of " . join(' ', @args) . " failed: $?\n";
    exit(1);
}

my $hash = <fromOpenSSL>;
chomp($hash);
close(fromOpenSSL);

print " Hash is $hash\n";

######################################################################

use File::Copy;

my $hashFile = "${confPath}/${hash}.0";

print "Copying CA certificate to $hashFile\n";

if (copy($certFile, $hashFile) == 0)
{
    print "Copy failed: $!\n";
    exit(1);
}

######################################################################

my $policyFile = "${confPath}/${hash}.signing_policy";

print "Creating $policyFile\n";

if (open(FH, ">$policyFile") == 0)
{
    print "Open of $policyFile failed: $!";
    exit(1);
}

my $policy = <<END;
access_id_CA      X509         '$subject'

pos_rights        globus        CA:sign

cond_subjects     globus     '"/C=US/O=TEST ORG/OU=TEST OU/*"'
END

print FH $policy;
close(FH);

######################################################################

my $serialFile = "${confPath}/serial";

print "Creating $serialFile\n";

if (open(FH, ">$serialFile") == 0)
{
    print "Open of $serialFile failed: $!";
    exit(1);
}

print FH "01\n";
close(FH);

######################################################################
#
# Change ownership of all created files
#

print "Setting ownership of created files.\n";
@args=("chown");
push(@args, "www");
push(@args, $certFile, $keyFile, $hashFile, $policyFile, $serialFile);


if (system(@args) != 0)
{
    print "Execution of " . join(' ', @args) . " failed: $?\n";
    exit(1);
}

######################################################################

print "Done.\n";
exit(0);
