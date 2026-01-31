package io.github.archipelagominecraft.buildplugin

import org.gradle.api.file.DirectoryProperty
import org.gradle.api.model.ObjectFactory
import javax.inject.Inject

abstract class BuildMultiversionExtension @Inject constructor(objects: ObjectFactory) {

    /**
     * The folder containing .properties files to load for each minecraft version
     */

    abstract val versionPropertiesFolder: DirectoryProperty

}
