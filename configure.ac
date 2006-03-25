dnl $Id$
dnl See Copyright statement below.
dnl See file 'LICENSE' for license.

AC_INIT(gridshib-ca, 0.1.3)

COPYRIGHT="Copyright 2006 The Board of Trustees of the University of Illinois."


AC_REVISION($Id$)

AC_SUBST(COPYRIGHT)
AC_COPYRIGHT(${COPYRIGHT})

LICENSE="LICENSE"
AC_SUBST_FILE(LICENSE)

AC_PROG_INSTALL
AC_PATH_PROG(PERL, perl)
AC_PATH_PROG(CAT, cat)

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

AC_ARG_WITH(gridshib-ca-conf-dir,
	AC_HELP_STRING([--with-grishib-ca-conf-dir],
		[Specify the gridshib-ca configuration directory]),
	[GRIDSHIB_CA_CONF_DIR=$withval],
	[GRIDSHIB_CA_CONF_DIR=/usr/local/${GRIDSHIB_CA_DIR_NAME}])
AC_SUBST(GRIDSHIB_CA_CONF_DIR)

AC_ARG_WITH(www-dir,
	AC_HELP_STRING([--with-www-dir],
		[Specify the base www directory]),
	[WWW_DIR=$withval],
	[WWW_DIR=/var/www/])
AC_SUBST(WWW_DIR)

AC_ARG_WITH(shib-protected-cgi-bin-url,
	AC_HELP_STRING([--with-shib-protected-cgi-bin-url],
		[Specify the Shibboleth-protected cgi-bin url]),
	[SHIB_PROTECTED_CGI_BIN_URL=$withval],
	[SHIB_PROTECTED_CGI_BIN_URL=/cgi-bin/shib-protected/${GRIDSHIB_CA_DIR_NAME}])
AC_SUBST(SHIB_PROTECTED_CGI_BIN_URL)

AC_ARG_WITH(shib-protected-cgi-bin-dir,
	AC_HELP_STRING([--with-shib-protected-cgi-bin-dir],
		[Specify the Shibboleth-protected cgi-bin directory]),
	[SHIB_PROTECTED_CGI_BIN_DIR=$withval],
	[SHIB_PROTECTED_CGI_BIN_DIR=${WWW_DIR}/${SHIB_PROTECTED_CGI_BIN_URL}])
AC_SUBST(SHIB_PROTECTED_CGI_BIN_DIR)

AC_ARG_WITH(gridshib-ca-url,
	AC_HELP_STRING([--with-gridshib-ca-url],
		[Specify the URL for the gridshib-ca]),
	[GRIDSHIB_CA_URL=$withval],
	[GRIDSHIB_CA_URL=/${GRIDSHIB_CA_DIR_NAME}/])
AC_SUBST(GRIDSHIB_CA_URL)

AC_ARG_WITH(gridshib-ca-html-dir,
	AC_HELP_STRING([--with-gridshib-ca-html_dir],
		[Specify the direcotry for the gridshib-ca HTML]),
	[GRIDSHIB_HTML_DIR=$withval],
	[GRIDSHIB_CA_HTML_DIR=${WWW_DIR}/html/${GRIDSHIB_CA_URL}])
AC_SUBST(GRIDSHIB_CA_HTML_DIR)

AC_ARG_WITH(www-user,
	AC_HELP_STRING([--with-www-user],
		[Specify the username that the httpd runs under]),
	[WWW_USER=$withval],
	[WWW_USER=apache])
AC_SUBST(WWW_USER)

AC_ARG_WITH(relative-dn,
	AC_HELP_STRING([--with-relative-dn],
		[Provide the relative DN for generated certificates]),
	[RELATIVE_DN=$withval],
	[RELATIVE_DN="/C=US/O=TEST ORG/OU=TEST OU"])
AC_SUBST(RELATIVE_DN)

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

CONF_GENERATED_SCRIPTS="webdocs/generateCred.cgi \
	webdocs/login \
	webdocs/index.html \
	webdocs/login.html \
	webdocs/sysinfo.pl \
	webdocs/welcome.pl \
	webdocs/faq.html \
	webdocs/CredentialRetriever.jnlp \
	doc/license.html \
	utils/mapper.sh"
AC_SUBST(CONF_GENERATED_SCRIPTS)
AC_CONFIG_FILES([${CONF_GENERATED_SCRIPTS}])

AC_CONFIG_FILES([Makefile])


AC_OUTPUT()
