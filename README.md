### Symmetric Encryption for the JVM
=======

##### Status: Functional but needs some tests and some refactoring. Needs better groovy doc comments. Feel free to make merge requests. USE AT YOUR OWN RISK!
-----------

#### Inspiration

The inspiration for this library comes from the ruby gem version which can be found [here](https://github.com/rocketjob/symmetric-encryption).

#### Why?

Could not find a decent solution for symmetric encryption for the JVM that was not maintained or was overly complicated.
In addition we have configuration files that have tokens, access ids, urls etc encrypted within our own jvm applications.

#### Current features

Currently the library can generate configuration files in xml, json and yaml. RSA keys and encryption keys. 

#### Future

1. Spock tests.

2. Gradle plugin for generating configuration files and keys.

3. Better documentation.

#### Generating documentation

```
gradle groovydoc
```

#### Building

This project currently uses [shadowjar](https://github.com/johnrengelman/shadow).

```
gradle clean

gradle shadowjar
```
 
#### Command line runner usage

There is a command line runner (main class) to generate configuration files and encryption keys. 
Make sure to install the JCE unlimited strength jars.

```
java -cp build/libs/SymmetricEncryption-1.0-SNAPSHOT-all.jar org.encryption.SymmetricEncryption -help

usage: java -cp SymmetricEncryption-1.0-SNAPSHOT-all.jar
            org.encryption.SymmetricEncryption [OPTIONS]
Options:
 -config <type>       Generate the symmetric encryption configuration.
                      Type: json, xml, yaml. Note: Running this will
                      generate a new configuration file every time!
 -env <environment>   Generate keys for given environment.
 -help                Print this message.
```


#### Loading the environment
 
The library is expecting for your generated `symmetric-encryption.(yaml, xml, json)` file to live on the classpath. It is recommended to include it in your resources folder of your project.

```groovy
// Load development encryption settings
SymmetricEncryption.load('development')

// Load production encryption settings
SymmetricEncryption.load('production')
```


#### Encrypting/Decrypting

```groovy
SymmetricEncryption.encrypt("Hello, World!") // -> V1dX8YgnU7CbJEUjejdxTA==

SymmetricEncryption.decrypt("V1dX8YgnU7CbJEUjejdxTA==") // -> Hello, World!
```
