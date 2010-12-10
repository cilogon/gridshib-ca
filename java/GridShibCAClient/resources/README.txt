Contents of this directory
--------------------------

keystore - This is a java keystore file (of type 'jks') utilized when
  signing the GridShibCA-2.0.0.jar file.  Its contents:

  # keytool -list -v -keystore keystore 
  Enter keystore password: abcdef <not echoed>

  Keystore type: JKS
  Keystore provider: SUN

  Your keystore contains 1 entry

  Alias name: default
  Creation date: Apr 20, 2010
  Entry type: PrivateKeyEntry
  Certificate chain length: 1
  Certificate[1]:
  Owner: DC=org, DC=cilogon, C=US, O=CILogon, CN=cilogon.org
  Issuer: DC=org, DC=cilogon, C=US, O=CILogon, CN=cilogon.org
  Serial number: 4bcdbdca
  Valid from: Tue Apr 20 09:44:26 CDT 2010 until: Fri Apr 19 09:44:26 CDT 2013
  Certificate fingerprints:
           MD5:  5E:D5:7E:4C:F1:09:8A:3F:0C:29:63:CD:15:C0:0E:C6
           SHA1: D6:09:3B:27:48:BA:5A:0C:74:A5:DC:9D:39:73:B8:47:D3:39:C8:86
           Signature algorithm name: SHA1withDSA
           Version: 3


  *******************************************
  *******************************************

-----------------------------------------------------------------------

keystore-passwd - This contains the password for the keystore file above.

-----------------------------------------------------------------------

These files can be utilized when building the GridShib-CA code.

Copy these two files to the top-level directory of the gridshib-ca CVS
checkout, change to that directory, and then call the 'configure' script as
follows.

# ./configure --enable-build \
              --with-jarsigner-keystore=`pwd`/keystore \
              --with-jarsigner-password-file=`pwd`/keystore-passwd
# make
# make dist

The resulting file gridshib-ca-2-0-1.tar.gz can be distributed.
