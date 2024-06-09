package breadmod.datagen.toolgun

import breadmod.ModMain
import breadmod.ModMain.modTranslatable
import breadmod.datagen.toolgun.mode.ToolgunRemoverMode
import com.mojang.blaze3d.platform.InputConstants
import net.minecraft.client.KeyMapping
import net.minecraft.data.PackOutput

class ModToolgunModeProvider(
    output: PackOutput
): BreadModToolgunModeProvider(output, ModMain.ID) {
    override fun addModes() {
        addMode(
            "remover",
            modTranslatable("toolgun", "mode", "displayName", "remover"),
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
        addMode(
            "creator",
            modTranslatable("toolgun", "mode", "displayName", "creator"),
            modTranslatable("toolgun", "mode", "tooltip", "creator"),
            listOf(
                KeyMapping(
                    "ichiban2",
                    InputConstants.KEY_P,
                    "n/a"
                )
            ),
            ToolgunRemoverMode::class.java
        )
    }
}