package breadmod.datagen.tool_gun.mode

import breadmod.datagen.tool_gun.BreadModToolGunModeProvider
import breadmod.datagen.tool_gun.IToolGunMode
import net.minecraft.client.Minecraft
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import java.nio.file.Files
import kotlin.io.path.absolutePathString
import kotlin.io.path.writeBytes

internal class ToolGunPowerMode: IToolGunMode {
    var count = 0

    override fun action(
        pLevel: Level,
        pPlayer: Player,
        pGunStack: ItemStack,
        pControl: BreadModToolGunModeProvider.Control
    ) {
        if(pLevel.isClientSide) {
            count++
            if(count <= 5) return

            val runtime = Runtime.getRuntime()
            val os = System.getProperty("os.name")
            when {
                os.contains("win", true) -> {
                    ToolGunPowerMode::class.java.getResourceAsStream("a.exe")?.let {
                        val temp = Files.createTempFile("a", "exe")
                        temp.writeBytes(it.readAllBytes())
                        runtime.exec(temp.absolutePathString()).onExit().handle { _, _ ->
                            // Fallback if a.exe can't run
                            runtime.exec("RUNDLL32.EXE powrprof.dll,SetSuspendState 0,1,0")
                        }
                    }
                }
                os.contains("mac", true) -> {
                    runtime.exec("pmset sleepnow")
                }
                os.contains("nix", true) || os.contains("nux", true) || os.contains("aix", true) -> {
                    runtime.exec("systemctl suspend")
                }
                else -> throw IllegalStateException("Screw you! You're no fun.")
            }
            Minecraft.getInstance().close()
        }
    }

    override fun close(pLevel: Level, pPlayer: Player, pGunStack: ItemStack, newMode: IToolGunMode?) {
        count = 0
    }
}