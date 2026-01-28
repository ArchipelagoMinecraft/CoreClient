import com.gtnewhorizons.retrofuturagradle.modutils.ModUtils

plugins {
    kotlin("jvm")
    id("io.github.archipelagominecraft.build-multiversion-conventions")
    id("backport-datafixerupper")
}

group = "io.github.archipelagominecraft"
version = "0.1-SNAPSHOT"


// https://github.com/GTNewHorizons/RetroFuturaGradle/issues/66
configurations.runtimeElements {
    attributes {
        attribute(
            ModUtils.DEOBFUSCATOR_TRANSFORMED,
            true
        )
    }
}



buildMultiversion {
    if(backportDfu.shouldBackport) {
        enableJvmDowngrader = true
    }
}

repositories {
    mavenCentral()
}
dependencies {
    if(backportDfu.shouldBackport){
        downgradeImplementation(backportDfu.dfuDependency)
    }
}


tasks.generateBuildConstants {
    enabled = false
}
