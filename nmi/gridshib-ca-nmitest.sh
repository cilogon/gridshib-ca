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

# Output a bunch of stuff for debugging
echo ""
echo "PATH:"
echo ${PATH}
echo ""
echo "LD_LIBRARY_PATH:"
echo ${LD_LIBRARY_PATH}
echo ""
PERL=`which perl`
echo "perl: ${PERL}"
OPENSSL=`which openssl`
echo "openssl: ${OPENSSL}"

dist_dirname=$1
shift

if test X${dist_dirname} = X ; then
    echo "Usage: $0 <dist-dirname>"
    exit 1
fi

echo "Distribution directory: ${dist_dirname}"

# CD into unpacked distribution directory.
cd ${dist_dirname}

#
# configure
#

tmpDir="install"

confOpts=""
confOpts="${confOpts} --with-gridshib-ca-conf-dir=${tmpDir}/gridshib-ca/"
confOpts="${confOpts} --with-shib-protected-cgi-bin-dir=${tmpDir}/gridshib-ca-shib-cgi/"
confOpts="${confOpts} --with-cgi-bin-dir=${tmpDir}/gridshib-ca-cgi/"
confOpts="${confOpts} --with-gridshib-ca-html_dir=${tmpDir}/gridshib-ca-html/"
# Make web user "us" so that chown() during installation work
confOpts="${confOpts} --with-www-user=${USER}"

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

