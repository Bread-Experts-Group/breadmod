package breadmod.datagen.toolgun

import breadmod.ModMain
import breadmod.ModMain.modTranslatable
import breadmod.datagen.toolgun.mode.ToolgunRemoverMode
import net.minecraft.data.PackOutput

class ModToolgunModeProvider(
    output: PackOutput
): BreadModToolgunModeProvider(output, ModMain.ID) {
    override fun addModes() {
        addMode(
            "remover",
            modTranslatable("toolgun", "mode", "displayName", "remover"),
            modTranslatable("toolgun", "mode", "tooltip", "remover"),
            listOf(), ToolgunRemoverMode::class.java
        )
    }
}