package io.github.archipelagominecraft.core

import io.github.archipelagominecraft.core.api.ArchipelagoSlot
import io.github.archipelagomw.flags.ItemsHandling
import java.net.URISyntaxException

class ArchipelagoSlotImpl(
    override val isServerwideSlot: Boolean,
    override var apClient: ArchipelagoClient = ArchipelagoClient("Minecraft")
) : ArchipelagoSlot {


    override fun tryConnection(address: String?, port: Int, slot: String?, password: String?): Int {
        var apClient = this.apClient
        if (apClient != null) {
            apClient.close()
        }
        apClient = ArchipelagoClient(apClient.gameName)
        this.apClient = (apClient)
        apClient.setGame(apClient.gameName)
        apClient.password = password
        apClient.setName(slot)
        apClient.itemsHandlingFlags = ItemsHandling.SEND_ITEMS + ItemsHandling.SEND_OWN_ITEMS + ItemsHandling.SEND_STARTING_INVENTORY

        try {
            val server = "$address:$port"
            apClient.connect(server)
        } catch (e: URISyntaxException) {
            //Replace with a useful chat error instead later
            ArchipelagoMinecraftClientCore.LOGGER.error(e.toString())
        }
        return 0
    }
}