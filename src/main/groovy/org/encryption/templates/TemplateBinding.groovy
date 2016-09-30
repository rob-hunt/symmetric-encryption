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
package org.encryption.templates

import org.encryption.generators.KeyPairGenerator

/**
 * Binding for our templates with some defaults.
 *
 * @author Andrew Medeiros
 */
class TemplateBinding {
    /**
     * Default private PEM message.
     */
    static final String PRIVATE_PEM_MSG = 'RSA PRIVATE KEY'

    /**
     * Default path to the keys on disk.
     */
    static final String KEY_FILE_PATH = '/etc/keys/'

    /**
     * Default cipher for encryption and decryption.
     */
    static final String DEFAULT_CIPHER = 'AES/CBC/PKCS5Padding'

    /**
     * Default encoding.
     */
    static final String DEFAULT_ENCODING = 'base64strict'

    /**
     * Default production key filename.
     */
    static final String DEFAULT_KEY_FILENAME_PRODUCTION = 'production.key'

    /**
     * Default production IV filename.
     */
    static final String DEFAULT_IV_FILENAME_PRODUCTION = 'production.iv'

    /**
     * Default staging key filename.
     */
    static final String DEFAULT_KEY_FILENAME_STAGING = 'staging.key'

    /**
     * Default staging IV filename.
     */
    static final String DEFAULT_IV_FILENAME_STAGING = 'staging.iv'

    /**
     * Default version.
     */
    static final String DEFAULT_VERSION = '1'

    /**
     * Return the map representation of our defaults for templates.
     *
     * @return Map representation of the defaults for our templates.
     */
    static Map bindingDefaults() {
        [
                rsaKeyProduction: KeyPairGenerator.writePem(PRIVATE_PEM_MSG, KeyPairGenerator.generatePrivateRSAKey()),
                rsaKeyStaging:    KeyPairGenerator.writePem(PRIVATE_PEM_MSG, KeyPairGenerator.generatePrivateRSAKey()),

                keyFilenameProduction: DEFAULT_KEY_FILENAME_PRODUCTION,
                ivFilenameProduction:  DEFAULT_IV_FILENAME_PRODUCTION,
                keyFilenameStaging:    DEFAULT_KEY_FILENAME_STAGING,
                ivFilenameStaging:     DEFAULT_IV_FILENAME_STAGING,

                keyFilePath: KEY_FILE_PATH,
                cipherName:  DEFAULT_CIPHER,
                encoding:    DEFAULT_ENCODING,
                version:     DEFAULT_VERSION
        ]
    }
}
