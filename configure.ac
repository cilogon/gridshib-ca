dnl $Id$
dnl See Copyright statement below.
dnl See file 'LICENSE' for license.

AC_INIT(gridshib-ca, 0.2.1-test)

COPYRIGHT="Copyright 2006 The Board of Trustees of the University of Illinois."


AC_REVISION($Id$)

AC_SUBST(COPYRIGHT)
AC_COPYRIGHT(${COPYRIGHT})

LICENSE="LICENSE"
AC_SUBST_FILE(LICENSE)

SUBDIRS=""
BINARIES=""

PACKAGE_VERSION_MOD=`echo ${PACKAGE_VERSION} | sed -e "s/\./_/g"`
DIST_FILE_BASE=${PACKAGE_NAME}-${PACKAGE_VERSION_MOD}
AC_SUBST(DIST_FILE_BASE)

DIST_DIR=${PACKAGE_NAME}-${PACKAGE_VERSION}
AC_SUBST(DIST_DIR)

AC_PROG_INSTALL
AC_PATH_PROG(PERL, perl)

CA_TYPE="openssl"
AC_MSG_CHECKING([for CA type])
AC_ARG_WITH(ca-type,
	AC_HELP_STRING([--ca-type],
		[Specify CA Type. Options are: openssl, myproxy (Default: openssl)]),
	[CA_TYPE=$withval])
AC_SUBST(CA_TYPE)
AC_MSG_RESULT(${CA_TYPE})

if test "$CA_TYPE" = "myproxy"; then

   # Extra places to look for MyProxy client binaries
   MYPROXY_PATH="/usr/local/myproxy/bin"
   if test -n "$GLOBUS_LOCATION" ; then
      MYPROXY_PATH=${MYPROXY_PATH}:${GLOBUS_LOCATION}"/bin"
   fi

   AC_ARG_WITH(myproxy-path,
	AC_HELP_STRING([--with-myproxy-path],
		[Specify path to myproxy binary (Default: None)]),
	[MYPROXY_PATH=${withval}])

   AC_PATH_PROG(MYPROXY_LOGON,
	 myproxy-logon,
	 [FAILED],
	 [$MYPROXY_PATH:$PATH])

   if test ${MYPROXY_LOGON} = "FAILED" ; then
      AC_MSG_ERROR([Could not find myproxy-logon binary in path.])
   fi

  GRIDSHIB_CA_MODULE="GridShibCA::MyProxyCA"

# CA_TYPE = myproxy
elif test "$CA_TYPE" = "openssl"; then

   AC_PROG_CC
   AC_CHECK_LIB(crypto, CRYPTO_malloc)

   AC_ARG_WITH(openssl-path,
	AC_HELP_STRING([--with-openssl-path],
		[Specify path to openssl binary (Default: None)]),
	[OPENSSL_PATH=${withval}],
	[OPENSSL_PATH=$PATH])

  AC_PATH_PROG(OPENSSL, openssl, [FAILED], [$OPENSSL_PATH])

  if test ${OPENSSL} = "FAILED"; then
      AC_MSG_ERROR([Could not find openssl binary in path.])
  fi

  # XXX It would be nice to make this RFC 2253 format, but it gets fed
  # to openssl req -subj, which requires /C=X/O=XX/...
  AC_MSG_CHECKING([for DN of CA])
  default="/C=US/O=TEST ORG/OU=TEST OU/CN=Test CA"
  AC_ARG_WITH(ca-dn,
	AC_HELP_STRING([--with-ca-dn],
		[Provide the DN for GridShib CA. Unless you are going to provide a CA key and certificate, the install process will create one for you. This will be its DN. (Default: /C=US/O=TEST ORG/OU=TEST OU/CN=Test CA)]),
	[CA_DN=$withval],
	[CA_DN=$default])
  AC_SUBST(CA_DN)
  AC_MSG_RESULT(${CA_DN})

  GRIDSHIB_CA_MODULE="GridShibCA::OpenSSLCA"
  BINARIES="${BINARIES} openssl-ca/openssl-ca"
  SUBDIRS="${SUBDIRS} openssl-ca"

else # CA_TYPE = openssl
     AC_MSG_ERROR([Unknown CA Type specified.])
fi 

AC_SUBST(GRIDSHIB_CA_MODULE)

AC_ARG_ENABLE(build,
	AC_HELP_STRING([--enable-build],
		[Enable build environment. For developers. (Default: Disabled)]),
	[MODE=build],
	[MODE=dist])
AC_SUBST(MODE)

if test ${MODE} = "build" ; then
   AC_CONFIG_FILES([edu/ncsa/gridshib/gridshibca/Version])

   # These tools are only needed for building distributions
   AC_PATH_PROG(KEYTOOL, keytool)
   AC_PATH_PROG(JAVAC, javac)
   AC_PATH_PROG(JAR, jar)
   AC_PATH_PROG(JARSIGNER, jarsigner)
   AC_PATH_PROG(TAR, tar)

   # Enable regeneration of configure from configure.ac
   AC_SUBST(CONFIGURE_DEP, configure.ac)
else
   # No regeneration of configure
   AC_SUBST(CONFIGURE_DEP, "")
fi

default=gridshib-ca-${PACKAGE_VERSION}
AC_ARG_WITH(gridshib-ca-dir-name,
	AC_HELP_STRING([--with-grishib-ca-dir-name=<name>],
		[Specifiy the name to use for install directories. (Default: gridshib-ca-<version>)]),
	[GRIDSHIB_CA_DIR_NAME=$withval],
	[GRIDSHIB_CA_DIR_NAME=$default])
AC_SUBST(GRIDSHIB_CA_DIR_NAME)

AC_MSG_CHECKING([for path to GridShib CA configuration directory])
default=/usr/local/${GRIDSHIB_CA_DIR_NAME}
AC_ARG_WITH(gridshib-ca-conf-dir,
	AC_HELP_STRING([--with-grishib-ca-conf-dir=<path>],
		[Specify the gridshib-ca configuration directory. This is where keys, certificates and other files for the GridShib CA are stored. (Default: /usr/local/<gridshib-ca-dir-name>)]),
	[GRIDSHIB_CA_CONF_DIR=$withval],
	[GRIDSHIB_CA_CONF_DIR=$default])
AC_SUBST(GRIDSHIB_CA_CONF_DIR)
AC_MSG_RESULT(${GRIDSHIB_CA_CONF_DIR})

AC_MSG_CHECKING([for Apache httpd root directory])
default=/var/www/
AC_ARG_WITH(www-dir,
	AC_HELP_STRING([--with-www-dir=<path>],
		[Specify the base www directory. This is directory under which the Apache HTTPD has the html/ and cgi-bin/ directories. (Default: /var/www/)]),
	[WWW_DIR=$withval],
	[WWW_DIR=$default])
AC_SUBST(WWW_DIR)
AC_MSG_RESULT(${WWW_DIR})

AC_MSG_CHECKING([for URL for Shibboleth-protected CGI-BIN directory])
default=/cgi-bin/shib-protected/${GRIDSHIB_CA_DIR_NAME}
AC_ARG_WITH(shib-protected-cgi-bin-url,
	AC_HELP_STRING([--with-shib-protected-cgi-bin-url=<url>],
		[Specify the Shibboleth-protected cgi-bin url. This is the relative URL, without hostname, where Shibboleth-protected CGI-BIN executables are installed. (Default: /cgi/bin/shib-protected/<gridshib-ca-dir-name>)]),
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
AC_SUBST(SHIB_PROTECTED_CGI_BIN_DIR)
AC_MSG_RESULT(${SHIB_PROTECTED_CGI_BIN_DIR})

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
	AC_HELP_STRING([--with-gridshib-ca-html_dir],
		[Specify the direcotry for the gridshib-ca HTML corresponding to the gridshib-ca-url. Default: <www-dir>/html/<gridshib-ca-dir-name>/]),
	[GRIDSHIB_CA_HTML_DIR=$withval],
	[GRIDSHIB_CA_HTML_DIR=${WWW_DIR}/html/${GRIDSHIB_CA_DIR_NAME}/])
AC_SUBST(GRIDSHIB_CA_HTML_DIR)
AC_MSG_RESULT(${GRIDSHIB_CA_HTML_DIR})

AC_MSG_CHECKING([for path to GridShib CA documentation directory])
AC_ARG_WITH(gridshib-ca-doc-dir,
	AC_HELP_STRING([--with-gridshib-ca-doc_dir],
		[Specify the direcotry for the gridshib-ca documentation. Not used for normal installation. (Default: <www-dir>/html/gridshib-ca/]),
	[GRIDSHIB_CA_DOC_DIR=$withval],
	[GRIDSHIB_CA_DOC_DIR=${WWW_DIR}/html/gridshib-ca/])
AC_SUBST(GRIDSHIB_CA_DOC_DIR)
AC_MSG_RESULT(${GRIDSHIB_CA_DOC_DIR})

AC_MSG_CHECKING([for hostname to use in GridShib CA URLS])
AC_ARG_WITH(www-hostname,
	AC_HELP_STRING([--with-www-hostname],
		[Specify the hostname used in the GridShib CA URL. (Default: <uname -n>)]),
	[WWW_HOSTNAME=$withval],
	[WWW_HOSTNAME=`uname -n`])
AC_MSG_RESULT(${WWW_HOSTNAME})
AC_SUBST(WWW_HOSTNAME)

AC_MSG_CHECKING([for owner of www files])
AC_ARG_WITH(www-user,
	AC_HELP_STRING([--with-www-user],
		[Specify the username that the httpd runs under. (Default: apache)]),
	[WWW_USER=$withval],
	[WWW_USER=apache])
AC_MSG_RESULT(${WWW_USER})
AC_SUBST(WWW_USER)

AC_MSG_CHECKING([for relative DN of generated certificates])
AC_ARG_WITH(relative-dn,
	AC_HELP_STRING([--with-relative-dn],
		[Provide the relative DN for generated certificates. (Default: OU=TEST OU, O=TEST ORG, C=US)]),
	[RELATIVE_DN=$withval],
	[RELATIVE_DN="OU=TEST OU, O=TEST ORG, C=US"])
AC_SUBST(RELATIVE_DN)
AC_MSG_RESULT(${RELATIVE_DN})

AC_MSG_CHECKING([for path to GridShib CA certificate])
AC_ARG_WITH(cert-path,
	AC_HELP_STRING([--with-cert-path],
		[Path to certificate file used either to generate certificates, if openssl is used as the CA, or authenticate to myproxy, if myproxy is used as a CA. (Default: <gridshib-ca-conf-dir>/gridshib-ca-cert.pem)]),
	[GRIDSHIB_CA_CERT_PATH=$withval],
	[GRIDSHIB_CA_CERT_PATH="${GRIDSHIB_CA_CONF_DIR}/gridshib-ca-cert.pem"])
AC_SUBST(GRIDSHIB_CA_CERT_PATH)
AC_MSG_RESULT(${GRIDSHIB_CA_CERT_PATH})

AC_MSG_CHECKING([for path to GridShib CA key])
AC_ARG_WITH(key-path,
	AC_HELP_STRING([--with-key-path],
		[Path to key file used either to generate certificates, if openssl is used as the CA, or authenticate to myproxy, if myproxy is used as a CA. (Default: <gridshib-ca-conf-dir>/gridshib-ca-key.pem)]),
	[GRIDSHIB_CA_KEY_PATH=$withval],
	[GRIDSHIB_CA_KEY_PATH="${GRIDSHIB_CA_CONF_DIR}/gridshib-ca-key.pem"])
AC_SUBST(GRIDSHIB_CA_KEY_PATH)
AC_MSG_RESULT(${GRIDSHIB_CA_KEY_PATH})

# We generate webdocs/header.html and webdocs/footer.html and then
# include those in other html files we generate. So they must be
# listed first before other html files.
HTML_HEADER=$srcdir/webdocs/header.html
AC_SUBST_FILE(HTML_HEADER)
HTML_FOOTER=$srcdir/webdocs/footer.html
AC_SUBST_FILE(HTML_FOOTER)
HTML_INCS="${HTML_HEADER} ${HTML_FOOTER}"
AC_SUBST(HTML_INCS)
AC_CONFIG_FILES([${HTML_INCS}])

DOC_HEADER=$srcdir/doc/header.html
AC_SUBST_FILE(DOC_HEADER)
DOC_FOOTER=$srcdir/doc/footer.html
AC_SUBST_FILE(DOC_FOOTER)

HTML_DEPS="${DOC_HEADER} ${DOC_FOOTER}"
AC_SUBST(HTML_DEPS)

CGI_BIN_SCRIPTS="webdocs/generateCred.cgi \
	webdocs/login \
	webdocs/sysinfo.pl \
	webdocs/welcome.pl"
AC_SUBST(CGI_BIN_SCRIPTS)
AC_CONFIG_FILES([$CGI_BIN_SCRIPTS])

HTML_FILES="webdocs/index.html \
	webdocs/login.html \
	webdocs/checkPrerequisites.html \
	webdocs/GridShibCATester.jnlp"
AC_SUBST(HTML_FILES)
AC_CONFIG_FILES([$HTML_FILES])

CONF_FILES="webdocs/CredentialRetriever.jnlp"
AC_SUBST(CONF_FILES)
AC_CONFIG_FILES([$CONF_FILES])

UTILS="utils/mapper.sh \
	utils/create-openssl-ca.pl \
	utils/ca-myproxy.pl"
AC_SUBST(UTILS)
AC_CONFIG_FILES([$UTILS])

AC_SUBST(BINARIES)

dnl syslog parameters
SYSLOG_ID="GridShib-CA"
AC_SUBST(SYSLOG_ID)
SYSLOG_FACILITY="local0"
AC_SUBST(SYSLOG_FACILITY)

dnl Java files for jar
JAVA_SRCS="`echo edu/ncsa/gridshib/gridshibca/*.java`"
AC_SUBST(JAVA_SRCS)

JAR_FILE="GridShibCA.jar"
AC_SUBST(JAR_FILE)

dnl PERL modules
PERL_MODULES="\
	perl/GridShibCA/CA.pm \
	perl/GridShibCA/CGIinterface.pm \
	perl/GridShibCA/Command.pm \
	perl/GridShibCA/Logger.pm \
	perl/GridShibCA/OpenSSLCA.pm"
AC_SUBST(PERL_MODULES)
AC_CONFIG_FILES([$PERL_MODULES])

MISC_FILES=""

if test ${MODE} = "build" ; then
   MISC_FILES="${MISC_FILES} utils/tag-version.pl"
   AC_CONFIG_FILES([utils/tag-version.pl])

   # Documentation files that are generated by autoconf
   GENERATED_DOC_FILES="\
	doc/index.html \
	doc/license.html \
        doc/install.html \
	doc/changelog.html \
	doc/callouts.html \
	doc/process-flow.html"
   AC_SUBST(GENERATED_DOC_FILES)
   AC_CONFIG_FILES([$GENERATED_DOC_FILES])

   # List of all documentation files to be installed
   DOC_FILES="\
	${GENERATED_DOC_FILES} \
	doc/gridshib-ca-process-flow.jpg"
   AC_SUBST(DOC_FILES)
fi

GRIDSHIB_LOGGER_PROG=${GRIDSHIB_CA_CONF_DIR}/gridshib-logger-prog
AC_SUBST(GRIDSHIB_LOGGER_PROG)

GRIDSHIB_AUTHZ_PROG=${GRIDSHIB_CA_CONF_DIR}/gridshib-authz-prog
AC_SUBST(GRIDSHIB_AUTHZ_PROG)

# I would like to put quotes around ${GRIDSHIB_CA_CONF_DIR} here but
# they get carried over into @INC and screw up Perl.
PERL_ARGS="-T -w -I ${GRIDSHIB_CA_CONF_DIR}"
AC_SUBST(PERL_ARGS)

dnl Make utils/*.pl executable
AC_CONFIG_COMMANDS([chmod-utils], [chmod +x utils/*.pl])

dnl Everything generated by configure
CONF_GENERATED_SCRIPTS="${CGI_BIN_SCRIPTS} \
	${HTML_FILES} \
	${CONF_FILES} \
	${UTILS} \
	${GENERATED_DOC_FILES} \
	${MISC_FILES} \
	${PERL_MODULES} \
	Makefile"

for subdir in ${SUBDIRS} ; do
    CONF_GENERATED_SCRIPTS="${CONF_GENERATED_SCRIPTS} ${subdir}/Makefile"
    AC_CONFIG_FILES([${subdir}/Makefile])
done
AC_SUBST(SUBDIRS)

AC_SUBST(CONF_GENERATED_SCRIPTS)

AC_CONFIG_FILES([Makefile])

AC_OUTPUT()
