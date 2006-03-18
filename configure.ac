AC_INIT(gridshib-ca, 0.1)

AC_PROG_INSTALL
AC_PATH_PROG(OPENSSL, openssl)
AC_PATH_PROG(MYPROXY_LOGON, myproxy-logon)

# These tools are only needed for building distributions
AC_PATH_PROG(KEYTOOL, keytool)
AC_PATH_PROG(JAR, jar)
AC_PATH_PROG(JARSIGNER, jarsigner)
AC_PATH_PROG(TAR, tar)
AC_PATH_PROG(PERL, perl)

# Read default configuration from INSTALL.config
AC_MSG_NOTICE([Reading INSTALL.config])
. INSTALL.config
AC_MSG_NOTICE([INSTALL.config parsed])

AC_ARG_WITH(gridshib-ca-dir-name,
	AC_HELP_STRING([--with-grishib-ca-dir-name],
		[Specifiy the name to use instead of "gridshib-ca" for install directories]),
	[GRIDSHIB_CA_DIR_NAME=$withval])
AC_SUBST(GRIDSHIB_CA_DIR_NAME)

AC_ARG_WITH(gridshib-ca-conf-dir,
	AC_HELP_STRING([--with-grishib-ca-conf-dir],
		[Specify the gridshib-ca configuration directory]),
	[GRIDSHIB_CA_CONF_DIR=$withval])
AC_SUBST(GRIDSHIB_CA_CONF_DIR)

AC_ARG_WITH(cgi-bin-url,
	AC_HELP_STRING([--with-cgi-bin-url],
		[Specify the relative unprotected cgi-bin url]),
	[CGI_BIN_URL=$withval])
AC_SUBST(CGI_BIN_URL)

AC_ARG_WITH(cgi-bin-dir,
	AC_HELP_STRING([--with-cgi-bin-dir],
		[Specify the gridshib-ca cgi-bin directory]),
	[CGI_BIN_URL, $withval])
AC_SUBST(CGI_BIN_URL)

AC_ARG_WITH(shib-protected-cgi-bin-url,
	AC_HELP_STRING([--with-shib-protected-cgi-bin-url],
		[Specify the Shibboleth-protected cgi-bin url]),
	[SHIB_PROTECTED_CGI_BIN_URL=$withval])
AC_SUBST(SHIB_PROTECTED_CGI_BIN_URL)

AC_ARG_WITH(shib-protected-cgi-bin-dir,
	AC_HELP_STRING([--with-shib-protected-cgi-bin-dir],
		[Specify the Shibboleth-protected cgi-bin directory]),
	[SHIB_PROTECTED_CGI_BIN_DIR=$withval])
AC_SUBST(SHIB_PROTECTED_CGI_BIN_DIR)

AC_ARG_WITH(gridshib-ca-url,
	AC_HELP_STRING([--with-gridshib-ca-url],
		[Specify the URL for the gridshib-ca]),
	[GRIDSHIB_URL=$withval])
AC_SUBST(GRIDSHIB_URL)

AC_ARG_WITH(gridshib-html-dir,
	AC_HELP_STRING([--with-gridshib-html_dir],
		[Specify the direcotry for the gridshib-ca HTML]),
	[GRIDSHIB_HTML_DIR=$withval])
AC_SUBST(GRIDSHIB_HTML_DIR)

AC_CONFIG_FILES([Makefile])
AC_CONFIG_FILES([webdocs/generateCred.cgi webdocs/login webdocs/index.html webdocs/sysinfo.pl webdocs/test.pl])

AC_OUTPUT()
