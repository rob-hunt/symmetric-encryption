### Symmetric Encryption for the JVM
=======

##### Status: Functional but needs some tests and some refactoring. Needs better groovy doc comments. Feel free to make merge requests.
-----------

#### Inspiration

The inspiration for this library comes from the ruby gem version which can be found [here](https://github.com/rocketjob/symmetric-encryption).

#### Why?

Could not find a decent solution for symmetric encryption for the JVM that was not maintained or was overly complicated.
In addition we have configuration files that have tokens, access ids, urls etc encrypted within our own jvm applications.

#### Current features

Currently the library can generate configuration files in xml, json and yaml. RSA keys and encryption keys.
 
#### Installation

1. Download the JCE jars (http://www.oracle.com/technetwork/java/javase/downloads/jce8-download-2133166.html)
2. Download org.bouncycastle:bcpkix-jdk15on:1.54 jar (https://search.maven.org/remotecontent?filepath=org/bouncycastle/bcpkix-jdk15on/1.54/bcpkix-jdk15on-1.54.jar)
3. Download org.bouncycastle:bcprov-jdk15on:1.54 jar (https://search.maven.org/remotecontent?filepath=org/bouncycastle/bcprov-jdk15on/1.54/bcprov-jdk15on-1.54.jar)
4. Make sure to have Groovy installed it is not included in the shadowjar!
5. Place the bouncy castle jars at `$JAVA_HOME/jre/lib/ext/`
6. Update the security policy `$JAVA_HOME/jre/lib/security/java.security` to include `security.provider.N=org.bouncycastle.jce.provider.BouncyCastleProvider`
7. Build Symmetric Encryption library jar in the building steps below. Add the jar to your project.
8. Enjoy!

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
 -config <type>                 Generate the symmetric encryption
                                configuration. Type: json, xml, yaml.
                                Note: Running this will generate a new
                                configuration file every time!
 -decrypt <environment=value>   Decrypt value for specific environment.
 -encrypt <environment=value>   Encrypt value for specific environment.
 -env <environment>             Generate keys for given environment.
 -help
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
