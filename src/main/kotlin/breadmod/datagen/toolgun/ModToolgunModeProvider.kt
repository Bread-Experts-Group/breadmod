package breadmod.datagen.toolgun

import breadmod.ModMain
import breadmod.ModMain.modTranslatable
import breadmod.datagen.toolgun.mode.ToolgunRemoverMode
import com.mojang.blaze3d.platform.InputConstants
import net.minecraft.client.KeyMapping
import net.minecraft.data.PackOutput
import org.jetbrains.annotations.ApiStatus.Internal

@Internal
internal class ModToolgunModeProvider(
    output: PackOutput
): BreadModToolgunModeProvider(output, ModMain.ID) {
    override fun addModes() {
        addMode(
            "remover",
            modTranslatable("toolgun", "mode", "display_name", "remover"),
            modTranslatable("toolgun", "mode", "tooltip", "remover"),
            listOf(
                KeyMapping(
                    "ichiban",
                    InputConstants.KEY_P,
                    "n/a"
                )
            ),
            ToolgunRemoverMode::class.java
        )
    }
}