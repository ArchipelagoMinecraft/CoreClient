package io.github.archipelagominecraft.plugin

import org.gradle.api.Action
import org.gradle.api.NamedDomainObjectSet
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import javax.inject.Inject

abstract class BuildMultiversionExtension @Inject constructor(objects: ObjectFactory) {
    /**
     * Run configurations to create
     */
    abstract val runs: NamedDomainObjectSet<RunConfigurationData>

    /**
     * Should default "client" and "server" runs be created ?
     * Note that fabric loom always creates "client" and "server" configurations regardless of this setting
     */
    abstract val createDefaultRuns: Property<Boolean>
    fun runs(action: Action<in NamedDomainObjectSet<RunConfigurationData>>) = action.execute(runs)

    init {
        createDefaultRuns.convention(true)
    }

    /**
     * The folder containing .properties files to load for each minecraft version
     */

    abstract val versionPropertiesFolder: DirectoryProperty


}
