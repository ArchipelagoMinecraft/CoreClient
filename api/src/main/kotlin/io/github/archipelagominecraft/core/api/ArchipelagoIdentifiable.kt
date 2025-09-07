package io.github.archipelagominecraft.api
interface ArchipelagoIdentifiable {
    val namespace: String
    val typeId: String
    val id: String
        get() = this.namespace + ":" + this.typeId
}
