package bread.mod.breadmod.command.client

import bread.mod.breadmod.command.CommandArguments.BooleanArgument
import com.mojang.brigadier.Command
import com.mojang.brigadier.builder.ArgumentBuilder
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import dev.architectury.event.events.client.ClientCommandRegistrationEvent.ClientCommandSourceStack
import dev.architectury.event.events.client.ClientCommandRegistrationEvent.argument

object AltToolGunModelCommand {
    // todo replace with config
    var useAltModel = false

    fun register(): ArgumentBuilder<ClientCommandSourceStack, *> =
        LiteralArgumentBuilder.literal<ClientCommandSourceStack>("altToolgunModel")
            .then(argument("value", BooleanArgument())
                .executes { value ->
                    val arg = value.getArgument("value", Boolean::class.java)
//                    ModConfiguration.CLIENT.ALT_TOOLGUN_MODEL.set(arg)
                    useAltModel = arg
                    return@executes Command.SINGLE_SUCCESS
                }
            )
}