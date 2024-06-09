package breadmod.datagen.tool_gun

import breadmod.ModMain
import breadmod.ModMain.modTranslatable
import breadmod.datagen.tool_gun.mode.ToolGunCreatorMode
import breadmod.datagen.tool_gun.mode.ToolGunExplodeMode
import breadmod.datagen.tool_gun.mode.ToolGunRemoverMode
import com.mojang.blaze3d.platform.InputConstants
import net.minecraft.data.PackOutput

internal class ModToolGunModeProvider(
    output: PackOutput
): BreadModToolGunModeProvider(output, ModMain.ID) {
    override fun addModes() {
        addMode(
            "remover",
            modTranslatable(TOOL_GUN_DEF, "mode", "display_name", "remover"),
            modTranslatable(TOOL_GUN_DEF, "mode", "tooltip", "remover"),
            listOf(
                Control(
                    "$TOOL_GUN_DEF.${ModMain.ID}.mode.controls.name.remover.rmb",
                    "$TOOL_GUN_DEF.${ModMain.ID}.mode.controls.category.remover.rmb",
                    modTranslatable(TOOL_GUN_DEF, "mode", "remover", "rmb"),
                    InputConstants.Type.MOUSE.getOrCreate(InputConstants.MOUSE_BUTTON_RIGHT)
                )
            ),
            ToolGunRemoverMode::class.java
        )

        addMode(
            "creator",
            modTranslatable(TOOL_GUN_DEF, "mode", "display_name", "creator"),
            modTranslatable(TOOL_GUN_DEF, "mode", "tooltip", "creator"),
            listOf(
                Control(
                    "$TOOL_GUN_DEF.${ModMain.ID}.mode.controls.name.creator.rmb",
                    "$TOOL_GUN_DEF.${ModMain.ID}.mode.controls.category.creator.rmb",
                    modTranslatable(TOOL_GUN_DEF, "mode", "creator", "rmb"),
                    InputConstants.Type.MOUSE.getOrCreate(InputConstants.MOUSE_BUTTON_RIGHT)
                )
            ),
            ToolGunCreatorMode::class.java
        )

        addMode(
            "explode",
            modTranslatable(TOOL_GUN_DEF, "mode", "display_name", "explode"),
            modTranslatable(TOOL_GUN_DEF, "mode", "tooltip", "explode"),
            listOf(
                Control(
                    "$TOOL_GUN_DEF.${ModMain.ID}.mode.controls.name.explode.rmb",
                    "$TOOL_GUN_DEF.${ModMain.ID}.mode.controls.category.explode.rmb",
                    modTranslatable(TOOL_GUN_DEF, "mode", "explode", "rmb"),
                    InputConstants.Type.MOUSE.getOrCreate(InputConstants.MOUSE_BUTTON_RIGHT)
                ),
                Control(
                    "$TOOL_GUN_DEF.${ModMain.ID}.mode.controls.name.explode.rmb",
                    "$TOOL_GUN_DEF.${ModMain.ID}.mode.controls.category.explode.rmb",
                    modTranslatable(TOOL_GUN_DEF, "mode", "explode", "rmb"),
                    InputConstants.Type.MOUSE.getOrCreate(InputConstants.MOUSE_BUTTON_RIGHT)
                )
            ),
            ToolGunExplodeMode::class.java
        )
    }
}