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

     
     
AC_DEFUN([AC_PROG_PERL_MODULES],[dnl
  ac_perl_modules="$1"
  # Make sure we have perl
  if test -z "$PERL"; then
     AC_CHECK_PROG(PERL,perl,perl)
  fi

  if test "x$PERL" != x; then
    ac_perl_modules_failed=0
    for ac_perl_module in $ac_perl_modules; do
    	AC_MSG_CHECKING(for perl module $ac_perl_module)
 	
 	# Would be nice to log result here, but can't rely on autoconf internals
 	$PERL "-M$ac_perl_module" -e exit > /dev/null 2>&1
 	if test $? -ne 0; then
 	  AC_MSG_RESULT(no);
 	  ac_perl_modules_failed=1
 	else
 	  AC_MSG_RESULT(ok);
 	fi
    done
 	
    # Run optional shell commands
    if test "$ac_perl_modules_failed" = 0; then
        :
       	$2
    else
	:
	$3
    fi
   else
	AC_MSG_WARN(could not find perl)
 fi])dnl

