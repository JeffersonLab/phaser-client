# phaser-client [![CI](https://github.com/JeffersonLab/phaser-client/actions/workflows/ci.yml/badge.svg)](https://github.com/JeffersonLab/phaser-client/actions/workflows/ci.yml)
Graphical user interface client for operators to interact with the RF Phaser server at JLab.

---
- [Overview](https://github.com/JeffersonLab/phaser-client#overview)
- [Quick Start with Compose](https://github.com/JeffersonLab/phaser-client#quick-start-with-compose)
- [Install](https://github.com/JeffersonLab/phaser-client#install)
- [API](https://github.com/JeffersonLab/phaser-client#api)
- [Configure](https://github.com/JeffersonLab/phaser-client#configure)
- [Build](https://github.com/JeffersonLab/phaser-client#build)
- [Develop](https://github.com/JeffersonLab/phaser-client#develop)
- [Test](https://github.com/JeffersonLab/phaser-client#test)
- [Release](https://github.com/JeffersonLab/phaser-client#release)
- [Deploy](https://github.com/JeffersonLab/phaser-client#deploy)
- [See Also](https://github.com/JeffersonLab/phaser-client#see-also)
---

## Overview
This application provides an interface for operators to control the RF Phaser server.  The server provides a means
to optimize RF cavity phase angle in order to maximize the amount of energy to the accelerator beam, while minimizing energy spread.

## Configure
The app expects two configuration files:

| NAME                | DESCRIPTION           | EXAMPLE                                                                                                    |
|---------------------|-----------------------|------------------------------------------------------------------------------------------------------------|
| client.properties   | client configuration  | [client.properties](https://github.com/JeffersonLab/phaser-client/blob/main/config/client.properties)      |
| logging.properties  | logging configuration | [logging.properties](https://github.com/JeffersonLab/phaser-client/blob/main/config/logging.properties)    | 

The client.properties must be in the classpath and the logging.properties must be referenced via Java System Property `java.util.logging.config.file`

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

**Note**: The client should be [configured](https://github.com/JeffersonLab/phaser-client#configure) to connect to the test server and Oracle DB on localhost and therefore `client.properties` should contain:
```
server.host=localhost
server.port=2048
db.url=jdbc:oracle:thin:@//localhost:1521/xepdb1
db.user=phaser_reader
db.password=password
```
