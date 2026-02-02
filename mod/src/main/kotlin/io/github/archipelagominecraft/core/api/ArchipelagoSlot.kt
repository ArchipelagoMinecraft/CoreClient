package io.github.archipelagominecraft.core.api

import io.github.archipelagominecraft.core.ArchipelagoClient

interface ArchipelagoSlot {

    /**
     * Returns true if the server is playing as a single slot
     */
    val isServerwideSlot: Boolean

    /**
     * Stores archipelago client connection
     */
    val apClient: ArchipelagoClient

    /**
     * Creates ArchipelagoClient object and tries for a connection
      */
    fun tryConnection(address: String?, port: Int, slot: String?, password: String?): Int
}
