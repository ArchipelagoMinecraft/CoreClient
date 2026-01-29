import com.gtnewhorizons.retrofuturagradle.modutils.ModUtils
import io.github.archipelagominecraft.plugin.configs.retroFuturaGradle

plugins {
    kotlin("jvm")
    id("io.github.archipelagominecraft.build-multiversion-conventions")
    id("backport-datafixerupper")
}

group = "io.github.archipelagominecraft"
version = "0.1-SNAPSHOT"


// https://github.com/GTNewHorizons/RetroFuturaGradle/issues/66
retroFuturaGradle {
    configurations.runtimeElements {
        attributes {
            attribute(
                ModUtils.DEOBFUSCATOR_TRANSFORMED,
                true
            )
        }
    }
}

modstitch {
    retroFuturaGradle {
        if(backportDfu.shouldBackport){
            enableJvmDowngrader = true
        }
    }
}

repositories {
    mavenCentral()
}
dependencies {
    if(backportDfu.shouldBackport){
        //todo find a way to make it work without quotes
        // here it can't because the configuration does't exist when neoforge or fabric are loaded
        backportDfu.dfuDependencies.forEach {
            "modstitchDowngradeImplementation"(it)
        }
    }
}


tasks.generateBuildConstants {
    enabled = false
}
