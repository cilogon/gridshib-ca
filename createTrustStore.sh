#!/bin/sh
# Exit on any error
set -e

# They target store we're creating
target=$1; shift
echo "Creating trust store: $target"

# Password to pass into keytool. Not actually used for anything, just keeps it for prompting for one.
passwd="abcdef"

# Where the certificates are stored
certificates_dir="certificates"

rm -f $target

for cert in $certificates_dir/*.[0-9]; do
	echo Adding $cert
	keytool -import -keystore $target -noprompt -alias $cert -storepass $passwd -file $cert
done