package org.bread_experts_group.breadmod.datagen.tool_gun

import net.minecraft.data.PackOutput
import org.bread_experts_group.breadmod.BreadMod

internal class ModToolGunModeProvider(
    output: PackOutput
) : ToolGunModeProvider(output, BreadMod.ID) {
    companion object {
//        val SCREEN_CONTROL = Control(
//            "screen",
//            "${TOOL_GUN_DEF}.${BreadMod.ID}.mode.controls.name.creator.r",
//            "$TOOL_GUN_DEF.${BreadMod.ID}.mode.controls.category.creator",
//            modTranslatable(TOOL_GUN_DEF, "mode", "key_tooltip", "creator", "r"),
//            InputConstants.Type.KEYSYM.getOrCreate(InputConstants.KEY_R).name
//        )
    }

    override fun addModes() {
//        addMode(
//            "remover",
//            modTranslatable(TOOL_GUN_DEF, "mode", "display_name", "remover"),
//            modTranslatable(TOOL_GUN_DEF, "mode", "tooltip", "remover"),
//            listOf(
//                Control(
//                    "use",
//                    "$TOOL_GUN_DEF.${BreadMod.ID}.mode.controls.name.remover.rmb",
//                    "$TOOL_GUN_DEF.${BreadMod.ID}.mode.controls.category.remover",
//                    modTranslatable(TOOL_GUN_DEF, "mode", "key_tooltip", "remover", "rmb"),
//                    InputConstants.Type.MOUSE.getOrCreate(InputConstants.MOUSE_BUTTON_RIGHT).name
//                )
//            ),
//            ToolGunRemoverMode::class.java
//        )
//
//        addMode(
//            "creator",
//            modTranslatable(TOOL_GUN_DEF, "mode", "display_name", "creator"),
//            modTranslatable(TOOL_GUN_DEF, "mode", "tooltip", "creator"),
//            listOf(
//                Control(
//                    "use",
//                    "$TOOL_GUN_DEF.${BreadMod.ID}.mode.controls.name.creator.rmb",
//                    "$TOOL_GUN_DEF.${BreadMod.ID}.mode.controls.category.creator",
//                    modTranslatable(TOOL_GUN_DEF, "mode", "key_tooltip", "creator", "rmb"),
//                    InputConstants.Type.MOUSE.getOrCreate(InputConstants.MOUSE_BUTTON_RIGHT).name
//                ),
//                SCREEN_CONTROL
//            ),
//            ToolGunCreatorMode::class.java
//        )
//
//        addMode(
//            "power",
//            modTranslatable(TOOL_GUN_DEF, "mode", "display_name", "power"),
//            modTranslatable(TOOL_GUN_DEF, "mode", "tooltip", "power"),
//            listOf(
//                Control(
//                    "use",
//                    "$TOOL_GUN_DEF.${BreadMod.ID}.mode.controls.name.power.rmb",
//                    "$TOOL_GUN_DEF.${BreadMod.ID}.mode.controls.category.power",
//                    modTranslatable(TOOL_GUN_DEF, "mode", "key_tooltip", "power", "rmb"),
//                    InputConstants.Type.MOUSE.getOrCreate(InputConstants.MOUSE_BUTTON_RIGHT).name
//                )
//            ),
//            ToolGunPowerMode::class.java
//        )
//
//        addMode(
//            "explode",
//            modTranslatable(TOOL_GUN_DEF, "mode", "display_name", "explode"),
//            modTranslatable(TOOL_GUN_DEF, "mode", "tooltip", "explode"),
//            listOf(
//                Control(
//                    "use",
//                    "$TOOL_GUN_DEF.${BreadMod.ID}.mode.controls.name.explode.rmb",
//                    "$TOOL_GUN_DEF.${BreadMod.ID}.mode.controls.category.explode",
//                    modTranslatable(TOOL_GUN_DEF, "mode", "key_tooltip", "explode", "rmb"),
//                    InputConstants.Type.MOUSE.getOrCreate(InputConstants.MOUSE_BUTTON_RIGHT).name
//                ),
//                Control(
//                    "fluid_toggle",
//                    "$TOOL_GUN_DEF.${BreadMod.ID}.mode.controls.name.explode.mmb",
//                    "$TOOL_GUN_DEF.${BreadMod.ID}.mode.controls.category.explode",
//                    modTranslatable(TOOL_GUN_DEF, "mode", "key_tooltip", "explode", "mmb", "off"),
//                    InputConstants.Type.MOUSE.getOrCreate(InputConstants.MOUSE_BUTTON_MIDDLE).name
//                )
//            ),
//            ToolGunExplodeMode::class.java
//        )
    }
}