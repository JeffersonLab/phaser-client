# phaser-client [![CI](https://github.com/JeffersonLab/phaser-client/actions/workflows/ci.yml/badge.svg)](https://github.com/JeffersonLab/phaser-client/actions/workflows/ci.yml)
Graphical client for operators to interact with the RF Phaser server

## Build
This project is built with [Java 17](https://adoptium.net/) (compiled to Java 8 bytecode), and uses the [Gradle 7](https://gradle.org/) build tool to automatically download dependencies and build the project from source:

```
git clone https://github.com/JeffersonLab/phaser-client.git
cd phaser-client
gradlew build
```

**Note**: If you do not already have Gradle installed, it will be installed automatically by the wrapper script included in the source

**Note for JLab On-Site Users**: Jefferson Lab has an intercepting [proxy](https://gist.github.com/slominskir/92c25a033db93a90184a5994e71d0b78)
