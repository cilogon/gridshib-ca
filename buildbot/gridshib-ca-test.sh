#!/bin/sh
######################################################################
#
# gridshib-ca-test.sh
#
# Build GridShib-CA tarball and test it. Intended to be run by
# buildbot.
#
# Usage: gridshib-ca-test.sh
#
# Expects to be called top level directory of gridshib-ca checkout.
#
######################################################################
#
# Exit on any error

set -e

######################################################################

echo "GridShib-CA Test Script running..."
echo "Script path: $0"
date
hostname
uname

# Check for and source initialization script
INIT_SCRIPT=${HOME}/.gridshib-buildbotrc
if test -e ${INIT_SCRIPT} ; then
    echo "Sourcing init script: ${INIT_SCRIPT}"
    . ${INIT_SCRIPT}
fi

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

######################################################################
#
# configure
#

echo ""
echo "Building GridShib-CA Distribution."

if test -f ./bootstrap.sh ; then
    :
else
    echo "Cannot find bootstrap file"
    ls -l
    exit 1
fi

echo ""
echo "Bootstraping..."
./bootstrap.sh

confOpts=""
confOpts="${confOpts} --enable-build"

# Need to use absolute path here
keystore=`pwd`/test/test.pkcs12
# Note that 0_5_0 branch doesn't support --with-jarsigner-keystore
# and uses ~/.keystore
confOpts="${confOpts} --with-jarsigner-keystore=${keystore}"
# Installing as non-root, so cannot write to default /var/run/gridshib-ca
confOpts="${confOpts} --with-gridshib-ca-runtime-path=/tmp/gridshib-ca-runtime"

echo "Configure options: ${confOpts}"

./configure ${confOpts}

echo "Cleaning up:"
make realclean
rm -rf *.tar.gz

echo "Building:"
make

echo "Running 'make test':"
make test

echo "Making distribution:"
make dist

tarFile=`ls *.tar.gz`
ls -l ${tarFile}

echo "Running test-dist.sh:"
test/test-dist.sh ${tarFile}

echo "Success."
date

echo ""
echo "GridShib-CA Test Script done."

exit 0

