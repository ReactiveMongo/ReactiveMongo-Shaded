#! /bin/bash

set -e

# curl -D - -X POST -u '...' "https://ossrh-staging-api.central.sonatype.com/manual/upload/defaultRepository/org.reactivemongo"

#REPO="https://oss.sonatype.org/service/local/staging/deploy/maven2/"
REPO="https://ossrh-staging-api.central.sonatype.com/service/local/staging/deploy/maven2/"

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
log_user 0
spawn mvn gpg:sign-and-deploy-file -DuniqueVersion=false -Dkeyname=$KEY -Dpassphrase=$PASS -DpomFile=$POM -Dfile=$BASE.jar -Djavadoc=$BASE-javadoc.jar -Dsources=$BASE-sources.jar $ARG -Durl=$REPO -DrepositoryId=sonatype-nexus-staging
log_user 1
expect "BUILD SUCCESS"
expect eof
EOF
}

if [ ! -f "shaded/target/reactivemongo-shaded-$VERSION.jar" ]; then
  mv "shaded/target/ReactiveMongo-Shaded-Assembly-$VERSION.jar" "shaded/target/reactivemongo-shaded-$VERSION.jar"
fi

OSES="osx linux"
ARCHES="x86_64 aarch_64"

for OS in $OSES; do
  for ARCH in $ARCHES; do
    A=`echo "$ARCH" | sed -e 's/_/-/'`
    P="shaded-native-${OS}-${A}"
    TARGET="shaded-native-${OS}-${ARCH}/target"
    JAR="${TARGET}/reactivemongo-$P-$VERSION.jar"
    ASM="$TARGET/reactivemongo-${P}-assembly-$VERSION.jar"

    if [ -r "$ASM" ]; then
      mv "$ASM" "$JAR"
    fi
  done
done

JAVA_MODULES="shaded:reactivemongo-shaded"

for OS in $OSES; do
  for ARCH in $ARCHES; do
    S="${OS}-${ARCH}"
    A=`echo "$ARCH" | sed -e 's/_/-/'`
    V="${OS}-${A}"
    
    JAVA_MODULES="$JAVA_MODULES shaded-native-${S}:reactivemongo-shaded-native-${V}"
  done
done

SCALA_MODULES="alias:reactivemongo-alias"
SCALA_VERSIONS="2.11 2.12 2.13 3.6.3"
BASES=""

for M in $JAVA_MODULES; do
  B=`echo "$M" | cut -d ':' -f 1`
  N=`echo "$M" | cut -d ':' -f 2`

  BASES="$BASES $B/target/$N-$VERSION"
done

for V in $SCALA_VERSIONS; do
    MV=`echo "$V" | sed -e 's/^3.*/3/'`

    for M in $SCALA_MODULES; do
        B=`echo "$M" | cut -d ':' -f 1`
        SCALA_DIR="$B/target/scala-$V"

        if [ ! -d "$SCALA_DIR" ]; then
            echo "Skip Scala version $V for $M"
        else
            N=`echo "$M" | cut -d ':' -f 2`
            BASES="$BASES $SCALA_DIR/$N"_$MV-$VERSION
        fi
    done
done

for B in $BASES; do
  deploy "$B"
done
