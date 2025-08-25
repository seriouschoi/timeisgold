/*
 * Copyright 2023 The Android Open Source Project
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package software.seriouschoi.timeisgold

import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.getByType
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinAndroidProjectExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension

val Project.libs: VersionCatalog
    get(): VersionCatalog = extensions.getByType<VersionCatalogsExtension>().named("libs")

internal fun Project.setJvmTarget(target: JvmTarget) {
    when (this) {
        is KotlinAndroidProjectExtension -> {
            compilerOptions
        }
        is KotlinJvmProjectExtension -> {
            compilerOptions
        }
        else -> {
            logger.warn("Unsupported project type: ${this.javaClass.simpleName}")
            null
        }
    }?.apply {
        jvmTarget.set(target)
    }
}

internal fun Project.setJvmToolchain(jdkVersion: Int) {
    // 먼저 KotlinAndroidProjectExtension을 찾아봅니다.
    val androidExtension = project.extensions.findByType(KotlinAndroidProjectExtension::class.java)
    if (androidExtension != null) {
        // Android 프로젝트인 경우
        androidExtension.jvmToolchain(jdkVersion)
    } else {
        // Android 프로젝트가 아닌 경우, KotlinJvmProjectExtension을 찾아봅니다.
        project.extensions.findByType(KotlinJvmProjectExtension::class.java)?.apply {
            jvmToolchain(jdkVersion)
        }
    }
}
