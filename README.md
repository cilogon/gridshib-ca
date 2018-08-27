# GridShib-CA for CILogon

This is the CILogon-specific version of
[GridShib-CA Version 2](http://gridshibca.cilogon.org/Version2).

## Building

Note that you will need a code-signing certificate stored in a Java
keystore (`KEYSTORE_CODESIGN`), and the password for the code-signing
certificate stored in a plain text file (`KEYSTORE_CODESIGN_PASSWORD`). 

After cloning the repository from GitHub, do the following.

```
./bootstrap.sh
export KEYSTORE_CODESIGN=/path/to/keystore
export KEYSTORE_CODESIGN_PASSWORD=/path/to/keystore/password/textfile
./configure --enable-build \
  --with-gridshib-ca-dir-name=gridshib-ca \
  --with-www-hostname=cilogon.org \
  --with-shib-protected-url=/secure/gridshib-ca \
  --with-relative-dn='O=Shibboleth User,DC=cilogon,DC=org' \
  --with-jarsigner-keystore=$KEYSTORE_CODESIGN \
  --with-jarsigner-password-file=$KEYSTORE_CODESIGN_PASSWORD
make
make dist
```

## Installing

This above result in a tarball `gridshib-ca-2-0-1.tar.gz` which can be
installed as follows.

```
tar xvzf gridshib-ca-2-0-1.tar.gz
cd gridshib-ca-2.0.1
./configure --with-gridshib-ca-dir-name=gridshib-ca \
       	--with-www-hostname=cilogon.org \
       	--with-shib-protected-url=/secure/gridshib-ca \
       	--with-relative-dn='O=Shibboleth User,DC=cilogon,DC=org'
make
sudo make install
```

Note that there are two directories installed by GridShib-CA which are also
present in https://github.com/cilogon/service . These two directories can be
removed (to be later re-installed by the `cilogon/service` repo).

```
sudo rm -rf /var/www/html/secure/gridshib-ca
sudo rm -rf /var/www/html/gridshib-ca
```

## License

The University of Illinois/NCSA Open Source License (NCSA). Please see
[License File](https://github.com/cilogon/service-lib/blob/master/LICENSE)
for more information.
