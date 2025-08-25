#!/bin/bash
VARIABLES=(DOWNLOAD_URL
           ZIP_FILE
           APP_HOME)

if [[ $# -eq 0 ]] ; then
    echo "Usage: $0 [var file] <GitHub tag/version>"
    echo "The var file arg should be the path to a file relative to this script containing bash variables that will be sourced."
    printf '\n'
    exit 0
fi

MYPATH="$(readlink -f "$0")"
MYDIR="${MYPATH%/*}"
ENV_FILE=$MYDIR/$1

if [ ! -z "$1" ] && [ -f "$ENV_FILE" ]
then
echo "Loading environment $1"

if [ -z "$2" ]
then
echo "Version/Tag required"
exit 0
fi

TAG=$2

if [[ $TAG != 'v'* ]]; then
TAG=v$TAG
fi

VERSION="${TAG#v}"

. $ENV_FILE
fi


# Verify expected env set:
for i in "${!VARIABLES[@]}"; do
  var=${VARIABLES[$i]}
  [ -z "${!var}" ] && { echo "$var is not set. Exiting."; exit 1; }
done

FILENAME="${ZIP_FILE%.*}"

deploy() {
cd /tmp
rm -rf /tmp/${ZIP_FILE}
wget ${DOWNLOAD_URL}
unzip /tmp/${ZIP_FILE}
mv /tmp/${FILENAME} ${APP_HOME}/${VERSION}
}

deploy
config