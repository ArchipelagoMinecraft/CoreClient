package io.github.archipelagominecraft.core.compat

import com.mojang.serialization.Codec
import com.mojang.serialization.DataResult

interface CompatCodecs {
    companion object {
        val RESOURCE_LOCATION: Codec<ResourceLocation> =
            //? if >1.12.2 {
            ResourceLocation.CODEC
//?} else {
        /*Codec.STRING
        .comapFlatMap<ResourceLocation>({ s: String ->
            val parts: List<String> = s.split(":".toRegex())
            when {
                parts.size != 2 -> {
                    DataResult.error("Invalid ResourceLocation, more than one colon : $s")
                }

                parts[0].isEmpty() || parts[1].isEmpty() -> {
                   DataResult.error("Invalid ResourceLocation, empty namespace or path: $s")
                }

                parts[0].lowercase() != parts[0] || parts[1].lowercase() != parts[1]
                    -> {
                    DataResult.error("Invalid ResourceLocation, namespace or path not lowercase: $s")
                }
                else ->
                    DataResult.success(ResourceLocation(parts[0], parts[1]))
            }
        }) { it.toString() }
    *///?}
    }
}

