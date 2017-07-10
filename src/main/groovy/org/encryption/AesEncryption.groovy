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

import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec
import java.nio.charset.Charset
import java.security.GeneralSecurityException

/**
 * AES encrypt or decrypt strings.
 *
 * @author Andrew Medeiros
 */
class AesEncryption {
    private static final Object LOCK = new Object()
    private final Cipher          cipher
    private final IvParameterSpec ivKey
    private final SecretKeySpec   secretKey

    /**
     * Default constructor for AesEncryption.
     * @param cipher
     * @param ivParameterSpec
     * @param secretKeySpec
     */
    AesEncryption(Cipher cipher, IvParameterSpec ivParameterSpec, SecretKeySpec secretKeySpec) {
        this.cipher    = cipher
        this.ivKey     = ivParameterSpec
        this.secretKey = secretKeySpec
    }

    /**
     * Constructor is used in development or test.
     * @param key
     */
    AesEncryption(String key, Cipher cipher) {
        Charset cs      = Charset.forName('UTF-8')
        this.ivKey      = new IvParameterSpec(key[0..15].getBytes(cs))
        this.secretKey  = new SecretKeySpec(key[16..-1].getBytes(cs), 'AES')
        this.cipher     = cipher
    }

    /**
     * Decrypt String.
     * @param encryptedString encrypted String to decrypt
     * @return decrypted String
     * @throws GeneralSecurityException
     */
    String decrypt(String encryptedString) throws GeneralSecurityException  {
        def decrypted

        synchronized (LOCK) {
            cipher.init(Cipher.DECRYPT_MODE, secretKey, ivKey)
            decrypted = new String(cipher.doFinal(encryptedString.decodeBase64()))
        }

        decrypted
    }

    /**
     * Encrypt String.
     * @param plainText to encrypt
     * @return Returns a String of the encrypted text
     * @throws GeneralSecurityException
     */
    String encrypt(String plainText) throws GeneralSecurityException {
        def encrypted

        synchronized (LOCK) {
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivKey)
            encrypted = cipher.doFinal(plainText.bytes).encodeBase64().toString()
        }

        encrypted
    }
}
