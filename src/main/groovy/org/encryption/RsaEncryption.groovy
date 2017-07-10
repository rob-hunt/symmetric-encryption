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

import org.bouncycastle.openssl.PEMKeyPair
import org.bouncycastle.openssl.PEMParser
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter

import javax.crypto.Cipher
import java.security.GeneralSecurityException
import java.security.KeyPair
import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey

/**
 * RSA encryption class to decrypt or encrypt the <i>Symmetric Encryption</i> keys.
 *
 * @author Andrew Medeiros
 */
class RsaEncryption {
    private static final Object LOCK     = new Object()
    private static final String PROVIDER = 'BC'
    private final Cipher  pkCipher
    private RSAPrivateKey privateKey
    private RSAPublicKey  publicKey

    /**
     * Default constructor.
     */
    RsaEncryption() {
        this.pkCipher = Cipher.getInstance('RSA/ECB/PKCS1Padding', PROVIDER)
    }

    /**
     * Loads a private key and generates the public key from the private key.
     * @param privateKeyString Private key to be loaded
     * @throws IOException IO exception
     * @throws GeneralSecurityException
     */
    RsaEncryption withKey(String privateKeyString)  throws IOException, GeneralSecurityException {
        PEMParser pemParser   = new PEMParser(new StringReader(privateKeyString))
        PEMKeyPair pemKeyPair = (PEMKeyPair) pemParser.readObject()
        pemParser.close()
        JcaPEMKeyConverter converter = new JcaPEMKeyConverter().setProvider(PROVIDER)
        KeyPair keyPair              = converter.getKeyPair(pemKeyPair)
        privateKey = (RSAPrivateKey) keyPair.private
        publicKey  = (RSAPublicKey)  keyPair.public

        this
    }

    /**
     * RSA encrypt plain bytes
     * @param plainBytes
     * @return
     */
    byte[] encrypt(byte[] plainBytes) {
        encryptBytes(plainBytes)
    }

    /**
     * Decrypt RSA encrypted file contents
     * @param encryptedFile The file containing the RSA encrypted text
     * @return The decrypted bytes
     */
    byte[] decrypt(File encryptedFile) {
        decryptBytes(encryptedFile.bytes)
    }

    private byte[] decryptBytes(byte[] encryptedBytes) throws GeneralSecurityException {
        def decryptedBytes
        synchronized (LOCK) {
            pkCipher.init(Cipher.DECRYPT_MODE, privateKey)
            decryptedBytes = pkCipher.doFinal(encryptedBytes)
        }

        decryptedBytes
    }

    private byte[] encryptBytes(byte[] plainBytes) throws GeneralSecurityException {
        def encryptedBytes

        synchronized (LOCK) {
            pkCipher.init(Cipher.ENCRYPT_MODE, publicKey)
            encryptedBytes = pkCipher.doFinal(plainBytes)
        }

        encryptedBytes
    }
}
