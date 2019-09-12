/*
 * Copyright 2019 the original author or authors.
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

package org.gradle.kotlin.dsl.tooling.models;

import java.io.File;
import java.util.Map;


/**
 * Kotlin DSL scripts model.
 *
 * Can only be requested on the root project, the builder will throw otherwise.
 *
 * Requires the <code>prepareKotlinBuildScriptModel</code> task to be executed before building the model.
 *
 * The set of scripts must be provided as a Gradle property named <code>org.gradle.kotlin.dsl.provider.scripts</code>,
 * as a list of absolute paths separated by <code>|</code>. See {@link KotlinDslScriptsModel#SCRIPTS_PROPERTY_NAME}.
 *
 * Optionally, the Gradle Kotlin DSL script provider can run in a lenient mode, ignoring, collecting and returning exceptions
 * in the built model by providing the system property <code>-Dorg.gradle.kotlin.dsl.provider.mode=classpath</code>.
 *
 * Optionally, an identifier can be provided as a Gradle property named <code>org.gradle.kotlin.dsl.provider.cid</code>,
 * it can then be used to correlate Gradle and TAPI client log statements.
 */
public interface KotlinDslScriptsModel {

    String SCRIPTS_PROPERTY_NAME = "org.gradle.kotlin.dsl.provider.scripts";

    /**
     * Script models by file.
     */
    Map<File, KotlinBuildScriptModel> getScriptModels();
}
