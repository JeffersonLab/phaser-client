#!/bin/bash

#
# phaser-client requires Java 11
# If run on RHEL7, you may default to Java 8, and fail to launch
# Therefore, this script checks if you are on RHEL9 (good), else ssh to RHEL9
#

RHEL9=opsl90
if [ "${HOST_ARCH}" != "rhel-9-x86_64"  ]
then
  CMD="ssh -q ${RHEL9} /cs/certified/apps/phaser/PRO/bin/phaser-client"
else
  CMD="/cs/certified/apps/phaser/PRO/bin/phaser-client"
fi
$CMD