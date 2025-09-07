package io.github.archipelagominecraft.core

import com.mojang.serialization.Codec
import com.mojang.serialization.DataResult
import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import io.github.archipelagominecraft.api.ArchipelagoType
import io.github.archipelagominecraft.api.items.ArchipelagoItemType
import io.github.archipelagominecraft.api.locations.ArchipelagoLocationType

data class ArchipelagoItemDefinition(
    val handlers: List<ArchipelagoItemHandler<*, *>>
) {
    companion object {
        fun codec(typeMap: Map<String, ArchipelagoItemType<*>>): Codec<ArchipelagoItemDefinition> =
            RecordCodecBuilder.create { builder ->
                builder.group(
                    Codec.list(ArchipelagoItemHandler.codec(typeMap))
                        .fieldOf("handlers")
                        .forGetter { it.handlers }
                ).apply(builder, ::ArchipelagoItemDefinition)
            }
    }
}

data class ArchipelagoLocationDefinition(
    val id: Int,
    val handler: ArchipelagoLocationHandler<*, *>
) {
    companion object {
        fun codec(typeMap: Map<String, ArchipelagoLocationType<*>>): Codec<ArchipelagoLocationDefinition> =
            RecordCodecBuilder.create { builder ->
                builder.group(
                    Codec.INT.fieldOf("id").forGetter { it.id },
                    ArchipelagoLocationHandler.codec(typeMap)
                        .fieldOf("handler")
                        .forGetter { it.handler }
                ).apply(builder, ::ArchipelagoLocationDefinition)
            }
    }
}


abstract class ArchipelagoHandler<T : ArchipelagoType<D>, D> {
    abstract val type: T
    abstract val data: D

    companion object {

        @JvmStatic
        protected fun <H : ArchipelagoHandler<*, *>> codec(
            typesMap: Map<String, ArchipelagoType<*>>,
            makeHandler: (ArchipelagoType<*>, Any) -> H
        ): MapCodec<H> =
            Codec.STRING.comapFlatMap({
                if (typesMap.containsKey(it)) {
                    DataResult.success(it)
                } else {
                    //? if <=1.12.2 {
                    /*DataResult.error("Unknown type: $it")
                    *///?} else {
                    DataResult.error { "Unknown type: $it" }
                    //?}
                }
            }) { it }.dispatchMap<H>({ it.type.id }) { b ->
                @Suppress("UNCHECKED_CAST")
                val type = typesMap[b]!! as ArchipelagoType<Any>
                val xmap: MapCodec<H> = type.dataCodec
                    .fieldOf("data")
                    .xmap(
                        { makeHandler(type, it) },
                        { it.data }
                    )
                //? if <=1.12.2 {
                /*xmap.codec()
                *///?} else {
                xmap
                //?}
            }
    }
}


data class ArchipelagoItemHandler<T : ArchipelagoItemType<D>, D>(
    override val type: T,
    override val data: D
) : ArchipelagoHandler<T, D>(
) {
    companion object {
        @JvmStatic
        fun codec(
            typesMap: Map<String, ArchipelagoItemType<*>>,
        ): Codec<ArchipelagoItemHandler<*, *>> =
            codec<ArchipelagoItemHandler<*, *>>(typesMap) { type, any ->
                create<ArchipelagoItemType<Any>>(type, any)
            }.codec()

        private fun <T : ArchipelagoItemType<Any>> create(
            type: ArchipelagoType<*>,
            data: Any
        ): ArchipelagoItemHandler<T, *> =
            @Suppress("UNCHECKED_CAST")
            ArchipelagoItemHandler(
                type as T, data
            )
    }
}

data class ArchipelagoLocationHandler<T : ArchipelagoLocationType<D>, D>(
    override val type: T,
    override val data: D
) : ArchipelagoHandler<T, D>() {
    companion object {
        @JvmStatic
        fun codec(
            typesMap: Map<String, ArchipelagoLocationType<*>>,
        ): Codec<ArchipelagoLocationHandler<*, *>> =
            codec<ArchipelagoLocationHandler<*, *>>(typesMap)
            { type, data -> create<ArchipelagoLocationType<Any>>(type, data) }
                .codec()

        private fun <T : ArchipelagoLocationType<Any>> create(
            type: ArchipelagoType<*>,
            data: Any
        ): ArchipelagoLocationHandler<T, *> =
            @Suppress("UNCHECKED_CAST")
            ArchipelagoLocationHandler(
                type as T, data
            )
    }
}


data class ArchipelagoDefinitions(
    val items: Map<Int, ArchipelagoItemDefinition>,
    val locations: Map<Int, ArchipelagoLocationDefinition>
) {
    companion object {
        @JvmStatic
        fun codec(
            itemTypes: Map<String, ArchipelagoItemType<*>>,
            locationTypes: Map<String, ArchipelagoLocationType<*>>
        ): Codec<ArchipelagoDefinitions> =
            RecordCodecBuilder.create { builder ->
                builder.group(
                    Codec.unboundedMap(
                        Codec.STRING.xmap({it.toInt()}) {it.toString()},
                        ArchipelagoItemDefinition.codec(itemTypes)
                    ).fieldOf("items").forGetter { it.items },
                    Codec.unboundedMap(
                        Codec.STRING.xmap({it.toInt()}) {it.toString()},
                        ArchipelagoLocationDefinition.codec(locationTypes)
                    ).fieldOf("locations").forGetter { it.locations }
                ).apply(builder, ::ArchipelagoDefinitions)
            }

    }
}
