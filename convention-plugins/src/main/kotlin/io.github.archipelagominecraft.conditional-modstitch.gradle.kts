import io.archipelagominecraft.gradle.stonecutter

val is_modern = stonecutter.eval(stonecutter.current.version, ">1.12.2")
if(is_modern)
    plugins.apply("io.github.archipelagominecraft.modstitch-conventions")
else
    plugins.apply("io.github.archipelagominecraft.forgegradle-conventions")
