AC_INIT(gridshib-ca, 0.1)
AC_REVISION($Id$)

AC_PROG_INSTALL
AC_PATH_PROG(OPENSSL, openssl)
AC_PATH_PROG(PERL, perl)
AC_PATH_PROG(CAT, cat)

AC_ARG_WITH(myproxy,
	AC_HELP_STRING([--with-myproxy],
		[Specify myproxy binary directory]),
	PATH=${PATH}:${withval})

AC_PATH_PROG(MYPROXY_LOGON,
	 myproxy-logon,
	 [FAILED])

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
		[Specifiy the name to use instead of "gridshib-ca" for install directories]),
	[GRIDSHIB_CA_DIR_NAME=$withval],
	[GRIDSHIB_CA_DIR_NAME=gridshib-ca])
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

AC_ARG_WITH(cgi-bin-url,
	AC_HELP_STRING([--with-cgi-bin-url],
		[Specify the relative unprotected cgi-bin url]),
	[CGI_BIN_URL=$withval],
	[CGI_BIN_URL=/cgi-bin/${GRIDSHIB_CA_DIR_NAME}])
AC_SUBST(CGI_BIN_URL)

AC_ARG_WITH(cgi-bin-dir,
	AC_HELP_STRING([--with-cgi-bin-dir],
		[Specify the gridshib-ca cgi-bin directory]),
	[CGI_BIN_URL=$withval],
	[CGI_BIN_DIR=${WWW_DIR}/${CGI_BIN_URL}])
AC_SUBST(CGI_BIN_DIR)

AC_ARG_WITH(shib-protected-cgi-bin-url,
	AC_HELP_STRING([--with-shib-protected-cgi-bin-url],
		[Specify the Shibboleth-protected cgi-bin url]),
	[SHIB_PROTECTED_CGI_BIN_URL=$withval],
	[SHIB_PROTECTED_CGI_BIN_URL=${CGI_BIN_URL}-protected])
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
	[GRIDSHIB_URL=$withval],
	[GRIDSHIB_URL=/${GRIDSHIB_CA_DIR_NAME}/])
AC_SUBST(GRIDSHIB_URL)

AC_ARG_WITH(gridshib-ca-html-dir,
	AC_HELP_STRING([--with-gridshib-ca-html_dir],
		[Specify the direcotry for the gridshib-ca HTML]),
	[GRIDSHIB_HTML_DIR=$withval],
	[GRIDSHIB_CA_HTML_DIR=${WWW_DIR}/html/${GRIDSHIB_URL}])
AC_SUBST(GRIDSHIB_CA_HTML_DIR)

AC_ARG_WITH(www-user,
	AC_HELP_STRING([--www-user],
		[Specify the username that the httpd runs under]),
	[WWW_USER=$withval],
	[WWW_USER=apache])
AC_SUBST(WWW_USER)

CONF_GENERATED_SCRIPTS="webdocs/generateCred.cgi \
	webdocs/login \
	webdocs/index.html \
	webdocs/sysinfo.pl \
	webdocs/test.pl \
	webdocs/welcome.pl"
AC_SUBST(CONF_GENERATED_SCRIPTS)
AC_CONFIG_FILES([${CONF_GENERATED_SCRIPTS}])

AC_CONFIG_FILES([Makefile])


AC_OUTPUT()
