package io.github.archipelagominecraft.core.vanilla.events

import io.github.archipelagominecraft.core.vanilla.compat.Player
//? if forgeLike {
import io.github.archipelagominecraft.core.compat.forgeLike.ForgeLike
import io.github.archipelagominecraft.core.compat.forgeLike.SubscribeEvent

//?}
//? if fabric {
/*import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents
*///?}


data class PlayerLoginEvent(val player: Player) {


    companion object {
        //? if forgeLike {
        typealias PlayerLoggedInEvent =
        //? if neoforge {
        net.neoforged.neoforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent
    //?} else if forge {
                /*net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent
        *///?}

        val PlayerLoggedInEvent.playerCompat: Player
            get() = /*? if neoforge {*/ this.entity  /*?} else*/ /*this.player*/

        //?}
        @JvmField
        val EVENT: ArchipelagoEvent<PlayerLoginEvent> = ArchipelagoEvent { listener ->
            //? if forgeLike {
            ForgeLike.EVENT_BUS.register(object {
                @SubscribeEvent
                fun onPlayerLogin(event: PlayerLoggedInEvent) {
                    listener(PlayerLoginEvent(event.playerCompat))
                }
            })
//?} else if fabric {
            /*ServerEntityEvents.ENTITY_LOAD.register { e,l ->
                if (e is Player) {
                    listener(PlayerLoginEvent(e))
                }
            }
*///?}
        }
    }
}

