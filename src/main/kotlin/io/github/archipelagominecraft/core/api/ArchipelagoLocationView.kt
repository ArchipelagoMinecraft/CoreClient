package io.github.archipelagominecraft.core.api

import io.github.archipelagominecraft.core.compat.Player


@Suppress("unused")
interface ArchipelagoLocationView<T : ArchipelagoLocationType<T, *>> {
    fun checkFor(player: Player)
    fun isCheckedFor(player: Player): Boolean
}
