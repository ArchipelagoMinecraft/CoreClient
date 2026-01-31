package io.github.archipelagominecraft.core.api
interface ArchipelagoIdentifiable {
    val namespace: String
    val typeId: String
    val id: String
        get() = this.namespace + ":" + this.typeId
}
