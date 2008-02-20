#!/bin/sh
######################################################################
#
# gridshib-ca-nmitest.sh
#
# Script to run with NMI B&T system to build and test tarball.
#
# Usage: gridshib-ca-nmitest.sh <dirname>
#
# Expects to be called from parent directory of unpacked distribution.
# <dirname> should be the directory name of the unpacked distribution.
#
# $Id$
#
######################################################################
#
# Exit on any error

set -e

######################################################################

echo "GridShib-CA NMI TEST Script running..."
date
hostname
uname

# Output a bunch of stuff for debugging
echo ""
echo "PATH:"
echo ${PATH}
echo ""
echo "LD_LIBRARY_PATH:"
echo ${LD_LIBRARY_PATH}
echo ""
echo "Key binaries:"
PERL=`which perl`
echo "perl: ${PERL}"
OPENSSL=`which openssl`
echo "openssl: ${OPENSSL}"
echo ""

# OpenSSL on the NMI B&T platforms seems to require this
# (Don't use 'export FOO=BAR' form here as it's portable.)
LIBS="-ldl"
export LIBS

dist_dirname=$1
shift

if test X${dist_dirname} = X ; then
    echo "Usage: $0 <dist-dirname>"
    exit 1
fi

echo "Distribution directory: ${dist_dirname}"

# CD into unpacked distribution directory.
cd ${dist_dirname}

######################################################################
#
# Build Perl Modules
#

perlModules="MIME-Base64-3.07"

perlPrefix=`pwd`/perllib

echo "Creating perl install dir: ${perlPrefix}"
test -d ${perlPrefix} || mkdir ${perlPrefix}

for module in $perlModules; do
    echo "Building perl module $module"
    tarball="nmi/${module}.tar.gz"
    tar xfz $tarball
    (cd $module && \
	perl Makefile.PL PREFIX="${perlPrefix}" LIB="${perlPrefix}" && \
	make && \
	make test && \
	make install \
	)
    echo "Done installing $module"
done    

######################################################################
#
# Set PERLLIB
#

# Use PERL5LIB instead of PERLLIB as it will automatically cause perl
# to include any architecture-specific subdirectories.
echo "Setting PERL5LIB to include our perl modules..."
perllib=${perlPrefix}
# Figure out if we setting PERL5LIB or augmenting it
if test X${PERL5LIB} = X; then
    # Setting
    PERL5LIB=${perllib}
else
    # Prepending to it
    PERL5LIB=${perllib}:${PERL5LIB}
fi
export PERL5LIB
echo "PERL5LIB is ${PERL5LIB}"

######################################################################
#
# configure
#

tmpDir="install"

confOpts=""
confOpts="${confOpts} --with-gridshib-ca-conf-dir=${tmpDir}/gridshib-ca/"
confOpts="${confOpts} --with-shib-protected-cgi-bin-dir=${tmpDir}/gridshib-ca-shib-cgi/"
confOpts="${confOpts} --with-cgi-bin-dir=${tmpDir}/gridshib-ca-cgi/"
confOpts="${confOpts} --with-gridshib-ca-html_dir=${tmpDir}/gridshib-ca-html/"
# Make web user "us" so that chown() during installation works
confOpts="${confOpts} --with-www-user=${USER}"

uname > /dev/null 2>&1
if test $? -eq 0 ; then
    arch=`uname`
else
    arch="unknown"
fi

if test $arch = "SunOS"; then
    # Specify FQDN
    hostname=`hostname`
    confOpts="${confOpts} --with-hostname=${hostname}.example.com"
fi

echo "Configure options: ${confOpts}"

./configure ${confOpts}

#
# Build and install, testing on the way
#

echo "Building:"
make

echo "Running 'make test':"
make test

echo "Installing:"
make install

echo "Running create-openssl-ca.pl:"
utils/create-openssl-ca.pl

echo "Running 'make test-post-install':"
make test-post-install

echo "Success."
date
echo "GridShib-CA NMI Test Script done."

exit 0

