#!/usr/bin/perl

$KeyFile = "/usr/local/SP-Service/keyfile";

$OpenSSL = "/usr/bin/openssl";

$ENV{PATH} = "/bin";

$MaxTimeDiff = 300; # 5 minutes

$X509_USER_CERT = "/usr/local/SP-Service/cert.pem";
$X509_USER_KEY = "/usr/local/SP-Service/key.pem";

######################################################################

sub errorExit($)
{
    my $string = shift;

    print "ERROR: $string\n";
    print STDERR "ERROR: $string\n";
    exit(0);
}

######################################################################

sub decryptString($)
{
    use IPC::Open2;

    my $encString = shift;

    if (! -r $KeyFile )
    {
      errorExit("Key file \"$KeyFile\" not readable.");
    }

    my $pid = open2(\*FROM_OPENSSL, \*TO_OPENSSL, "$OpenSSL des3 -d -kfile $KeyFile -a");

    print TO_OPENSSL $encString . "\n";
    close(TO_OPENSSL);

    my $string = <FROM_OPENSSL>;
    close(FROM_OPENSSL);

    if (!defined($string) || ($string eq ""))
    {
        errorExit("Could not decrypt token.");
    }
    chomp($string);

    return $string;
}

sub parseToken($)
{
    use URI::Escape;

    my $encToken = shift;

    if (!defined($encToken) or ($encToken eq ""))
    {
	errorExit("No token argument provided");
    }

    $encToken = uri_unescape($encToken);

    my $token = decryptString($encToken);

    my ($time, $user) = split(/-/, $token);

    if (!defined($time) or ($time eq "") or
	!defined($user) or ($user eq ""))
    {
	errorExit("Malformed token: $token");
    }

    my $now = time();

    if ($now - $time > $MaxTimeDiff)
    {
	errorExit("Token too old: $time (currently $now)");
    }

    # XXX check for replay

    return $user;
}


######################################################################

use CGI;

$cgi = new CGI;

print $cgi->header("text/plain");

$token = $cgi->param("token");

$user = parseToken($token);

# XXX Log credential creation

######################################################################

if (! -r $X509_USER_CERT)
{
  errorExit("Could not read certificate: $X509_USER_CERT");
}
$ENV{"X509_USER_CERT"}=$X509_USER_CERT;

if (! -r $X509_USER_KEY)
{
   errorExit("Could not read private key: $X509_USER_KEY");
}
$ENV{"X509_USER_KEY"}=$X509_USER_KEY;

my $temp_cred_file=`mktemp`;
chomp($temp_cred_file);

my $temp_err_file=$temp_cred_file . ".err";

my $status = system("/usr/local/myproxy/bin/myproxy-logon -s localhost -l $user -n -o $temp_cred_file > /dev/null 2> $temp_err_file");

if ($status == 0)
{
	system("cat $temp_cred_file");
}
else
{
	print "ERROR ";
	system("cat $temp_err_file");
}

unlink($temp_cred_file);
unlink($temp_err_file);

exit(0);

