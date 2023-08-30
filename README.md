# phaser-client [![CI](https://github.com/JeffersonLab/phaser-client/actions/workflows/ci.yml/badge.svg)](https://github.com/JeffersonLab/phaser-client/actions/workflows/ci.yml)
A graphical user interface client for operators to interact with the RF Phaser server at JLab.

![Screenshot](https://raw.githubusercontent.com/JeffersonLab/phaser-client/main/Screenshot.png)

---
- [Overview](https://github.com/JeffersonLab/phaser-client#overview)
- [Install](https://github.com/JeffersonLab/phaser-client#install)
- [Configure](https://github.com/JeffersonLab/phaser-client#configure)
- [Build](https://github.com/JeffersonLab/phaser-client#build)
- [Develop](https://github.com/JeffersonLab/phaser-client#develop)
- [Release](https://github.com/JeffersonLab/phaser-client#release)
- [Deploy](https://github.com/JeffersonLab/phaser-client#deploy)
- [See Also](https://github.com/JeffersonLab/phaser-client#see-also)  
---

## Overview
This application provides an interface for operators to control the RF Phaser server.  The server provides a means to optimize RF cavity phase angle in order to maximize the amount of energy to the accelerator beam, while minimizing energy spread.  See [docs](https://github.com/JeffersonLab/phaser-client/tree/main/doc).

## Install
This application requires a Java 8+ JVM and standard library to run.  The app requires an Oracle database ([schema setup](https://github.com/JeffersonLab/phaser-client/tree/main/docker/oracle/setup)) and an RF Phaser server.  Docker is used to provide a local Oracle database during development and testing.  A simple RF Phaser test server is provided in this project as well.

Download from [Releases](https://github.com/JeffersonLab/phaser-client/releases) or [build](https://github.com/JeffersonLab/phaser-client#build) the [distribution](https://github.com/JeffersonLab/phaser-client#release) yourself.

Launch with:

UNIX:
```
bin/phaser-client
```
Windows:
```
bin/phaser-client.bat
```

## Configure
The app expects two configuration files:

| NAME                | DESCRIPTION           | EXAMPLE                                                                                                    |
|---------------------|-----------------------|------------------------------------------------------------------------------------------------------------|
| client.properties   | client configuration  | [client.properties](https://github.com/JeffersonLab/phaser-client/blob/main/config/client.properties)      |
| logging.properties  | logging configuration | [logging.properties](https://github.com/JeffersonLab/phaser-client/blob/main/config/logging.properties)    | 

The client.properties must be in the classpath and the logging.properties must be referenced via Java System Property `java.util.logging.config.file`.  The scripts generated in the install step handle both the classpath and system property and assume the config files are in a directory named `config` at the root of the project.

## Build
This project is built with [Java 17](https://adoptium.net/) (compiled to Java 8 bytecode), and uses the [Gradle 7](https://gradle.org/) build tool to automatically download dependencies and build the project from source:

```
git clone https://github.com/JeffersonLab/phaser-client.git
cd phaser-client
gradlew build
```

**Note**: If you do not already have Gradle installed, it will be installed automatically by the wrapper script included in the source

**Note for JLab On-Site Users**: Jefferson Lab has an intercepting [proxy](https://gist.github.com/slominskir/92c25a033db93a90184a5994e71d0b78)

## Develop
In order to iterate rapidly when making changes it's often useful to run the app directly on the local workstation, perhaps leveraging an IDE.  In this scenario run the Oracle service dependency with:
```
docker compose -f deps.yml up
```

Then run the test server with:
```
gradlew runServer
```
And run the client with:
```
gradlew runClient
```

**Note**: The default [configuration](https://github.com/JeffersonLab/phaser-client#configure) assumes a connection to the test server and Oracle DB on localhost and therefore `client.properties` contain:
```
server.host=localhost
server.port=2048
db.url=jdbc:oracle:thin:@//localhost:1521/xepdb1
db.user=phaser_reader
db.password=password
```

**Note**: Javadocs can be generated with the command:
```
gradlew javadoc
```

**Note**: The graphical Java Swing forms were built using the [Apache Netbeans](https://netbeans.apache.org/) Matisse builder tool.  It's recommended that graphical component modifications be made using this tool, which modifies the XML `*.form` files.  The XML is used to dyanamically generate Java Swing code.  

## Release
1. Bump the version number in build.gradle and commit and push to GitHub (using [Semantic Versioning](https://semver.org/)).
1. Run the Gradle distribution target:
```
gradlew assembleDist
```
3. Create a new release on the GitHub [Releases](https://github.com/JeffersonLab/phaser-client/releases) page corresponding to same version in build.gradle (Enumerate changes and link issues).   Attach the generated distribution zip to the release.

## Deploy
At Jefferson Lab this application is deployed to the certified apps area and accessed via JMenu.  Deploying a new version typically looks like (version 2.0.0 shown):
```
# Can't wget from ops network so use dev then scp
ssh devl00
cd /tmp
wget https://github.com/JeffersonLab/phaser-client/releases/download/v2.0.0/phaser-client-2.0.0.zip

ssh sqam@opsl00
cd /tmp
scp devl00:/tmp/phaser-client-2.0.0.zip .
unzip phaser-client-2.0.0.zip
mv phaser-client-2.0.0 /a/certified/apps/phaser/2.0.0
cd /a/certified/rhel-9-x86_64/bin
unlink phaser-client
ln -s ../../apps/phaser/2.0.0/bin/phaser-client-from-nested-symlinks phaser-client
```

## See Also
- [icalibrate](https://github.com/JeffersonLab/icalibrate)
- [jlog](https://github.com/JeffersonLab/jlog)
