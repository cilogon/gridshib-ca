#!/bin/sh
# Exit on any error
set -e

host=buildbot.ncsa.uiuc.edu

# -x Disable X11 forwarding
ssh_opts="-x"

echo "Copying configuration to ${host}...."
scp gridshib-ca-buildbot.cfg ${host}:/tmp

echo "Installing configuration..."
ssh ${ssh_opts} ${host} \
    "sudo -u buildmaster cp /tmp/gridshib-ca-buildbot.cfg ~buildmaster/Buildbot/"

echo "Restarting buildbot..."
ssh ${ssh_opts} ${host} \
    "sudo -u buildmaster buildbot reconfig ~buildmaster/Buildbot/"

echo "Success."
exit 0
