package io.github.archipelagominecraft.core

import io.github.archipelagominecraft.core.api.ArchipelagoSlot
import io.github.archipelagominecraft.core.api.locations.ArchipelagoLocationView

@JvmInline
value class APLocationID(val value: Int)

class ArchipelagoLocationImpl(val id: APLocationID, val client: ArchipelagoClient) : ArchipelagoLocationView {
    override fun checkFor(slots: List<ArchipelagoSlot>) =
        slots.forEach { client.markLocationAsCheckedForSlot(it, id) }

    override fun isCheckedFor(slot: ArchipelagoSlot): Boolean =
        client.isLocationCheckedForSlot(slot, id)

}
