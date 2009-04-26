dnl $Id$
dnl See Copyright statement below.
dnl See file 'LICENSE' for license.

AC_INIT(gridshib-ca, 0.6.0)

# Status of this version, e.g. preview, alpha, beta
# Set to empty string("") for unqualified (e.g. final) release
PACKAGE_RELEASE_STATUS="preview"

AC_REVISION($Id$)

COPYRIGHT="Copyright 2008 The Board of Trustees of the University of Illinois."
AC_SUBST(COPYRIGHT)
AC_COPYRIGHT(${COPYRIGHT})

LICENSE="LICENSE"
AC_SUBST_FILE(LICENSE)

dnl Sort out version strings based on our version
PACKAGE_VERSION_STRING=${PACKAGE_VERSION}
if test -n "${PACKAGE_RELEASE_STATUS}" ; then
   PACKAGE_VERSION_STRING="${PACKAGE_VERSION_STRING}-${PACKAGE_RELEASE_STATUS}"
fi
AC_SUBST(PACKAGE_VERSION_STRING)

PACKAGE_FULLNAME=${PACKAGE_NAME}-${PACKAGE_VERSION_STRING}
AC_SUBST(PACKAGE_FULLNAME)

dnl tarball uses dashes of dots
PACKAGE_VERSION_MOD=`echo ${PACKAGE_VERSION_STRING} | sed -e "s/\./-/g"`
DIST_FILE_BASE=${PACKAGE_NAME}-${PACKAGE_VERSION_MOD}
AC_SUBST(DIST_FILE_BASE)

DIST_TARBALL_NAME=${DIST_FILE_BASE}.tar.gz
AC_SUBST(DIST_TARBALL_NAME)

dnl Documentation directory doesn't include release status (e.g. alpha/beta)
DOC_INSTALL_DIR=${PACKAGE_NAME}-${PACKAGE_VERSION}
AC_SUBST(DOC_INSTALL_DIR)

DIST_DIR=${PACKAGE_FULLNAME}
AC_SUBST(DIST_DIR)

dnl ------------------------------------------------------------
dnl Subdirectores we will be building in
SUBDIRS=""
AC_SUBST(SUBDIRS)

dnl ------------------------------------------------------------
dnl AC_SRC_FILES(FILES, SRC_FILES)
dnl For each file listed in FILES add ${file}.in to SRC_FILES
AC_DEFUN([AC_SRC_FILES],
[
$2="";
for file in ${$1} ; do
  $2="${$2} ${file}.in";
done
])dnl

dnl ------------------------------------------------------------
dnl AC_CONVERT_EXT(SRC_FILES, SRC_EXT, DST_EXT, DST_FILES) For each
dnl file in SFC_FILES add to DST_FILES with extension changed deom
dnl SRC_EXT to DST_EXT.
AC_DEFUN([AC_CONVERT_EXT],
[
$4=""
for file in ${$1} ; do
  $4="${$4} `echo ${file} | sed -e s/$2/$3/`"
done
])dnl

dnl ------------------------------------------------------------
dnl AC_CLEAN_PATH(PATH)
dnl Clean up a path, removing duplicate /'s
AC_DEFUN([AC_CLEAN_PATH],
[$1=`echo ${$1} | sed -e "s/\/\/*/\//g"`])dnl


dnl ----------------------------------------------------------------------	
dnl Find required binaries
AC_PROG_INSTALL
AC_PATH_PROG(PERL, perl)
AC_PATH_PROG(POD2HTML, pod2html)
AC_PATH_PROG(MKDIR, mkdir)
AC_PATH_PROG(RMDIR, rmdir)
AC_PATH_PROG(CP, cp)
AC_PATH_PROG(TOUCH, touch)
AC_PATH_PROG(RM, rm)
AC_PATH_PROG(ENV, env)

dnl ------------------------------------------------------------
dnl Figure out our FQDN
AC_ARG_WITH(hostname,
	AC_HELP_STRING([--with-hostname],
		[Specify the fully-qualified hostname]),
	[HOSTNAME=$withval],
	[AC_HOSTNAME([HOSTNAME],
		[AC_MSG_ERROR([Could not determine fully-qualified hostname. Specify with --with-hostname=<name>])])])
AC_SUBST(HOSTNAME)

dnl ------------------------------------------------------------
dnl Different groups of files

dnl Extra files that are distributed
DIST_EXTRA_FILES="\
	configure \
	configure.ac \
	Makefile.in \
	install-sh \
	LICENSE \
	nmi/MIME-Base64-3.07.tar.gz \
	nmi/gridshib-ca-nmitest.sh \
	test/test-cert.pem \
	test/test-key.pem"
AC_SUBST(DIST_EXTRA_FILES)

dnl Generated binaries
BINARIES=""
AC_SUBST(BINARIES)

dnl Included HTML files that are installed in the configuration directory
HTML_HEADER=$srcdir/webdocs/header.html
AC_SUBST_FILE(HTML_HEADER)
HTML_FOOTER=$srcdir/webdocs/footer.html
AC_SUBST_FILE(HTML_FOOTER)
HTML_INCS="${HTML_HEADER} ${HTML_FOOTER}"
AC_SUBST(HTML_INCS)
AC_CONFIG_FILES([${HTML_INCS}])

dnl Header and footer files for generated documentation
DOC_HEADER=$srcdir/doc/header.html
AC_SUBST_FILE(DOC_HEADER)
DOC_FOOTER=$srcdir/doc/footer.html
AC_SUBST_FILE(DOC_FOOTER)
DOC_INCS="${DOC_HEADER} ${DOC_FOOTER}"
AC_SUBST(DOC_INCS)

dnl CGI-BIN scripts
CGI_BIN_SCRIPTS="webdocs/generateCred.cgi \
	webdocs/create-jnlp \
	webdocs/sysinfo.pl \
	webdocs/login.cgi \
	webdocs/portalLogin.cgi \
	webdocs/getTrustedCAs.cgi"
AC_SUBST(CGI_BIN_SCRIPTS)
AC_CONFIG_FILES([$CGI_BIN_SCRIPTS])
AC_SRC_FILES(CGI_BIN_SCRIPTS, CGI_BIN_SRCS)
AC_SUBST(CGI_BIN_SRCS)

dnl Files to be installed in unprotected html directory
HTML_FILES="webdocs/index.html \
	webdocs/checkPrerequisites.html \
	webdocs/GridShibCA.css \
	webdocs/GridShibCATester.jnlp"
AC_SUBST(HTML_FILES)
AC_CONFIG_FILES([$HTML_FILES])
AC_SRC_FILES(HTML_FILES, HTML_SRCS)
AC_SUBST(HTML_SRCS)

TEMPLATE_FILES="\
	templates/CredentialRetriever.jnlp \
	templates/error.html \
	templates/login.html"
AC_SUBST(TEMPLATE_FILES)

dnl Configuration files that should not be overwritten by installation.
CONF_FILES="\
	conf/gridshib-ca.conf \
	conf/authorizedIdps.conf \
	conf/authorizedPortals.conf \
	${HTML_INCS}"
AC_SUBST(CONF_FILES)
AC_CONFIG_FILES([$CONF_FILES])
AC_SRC_FILES(CONF_FILES, CONF_SRCS)
AC_SUBST(CONF_SRCS)

dnl Configuration files that should be overwritten by installation.
OVERWRITE_CONF_FILES="\
	webdocs/CredentialRetriever.jnlp"
AC_SUBST(OVERWRITE_CONF_FILES)
AC_CONFIG_FILES([$OVERWRITE_CONF_FILES])
AC_SRC_FILES(OVERWRITE_CONF_FILES, OVERWRITE_CONF_SRCS)
AC_SUBST(OVERWRITE_CONF_SRCS)

dnl Utilities
dnl Each AC_CONFIG_FILES must be separate so chmod works.
UTILS=""
UTILS="$UTILS utils/create-openssl-ca.pl"
AC_CONFIG_FILES([utils/create-openssl-ca.pl], [chmod +x utils/create-openssl-ca.pl])
UTILS="$UTILS utils/check-config.pl"
AC_CONFIG_FILES([utils/check-config.pl], [chmod +x utils/check-config.pl])
UTILS="$UTILS utils/issue-cert.pl"
AC_CONFIG_FILES([utils/issue-cert.pl], [chmod +x utils/issue-cert.pl])
UTILS="$UTILS utils/myproxy-mapapp.pl"
AC_CONFIG_FILES([utils/myproxy-mapapp.pl], [chmod +x utils/myproxy-mapapp.pl])
AC_SUBST(UTILS)
AC_SRC_FILES(UTILS, UTIL_SRCS)
AC_SUBST(UTIL_SRCS)

dnl Java source files
JAVA_CLASSES="\
	edu/ncsa/gridshib/gridshibca/CredentialRetriever.class \
	edu/ncsa/gridshib/gridshibca/GUI.class \
	edu/ncsa/gridshib/gridshibca/GridShibCATester.class \
	edu/ncsa/gridshib/gridshibca/PEM.class \
	edu/ncsa/gridshib/gridshibca/UMask.class \
	edu/ncsa/gridshib/gridshibca/UMaskException.class \
	edu/ncsa/gridshib/gridshibca/Version.class \
	org/globus/util/ConfigUtil.class \
	org/globus/util/Util.class"
AC_SUBST(JAVA_CLASSES)

dnl Jar file
JAR_FILE="GridShibCA-${PACKAGE_VERSION_STRING}.jar"
AC_SUBST(JAR_FILE)

dnl PERL modules

dnl These PERL modules we test
REQUIRED_PERL_MODULES="\
	perl/GridShibCA/Authorization.pm \
	perl/GridShibCA/CA.pm \
	perl/GridShibCA/CertRegistry.pm \
	perl/GridShibCA/CGIinterface.pm \
	perl/GridShibCA/Command.pm \
	perl/GridShibCA/Config.pm \
	perl/GridShibCA/HTMLError.pm \
	perl/GridShibCA/HTMLTemplate.pm \
	perl/GridShibCA/Logger.pm \
	perl/GridShibCA/NameMapper.pm \
	perl/GridShibCA/OpenSSLCA.pm \
	perl/GridShibCA/SAMLExtension.pm \
	perl/GridShibCA/ShibInterface.pm \
	perl/GridShibCA/Template.pm \
	perl/GridShibCA/TokenGenerator.pm"
AC_SUBST(REQUIRED_PERL_MODULES)

dnl All perl modules generated by configure
PERL_MODULES="\
	perl/GridShibCA/FakeShib.pm \
	perl/GridShibCA/MyProxyCA.pm \
	perl/GridShibCA/myVocsDBCertRegistry.pm \
	${REQUIRED_PERL_MODULES}"
AC_SUBST(PERL_MODULES)
AC_CONFIG_FILES([$PERL_MODULES])
AC_SRC_FILES(PERL_MODULES, PERL_MODULE_SRCS)
AC_SUBST(PERL_MODULE_SRCS)

dnl Check for PERL modules needed for MyProxy client
AC_PROG_PERL_MODULES([IO::Socket::SSL],
	[AC_MSG_RESULT([Perl modules prequisites for MyProxy CA found.])],
	[AC_MSG_WARN([PERL module prequisites for MyProxy CA not found.])])

dnl PERL distributed modules
dnl These are perl modules that are distributed and not generated by
dnl autoconf
PERL_DIST_MODULES="\
	perl/Globus/Config.pm \
	perl/Globus/GSICredential.pm \
	perl/Globus/GSISocket.pm \
	perl/Globus/MyProxyClient.pm"
AC_SUBST(PERL_DIST_MODULES)

dnl Documenation directory
DOC_DIR="doc"
AC_SUBST(DOC_DIR)

dnl Documentation files that are generated by autoconf and docbook
dnl First file listed must be the master file to be listed on the
dnl docbook2html commandline
DOCBOOK_FILES="\
	doc/index.xml \
	doc/readme.xml \
	doc/test-deployment.xml \
	doc/license.xml \
	doc/faq.xml \
	doc/changelog.xml \
	doc/admin/index.xml \
	doc/admin/install.xml \
	doc/admin/myproxy-ca.xml \
	doc/admin/perl-modules.xml \
	doc/admin/portal-delegation.xml \
	doc/admin/process-flow.xml \
	doc/admin/saml-tools.xml \
	doc/admin/https-cas.xml"
AC_SUBST(DOCBOOK_FILES)
AC_SRC_FILES(DOCBOOK_FILES, DOCBOOK_SRCS)
AC_SUBST(DOCBOOK_SRCS)
AC_CONVERT_EXT(DOCBOOK_FILES, .xml, .html, DOC_FILES)
AC_SUBST(DOC_FILES)

dnl Documentation images
DOC_IMAGES="\
 	doc/admin/gridshib-ca-process-flow.jpg \
	doc/admin/portal-delegation.jpg"
AC_SUBST(DOC_IMAGES)

dnl Output directory for autogenerated perl documentation
PERL_DOC_DIR=${DOC_DIR}/admin/perl
AC_SUBST(PERL_DOC_DIR)

dnl Create paths for rsyncing doc files such that doc/ prefix is trimmed
dnl when using 'rsync -R'
DOC_INSTALL_FILES=`echo ${DOC_FILES} ${DOC_IMAGES} ${PERL_DOC_DIR} | sed "s/doc\//doc\/.\//g"`
AC_SUBST(DOC_INSTALL_FILES)

dnl ----------------------------------------------------------------------
dnl check for required perl modules that should be on any reasonably
dnl recent perl, but don't always seem to be
AC_PROG_PERL_MODULES([CGI Config::General File::Basename FileHandle File::Copy File::Path File::Spec File::Temp Getopt::Std IPC::Open3 MIME::Base64 Sys::Syslog],
	[AC_MSG_RESULT([All standard perl modules found.])],
	[AC_MSG_ERROR([One or more required standard perl modules not found. Old version of perl?])])

dnl Check for extra perl modules we need
AC_PROG_PERL_MODULES([Text::Template],
	[AC_MSG_RESULT([Required extra perl modules found.])],
	[AC_MSG_ERROR([One or more required extra perl modules not found.])])

dnl ----------------------------------------------------------------------
dnl Stuff needed to build our openssl-based CA
AC_PROG_CC


AC_ARG_WITH(openssl,
	AC_HELP_STRING([--with-openssl],
		[Specify path to openssl binary (Default: None)]),
	[OPENSSL=${withval}],
	[OPENSSL="NONE"])

if test "X${OPENSSL}" = "XNONE" ; then
   AC_PATH_PROG(OPENSSL, openssl, [FAILED])
   if test ${OPENSSL} = "FAILED"; then
      AC_MSG_ERROR([Could not find openssl binary in path.])
   fi
else
   if test -x "${OPENSSL}" ; then
      :
   else
      AC_MSG_ERROR([Could not find openssl binary: ${OPENSSL}])
   fi
fi
AC_MSG_RESULT([Using openssl binary ${OPENSSL}])

AC_ARG_WITH(openssl-prefix,
	AC_HELP_STRING([--with-openssl-prefix],
		[Specify prefix for openssl include and lib (Default: None)]),
	[OPENSSL_PREFIX=${withval}],
	[OPENSSL_PREFIX="NONE"])

if test "X${OPENSSL_PREFIX}" != "XNONE" ; then
   AC_SUBST(OPENSSL_PREFIX)
   if test -d ${OPENSSL_PREFIX}/include ; then
      CFLAGS="${CFLAGS} -I${OPENSSL_PREFIX}/include"
      AC_MSG_RESULT([Adding -I${OPENSSL_PREFIX}/include to CFLAGS])
   fi

   if test -d ${OPENSSL_PREFIX}/lib ; then
      LDFLAGS="${LDFLAGS} -L${OPENSSL_PREFIX}/lib"
      AC_MSG_RESULT([Adding -L${OPENSSL_PREFIX}/lib to LDFLAGS])
   fi
fi

AC_CHECK_LIB(crypto, CRYPTO_malloc,
        [],
	[AC_MSG_ERROR([Could not find OpenSSL libraries (libcrypto)])])
AC_CHECK_HEADER([openssl/opensslv.h],
	[],
	[AC_MSG_ERROR([OpenSSL header file not found.])])

AC_MSG_CHECKING([for OpenSSL version > 0.9.7])
AC_CHECK_OPENSSL_VERSION([0x00907000L],
	[AC_MSG_RESULT([ok])],
	[AC_MSG_ERROR([OpenSSL version is too old.])])


BINARIES="${BINARIES} openssl-ca/openssl-ca"
SUBDIRS="${SUBDIRS} openssl-ca"

OPENSSL_CA_SRCS="\
	openssl-ca/Makefile.in \
	openssl-ca/*.c \
	openssl-ca/*.h"
AC_SUBST(OPENSSL_CA_SRCS)

dnl ------------------------------------------------------------
dnl Test scripts
dnl Each AC_CONFIG_FILES must be separate so chmod works.
TEST_SCRIPTS=""
TEST_SCRIPTS="$TEST_SCRIPTS openssl-ca/openssl-ca-test.sh"
AC_CONFIG_FILES([openssl-ca/openssl-ca-test.sh], [chmod +x openssl-ca/openssl-ca-test.sh])
TEST_SCRIPTS="$TEST_SCRIPTS test/test-dist.sh"
AC_CONFIG_FILES([test/test-dist.sh], [chmod +x test/test-dist.sh])
AC_SUBST(TEST_SCRIPTS)

TEST_SCRIPTS="\
	openssl-ca/openssl-ca-test.sh \
	test/test-cred.sh \
	test/test-dist.sh"
AC_SUBST(TEST_SCRIPTS)
AC_CONFIG_FILES([$TEST_SCRIPTS])
AC_SRC_FILES(TEST_SCRIPTS, TEST_SRCS)
AC_SUBST(TEST_SRCS)

dnl ------------------------------------------------------------
dnl Command-line options

dnl Enable building (aka development) mode?
AC_ARG_ENABLE(build,
	AC_HELP_STRING([--enable-build],
		[Enable build environment. For developers. (Default: Disabled)]),
	[MODE=build],
	[MODE=dist])
AC_SUBST(MODE)

AC_MSG_CHECKING([for name of various GridShib-CA directories])
default=gridshib-ca-${PACKAGE_VERSION}
AC_ARG_WITH(gridshib-ca-dir-name,
	AC_HELP_STRING([--with-gridshib-ca-dir-name=<name>],
		[Specifiy the name to use for install directories. (Default: gridshib-ca-<version>)]),
	[GRIDSHIB_CA_DIR_NAME=$withval],
	[GRIDSHIB_CA_DIR_NAME=$default])
AC_SUBST(GRIDSHIB_CA_DIR_NAME)
AC_MSG_RESULT(${GRIDSHIB_CA_DIR_NAME})

dnl Figure out prefix
myprefix=${prefix}
if test "x$myprefix" = xNONE ; then
   myprefix=$ac_default_prefix
fi

AC_MSG_CHECKING([for path to GridShib CA configuration directory])
default=${myprefix}/${GRIDSHIB_CA_DIR_NAME}
AC_ARG_WITH(gridshib-ca-conf-dir,
	AC_HELP_STRING([--with-gridshib-ca-conf-dir=<path>],
		[Specify the full path of the gridshib-ca configuration directory. This is where keys, certificates and other files for the GridShib CA are stored. (Default: <prefix>/<gridshib-ca-dir-name>)]),
	[GRIDSHIB_CA_CONF_DIR=$withval],
	[GRIDSHIB_CA_CONF_DIR=$default])
AC_CLEAN_PATH(GRIDSHIB_CA_CONF_DIR)
AC_SUBST(GRIDSHIB_CA_CONF_DIR)
AC_MSG_RESULT(${GRIDSHIB_CA_CONF_DIR})

AC_MSG_CHECKING([for Apache httpd root directory])
default=/var/www/
AC_ARG_WITH(www-dir,
	AC_HELP_STRING([--with-www-dir=<path>],
		[Specify the base www directory. This is directory under which the Apache HTTPD has the html/ and cgi-bin/ directories. (Default: /var/www/)]),
	[WWW_DIR=$withval],
	[WWW_DIR=$default])
AC_CLEAN_PATH(WWW_DIR)
AC_SUBST(WWW_DIR)
AC_MSG_RESULT(${WWW_DIR})

AC_MSG_CHECKING([for URL for Shibboleth-protected CGI-BIN directory])
default=/cgi-bin/shib-protected/${GRIDSHIB_CA_DIR_NAME}
AC_ARG_WITH(shib-protected-cgi-bin-url,
	AC_HELP_STRING([--with-shib-protected-cgi-bin-url=<url>],
		[Specify the Shibboleth-protected cgi-bin url. This is the relative URL, without hostname, where Shibboleth-protected CGI-BIN executables are installed. (Default: /cgi-bin/shib-protected/<gridshib-ca-dir-name>)]),
	[SHIB_PROTECTED_CGI_BIN_URL=$withval],
	[SHIB_PROTECTED_CGI_BIN_URL=$default])
AC_SUBST(SHIB_PROTECTED_CGI_BIN_URL)
AC_MSG_RESULT(${SHIB_PROTECTED_CGI_BIN_URL})

AC_MSG_CHECKING([for path to Shibboleth-protected CGI-BIN directory])
default=${WWW_DIR}/${SHIB_PROTECTED_CGI_BIN_URL}
AC_ARG_WITH(shib-protected-cgi-bin-dir,
	AC_HELP_STRING([--with-shib-protected-cgi-bin-dir=<path>],
		[Specify the Shibboleth-protected cgi-bin directory. This is the path that corresponds the the shib-protected-cgi-bin url. (Default: <www-dir>/<gridshib-ca-cgi-bin-url>)]),
	[SHIB_PROTECTED_CGI_BIN_DIR=$withval],
	[SHIB_PROTECTED_CGI_BIN_DIR=$default])
AC_CLEAN_PATH(SHIB_PROTECTED_CGI_BIN_DIR)
AC_SUBST(SHIB_PROTECTED_CGI_BIN_DIR)
AC_MSG_RESULT(${SHIB_PROTECTED_CGI_BIN_DIR})

AC_MSG_CHECKING([for protocol for Shibboleth-protected CGI-BIN directory])
default=https
AC_ARG_WITH(shib-protected-protocol,
	AC_HELP_STRING([--with-shib-protected-protocol],
		[Specify the protocol to use for the Shibboleth protected cgi-bin directory. By default is 'https' and should only be changed to 'http' for test deployments.]),
	[SHIB_PROTECTED_PROTOCOL=$withval],
	[SHIB_PROTECTED_PROTOCOL=$default])
AC_SUBST(SHIB_PROTECTED_PROTOCOL)
AC_MSG_RESULT(${SHIB_PROTECTED_PROTOCOL})

AC_MSG_CHECKING([for URL to GridSHIB-CA])
default=/${GRIDSHIB_CA_DIR_NAME}/
AC_ARG_WITH(gridshib-ca-url,
	AC_HELP_STRING([--with-gridshib-ca-url=<url>],
		[Specify the URL for the gridshib-ca. This is the relative URL, without hostname, where the main GridShib-CA starting page will be found. (Default: /<gridshib-ca-dir-name>/)]),
	[GRIDSHIB_CA_URL=$withval],
	[GRIDSHIB_CA_URL=$default])
AC_SUBST(GRIDSHIB_CA_URL)
AC_MSG_RESULT(${GRIDSHIB_CA_URL})

AC_MSG_CHECKING([for path to GridShib CA html directory])
AC_ARG_WITH(gridshib-ca-html-dir,
	AC_HELP_STRING([--with-gridshib-ca-html-dir],
		[Specify the direcotry for the gridshib-ca HTML corresponding to the gridshib-ca-url. Default: <www-dir>/html/<gridshib-ca-dir-name>/]),
	[GRIDSHIB_CA_HTML_DIR=$withval],
	[GRIDSHIB_CA_HTML_DIR=${WWW_DIR}/html/${GRIDSHIB_CA_DIR_NAME}/])
AC_CLEAN_PATH(GRIDSHIB_CA_HTML_DIR)
AC_SUBST(GRIDSHIB_CA_HTML_DIR)
AC_MSG_RESULT(${GRIDSHIB_CA_HTML_DIR})

AC_MSG_CHECKING([for hostname to use in GridShib CA URLS])
AC_ARG_WITH(www-hostname,
	AC_HELP_STRING([--with-www-hostname],
		[Specify the hostname used in the GridShib CA URL. (Default: local hostname)]),
	[WWW_HOSTNAME=$withval],
	[WWW_HOSTNAME=$HOSTNAME])
AC_MSG_RESULT(${WWW_HOSTNAME})
AC_SUBST(WWW_HOSTNAME)

AC_MSG_CHECKING([for CSS URL to use])
AC_ARG_WITH(css-url,
	AC_HELP_STRING([--with-css-url],
		[Specify the URL for the CSS file to use. (Default: <gridshib-ca-dir-name>/GridShibCA.css]),
	[CSS_URL=$withval],
	[CSS_URL=${GRIDSHIB_CA_URL}/GridShibCA.css])
AC_SUBST(CSS_URL)
AC_MSG_RESULT(${CSS_URL})

AC_MSG_CHECKING([for owner of www files])
AC_ARG_WITH(www-user,
	AC_HELP_STRING([--with-www-user],
		[Specify the username that the httpd runs under. (Default: apache)]),
	[WWW_USER=$withval],
	[WWW_USER=apache])
AC_MSG_RESULT(${WWW_USER})
AC_SUBST(WWW_USER)

dnl Figure out our base DN
AC_MSG_CHECKING([for our base DN])
BASE_DN=""
hostnameComponents=`echo $HOSTNAME | sed -e "s/\./ /g"`
for component in $hostnameComponents ; do
    test -n "${BASE_DN}" && BASE_DN="${BASE_DN},"
    BASE_DN="${BASE_DN}DC=${component}"
done
AC_MSG_RESULT(${BASE_DN})

dnl Check and make sure we have a valid Relative DN
if test "X${BASE_DN}" = "X"; then
   AC_MSG_ERROR([Relative DN string is empty ('hostname' returned empty string?)])
fi

dnl Relative DN for certificates issued to users
AC_MSG_CHECKING([for the CA relative DN])
AC_ARG_WITH(relative-dn,
	AC_HELP_STRING([--with-relative-dn=<rfc2253DN>],
		[Specify the relative DN in RFC 2253 format (no extra whitespace)]),
		[RELATIVE_DN=$withval],
		[RELATIVE_DN="O=Shibboleth User,${BASE_DN}"])
AC_MSG_RESULT(${RELATIVE_DN})
AC_SUBST(RELATIVE_DN)

dnl Generate an OpenSSL version of Relative DN
dnl This assumes no commas in DN
AC_MSG_CHECKING([for the relativce DN in OpenSSL format])
OPENSSL_RELATIVE_DN=`(\
	IFS=",";\
	DN="";\
	for component in ${RELATIVE_DN} ; do \
	    DN="/${component}${DN}" ;\
	done ;\
	echo ${DN} ;\
	)`
AC_MSG_RESULT(${OPENSSL_RELATIVE_DN})
AC_SUBST(OPENSSL_RELATIVE_DN)

dnl DN for the default CA create-openssl-ca.pl will create
AC_MSG_CHECKING([for the DN of the GridShib-ca])
CA_DN="CN=GridShib CA,O=Certificate Authority,${BASE_DN}"
AC_MSG_RESULT(${CA_DN})
AC_SUBST(CA_DN)

dnl ------------------------------------------------------------
dnl CGI-BIN demo scripts.

CGI_DEMO_SCRIPTS="\
	webdocs/gridshib-ca-demo-portal.cgi"
AC_CONFIG_FILES([$CGI_DEMO_SCRIPTS])
AC_SUBST(CGI_DEMO_SCRIPTS)
AC_SRC_FILES(CGI_DEMO_SCRIPTS, CGI_DEMO_SRCS)
AC_SUBST(CGI_DEMO_SRCS)

AC_ARG_ENABLE(demos,
	AC_HELP_STRING([--enable-demos],
		[Build demos. (Default: Disabled)]),
	[ENABLE_DEMOS=true],
	[ENABLE_DEMOS=false])
AC_SUBST(ENABLE_DEMOS)

if test ${ENABLE_DEMOS} = "true" ; then
   AC_MSG_CHECKING([for path to cgi-bin directory])
   default=${WWW_DIR}/cgi-bin/
   AC_ARG_WITH(cgi-bin-dir,
	AC_HELP_STRING([--with-cgi-bin-dir=<path>],
		[Specify the (unprotected) cgi-bin directory. (Default: <www-dir>/cgi-bin/)]),
	[CGI_BIN_DIR=$withval],
	[CGI_BIN_DIR=$default])
   AC_CLEAN_PATH(CGI_BIN_DIR)
   AC_SUBST(CGI_BIN_DIR)
   AC_MSG_RESULT(${CGI_BIN_DIR})

   AC_PROG_PERL_MODULES([Crypt::OpenSSL::PKCS10])

   AC_GLOBUS_LOCATION()

   if test "X${GLOBUS_LOCATION}" = "X" ; then
      AC_MSG_ERROR([Could not find GLOBUS_LOCATION needed for demos. Please specify using --with-globus-location])
   fi
fi

dnl ------------------------------------------------------------
dnl syslog parameters
SYSLOG_ID="GridShib-CA"
AC_SUBST(SYSLOG_ID)
SYSLOG_FACILITY="local0"
AC_SUBST(SYSLOG_FACILITY)

dnl ------------------------------------------------------------
dnl Development mode

dnl Development utilities
DEV_UTILS=""
AC_SUBST(DEV_UTILS)

dnl Bouncy Castle source
BC_SOURCE=""
AC_SUBST(BC_SOURCE)

dnl Configure dependancies
CONFIGURE_DEP=""
AC_SUBST(CONFIGURE_DEP)

MAKEFILE_DEV_INCLUDE=""
AC_SUBST(MAKEFILE_DEV_INCLUDE)

if test ${MODE} = "build" ; then

   dnl Include Makefile include file in Makefile
   AC_CONFIG_FILES([dev-utils/Makefile-inc])
   MAKEFILE_DEV_INCLUDE="include dev-utils/Makefile-inc"

   dnl Enable building of java
   AC_CONFIG_FILES([edu/ncsa/gridshib/gridshibca/Version])

   dnl Programs needed for building java
   AC_PATH_PROG(KEYTOOL, keytool)
   AC_PATH_PROG(JAVAC, javac)
   AC_PATH_PROG(JAR, jar)
   AC_PATH_PROG(JARSIGNER, jarsigner)
   AC_PATH_PROG(TAR, tar)
   AC_PATH_PROG(FIND, find)
   AC_PATH_PROG(GREP, grep)
   AC_PATH_PROG(MKTEMP, mktemp)
   AC_PATH_PROG(TAIL, tail)
   AC_PATH_PROG(MV, mv)

   dnl Generate development utilities and tests
   DEV_UTILS="$DEV_UTILS \
	dev-utils/clean-pod2html.sh \
   	dev-utils/build-test.sh"
   dnl Each AC_CONFIG_FILES() must be seperate for chmod to work.
   AC_CONFIG_FILES([dev-utils/build-test.sh], [chmod +x dev-utils/build-test.sh])
   AC_CONFIG_FILES([dev-utils/clean-pod2html.sh], [chmod +x dev-utils/clean-pod2html.sh])


   dnl Generated NMI build and test files
   NMI_FILES="\
   	nmi/gridshib-ca-cmdfile \
	nmi/gridshib-ca.ftp"
   AC_CONFIG_FILES([${NMI_FILES}])
   AC_SUBST(NMI_FILES)

   dnl Host to use for NMI Build and Test
   dnl Old Host: grandcentral.cs.wisc.edu
   NMI_HOST="nmi-s003.cs.wisc.edu"
   AC_SUBST(NMI_HOST)

   dnl Enable regeneration of configure from configure.ac and aclocal.m4
   CONFIGURE_DEP="configure.ac aclocal.m4"

   dnl Generate documentation
   dnl SUBDIRS="${SUBDIRS} docbook"

   AC_MSG_CHECKING([for Bouncy Castle source directory])
   AC_ARG_WITH(bc-source-path,
	AC_HELP_STRING([--with-bc-source-path=<path>],
		[Provide the path to the Bouncy Castle source]),
	[BC_SOURCE=$withval],
	[BC_SOURCE="FAILED"])
	AC_MSG_RESULT(${BC_SOURCE})

   if test ${BC_SOURCE} = "FAILED"; then
	AC_MSG_ERROR([Could not find Bouncy Castle source path.])
   fi

   AC_PATH_PROG(CVS, cvs)
   AC_PATH_PROG(MAKE, make)

   AC_PATH_PROG(XSLTPROC, xsltproc)

   AC_MSG_CHECKING([for XSLT stylesheet])
   AC_ARG_WITH(xsl-path,
	AC_HELP_STRING([--with-xsl-path=<path>],
		[Provide the base path for xsltproc transformations (e.g. /usr/local/share/xml/xsl/)]),
	[XSL_PATH=$withval],
	[XSL_PATH="/usr/local/share/xml/xsl/"])
   AC_MSG_RESULT([${XSL_PATH}])
		
   XSLT_STYLE_SHEET="${XSL_PATH}/docbook-xsl/html/chunk.xsl"
   if test -f ${XSLT_STYLE_SHEET} ; then
         AC_MSG_RESULT([${XSLT_STYLE_SHEET}])
   else
	AC_MSG_ERROR([Could not find XSLT style sheet (${XSLT_STYLE_SHEET}).])
   fi			
   AC_SUBST(XSLT_STYLE_SHEET)

   JARSIGNER_OPTS=""
   
   AC_ARG_WITH(jarsigner-keystore,
	AC_HELP_STRING([--with-jarsigner-keystore=<url>],
		[Provide url to keystore to use for signing jar.]),
	[JARSIGNER_OPTS="${JARSIGNER_OPTS} -keystore $withval"])
   AC_ARG_WITH(jarsigner-password,
	AC_HELP_STRING([--with-jarsigner-password=<password>],
		[Provide password for jarsigner private key (meant for testing, do not provide high-value passwords).]),
	[JARSIGNER_OPTS="${JARSIGNER_OPTS} -storepass $withval"])

   AC_SUBST(JARSIGNER_OPTS)
fi

dnl ------------------------------------------------------------
dnl Installed path of the configuration file

GRIDSHIB_CA_CONFIG_FILE=${GRIDSHIB_CA_CONF_DIR}/gridshib-ca.conf
AC_CLEAN_PATH(GRIDSHIB_CA_CONFIG_FILE)
AC_SUBST(GRIDSHIB_CA_CONFIG_FILE)

dnl Path to template directory
TEMPLATE_DIR=${GRIDSHIB_CA_CONF_DIR}/templates
AC_SUBST(TEMPLATE_DIR)

dnl ------------------------------------------------------------
dnl PERL arguments
dnl I would like to put quotes around ${GRIDSHIB_CA_CONF_DIR} here but
dnl they get carried over into @INC and screw up Perl.
PERL_ARGS="-I ${GRIDSHIB_CA_CONF_DIR}/perl"
AC_SUBST(PERL_ARGS)

dnl PERL arguments for CGI-BIN scripts
PERL_CGI_BIN_ARGS="-T -w ${PERL_ARGS}"
AC_SUBST(PERL_CGI_BIN_ARGS)

dnl ------------------------------------------------------------
dnl URLs for common targets

SHIB_PROTECTED_FULL_URL=${SHIB_PROTECTED_PROTOCOL}://${WWW_HOSTNAME}${SHIB_PROTECTED_CGI_BIN_URL}
AC_SUBST(SHIB_PROTECTED_FULL_URL)

LOGIN_URL=${SHIB_PROTECTED_FULL_URL}/login.cgi
AC_SUBST(LOGIN_URL)

dnl ------------------------------------------------------------
dnl Makefiles
AC_CONFIG_FILES([Makefile])

for subdir in ${SUBDIRS} ; do
    AC_CONFIG_FILES([${subdir}/Makefile])
done

dnl ------------------------------------------------------------
dnl And wrap it up
AC_OUTPUT()
