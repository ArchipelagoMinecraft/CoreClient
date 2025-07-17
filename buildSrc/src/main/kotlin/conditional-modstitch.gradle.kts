import dev.isxander.controlify.stonecutter
plugins{
    id("common-conventions")
}

val is_modern = stonecutter.eval(stonecutter.current.version, ">1.12.2")
if(stonecutter.current.version != "1.12.2")
    plugins.apply("modstitch-conventions")
else
    plugins.apply("forgegradle-conventions")
