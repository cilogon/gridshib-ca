######################################################################
#
# GridShibCA Unit tests for FakeSignOn.pm
#
######################################################################
package GridShibCA::test::UserIdentityTest;

use base qw(Test::Unit::TestCase);

use GridShibCA::UserIdentity;

sub new
{
    my $self = shift()->SUPER::new(@_);
    return $self;
}

sub set_up
{
    my $self = shift;
    # No-Op
}

sub tear_down
{
    # No-Op
}

sub test_default
{
    my $self = shift;
    my $userId = "juser\@example.com";
    my $authMethod = "Shibboleth";
    my $idpId = "urn:some:idp";
    my $clientHost = "somewhere.example.com";
    my $attributes = {
	"realName" => "Jane User",
	"assurance" => "some",
    };
    my $id = GridShibCA::UserIdentity->new(
	-authMethod => $authMethod,
	-userId => $userId,
	-idpId => $idpId,
	-clientHost => $clientHost,
	-attributes => $attributes
	);
    $self->assert_equals($authMethod, $id->authMethod());
    $self->assert_equals($userId, $id->userId());
    $self->assert_equals($idpId, $id->idpId());
    $self->assert_equals($clientHost, $id->clientHost());
    my $a = $id->attributes();
    $self->assert_not_null($a);
    $self->assert_equals(scalar(keys(%$attributes)), scalar(keys(%$a)));
    for my $key (keys(%$attributes))
    {
	$self->assert_equals($attributes->{$key}, $a->{$key});
    }
}

sub test_userIdentityException
{
    my $self = shift;
    $self->assert_raises(GridShibCA::UserIdentityException,
			 sub { throw GridShibCA::UserIdentityException("test"); });
}

sub test_missingArg
{
    my $self = shift;
    $self->assert_raises(GridShibCA::UserIdentityException,
			 sub { GridShibCA::UserIdentity->new(); });
}

# Return true for import/use
1;

### Local Variables: ***
### mode:perl ***
### End: ***
######################################################################
