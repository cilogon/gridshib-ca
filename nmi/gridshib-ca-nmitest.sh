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

dist_dirname=$1
shift

if test X${dist_dirname} = X ; then
    echo "Usage: $0 <dist-dirname>"
    exit 1
fi

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

echo "Building and testing:"
make
make test

echo "Installing and testing:"
make install
utils/create-openssl-ca.pl
make test-post-install

echo "Success."
echo "GridShib-CA NMI Test Script done."

exit 0

