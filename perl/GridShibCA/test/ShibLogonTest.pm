######################################################################
#
# GridShibCA Unit tests for ShibSignOn.pm
#
######################################################################
package GridShibCA::test::ShibLogonTest;

use base qw(Test::Unit::TestCase);

use GridShibCA::ShibLogon;

my $expectedMethod = "Shibboleth";

sub new
{
    my $self = shift()->SUPER::new(@_);
    return $self;
}

sub set_up
{
    my $self = shift;
    $self->{logon} = GridShibCA::ShibLogon->new();
    $self->assert_not_null($self->{logon});
}

sub tear_down
{
    delete $ENV{"REMOTE_USER"};
    delete $ENV{"HTTP_SHIB_IDENTITY_PROVIDER"};
}

sub test_EPPN
{
    my $self = shift;
    my $userId = "testuser\@fakeidp.org";
    my $idp = "urn:mace:incommon:fakeidp";
    $self->_setupEnvironment($userId, $idp);
    $self->assert_equals($expectedMethod, $self->{logon}->getMethodName());
    $self->assert_equals($userId, $self->{logon}->getUserId());
    $self->assert_equals($idp, $self->{logon}->getIdP());
}

sub test_EPTID
{
    my $self = shift;
    my $userId = "eptid:like/id\@fakeidp.org";
    my $idp = "urn:mace:incommon:fakeidp";
    $self->_setupEnvironment($userId, $idp);
    $self->assert_equals($expectedMethod, $self->{logon}->getMethodName());
    $self->assert_equals($userId, $self->{logon}->getUserId());
    $self->assert_equals($idp, $self->{logon}->getIdP());
}

sub _setupEnvironment
{
    my $self = shift;
    my $userId = shift;
    my $idp = shift;
    $ENV{"REMOTE_USER"} = $userId;
    $ENV{"HTTP_SHIB_IDENTITY_PROVIDER"} = $idp;
}

# Return a bas64-encode SAML reponse
sub _getAssertion
{
    return "PFJlc3BvbnNlIHhtbG5zPSJ1cm46b2FzaXM6bmFtZXM6dGM6U0FNTDoxLjA6cHJvdG9jb2wiIEluUmVzcG9uc2VUbz0iX2U5NGI1YTkxYjkxNzAxOTE5YzA0OTQxNDY2Y2E4ZDc2IiBJc3N1ZUluc3RhbnQ9IjIwMDctMDMtMTdUMDM6MDk6MTguNTQwWiIgTWFqb3JWZXJzaW9uPSIxIiBNaW5vclZlcnNpb249IjEiIFJlc3BvbnNlSUQ9Il85NTk4ZGQ4NmI4N2NkY2Y0ZTZlNzA4MmI3OWI4NGJjZSIgeG1sbnM6c2FtbD0idXJuOm9hc2lzOm5hbWVzOnRjOlNBTUw6MS4wOmFzc2VydGlvbiIgeG1sbnM6c2FtbHA9InVybjpvYXNpczpuYW1lczp0YzpTQU1MOjEuMDpwcm90b2NvbCIgeG1sbnM6eHNkPSJodHRwOi8vd3d3LnczLm9yZy8yMDAxL1hNTFNjaGVtYSIgeG1sbnM6eHNpPSJodHRwOi8vd3d3LnczLm9yZy8yMDAxL1hNTFNjaGVtYS1pbnN0YW5jZSI+PFN0YXR1cz48U3RhdHVzQ29kZSBWYWx1ZT0ic2FtbHA6U3VjY2VzcyIvPjwvU3RhdHVzPjxBc3NlcnRpb24geG1sbnM9InVybjpvYXNpczpuYW1lczp0YzpTQU1MOjEuMDphc3NlcnRpb24iIEFzc2VydGlvbklEPSJfNTJlMWJlYjBjNTgyY2JmNzBkOWM2NTJkYWUyM2E3MDAiIElzc3VlSW5zdGFudD0iMjAwNy0wMy0xN1QwMzowOToxOC41MjRaIiBJc3N1ZXI9InVybjptYWNlOmlucXVldWU6c2hpYjEzLm9wZW5pZHAub3JnIiBNYWpvclZlcnNpb249IjEiIE1pbm9yVmVyc2lvbj0iMSI+PENvbmRpdGlvbnMgTm90QmVmb3JlPSIyMDA3LTAzLTE3VDAzOjA5OjE4LjUyNFoiIE5vdE9uT3JBZnRlcj0iMjAwNy0wMy0xN1QxMTowOToxOC41MjRaIj48QXVkaWVuY2VSZXN0cmljdGlvbkNvbmRpdGlvbj48QXVkaWVuY2U+aHR0cHM6Ly90ZXN0LXNwLm5jc2EudWl1Yy5lZHUvc2hpYmJvbGV0aDwvQXVkaWVuY2U+PEF1ZGllbmNlPnVybjptYWNlOmlucXVldWU8L0F1ZGllbmNlPjwvQXVkaWVuY2VSZXN0cmljdGlvbkNvbmRpdGlvbj48L0NvbmRpdGlvbnM+PEF0dHJpYnV0ZVN0YXRlbWVudD48U3ViamVjdD48TmFtZUlkZW50aWZpZXIgRm9ybWF0PSJ1cm46bWFjZTpzaGliYm9sZXRoOjEuMDpuYW1lSWRlbnRpZmllciIgTmFtZVF1YWxpZmllcj0idXJuOm1hY2U6aW5xdWV1ZTpzaGliMTMub3BlbmlkcC5vcmciPl8wNDZiMjJiNzdlZjg1YmIxYzlhMmU0MjI2ZGUyMmI3MDwvTmFtZUlkZW50aWZpZXI+PC9TdWJqZWN0PjxBdHRyaWJ1dGUgQXR0cmlidXRlTmFtZT0idXJuOm1hY2U6ZGlyOmF0dHJpYnV0ZS1kZWY6bWFpbCIgQXR0cmlidXRlTmFtZXNwYWNlPSJ1cm46bWFjZTpzaGliYm9sZXRoOjEuMDphdHRyaWJ1dGVOYW1lc3BhY2U6dXJpIiB4bWxuczp0eXBlbnM9InVybjptYWNlOnNoaWJib2xldGg6MS4wIj48QXR0cmlidXRlVmFsdWUgeHNpOnR5cGU9InR5cGVuczpBdHRyaWJ1dGVWYWx1ZVR5cGUiPnZ3ZWxjaEBuY3NhLnVpdWMuZWR1PC9BdHRyaWJ1dGVWYWx1ZT48L0F0dHJpYnV0ZT48QXR0cmlidXRlIEF0dHJpYnV0ZU5hbWU9InVybjptYWNlOmRpcjphdHRyaWJ1dGUtZGVmOmVkdVBlcnNvblByaW5jaXBhbE5hbWUiIEF0dHJpYnV0ZU5hbWVzcGFjZT0idXJuOm1hY2U6c2hpYmJvbGV0aDoxLjA6YXR0cmlidXRlTmFtZXNwYWNlOnVyaSIgeG1sbnM6dHlwZW5zPSJ1cm46bWFjZTpzaGliYm9sZXRoOjEuMCI+PEF0dHJpYnV0ZVZhbHVlIFNjb3BlPSJvcGVuaWRwLm9yZyIgeHNpOnR5cGU9InR5cGVuczpBdHRyaWJ1dGVWYWx1ZVR5cGUiPnZ3ZWxjaDwvQXR0cmlidXRlVmFsdWU+PC9BdHRyaWJ1dGU+PC9BdHRyaWJ1dGVTdGF0ZW1lbnQ+PGRzOlNpZ25hdHVyZSB4bWxuczpkcz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC8wOS94bWxkc2lnIyI+CjxkczpTaWduZWRJbmZvPgo8ZHM6Q2Fub25pY2FsaXphdGlvbk1ldGhvZCBBbGdvcml0aG09Imh0dHA6Ly93d3cudzMub3JnLzIwMDEvMTAveG1sLWV4Yy1jMTRuIyIvPgo8ZHM6U2lnbmF0dXJlTWV0aG9kIEFsZ29yaXRobT0iaHR0cDovL3d3dy53My5vcmcvMjAwMC8wOS94bWxkc2lnI3JzYS1zaGExIi8+CjxkczpSZWZlcmVuY2UgVVJJPSIjXzUyZTFiZWIwYzU4MmNiZjcwZDljNjUyZGFlMjNhNzAwIj4KPGRzOlRyYW5zZm9ybXM+CjxkczpUcmFuc2Zvcm0gQWxnb3JpdGhtPSJodHRwOi8vd3d3LnczLm9yZy8yMDAwLzA5L3htbGRzaWcjZW52ZWxvcGVkLXNpZ25hdHVyZSIvPgo8ZHM6VHJhbnNmb3JtIEFsZ29yaXRobT0iaHR0cDovL3d3dy53My5vcmcvMjAwMS8xMC94bWwtZXhjLWMxNG4jIj48ZWM6SW5jbHVzaXZlTmFtZXNwYWNlcyB4bWxuczplYz0iaHR0cDovL3d3dy53My5vcmcvMjAwMS8xMC94bWwtZXhjLWMxNG4jIiBQcmVmaXhMaXN0PSJjb2RlIGRzIGtpbmQgcncgc2FtbCBzYW1scCB0eXBlbnMgI2RlZmF1bHQgeHNkIHhzaSIvPjwvZHM6VHJhbnNmb3JtPgo8L2RzOlRyYW5zZm9ybXM+CjxkczpEaWdlc3RNZXRob2QgQWxnb3JpdGhtPSJodHRwOi8vd3d3LnczLm9yZy8yMDAwLzA5L3htbGRzaWcjc2hhMSIvPgo8ZHM6RGlnZXN0VmFsdWU+QUtjSlVHSmg0dmQvc1h4VTZwN1BmOVRNaTFVPTwvZHM6RGlnZXN0VmFsdWU+CjwvZHM6UmVmZXJlbmNlPgo8L2RzOlNpZ25lZEluZm8+CjxkczpTaWduYXR1cmVWYWx1ZT4KZS9KYU50bTE0K1NabEhJYnFUaGZ6TmtHWjdxelB2SURjOGI1cjNnMncvWENhbWRtSEhuZlNOQm0wTUF4b3htSGVNZkJFOEprNXFTbQp1VDNMWE5QRDZWbmdKZ0c0V0F5R2ljM0J6T0ZZVGw3K2phMlQrZ0pZZDBDOGh5NHJQc1hmb28zc3pUcTRhaDgwdUdrQVY0VnZtNFpMCkU5UUhmR0JyVXBsNXJ2N1dYMkk9CjwvZHM6U2lnbmF0dXJlVmFsdWU+CjxkczpLZXlJbmZvPgo8ZHM6WDUwOURhdGE+CjxkczpYNTA5Q2VydGlmaWNhdGU+Ck1JSUMwekNDQWp5Z0F3SUJBZ0lDQndVd0RRWUpLb1pJaHZjTkFRRUVCUUF3Z2FreEN6QUpCZ05WQkFZVEFsVlRNUkl3RUFZRFZRUUkKRXdsWGFYTmpiMjV6YVc0eEVEQU9CZ05WQkFjVEIwMWhaR2x6YjI0eElEQWVCZ05WQkFvVEYxVnVhWFpsY25OcGRIa2diMllnVjJsegpZMjl1YzJsdU1Tc3dLUVlEVlFRTEV5SkVhWFpwYzJsdmJpQnZaaUJKYm1admNtMWhkR2x2YmlCVVpXTm9ibTlzYjJkNU1TVXdJd1lEClZRUURFeHhJUlZCTFNTQlRaWEoyWlhJZ1EwRWdMUzBnTWpBd01qQTNNREZCTUI0WERUQTJNREV3T1RBeE1UUTBOMW9YRFRFd01ESXgKT0RBeE1UUTBOMW93Z1pReEN6QUpCZ05WQkFZVEFsVlRNUkF3RGdZRFZRUUlFd2RCYkdGaVlXMWhNUk13RVFZRFZRUUhFd3BDYVhKdAphVzVuYUdGdE1TTXdJUVlEVlFRS0V4cFVhR1VnVDNCbGJpQkpaR1Z1ZEdsMGVTQlFjbTkyYVdSbGNqRVlNQllHQTFVRUF4TVBjM052CkxtOXdaVzVwWkhBdWIzSm5NUjh3SFFZSktvWklodmNOQVFrQkZoQnliMjkwUUc5d1pXNXBaSEF1YjNKbk1JR2ZNQTBHQ1NxR1NJYjMKRFFFQkFRVUFBNEdOQURDQmlRS0JnUUNabERLYk9PZ1ZsTlpDdURjUTJ0UWkyUDlia3hPckY2ajNLMHRvWTVLNzhLNnFlbVlOOHBIaAovVHE3SDIwWmJHbWdQa3RNNXdmc0JhQ0libmMrSVVrN0VjWURSR3pBN21Sa29yUHJXTGV4UmZIVXBUS0IvRk1LZVVwZWpnMWh5ZmtOCkxoYWs2SDBHTk4vVEFQY2FRVWErMlNNZG1MQWN5ZDNkR3R5NWFBd05sd0lEQVFBQm94MHdHekFNQmdOVkhSTUJBZjhFQWpBQU1Bc0cKQTFVZER3UUVBd0lGb0RBTkJna3Foa2lHOXcwQkFRUUZBQU9CZ1FBa2VTUUZPZjlJMWl2S3JLWTRmZkI2VGJlT2xyYlhGcGFLWGJBMwpoOFM4S0ZaZ2JpSGFzcTR6bUxuSE9OUms3aXd1ZHllb0d1VkFCT1JSWWw0aDlzM2RoWWNRakk3ellQS1FSZTAxTk9tSUEva1FiRXkwCndQeG9CZWtYWTJxN1lvdWRVWTV1WjlPWkdlbkhnamJhajdHeHZjQ0NzNzFyY2tUSEhtbUhhTm92QUE9PQo8L2RzOlg1MDlDZXJ0aWZpY2F0ZT4KPC9kczpYNTA5RGF0YT4KPC9kczpLZXlJbmZvPjwvZHM6U2lnbmF0dXJlPjwvQXNzZXJ0aW9uPjwvUmVzcG9uc2U+";
}


# Return true for import/use
1;

### Local Variables: ***
### mode:perl ***
### End: ***
######################################################################
