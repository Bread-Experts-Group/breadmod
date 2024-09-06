package bread.mod.breadmod.registry

import dev.architectury.event.events.common.CommandRegistrationEvent

object CommonEvents {

    fun registerCommands() =
        CommandRegistrationEvent.EVENT.register { dispatcher, registry, selection ->

        }
}