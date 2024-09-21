package bread.mod.breadmod.command.client

import bread.mod.breadmod.command.CommandArguments.BooleanArgument
import bread.mod.breadmod.registry.config.ClientConfig
import bread.mod.breadmod.registry.config.ClientConfig.ALT_TOOLGUN_MODEL
import com.mojang.brigadier.Command
import com.mojang.brigadier.builder.ArgumentBuilder
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import dev.architectury.event.events.client.ClientCommandRegistrationEvent.ClientCommandSourceStack
import dev.architectury.event.events.client.ClientCommandRegistrationEvent.argument

object AltToolGunModelCommand {
    fun register(): ArgumentBuilder<ClientCommandSourceStack, *> =
        LiteralArgumentBuilder.literal<ClientCommandSourceStack>("altToolgunModel")
            .then(argument("value", BooleanArgument())
                .executes { value ->
                    val arg = value.getArgument("value", Boolean::class.java)
                    ClientConfig.setConfigValue(ALT_TOOLGUN_MODEL, arg)
                    return@executes Command.SINGLE_SUCCESS
                }
            )
}