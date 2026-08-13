#! /bin/bash

set -e

# curl -D - -X POST -u "$SONATYPE_USERNAME:$SONATYPE_PASSWORD" "https://ossrh-staging-api.central.sonatype.com/manual/upload/defaultRepository/org.reactivemongo"

#REPO="https://oss.sonatype.org/service/local/staging/deploy/maven2/"
REPO="https://ossrh-staging-api.central.sonatype.com/service/local/staging/deploy/maven2/"

if [ $# -lt 2 ]; then
    echo "Usage $0 version gpg-key"
    exit 1
fi

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

RT="target/out/jvm/u"

if [ ! -f "$RT/reactivemongo-shaded/reactivemongo-shaded-$VERSION.jar" ]; then
  mv "$RT/reactivemongo-shaded/ReactiveMongo-Shaded-assembly-$VERSION.jar" "$RT/reactivemongo-shaded/reactivemongo-shaded-$VERSION.jar"
fi

OSES="osx linux"
ARCHES="x86-64 aarch-64"

for OS in $OSES; do
  for ARCH in $ARCHES; do
    P="shaded-native-${OS}-${ARCH}"
    TARGET="${RT}/reactivemongo-shaded-native-${OS}-${ARCH}"
    JAR="${TARGET}/reactivemongo-$P-$VERSION.jar"
    ASM="$TARGET/reactivemongo-${P}-assembly-$VERSION.jar"

    if [ -r "$ASM" ]; then
      cp "$ASM" "$JAR"
    fi
  done
done

JAVA_MODULES="reactivemongo-shaded"

for OS in $OSES; do
  for ARCH in $ARCHES; do
    S="${OS}-${ARCH}"
    
    JAVA_MODULES="$JAVA_MODULES reactivemongo-shaded-native-${S}"
  done
done

SCALA_MODULES="reactivemongo-alias"
SCALA_VERSIONS="2.11 2.12 2.13 3.3.8"
BASES=""

RT="target/out/jvm/u/"

for M in $JAVA_MODULES; do
  BASES="$BASES $RT/$M/$M-$VERSION"
done

for V in $SCALA_VERSIONS; do
    MV="${V/#3*/3}"

    for M in $SCALA_MODULES; do
        SCALA_DIR=(target/out/jvm/scala-${V}*/$M)

        if [ ! -d "$SCALA_DIR" ]; then
            echo "Skip Scala version $V for $M: $SCALA_DIR"
        else
            BASES="$BASES $SCALA_DIR/$M"_$MV-$VERSION
        fi
    done
done

for B in $BASES; do
  deploy "$B"
done
