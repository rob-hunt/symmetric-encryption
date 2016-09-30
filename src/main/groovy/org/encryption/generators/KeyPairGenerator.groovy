/*
 * Copyright 2016 Andrew Medeiros.
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
package org.encryption.generators

import org.bouncycastle.asn1.ASN1Encodable
import org.bouncycastle.asn1.ASN1Primitive
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo
import org.bouncycastle.jcajce.provider.asymmetric.rsa.KeyPairGeneratorSpi
import org.bouncycastle.util.io.pem.PemObject
import org.bouncycastle.util.io.pem.PemWriter
import org.encryption.RsaEncryption

import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import java.security.KeyPair
import java.security.PrivateKey
import java.security.SecureRandom

/**
 * Generate a key and IV, or a Private PEM.
 * Saving them to disk.
 *
 * @author Andrew Medeiros
 */
class KeyPairGenerator {
    private static final SecureRandom SECURE_RANDOM = new SecureRandom()
    private static final int AES_KEYLENGTH          = 256
    private static final int IV_KEYLENGTH           = AES_KEYLENGTH / 16

    private final String environment
    private final RsaEncryption rsaEncryption

    KeyPairGenerator(RsaEncryption rsaEncryption, String environment) {
        this.environment   = environment
        this.rsaEncryption = rsaEncryption
    }

    /**
     * Generate the AES key and IV and write them to disk.
     */
    void generateKeyPair() {
        generateKey()
        generateIv()
    }

    /**
     * Generate an new AES key and write it to disk.
     */
    private void generateKey() {
        KeyGenerator kg = KeyGenerator.getInstance('AES', 'BC')
        kg.init(AES_KEYLENGTH, SECURE_RANDOM)
        SecretKey sk = kg.generateKey()

        FileOutputStream outputStream = new FileOutputStream(new File("${environment}.key"))
        outputStream.write(rsaEncryption.encrypt(sk.encoded))
    }

    /**
     * Generate a new IV and write it to disk.
     */
    private void generateIv() {
        byte[] keyBytes = new byte[IV_KEYLENGTH]
        SECURE_RANDOM.nextBytes(keyBytes)

        FileOutputStream outputStream = new FileOutputStream(new File("${environment}.iv"))
        byte[] iv = rsaEncryption.encrypt(keyBytes)

        outputStream.write(iv)
    }

    /**
     * Generate a new RSA key pair.
     * @return
     */
    static KeyPair generateRSAKeyPair() {
        new KeyPairGeneratorSpi().generateKeyPair()
    }

    /**
     * Generate an RSA key pair.
     * Just return the private key from the pair as ANS1 bytes.
     * @return Private key
     */
    static byte[] generatePrivateRSAKey() {
        PrivateKey privateKey = generateRSAKeyPair().private
        byte[] privateBytes   = privateKey.encoded

        PrivateKeyInfo privateKeyInfo = PrivateKeyInfo.getInstance(privateBytes)
        ASN1Encodable encodable = privateKeyInfo.parsePrivateKey()
        ASN1Primitive primitive = encodable.toASN1Primitive()

        primitive.encoded
    }

    /**
     * Write the key to a pem string.
     * @param description Header and footer of the pem string.
     * @param key Key we are turning into a pem string.
     * @return String representation of the key as a PEM.
     */
    static String writePem(String description, byte[] key) {
        StringWriter stringWriter = new StringWriter()
        PemWriter pemWriter = new PemWriter(stringWriter)
        PemObject pemObject = new PemObject(description, key)
        pemWriter.writeObject(pemObject)
        pemWriter.flush()
        pemWriter.close()

        stringWriter.toString()
    }
}
