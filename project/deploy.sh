#! /bin/bash

set -e

REPO="https://oss.sonatype.org/service/local/staging/deploy/maven2/"

if [ $# -lt 2 ]; then
    echo "Usage $0 version gpg-key"
    exit 1
fi

#echo "Check the project version"
#VERSION=`sbt ';project ReactiveMongo ;show version' 2>&1 | tail -n 1 | cut -d ' ' -f 2 | sed -e 's/^.*([0-9.]*).*$/$1/'`

VERSION="$1"
KEY="$2"

echo "Password: "
read -s PASS

function deploy {
  BASE="$1"
  POM="$BASE.pom"

  expect << EOF
set timeout 300
spawn mvn gpg:sign-and-deploy-file -DuniqueVersion=false -Dkeyname=$KEY -DpomFile=$POM -Dfile=$BASE.jar -Djavadoc=$BASE-javadoc.jar -Dsources=$BASE-sources.jar $ARG -Durl=$REPO -DrepositoryId=sonatype-nexus-staging
log_user 0
expect "GPG Passphrase:"
send "$PASS\r"
log_user 1
expect "BUILD SUCCESS"
expect eof
EOF
}

if [ ! -f "shaded/target/reactivemongo-shaded-$VERSION.jar" ]; then
  mv "shaded/target/ReactiveMongo-Shaded-Assembly-$VERSION.jar" "shaded/target/reactivemongo-shaded-$VERSION.jar"
fi

if [ ! -f "shaded-native-osx-x86_64/target/reactivemongo-shaded-native-$VERSION-osx-x86-64.jar" ]; then
  mv "shaded-native-osx-x86_64/target/ReactiveMongo-Shaded-Native-assembly-$VERSION-osx-x86-64.jar" \
     "shaded-native-osx-x86_64/target/reactivemongo-shaded-native-$VERSION-osx-x86-64.jar"
fi

if [ ! -f "shaded-native-linux-x86_64/target/reactivemongo-shaded-native-$VERSION-linux-x86-64.jar" ]; then
  mv "shaded-native-linux-x86_64/target/ReactiveMongo-Shaded-Native-assembly-$VERSION-linux-x86-64.jar" \
     "shaded-native-linux-x86_64/target/reactivemongo-shaded-native-$VERSION-linux-x86-64.jar"
fi

JAVA_MODULES="shaded:reactivemongo-shaded shaded-native-osx-x86_64:reactivemongo-shaded-native:osx-x86-64 shaded-native-linux-x86_64:reactivemongo-shaded-native:linux-x86-64"
BASES=""

for M in $JAVA_MODULES; do
  B=`echo "$M" | cut -d ':' -f 1`
  N=`echo "$M" | cut -d ':' -f 2`
  V=`echo "$M" | cut -d ':' -f 3`

  if [ ! "x$V" = "x" ]; then
    V="-$V"
  fi 

  BASES="$BASES $B/target/$N-$VERSION$V"
done

for B in $BASES; do
  deploy "$B"
done
