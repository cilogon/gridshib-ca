#!/bin/sh
######################################################################
#
# Bootstrap script for GridShib-CA development.
#
# Run after doing cvs checkout to build initial autoconf stuff.
#
# $Id$
#
######################################################################

# Exit on any error
set -e

echo "Running aclocal..."
aclocal

echo "Running autoconf..."
autoconf

echo "Done."
exit 0
