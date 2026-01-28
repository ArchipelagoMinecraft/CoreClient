import org.gradle.internal.impldep.org.apache.maven.model.PluginConfiguration
import org.gradle.kotlin.dsl.kotlin

plugins{
    `kotlin-dsl`
}


repositories{
    mavenCentral()
    gradlePluginPortal()
}




dependencies {
    fun plugin(id: String, version: String) = "$id:$id.gradle.plugin:$version"
    implementation(plugin("dev.kikugie.stonecutter", "0.8.3"))
}
