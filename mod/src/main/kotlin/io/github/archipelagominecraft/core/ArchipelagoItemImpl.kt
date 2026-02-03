package io.github.archipelagominecraft.core

import io.github.archipelagominecraft.core.api.ArchipelagoSlot
import io.github.archipelagominecraft.core.api.items.ArchipelagoItemReceivedListener
import io.github.archipelagominecraft.core.api.items.ArchipelagoItemView
import java.util.Collections

@JvmInline
value class APItemID(val value: Int)

class ArchipelagoItemImpl(val id: APItemID, val client: ArchipelagoClient) : ArchipelagoItemView {

    init {
        client.registerItemListener { id, slot ->
            if(this.id == id){
                listeners.forEach { it.onItemReceived(slot) }
            }
        }
    }

    override fun hasSlotReceivedItem(slot: ArchipelagoSlot): Boolean =
        client.wasItemReceivedForSlot(slot, id)

    //synchronized just in case
    private val listeners = Collections.synchronizedSet(mutableSetOf<ArchipelagoItemReceivedListener>())
    override fun registerOnItemReceivedListener(listener: ArchipelagoItemReceivedListener) {
        listeners.add(listener)
    }

    override fun unregisterOnItemReceivedListener(listener: ArchipelagoItemReceivedListener) {
        listeners.remove(listener)
    }

}
