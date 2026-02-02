package io.github.archipelagominecraft.core

import io.github.archipelagomw.Client

class ArchipelagoClient(var gameName: String) : Client() {

    override fun onError(ex: Exception?) {
        TODO("Not yet implemented")
    }

    override fun onClose(Reason: String?, attemptingReconnect: Int) {
        TODO("Not yet implemented")
    }
}