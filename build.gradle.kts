import io.archipelagominecraft.gradle.*

plugins{
    id("org.jetbrains.gradle.plugin.idea-ext")
    id("conditional-modstitch")
}


j52j{
    params {
        prettyPrinting = true
    }
}
stonecutter.apply {

    filters{
        include("resources/**.json")
    }

    consts( //todo fix deprecated
        "fabric" to isFabric,
        "neoforge" to isNeoForge,
        "forge" to isForge,
        "forgeLike" to isForgeLike,
        "legacy" to isLegacy,
        "modern" to isModern
    )
}
