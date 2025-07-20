import io.archipelagominecraft.gradle.stonecutter

val is_modern = stonecutter.eval(stonecutter.current.version, ">1.12.2")
if(is_modern)
    plugins.apply("modstitch-conventions")
else
    plugins.apply("forgegradle-conventions")
