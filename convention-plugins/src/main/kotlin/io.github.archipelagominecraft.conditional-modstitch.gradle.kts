import io.archipelagominecraft.gradle.loader
import io.archipelagominecraft.gradle.stonecutter

val is_1_12_2 = stonecutter.eval(stonecutter.current.version, "=1.12.2")
val is_1_7_10 = stonecutter.eval(stonecutter.current.version, "=1.7.10")
val loaderSupportedByRfg = loader == LoaderConstants.FORGE || loader == LoaderConstants.VANILLA
if((is_1_12_2 || is_1_7_10)) {
    plugins.apply("io.github.archipelagominecraft.forgegradle-conventions")
} else if (stonecutter.eval(stonecutter.current.version, ">1.12.2")){
    plugins.apply("io.github.archipelagominecraft.modstitch-conventions")
} else {
    throw GradleException("Unsupported Minecraft version and/or loader for conditional-modstitch plugin: ${stonecutter.current.version} : $loader")
}
