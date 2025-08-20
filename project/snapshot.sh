#! /bin/sh

V=`sbt 'show version' 2>&1 | tail -n 1 | cut -d ']' -f 2 | perl -pe 's/^[ \t]+//;s/[ \t]+$//'`

if [ `echo "$V" | grep -E 'SNAPSHOT$' | wc -l` -eq 0 ]; then
  echo "Cannot publish release $V"
  exit 1
fi

export PUBLISH_REPO_NAME="Sonatype Nexus Repository Manager"
export PUBLISH_REPO_ID="central.sonatype.com"
export PUBLISH_REPO_URL="https://central.sonatype.com/repository/maven-snapshots/"

if [ -z "$PUBLISH_USER" ]; then
  echo "User: "
  read PUBLISH_USER
fi

export PUBLISH_USER

if [ -z "$PUBLISH_PASS" ]; then
  echo "Password: "
  read PUBLISH_PASS
fi

export PUBLISH_PASS

sbt
