/*
 * Copyright 2016-2017 Andrew Medeiros.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.encryption

import groovy.json.JsonSlurper
import groovy.util.slurpersupport.GPathResult
import org.encryption.exceptions.SymmetricEncryptionException
import org.encryption.generators.ConfigGenerator
import org.encryption.generators.KeyPairGenerator
import org.yaml.snakeyaml.Yaml

import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

/**
 * API into decrypting and encrypting.
 *
 * @author Andrew Medeiros
 */
@SuppressWarnings('DuplicateStringLiteral')
class SymmetricEncryption {
    private static final String PROVIDER = 'BC'
    private static AesEncryption aesEncryption

    /**
     * Commandline runner for generating the configuration and encryption keys.
     * @param args
     */
    static void main(String[] args) throws Exception {
        def options = CliParser.instance.parse(args)

        if (options.getProperty('env')) {
            String environment          = options.env
            Map<String, String> config  = readConfig()
            String privateRsaKey        = normalizePem(config[environment]['private_rsa_key'] as String)
            RsaEncryption rsaEncryption = new RsaEncryption().withKey(privateRsaKey)
            new KeyPairGenerator(rsaEncryption, environment).generateKeyPair()
            println("Generated new encryption keys for environment ${options.env}")
        } else if (options.getProperty('config')) {
            new ConfigGenerator(options.config as String).generateConfiguration()
            println("Generated new configuration file of type ${options.config}.")
        } else if (options.getProperty('encrypt')) {
            load(options.encrypts[0] as String)
            println("Encrypted value: ${encrypt(options.encrypts[1] as String)}")
        } else if (options.getProperty('decrypt')) {
            load(options.decrypts[0] as String)
            println("Decrypted value: ${decrypt(options.decrypts[1] as String)}")
        } else {
            CliParser.instance.usage() // Default display help
        }
    }

    /**
     * Load Symmetric Encryption for a specific environment
     * Include an optional filename to load.
     * @param environment Environment we are using.
     * @param fileName Optional Symmetric Encryption file.
     */
    static void load(String environment, String fileName = null) {
        if (fileName) {
            File file = new File(fileName)
            if (file.exists()) {
                init(readConfig(file), environment)
            } else {
                throw new SymmetricEncryptionException("File ${fileName} does not exist.")
            }
        } else {
            init(readConfig(), environment)
        }
    }

    /**
     * Read the encryption file. Passing an optional fileName to load.
     * @param filename Optional filename to load.
     * @return
     */
    private static Map<String, String> readConfig(File filename = null) {
        if (isJson(filename)) {
            return loadJson(filename)
        } else if (isXml(filename)) {
            return loadXml(filename)
        } else if (isYaml(filename)) {
            return loadYaml(filename)
        }

        throw new SymmetricEncryptionException('Missing Symmetric Encryption configuration file.')
    }

    /**
     * Initiation the RSA and Aes encryption classes.
     * @param configFile Configuration file.
     * @param environment Environment we are using.
     */
    private static void init(Map<String, String> configMap, String environment) {
        def config = configMap[environment]

        if (environment == 'development' || environment == 'test') {
            String cipherName    = config['cipher_name']
            String encryptionKey = config['encryption_key']
            Cipher cipher        = Cipher.getInstance(cipherName, PROVIDER)
            aesEncryption        = new AesEncryption(encryptionKey, cipher)
        } else {
            def cipherConfig     = config['cipher']
            String ivFile        = cipherConfig['iv_filename']
            String keyFile       = cipherConfig['key_filename']
            String cipherName    = cipherConfig['cipher_name']
            String privateRsaKey = normalizePem(config['private_rsa_key'] as String)
            Cipher cipher        = Cipher.getInstance(cipherName, PROVIDER)
            RsaEncryption rsa    = new RsaEncryption().withKey(privateRsaKey)

            IvParameterSpec ivParameterSpec = new IvParameterSpec(rsa.decrypt(new File(ivFile)))
            SecretKeySpec secretKeySpec     = new SecretKeySpec(rsa.decrypt(new File(keyFile)), 'AES')

            aesEncryption = new AesEncryption(cipher, ivParameterSpec, secretKeySpec)
        }
    }

    /**
     * Decrypt an encrypted string.
     * @param encryptedString
     * @return Decrypted String.
     */
    static String decrypt(String encryptedString) {
        checkInitalized()
        aesEncryption.decrypt(encryptedString)
    }

    /**
     * Encrypted a plain text string.
     * @param plainText
     * @return Encrypted String.
     */
    static String encrypt(String plainText) {
        checkInitalized()
        aesEncryption.encrypt(plainText)
    }

    /**
     * Normalize the pem from its formatting inside the xml, json, yaml configuration files.
     * @param pem
     * @return
     */
    static String normalizePem(String pem) {
        pem.replace('  ', '')
    }

    private static boolean isJson(File file = null) {
        if (file) {
            return file.absolutePath.endsWith('.json')
        }

        filePresent("${ConfigGenerator.BASE_FILE_NAME}.json")
    }

    private static boolean isXml(File file = null) {
        if (file) {
            return file.absolutePath.endsWith('.xml')
        }

        filePresent("${ConfigGenerator.BASE_FILE_NAME}.xml")
    }

    private static boolean isYaml(File file = null) {
        if (file) {
            return file.absolutePath.endsWith('.yaml')
        }

        filePresent("${ConfigGenerator.BASE_FILE_NAME}.yaml")
    }

    private static boolean filePresent(String filename) {
        loadFile(filename) != null
    }

    private static Map<String, String> loadJson(File file = null) {
        if (file) {
            return (Map) new JsonSlurper().parse(file.newInputStream())
        }

        (Map) new JsonSlurper().parse(loadFile("${ConfigGenerator.BASE_FILE_NAME}.json"))
    }

    private static Map<String, String> loadXml(File file = null) {
        if (file) {
            return nodeToMap(new XmlSlurper().parse(file.newInputStream()))
        }

        nodeToMap(new XmlSlurper().parse(loadFile("${ConfigGenerator.BASE_FILE_NAME}.xml")))
    }

    private static Map<String, String> loadYaml(File file = null) {
        if (file) {
            return (Map) new Yaml().load(file.newInputStream())
        }

        (Map) new Yaml().load(loadFile("${ConfigGenerator.BASE_FILE_NAME}.yaml"))
    }

    // Convert XML nodes into a map
    private static Map<String, String> nodeToMap(GPathResult nodes) {
        nodes.children().collectEntries {
            [ it.name(), it.childNodes() ? nodeToMap(it) : it.text() ]
        }
    }

    private static InputStream loadFile(String filename) {
        SymmetricEncryption.classLoader.getResourceAsStream(filename)
    }

    private static void checkInitalized() {
        if (aesEncryption == null) {
            throw new SymmetricEncryptionException('Symmetric encryption has not been initialized.')
        }
    }
}
