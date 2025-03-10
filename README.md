# phaser-client [![CI](https://github.com/JeffersonLab/phaser-client/actions/workflows/ci.yaml/badge.svg)](https://github.com/JeffersonLab/phaser-client/actions/workflows/ci.yaml)
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
This application requires a Java 11+ JVM and standard library to run.  The app requires an Oracle database ([schema setup](https://github.com/JeffersonLab/phaser-client/tree/main/docker/oracle/setup)) and an RF Phaser server.  Docker is used to provide a local Oracle database during development and testing.  A simple RF Phaser test server is provided in this project as well.

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
This project is built with [Java 17](https://adoptium.net/) (compiled to Java 11 bytecode), and uses the [Gradle 7](https://gradle.org/) build tool to automatically download dependencies and build the project from source:

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
docker compose -f deps.yaml up
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
1. Bump the version number in the VERSION file and commit and push to GitHub (using [Semantic Versioning](https://semver.org/)).
1. The CD GitHub Action should run automatically invoking:
    - The Create release GitHub Action to tag the source and create release notes summarizing any pull requests. Edit the release notes to add any missing details. A distribution zip file artifact is attached to the release.

## Deploy
At Jefferson Lab this application is deployed to the certified apps area and launched via JMenu using search keyword `phaser`.  Deploying a new version typically looks like (version 2.0.0 shown):
```
# Install on dev fiefdom and it will be propogated to ops via sync; note: firewall blocks wget on ops fiefdom anyways
ssh sqam@devl00
cd /tmp
wget https://github.com/JeffersonLab/phaser-client/releases/download/v2.0.0/phaser-client-2.0.0.zip
unzip phaser-client-2.0.0.zip
mv phaser-client-2.0.0 /cs/certified/apps/phaser/2.0.0
cd /cs/certified/apps/phaser
unlink PRO
ln -s 2.0.0 PRO
```

Generally the [configure](https://github.com/JeffersonLab/phaser-client/tree/main#configure) step must be done as the default configs assume localhost.   Copying the previous version config dir may be sufficient.  It's also a good idea to launch the new version of the app and at least verify the Help dialog indicates the new version.

Thie steps above will only update the `dev` filesystem.   To update others such as `ops` generally the SQAM runs a sync with:
```
/cs/certified/admin/rsync_certified
```

**Note**: The JLab certified app linking system may attempt to use nested relative linking, which [doesn't work with our start script](https://github.com/JeffersonLab/phaser-client/issues/2).  Be sure absolute paths are set.  Generally certified apps are available in a fiefdom user's path by default via this linking.  Currently the app is available on the path as `phaser-client`.

## See Also
- [icalibrate](https://github.com/JeffersonLab/icalibrate)
- [jlog](https://github.com/JeffersonLab/jlog)
