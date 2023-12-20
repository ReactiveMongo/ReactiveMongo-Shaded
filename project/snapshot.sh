#! /bin/sh

V=`sbt 'show version' 2>&1 | tail -n 1 | cut -d ']' -f 2 | perl -pe 's/^[ \t]+//;s/[ \t]+$//'`

if [ `echo "$V" | grep -E 'SNAPSHOT$' | wc -l` -eq 0 ]; then
  echo "Cannot publish release $V"
  exit 1
fi

export PUBLISH_REPO_NAME="Sonatype Nexus Repository Manager"
export PUBLISH_REPO_ID="oss.sonatype.org"
export PUBLISH_USER="cchantep"
export PUBLISH_REPO_URL="https://oss.sonatype.org/content/repositories/snapshots"

echo "Password: "
read PASS
export PUBLISH_PASS="$PASS"

sbt
