dnl $Id$
dnl See Copyright statement below.
dnl See file 'LICENSE' for license.

AC_INIT(gridshib-ca, 0.1.5-test)

COPYRIGHT="Copyright 2006 The Board of Trustees of the University of Illinois."


AC_REVISION($Id$)

AC_SUBST(COPYRIGHT)
AC_COPYRIGHT(${COPYRIGHT})

LICENSE="LICENSE"
AC_SUBST_FILE(LICENSE)

AC_PROG_INSTALL
AC_PATH_PROG(PERL, perl)

# Extra places to look for MyProxy client binaries
MYPROXY_PATH="/usr/local/myproxy/bin"
if test -n "$GLOBUS_LOCATION" ; then
   MYPROXY_PATH=${MYPROXY_PATH}:${GLOBUS_LOCATION}"/bin"
fi

AC_ARG_WITH(myproxy,
	AC_HELP_STRING([--with-myproxy],
		[Specify myproxy binary directory]),
	MYPROXY_PATH=${withval})

AC_PATH_PROG(MYPROXY_LOGON,
	 myproxy-logon,
	 [FAILED],
	 [$MYPROXY_PATH:$PATH])

if test ${MYPROXY_LOGON} = "FAILED" ; then
   AC_MSG_ERROR([Could not find myproxy-logon binary in path.])
fi

AC_ARG_ENABLE(build,
	AC_HELP_STRING([--enable-build],
		[Enable build environment]),
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

AC_ARG_WITH(gridshib-ca-dir-name,
	AC_HELP_STRING([--with-grishib-ca-dir-name],
		[Specifiy the name to use instead of "gridshib-ca-<version>" for install directories]),
	[GRIDSHIB_CA_DIR_NAME=$withval],
	[GRIDSHIB_CA_DIR_NAME=gridshib-ca-${PACKAGE_VERSION}])
AC_SUBST(GRIDSHIB_CA_DIR_NAME)

AC_MSG_CHECKING([for path to GridShib CA configuration directory])
AC_ARG_WITH(gridshib-ca-conf-dir,
	AC_HELP_STRING([--with-grishib-ca-conf-dir],
		[Specify the gridshib-ca configuration directory]),
	[GRIDSHIB_CA_CONF_DIR=$withval],
	[GRIDSHIB_CA_CONF_DIR=/usr/local/${GRIDSHIB_CA_DIR_NAME}])
AC_SUBST(GRIDSHIB_CA_CONF_DIR)
AC_MSG_RESULT(${GRIDSHIB_CA_CONF_DIR})

AC_ARG_WITH(www-dir,
	AC_HELP_STRING([--with-www-dir],
		[Specify the base www directory]),
	[WWW_DIR=$withval],
	[WWW_DIR=/var/www/])
AC_SUBST(WWW_DIR)

AC_MSG_CHECKING([for URL for Shibboleth-protected CGI-BIN directory])
AC_ARG_WITH(shib-protected-cgi-bin-url,
	AC_HELP_STRING([--with-shib-protected-cgi-bin-url],
		[Specify the Shibboleth-protected cgi-bin url]),
	[SHIB_PROTECTED_CGI_BIN_URL=$withval],
	[SHIB_PROTECTED_CGI_BIN_URL=/cgi-bin/shib-protected/${GRIDSHIB_CA_DIR_NAME}])
AC_SUBST(SHIB_PROTECTED_CGI_BIN_URL)
AC_MSG_RESULT(${SHIB_PROTECTED_CGI_BIN_URL})

AC_MSG_CHECKING([for path to Shibboleth-protected CGI-BIN directory])
AC_ARG_WITH(shib-protected-cgi-bin-dir,
	AC_HELP_STRING([--with-shib-protected-cgi-bin-dir],
		[Specify the Shibboleth-protected cgi-bin directory]),
	[SHIB_PROTECTED_CGI_BIN_DIR=$withval],
	[SHIB_PROTECTED_CGI_BIN_DIR=${WWW_DIR}/${SHIB_PROTECTED_CGI_BIN_URL}])
AC_SUBST(SHIB_PROTECTED_CGI_BIN_DIR)
AC_MSG_RESULT(${SHIB_PROTECTED_CGI_BIN_DIR})

AC_MSG_CHECKING([for URL to GridSHIB-CA])
AC_ARG_WITH(gridshib-ca-url,
	AC_HELP_STRING([--with-gridshib-ca-url],
		[Specify the URL for the gridshib-ca]),
	[GRIDSHIB_CA_URL=$withval],
	[GRIDSHIB_CA_URL=/${GRIDSHIB_CA_DIR_NAME}/])
AC_SUBST(GRIDSHIB_CA_URL)
AC_MSG_RESULT(${GRIDSHIB_CA_URL})

AC_MSG_CHECKING([for path to GridShib CA HTML directory])
AC_ARG_WITH(gridshib-ca-html-dir,
	AC_HELP_STRING([--with-gridshib-ca-html_dir],
		[Specify the direcotry for the gridshib-ca HTML]),
	[GRIDSHIB_CA_HTML_DIR=$withval],
	[GRIDSHIB_CA_HTML_DIR=${WWW_DIR}/html/${GRIDSHIB_CA_URL}])
AC_SUBST(GRIDSHIB_CA_HTML_DIR)
AC_MSG_RESULT(${GRIDSHIB_CA_HTML_DIR})

AC_MSG_CHECKING([for hostname to use in GridShib CA URLS])
AC_ARG_WITH(www-hostname,
	AC_HELP_STRING([--with-www-hostname],
		[Specify the hostname used in the GridShib CA URL]),
	[WWW_HOSTNAME=$withval],
	[WWW_HOSTNAME=`uname -n`])
AC_MSG_RESULT(${WWW_HOSTNAME})
AC_SUBST(WWW_HOSTNAME)

AC_MSG_CHECKING([for owner of www files])
AC_ARG_WITH(www-user,
	AC_HELP_STRING([--with-www-user],
		[Specify the username that the httpd runs under]),
	[WWW_USER=$withval],
	[WWW_USER=apache])
AC_MSG_RESULT(${WWW_USER})
AC_SUBST(WWW_USER)

AC_MSG_CHECKING([for relative DN of generated certificates])
AC_ARG_WITH(relative-dn,
	AC_HELP_STRING([--with-relative-dn],
		[Provide the relative DN for generated certificates]),
	[RELATIVE_DN=$withval],
	[RELATIVE_DN="/C=US/O=TEST ORG/OU=TEST OU"])
AC_SUBST(RELATIVE_DN)
AC_MSG_RESULT(${RELATIVE_DN})

# We generate header.html and footer.html and then include those in 
# other html files we generate. So they must be listed first before
# other html files.
HTML_HEADER=$srcdir/webdocs/header.html
AC_SUBST_FILE(HTML_HEADER)
HTML_FOOTER=$srcdir/webdocs/footer.html
AC_SUBST_FILE(HTML_FOOTER)
HTML_INCS="${HTML_HEADER} ${HTML_FOOTER}"
AC_SUBST(HTML_INCS)
AC_CONFIG_FILES([${HTML_INCS}])

CGI_BIN_SCRIPTS="webdocs/generateCred.cgi \
	webdocs/login \
	webdocs/sysinfo.pl \
	webdocs/welcome.pl"
AC_SUBST(CGI_BIN_SCRIPTS)
AC_CONFIG_FILES([$CGI_BIN_SCRIPTS])

HTML_FILES="webdocs/index.html \
	webdocs/login.html \
	webdocs/faq.html \
	webdocs/checkPrerequisites.html \
	webdocs/GridShibCATester.jnlp"
AC_SUBST(HTML_FILES)
AC_CONFIG_FILES([$HTML_FILES])

CONF_FILES="webdocs/CredentialRetriever.jnlp"
AC_SUBST(CONF_FILES)
AC_CONFIG_FILES([$CONF_FILES])

UTILS="utils/mapper.sh"
AC_SUBST(UTILS)
AC_CONFIG_FILES([$UTILS])

dnl syslog parameters
SYSLOG_ID="GridShib-CA"
AC_SUBST(SYSLOG_ID)
SYSLOG_FACILITY="local0"
AC_SUBST(SYSLOG_FACILITY)

DOC_FILES="doc/license.html"
AC_SUBST(DOC_FILES)
AC_CONFIG_FILES([$DOC_FILES])

dnl Java files for jar
JAVA_SRCS="`echo edu/ncsa/gridshib/gridshibca/*.java`"
AC_SUBST(JAVA_SRCS)

JAR_FILE="GridShibCA.jar"
AC_SUBST(JAR_FILE)

MISC_FILES=""

if test ${MODE} = "build" ; then
   dnl Make utils/tag-version.pl executable so I can call it
   MISC_FILES="${MISC_FILES} utils/tag-version.pl"
   AC_CONFIG_FILES([utils/tag-version.pl])
   AC_CONFIG_COMMANDS([chmod-utils-tag-version],
		[chmod +x utils/tag-version.pl])
		
fi

dnl Everything generated by configure
CONF_GENERATED_SCRIPTS="${CGI_BIN_SCRIPTS} \
	${HTML_FILES} \
	${CONF_FILES} \
	${UTILS} \
	${DOC_FILES} \
	${MISC_FILES}"
AC_SUBST(CONF_GENERATED_SCRIPTS)

AC_CONFIG_FILES([Makefile])

AC_OUTPUT()
