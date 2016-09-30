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
package org.encryption

/**
 * Parse the arguments passed to the command line runner.
 *
 * @author Andrew Medeiros
 */
@Singleton(strict = false)
class CliParser {
    @Delegate private final CliBuilder cliBuilder

    private CliParser() {
        this.cliBuilder = new CliBuilder(
                usage: 'java -cp SymmetricEncryption-1.0-SNAPSHOT-all.jar org.encryption.SymmetricEncryption [OPTIONS]',
                header: 'Options:')
        cliBuilder.help('Print this message.')
        cliBuilder.env(args: 1, argName: 'environment', 'Generate keys for given environment.')
        cliBuilder.config(args: 1, argName: 'type', 'Generate the symmetric encryption configuration. ' +
                'Type: json, xml, yaml. Note: Running this will generate a new configuration file every time!')
    }
}
