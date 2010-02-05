dnl
dnl Configure script for GridshibCA.
dnl
dnl See file 'LICENSE' for copyright and license.

AC_INIT(gridshib-ca, 2.0.0)

dnl Status of this version, e.g. preview, alpha, beta
dnl Set to empty string("") for unqualified (e.g. final) release
PACKAGE_RELEASE_STATUS=""

COPYRIGHT="Copyright 2006-2009 The Board of Trustees of the University of Illinois."
AC_SUBST(COPYRIGHT)
AC_COPYRIGHT(${COPYRIGHT})

LICENSE="LICENSE"
AC_SUBST_FILE(LICENSE)

dnl ----------------------------------------------------------------------
dnl Various strings describing for our tarball and install

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

DIST_DIR=${PACKAGE_FULLNAME}
AC_SUBST(DIST_DIR)

dnl Documentation directory does not include release status
DIST_DOC_DIR=${PACKAGE_NAME}-${PACKAGE_VERSION}
AC_SUBST(DIST_DOC_DIR)

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
dnl file in SRC_FILES add to DST_FILES with extension changed deom
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
AC_PATH_PROG(CAT, cat)
AC_PATH_PROG(FALSE, false)
AC_PATH_PROG(TRUE, true)
AC_PATH_PROG(ECHO, echo)

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
	test/test-key.pem \
	"
AC_SUBST(DIST_EXTRA_FILES)

dnl Generated binaries
BINARIES=""
AC_SUBST(BINARIES)

dnl Shib-protected webapps
SHIB_WEBAPPS="\
	webapps/shib.cgi \
	webapps/shibLaunchGSCA.jnlp \
	"
AC_SUBST(SHIB_WEBAPPS)
AC_CONFIG_FILES([$SHIB_WEBAPPS])
AC_SRC_FILES(SHIB_WEBAPPS, SHIB_WEBAPP_SRCS)
AC_SUBST(SHIB_WEBAPP_SRCS)

dnl Webapplications to be installed in GRIDSHIB_CA_WEB_PATH
WEBAPPS="\
	webapps/GridShibCA.cgi \
	webapps/launchGSCA.jnlp \
	webapps/openid.cgi \
	webapps/shibLaunchGSCA.jnlp \
	"
AC_SUBST(WEBAPPS)
AC_CONFIG_FILES([$WEBAPPS])
AC_SRC_FILES(WEBAPPS, WEBAPP_SRCS)
AC_SUBST(WEBAPP_SRCS)

TEMPLATE_FILES="\
	templates/CredentialRetriever.jnlp \
	templates/debug.html \
	templates/error.html \
	templates/GSCA.jnlp \
	templates/jnlp-launch.html \
	templates/jnlp-launch-form.html \
	templates/openid-cancel.html \
	templates/openid-logon-form.html \	
	templates/login.html \
	templates/logout.html \
	templates/python.html \
	templates/session-expired.html \
	templates/session-missing.html \
	"
AC_SUBST(TEMPLATE_FILES)

dnl Configuration files that should not be overwritten by installation.
CONF_FILES="\
	conf/gridshib-ca.conf \
	conf/policy.conf"
AC_SUBST(CONF_FILES)
AC_CONFIG_FILES([$CONF_FILES])
AC_SRC_FILES(CONF_FILES, CONF_SRCS)
AC_SUBST(CONF_SRCS)

dnl Utilities
dnl Each AC_CONFIG_FILES must be separate so chmod works.
UTILS=""
UTILS="$UTILS utils/create-openid-consumer-secret.pl"
AC_CONFIG_FILES([utils/create-openid-consumer-secret.pl],
		[chmod +x utils/create-openid-consumer-secret.pl])

UTILS="$UTILS utils/create-openssl-ca.pl"
AC_CONFIG_FILES([utils/create-openssl-ca.pl],
		[chmod +x utils/create-openssl-ca.pl])

UTILS="$UTILS utils/check-config.pl"
AC_CONFIG_FILES([utils/check-config.pl],
		[chmod +x utils/check-config.pl])

UTILS="$UTILS utils/config.pl"
AC_CONFIG_FILES([utils/config.pl],
		[chmod +x utils/config.pl])

UTILS="$UTILS utils/issue-cert.pl"
AC_CONFIG_FILES([utils/issue-cert.pl],
		[chmod +x utils/issue-cert.pl])

UTILS="$UTILS utils/myproxy-mapapp.pl"
AC_CONFIG_FILES([utils/myproxy-mapapp.pl],
		[chmod +x utils/myproxy-mapapp.pl])

AC_SUBST(UTILS)
AC_SRC_FILES(UTILS, UTIL_SRCS)
AC_SUBST(UTIL_SRCS)

dnl Jar file
JAR_NAME="GridShibCA-${PACKAGE_VERSION_STRING}.jar"
AC_SUBST(JAR_NAME)

JAR_PATH="java/GridShibCAClient/dist/${JAR_NAME}"
AC_SUBST(JAR_PATH)

dnl PERL modules

dnl These PERL modules we test
REQUIRED_PERL_MODULES="\
	perl/GridShibCA/Authorization.pm \
	perl/GridShibCA/CA.pm \
	perl/GridShibCA/CertRegistry.pm \
	perl/GridShibCA/Constants.pm \
	perl/GridShibCA/CGIinterface.pm \
	perl/GridShibCA/CGISession.pm \
	perl/GridShibCA/Command.pm \
	perl/GridShibCA/Config.pm \
	perl/GridShibCA/CSRF.pm \
	perl/GridShibCA/DisplayTemplate.pm \
	perl/GridShibCA/ErrorHandler.pm \
	perl/GridShibCA/Exception.pm \
	perl/GridShibCA/HTMLTemplate.pm \
	perl/GridShibCA/IssueCert.pm \
	perl/GridShibCA/JNLPForm.pm \
	perl/GridShibCA/LaunchClient.pm \
	perl/GridShibCA/LaunchJNLP.pm \
	perl/GridShibCA/Logger.pm \
	perl/GridShibCA/Logon.pm \
	perl/GridShibCA/Logout.pm \
	perl/GridShibCA/NameMapper.pm \
	perl/GridShibCA/OpenSSLCA.pm \
	perl/GridShibCA/Policy.pm \	
	perl/GridShibCA/PythonClient.pm \
	perl/GridShibCA/SAMLExtension.pm \
	perl/GridShibCA/ShibLaunchJNLP.pm \
	perl/GridShibCA/ShibLogon.pm \
	perl/GridShibCA/Template.pm \
	perl/GridShibCA/TrustRoots.pm \
	perl/GridShibCA/UserIdentity.pm \
	perl/GridShibCA/WebApp.pm \
	perl/GridShibCA/WebCommand.pm \
	perl/GridShibCA/X509DN.pm \
	"
AC_SUBST(REQUIRED_PERL_MODULES)

dnl All perl modules generated by configure
PERL_MODULES="\
	perl/GridShibCA/MyProxyCA.pm \
	perl/GridShibCA/OpenIdLogon.pm \
	${REQUIRED_PERL_MODULES}"
AC_SUBST(PERL_MODULES)
AC_CONFIG_FILES([$PERL_MODULES])
AC_SRC_FILES(PERL_MODULES, PERL_MODULE_SRCS)
AC_SUBST(PERL_MODULE_SRCS)

dnl PERL distributed modules
dnl These are perl modules that are distributed and not generated by
dnl autoconf
PERL_DIST_MODULES="\
	perl/Globus/Config.pm \
	perl/Globus/GSICredential.pm \
	perl/Globus/GSISocket.pm \
	perl/Globus/MyProxyClient.pm"
AC_SUBST(PERL_DIST_MODULES)

dnl Python Scripts
PYTHON_SCRIPTS="\
	python/gridshibca-client.py \
	"
AC_SUBST(PYTHON_SCRIPTS)
AC_CONFIG_FILES([$PYTHON_SCRIPTS])
AC_SRC_FILES(PYTHON_SCRIPTS, PYTHON_SCRIPT_SRCS)
AC_SUBST(PYTHON_SCRIPT_SRCS)

dnl ----------------------------------------------------------------------
dnl Documentation

DOC_DIR=doc
AC_SUBST(DOC_DIR)

PERL_DOC_DIR=${DOC_DIR}/perl
AC_SUBST(PERL_DOC_DIR)

dnl ----------------------------------------------------------------------
dnl check for perl modules that should be on any reasonably recent
dnl perl, but don't always seem to be
PERL_MODULES_TO_CHECK=" \
			CGI \
			File::Basename \
			File::Copy \
			File::Path \
			File::Spec \
			File::Temp \
			FileHandle \
			Getopt::Std \
			IPC::Open3 \
			LWP::UserAgent \
			MIME::Base64 \
			Sys::Syslog \
			"
AC_MSG_RESULT([Checking for basic PERL modules...])
AC_PROG_PERL_MODULES([${PERL_MODULES_TO_CHECK}])

dnl Extra PERL modules, not included with PERL by default, that need
dnl to be installed.
EXTRA_PERL_MODULES="\
	CGI::Session \
	Config::General \
	Crypt::OpenSSL::X509 \
	Date::Parse \
	Error \
	IO::Socket::SSL \
	Net::OpenID::Consumer \
	Text::Template \
"
AC_MSG_RESULT([Checking for extra PERL modules...])
AC_PROG_PERL_MODULES([${EXTRA_PERL_MODULES}])

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
AC_CONFIG_FILES([openssl-ca/openssl-ca-test.sh],
		[chmod +x openssl-ca/openssl-ca-test.sh])

TEST_SCRIPTS="$TEST_SCRIPTS test/test-dist.sh"
AC_CONFIG_FILES([test/test-dist.sh],
		[chmod +x test/test-dist.sh])

TEST_SCRIPTS="$TEST_SCRIPTS test/test-cred.sh"
AC_CONFIG_FILES([test/test-cred.sh],
		[chmod +x test/test-cred.sh])

AC_SUBST(TEST_SCRIPTS)

AC_SRC_FILES(TEST_SCRIPTS, TEST_SRCS)
AC_SUBST(TEST_SRCS)

dnl ------------------------------------------------------------
dnl Enable building (aka development) mode?

AC_ARG_ENABLE(build,
	AC_HELP_STRING([--enable-build],
		[Enable build environment. For developers. (Default: Disabled)]),
	[MODE=build],
	[MODE=dist])
AC_SUBST(MODE)

dnl ------------------------------------------------------------
dnl Figure out our install paths and URLs

dnl Figure out prefix
myprefix=${prefix}
if test "x$myprefix" = xNONE ; then
   myprefix=$ac_default_prefix
fi

AC_MSG_CHECKING([for name of GridShib-CA directories])
default=gridshib-ca-${PACKAGE_VERSION}
AC_ARG_WITH(gridshib-ca-dir-name,
	AC_HELP_STRING([--with-gridshib-ca-dir-name=<name>],
		[Specifiy the name to use for install directories. (Default: gridshib-ca-<version>)]),
	[GRIDSHIB_CA_DIR_NAME=$withval],
	[GRIDSHIB_CA_DIR_NAME=$default])
AC_SUBST(GRIDSHIB_CA_DIR_NAME)
AC_MSG_RESULT(${GRIDSHIB_CA_DIR_NAME})

AC_MSG_CHECKING([for hostname to use in GridShib CA URLS])
AC_ARG_WITH(www-hostname,
	AC_HELP_STRING([--with-www-hostname],
		[Specify the hostname used in the GridShib CA URL. (Default: local hostname)]),
	[WWW_HOSTNAME=$withval],
	[WWW_HOSTNAME=$HOSTNAME])
AC_MSG_RESULT(${WWW_HOSTNAME})
AC_SUBST(WWW_HOSTNAME)

AC_MSG_CHECKING([for base URL])
BASE_URL=https://${WWW_HOSTNAME}/
AC_MSG_RESULT(${BASE_URL})
AC_SUBST(BASE_URL)

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

default=/var/run/gridshib-ca
AC_MSG_CHECKING([for path to GridShib CA run-time directory])
AC_ARG_WITH(gridshib-ca-runtime-path,
	AC_HELP_STRING([--with-gridshib-ca-runtime-path],
		[Specify the full path to the gridshib-ca runtime directory. This is where session files are stored. (Default: /var/run/gridshib-ca)]),
	[GRIDSHIB_CA_RUNTIME_PATH=$withval],
	[GRIDSHIB_CA_RUNTIME_PATH=$default])
AC_SUBST(GRIDSHIB_CA_RUNTIME_PATH)
AC_MSG_RESULT(${GRIDSHIB_CA_RUNTIME_PATH})

AC_MSG_CHECKING([for path to Apache doc root])
default=/var/www/html/
AC_ARG_WITH(apache-docroot,
	AC_HELP_STRING([--with-apache-docroot=<path>],
		[Specify the Apache docroot. (Default: /var/www/html/)]),
	[APACHE_DOCROOT=$withval],
	[APACHE_DOCROOT=$default])
AC_CLEAN_PATH(APACHE_DOCROOT)
AC_SUBST(APACHE_DOCROOT)
AC_MSG_RESULT(${APACHE_DOCROOT})

dnl GridShib-CA web space URL and path

AC_MSG_CHECKING([for base URL to GridSHIB-CA web space])
default=/${GRIDSHIB_CA_DIR_NAME}/
AC_ARG_WITH(gridshib-ca-url,
	AC_HELP_STRING([--with-gridshib-ca-url=<url>],
		[Specify the URL for the gridshib-ca. This is the relative URL, without hostname, where the main GridShib-CA starting page will be found. (Default: /<gridshib-ca-dir-name>/)]),
	[GRIDSHIB_CA_URL=$withval],
	[GRIDSHIB_CA_URL=$default])
AC_SUBST(GRIDSHIB_CA_URL)
AC_MSG_RESULT(${GRIDSHIB_CA_URL})

AC_MSG_CHECKING([for path to GridShib-CA web space])
default=${APACHE_DOCROOT}/${GRIDSHIB_CA_URL}
AC_ARG_WITH(gridshib-ca-web-path,
	AC_HELP_STRING([--with-gridshib-ca-web-path=<path>],
		[Specify the path for the GridShib-CA web space. This is the path that corresponds the the GridShib-CA base URL. (Default: <www-dir>/<gridshib-ca-url>)]),
	[GRIDSHIB_CA_WEB_PATH=$withval],
	[GRIDSHIB_CA_WEB_PATH=$default])
AC_CLEAN_PATH(GRIDSHIB_CA_WEB_PATH)
AC_SUBST(GRIDSHIB_CA_WEB_PATH)
AC_MSG_RESULT(${GRIDSHIB_CA_WEB_PATH})

dnl Base URL for GridShibCA web space (include protocol and host)
GRIDSHIB_CA_BASE_URL=${BASE_URL}/${GRIDSHIB_CA_URL}
AC_SUBST(GRIDSHIB_CA_BASE_URL)

dnl Full URL for GridShibCA.cgi web application
GRIDSHIB_CA_WEBAPP_URL=${GRIDSHIB_CA_BASE_URL}/GridShibCA.cgi
AC_SUBST(GRIDSHIB_CA_WEBAPP_URL)

dnl Shibboleth-protected web space URL and path

AC_MSG_CHECKING([for base URL to Shibboleth-protected web space])
default=/shib-protected/${GRIDSHIB_CA_DIR_NAME}
AC_ARG_WITH(shib-protected-url,
	AC_HELP_STRING([--with-shib-protected-url=<url>],
		[Specify the base url for the Shibboleth-protected web space. This is the relative URL, without hostname, where Shibboleth-protected webapps are installed. (Default: /shib-protected/<gridshib-ca-dir-name>)]),
	[SHIB_PROTECTED_URL=$withval],
	[SHIB_PROTECTED_URL=$default])
AC_SUBST(SHIB_PROTECTED_URL)
AC_MSG_RESULT(${SHIB_PROTECTED_URL})

AC_MSG_CHECKING([for path to Shibboleth-protected web space])
default=${APACHE_DOCROOT}/${SHIB_PROTECTED_URL}
AC_ARG_WITH(shib-protected-path,
	AC_HELP_STRING([--with-shib-protected-path=<path>],
		[Specify the path for the Shibboleth-protected web space. This is the filesystem path where Shibboleth-protected webapps are installed. (Default: <apache_docroot>/<shib_protected_url>)]),
	[SHIB_PROTECTED_PATH=$withval],
	[SHIB_PROTECTED_PATH=$default])
AC_CLEAN_PATH(SHIB_PROTECTED_PATH)
AC_SUBST(SHIB_PROTECTED_PATH)
AC_MSG_RESULT(${SHIB_PROTECTED_PATH})

dnl ----------------------------------------------------------------------
dnl Miscellaneous Install parameters

AC_MSG_CHECKING([for Apache user])
AC_ARG_WITH(www-user,
	AC_HELP_STRING([--with-www-user],
		[Specify the username that the httpd runs under. (Default: apache)]),
	[WWW_USER=$withval],
	[WWW_USER=apache])
AC_MSG_RESULT(${WWW_USER})
AC_SUBST(WWW_USER)

AC_MSG_CHECKING([for Apache group])
AC_ARG_WITH(www-group,
	AC_HELP_STRING([--with-www-group],
		[Specify the group that the httpd runs under. (Default: apache)]),
	[WWW_GROUP=$withval],
	[WWW_GROUP=apache])
AC_MSG_RESULT(${WWW_GROUP})
AC_SUBST(WWW_GROUP)

AC_MSG_CHECKING([for ROOT user])
AC_ARG_WITH(root-user,
	AC_HELP_STRING([--with-root-user],
		[Specify the username for ROOT. (Default: root)]),
	[ROOT_USER=$withval],
	[ROOT_USER=root])
AC_MSG_RESULT(${ROOT_USER})
AC_SUBST(ROOT_USER)

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

dnl Syslog ID to use (can be changed later in gridshib-ca.conf)
AC_MSG_CHECKING([for syslog Id to use])
AC_ARG_WITH(syslog-id,
	AC_HELP_STRING([--with-syslog-id=<string>],
		[Specify the syslog identifier to use (Default: GridShib-CA)]),
		[SYSLOG_ID=$withval],
		[SYSLOG_ID="GridShib-CA"])
AC_MSG_RESULT(${SYSLOG_ID})
AC_SUBST(SYSLOG_ID)

dnl Syslog Facility to use (can be changed later in gridshib-ca.conf)
AC_MSG_CHECKING([for syslog facility to use])
AC_ARG_WITH(syslog-facility,
	AC_HELP_STRING([--with-syslog-facility=<string>],
		[Specify the syslog facility to use (Default: local0)]),
		[SYSLOG_FACILITY=$withval],
		[SYSLOG_FACILITY="local0"])
AC_MSG_RESULT(${SYSLOG_FACILITY})
AC_SUBST(SYSLOG_FACILITY)

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
dnl Install paths

INSTALL_CERTS_DIR=${GRIDSHIB_CA_CONF_DIR}/certs
AC_SUBST(INSTALL_CERTS_DIR)

INSTALL_TEMPLATES_DIR=${GRIDSHIB_CA_CONF_DIR}/templates
AC_SUBST(INSTALL_TEMPLATES_DIR)

INSTALL_CONF_DIR=${GRIDSHIB_CA_CONF_DIR}/conf
AC_SUBST(INSTALL_CONF_DIR)

dnl Installed path of the configuration file
GRIDSHIB_CA_CONFIG_FILE=${INSTALL_CONF_DIR}/gridshib-ca.conf
AC_CLEAN_PATH(GRIDSHIB_CA_CONFIG_FILE)
AC_SUBST(GRIDSHIB_CA_CONFIG_FILE)

INSTALL_MYPROXY_DIR=${GRIDSHIB_CA_CONF_DIR}/myproxy
AC_SUBST(INSTALL_MYPROXY_DIR)

INSTALL_OPENSSL_CA_DIR=${GRIDSHIB_CA_CONF_DIR}/openssl-ca
AC_SUBST(INSTALL_OPENSSL_CA_DIR)

INSTALL_OPENSSL_CA_RUNTIME_DIR=${GRIDSHIB_CA_CONF_DIR}/openssl-ca-runtime
AC_SUBST(INSTALL_OPENSSL_CA_RUNTIME_DIR)

INSTALL_BIN_DIR=${GRIDSHIB_CA_CONF_DIR}/bin
AC_SUBST(INSTALL_BIN_DIR)

INSTALL_PERL_DIR=${GRIDSHIB_CA_CONF_DIR}/perl
AC_SUBST(INSTALL_PERL_DIR)

INSTALL_OPENID_DIR=${GRIDSHIB_CA_CONF_DIR}/openid
AC_SUBST(INSTALL_OPENID_DIR)

dnl ------------------------------------------------------------
dnl Development mode

dnl Development utilities
DEV_UTILS=""
AC_SUBST(DEV_UTILS)

dnl Configure dependancies
CONFIGURE_DEP=""
AC_SUBST(CONFIGURE_DEP)

MAKEFILE_DEV_INCLUDE=""
AC_SUBST(MAKEFILE_DEV_INCLUDE)

if test ${MODE} = "build" ; then

   dnl Include Makefile include file in Makefile
   AC_CONFIG_FILES([dev-utils/Makefile-inc])
   MAKEFILE_DEV_INCLUDE="include dev-utils/Makefile-inc"

   dnl ----------------------------------------
   dnl Java

   dnl Files for jar generated by configure
   JAR_GENERATED_FILES="\
	java/GridShibCAClient/gsca.properties \
	java/GridShibCAClient/resources/properties \
	"
   AC_CONFIG_FILES([${JAR_GENERATED_FILES}])

   dnl Extra dependencies for building jar
   JAR_DEPS="\
	${JAR_GENERATED_FILES} \
	java/GridShibCAClient/resources/trustStore \
	java/GridShibCAClient/src/edu/ncsa/gridshib/gridshibca/*.java \
	"
   AC_SUBST(JAR_DEPS)

   dnl Programs needed for building java
   AC_PATH_PROG(KEYTOOL, keytool)
   AC_PATH_PROG(TAR, tar)
   AC_PATH_PROG(FIND, find)
   AC_PATH_PROG(JAVA, java)
   AC_PATH_PROG(JAR, jar)
   AC_PATH_PROG(JARSIGNER, jarsigner)
   AC_PATH_PROG(GREP, grep)
   AC_PATH_PROG(MKTEMP, mktemp)
   AC_PATH_PROG(TAIL, tail)
   AC_PATH_PROG(MV, mv)
   AC_PATH_PROG(ANT, ant)
   AC_PATH_PROG(CVS, cvs)
   AC_PATH_PROG(MAKE, make)
   AC_PATH_PROG(GPG, gpg)

   dnl findbugs test
   FINDBUGS_HOME="/usr/local/findbugs"
   AC_SUBST(FINDBUGS_HOME)

   AC_CONFIG_FILES([java/GridShibCAClient/run-findbugs.sh],
		[chmod +x java/GridShibCAClient/run-findbugs.sh])

   dnl ----------------------------------------
   dnl PERL
   AC_MSG_RESULT([Checking for PERL modules required for development...])
   PERL_DEV_MODULES="\
	Test::Unit \
	"
   AC_PROG_PERL_MODULES([${PERL_DEV_MODULES}])
		
   dnl ----------------------------------------
   dnl Development

   dnl Generate development utilities and tests
   DEV_UTILS="$DEV_UTILS \
   	dev-utils/build-test.sh \
	dev-utils/clean-pod2html.sh \
	"
   dnl Each AC_CONFIG_FILES() must be seperate for chmod to work.
   AC_CONFIG_FILES([dev-utils/build-test.sh],
		[chmod +x dev-utils/build-test.sh])
   AC_CONFIG_FILES([dev-utils/clean-pod2html.sh],
		[chmod +x dev-utils/clean-pod2html.sh])

   dnl Enable regeneration of configure from configure.ac and aclocal.m4
   CONFIGURE_DEP="configure.ac aclocal.m4"

   dnl ----------------------------------------
   dnl Build and Test

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

   dnl ----------------------------------------
   dnl Publishing

   dnl TODO: This assumes sourceforge username == local username. Add
   dnl       configure option to allow specification.
   PUBLISH_USER=`whoami`,cilogon
   PUBLISH_TARGET=${PUBLISH_USER}@web.sourceforge.net
   AC_SUBST(PUBLISH_TARGET)

   dnl ----------------------------------------
   dnl Jarsigner options

   AC_MSG_CHECKING([for path to jarsigner password file])
   default=${HOME}/.keystore-password
   AC_ARG_WITH(jarsigner-password-file,
	AC_HELP_STRING([--with-jarsigner-password-file=<path>],
		[Specify path to file with jarsigner password. (Default: ~/.keystore-password)]),
		[JARSIGNER_PASSWORD_FILE=$withval],
		[JARSIGNER_PASSWORD_FILE=$default])
   AC_MSG_RESULT(${JARSIGNER_PASSWORD_FILE})
   AC_SUBST(JARSIGNER_PASSWORD_FILE)

   JARSIGNER_ALIAS="default"
   AC_SUBST(JARSIGNER_ALIAS)

   JARSIGNER_STORETYPE="pkcs12"
   AC_SUBST(JARSIGNER_STORETYPE)

   AC_MSG_CHECKING([for path to jarsigner keystore])
   AC_ARG_WITH(jarsigner-keystore,
	AC_HELP_STRING([--with-jarsigner-keystore=<path>],
		[Provide path to keystore to use for signing jar. (Default: ~/.keystore)]),
	[JARSIGNER_KEYSTORE=$withval],
	[JARSIGNER_KEYSTORE=${HOME}/.keystore])
  AC_MSG_RESULT(${JARSIGNER_KEYSTORE})	
  AC_SUBST(JARSIGNER_KEYSTORE)
fi
dnl End development mode.


dnl ------------------------------------------------------------
dnl PERL arguments
dnl I would like to put quotes around ${GRIDSHIB_CA_CONF_DIR} here but
dnl they get carried over into @INC and screw up Perl.
PERL_ARGS="-I ${INSTALL_PERL_DIR}"
AC_SUBST(PERL_ARGS)

dnl PERL arguments for CGI-BIN scripts
PERL_CGI_BIN_ARGS="-T -w ${PERL_ARGS}"
AC_SUBST(PERL_CGI_BIN_ARGS)

dnl ------------------------------------------------------------
dnl URLs for common targets

SHIB_PROTECTED_FULL_URL=${SHIB_PROTECTED_PROTOCOL}://${WWW_HOSTNAME}${SHIB_PROTECTED_URL}
AC_SUBST(SHIB_PROTECTED_FULL_URL)

SHIB_LOGON_URL=${SHIB_PROTECTED_FULL_URL}/shib.cgi
AC_SUBST(SHIB_LOGON_URL)

dnl ------------------------------------------------------------
dnl Makefiles
AC_CONFIG_FILES([Makefile])

for subdir in ${SUBDIRS} ; do
    AC_CONFIG_FILES([${subdir}/Makefile])
done

dnl ------------------------------------------------------------
dnl And wrap it up
AC_OUTPUT()
