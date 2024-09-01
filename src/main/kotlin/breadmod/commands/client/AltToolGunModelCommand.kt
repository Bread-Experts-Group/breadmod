package breadmod.commands.client

import breadmod.commands.CommandArguments.BooleanArgument
import breadmod.registry.ModConfiguration
import com.mojang.brigadier.Command
import com.mojang.brigadier.builder.ArgumentBuilder
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands

internal object AltToolGunModelCommand {
    fun register(): ArgumentBuilder<CommandSourceStack, *> =
        Commands.literal("altToolgunModel")
            .then(Commands.argument("value", BooleanArgument())
                .executes { value ->
                    val arg = value.getArgument("value", Boolean::class.java)
                    ModConfiguration.CLIENT.ALT_TOOLGUN_MODEL.set(arg)
                    return@executes Command.SINGLE_SUCCESS
                }
            )
}