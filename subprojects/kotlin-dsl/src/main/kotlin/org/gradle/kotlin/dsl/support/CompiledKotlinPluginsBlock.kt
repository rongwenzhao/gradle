/*
 * Copyright 2016 the original author or authors.
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

package org.gradle.kotlin.dsl.support

import org.gradle.api.Project
import org.gradle.api.initialization.dsl.ScriptHandler

import org.gradle.kotlin.dsl.ScriptHandlerScope

import org.gradle.plugin.use.PluginDependenciesSpec


/**
 * Base class for `plugins` block evaluation.
 */
abstract class CompiledKotlinPluginsBlock(val pluginDependencies: PluginDependenciesSpec) {

    inline fun plugins(configuration: PluginDependenciesSpec.() -> Unit) {
        pluginDependencies.configuration()
    }
}


/**
 * Base class for the evaluation of a `buildscript` block followed by a `plugins` block.
 */
@ImplicitReceiver(Project::class)
abstract class CompiledKotlinBuildscriptAndPluginsBlock(
    private val host: KotlinScriptHost<Project>,
    val pluginDependencies: PluginDependenciesSpec
) {

    /**
     * The [ScriptHandler] for this script.
     */
    val buildscript: ScriptHandler
        get() = host.scriptHandler

    /**
     * Configures the build script classpath for this project.
     *
     * @see [Project.buildscript]
     */
    fun buildscript(block: ScriptHandlerScope.() -> Unit) {
        buildscript.configureWith(block)
    }

    inline fun plugins(configuration: PluginDependenciesSpec.() -> Unit) {
        pluginDependencies.configuration()
    }
}
