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

import org.encryption.exceptions.SymmetricEncryptionException
import org.encryption.templates.ConfigurationTemplate
import org.encryption.templates.TemplateBinding

/**
 * Configuration generator class.
 *
 * @author Andrew Medeiros
 */
class ConfigGenerator {
    static final String BASE_FILE_NAME = 'symmetric-encryption'

    private final String type
    private final String fileName

    /**
     * Default constructor for the symmetric encryption configuration file generation.
     * @param type Can be one of xml, json or yaml
     */
    ConfigGenerator(String type) {
        this.type     = type
        this.fileName = "${BASE_FILE_NAME}.${type}"
    }

    /**
     * Generates the new symmetric encryption configuration file.
     */
    void generateConfiguration() {
        if (ConfigurationTemplate.SUPPORTED_FILE_TYPES.contains(type)) {
            ConfigurationTemplate template = new ConfigurationTemplate(loadTemplate())
            Writable writeable = template.generate(TemplateBinding.bindingDefaults())

            writeable.writeTo(new FileWriter(fileName))
        } else {
            throw new SymmetricEncryptionException("Unkown configuration file type ${type}.")
        }
    }

    private InputStream loadTemplate() {
        ConfigGenerator.classLoader.getResourceAsStream("${type}-template.${type}")
    }
}
