# generated automatically by aclocal 1.9.6 -*- Autoconf -*-

# Copyright (C) 1996, 1997, 1998, 1999, 2000, 2001, 2002, 2003, 2004,
# 2005  Free Software Foundation, Inc.
# This file is free software; the Free Software Foundation
# gives unlimited permission to copy and/or distribute it,
# with or without modifications, as long as this notice is preserved.

# This program is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY, to the extent permitted by law; without
# even the implied warranty of MERCHANTABILITY or FITNESS FOR A
# PARTICULAR PURPOSE.

dnl Checks for a version of OpenSSL. Uses openssl/opensslv.h
dnl Usage: CHECK_OPENSSL_VERSION(version-hexstring,
dnl				Action if =>,
dnl				Action if <)
dnl version-hexstring is the OpenSSL version as a hex value, e.g.
dnl 0x0090607fL is 0.9.6g (07==g).
dnl
dnl Derived from:
dnl   http://www.opensync.org/browser/plugins/sunbird/macros/neon.m4?rev=971
dnl
dnl Copyright (C) 1998-2004 Joe Orton <joe@manyfish.co.uk>
dnl
dnl This file is free software; you may copy and/or distribute it with
dnl or without modifications, as long as this notice is preserved.
dnl This software is distributed in the hope that it will be useful, but
dnl WITHOUT ANY WARRANTY, to the extent permitted by law; without even
dnl the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR
dnl PURPOSE.

dnl Changed to use AC_RUN_IFELSE instead of AC_EGREP_CPP so that it honors
dnl CFLAGS.

AC_DEFUN([AC_CHECK_OPENSSL_VERSION], [
AC_RUN_IFELSE([AC_LANG_SOURCE([
#include <openssl/opensslv.h>
int
main()
{
int status = 1;
#if OPENSSL_VERSION_NUMBER >= $1
status = 0;
#endif
return(status);
}
])], [$2], [$3]
)])
dnl set GLOBUS_LOCATION to the proper value for $GLOBUS_LOCATION
dnl Checks (in order):
dnl    --with-globus-location=<path>
dnl    The environment variable $GLOBUS_LOCATION
dnl
AC_DEFUN([AC_GLOBUS_LOCATION],[dnl
   AC_MSG_CHECKING(for GLOBUS_LOCATION)
   AC_ARG_WITH(globus-location,
	AC_HELP_STRING([--with-globus-location=<path>],
		[Specify the Globus Location to use. (Default: $GLOBUS_LOCATION)]),
	[GLOBUS_LOCATION=$withval])

  if test "x$GLOBUS_LOCATION" = "x" ; then
     AC_MSG_WARN(could not find GLOBUS_LOCATION)
  else
     AC_MSG_RESULT($GLOBUS_LOCATION)
     AC_SUBST(GLOBUS_LOCATION)
  fi])dnl

     
     
dnl -*- mode: autoconf -*-
dnl Get fully qualified hostname
dnl Usage: AC_HOSTNAME([variable_name],[if-failure])
AC_DEFUN([AC_HOSTNAME],[dnl
  tempHOSTNAME=""
  AC_PATH_PROG(PROG_HOSTNAME,hostname)
  if test -n "$PROG_HOSTNAME"; then
    $PROG_HOSTNAME -f > /dev/null 2>&1
    if test $? -eq 0; then
      tempHOSTNAME=`$PROG_HOSTNAME -f`
    fi
  fi
  if test -z "$tempHOSTNAME"; then
     AC_PATH_PROG(PROG_UNAME,uname)
     if test -n "$PROG_UNAME"; then
	tempHOSTNAME=`$PROG_UNAME -n`
     fi
  fi
  if test -z "$tempHOSTNAME"; then
     :
     $2
  else
     echo "$tempHOSTNAME" | grep "\." > /dev/null 2>&1
     if test $? -eq 0 ; then
       # Success
       $1=$tempHOSTNAME
     else
       :
       $2
     fi
  fi
])dnl

AC_DEFUN([AC_PROG_PERL_MODULES],[dnl
  ac_perl_modules="$1"
  # Make sure we have perl
  if test -z "$PERL"; then
     AC_PATH_PROG(PERL,perl)
  fi

  if test "x$PERL" != x; then
    ac_perl_modules_failed=""
    for ac_perl_module in $ac_perl_modules; do
    	AC_MSG_CHECKING(for perl module $ac_perl_module)
 	
 	# Would be nice to log result here, but can't rely on autoconf internals
 	$PERL "-M$ac_perl_module" -e exit > /dev/null 2>&1
 	if test $? -ne 0; then
 	  AC_MSG_RESULT(no);
 	  ac_perl_modules_failed="${ac_perl_modules_failed} ${ac_perl_module}"
 	else
 	  AC_MSG_RESULT(ok);
 	fi
    done
 	
    # Run optional shell commands
    if test -z "$ac_perl_modules_failed"; then
        if test -n "$2" ; then
	   :
       	   $2
	else
	   AC_MSG_RESULT([All Perl modules found.]) 
	fi
    else
	if test -n "$3" ; then
	   :
	   $3
	else
	   AC_MSG_ERROR([Perl modules missing. Using following command to install: cpan${ac_perl_modules_failed}])
	fi
    fi
  else
    AC_MSG_WARN(could not find perl)
  fi])dnl

