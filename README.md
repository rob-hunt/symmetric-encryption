Symmetric Encryption for the JVM
=======

Status: Functional but needs some tests and some refactoring. Needs better groovy doc comments. Feel free to make merge requests. USE AT YOUR OWN RISK!
-----------

## Inspiration
The inspiration for this library comes from the ruby gem version which can be found [here](https://github.com/rocketjob/symmetric-encryption).

## Why?
Could not find a decent solution for symmetric encryption for the JVM that was not maintained or was overly complicated.
In addition we have configuration files that have tokens, access ids, urls etc encrypted within our own jvm applications.
The current way we handle this was a hack that has hung around for a year!

## Current features
Currently the library can generate configuration files in xml, json and yaml. RSA keys, encryption keys and
configuration files are compatible with the rocket job gem mentioned in inspiration. Note the rocket job gem only supports
yaml for configuration.

## Future
A bunch of stuff.
 
## Usage
There is a command line runner (main class) to generate configuration files and encryption keys. 
Make sure to install the JCE unlimited strength jars.

```
gradle clean

gradle shadowjar

java -cp build/libs/SymmetricEncryption-VERSION-all.jar org.esp.SymmetricEncryption
```
