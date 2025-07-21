package io.github.archipelagominecraft.core.vanilla.events

class ArchipelagoEvent<T>(
    private val customRegistration: (((T) -> Unit) -> Unit)? = null
) {
    private val listeners = mutableListOf<(T) -> Unit>()

    fun registerListener(listener: (T) -> Unit) {
        if (customRegistration != null)
            customRegistration(listener)
        else listeners.add(listener)
    }

    fun trigger(event: T) {
        for (listener in listeners) {
            listener(event)
        }
    }
}
