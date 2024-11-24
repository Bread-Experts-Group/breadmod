package org.bread_experts_group.breadmod.registry.item.actual.tool_gun

import com.mojang.blaze3d.platform.InputConstants
import net.minecraft.client.KeyMapping
import net.minecraft.client.player.LocalPlayer
import net.minecraft.sounds.SoundEvents
import net.minecraft.world.item.ItemStack
import net.neoforged.neoforge.client.settings.KeyConflictContext
import net.neoforged.neoforge.client.settings.KeyModifier
import net.neoforged.neoforge.network.PacketDistributor
import org.bread_experts_group.breadmod.BreadMod
import org.bread_experts_group.breadmod.ClientNeoForgeEventBus.createdMappings
import org.bread_experts_group.breadmod.datagen.tool_gun.ToolGunModeProvider
import org.bread_experts_group.breadmod.datagen.tool_gun.ToolGunModeProvider.Companion.TOOL_GUN_DEF
import org.bread_experts_group.breadmod.network.serverbound.ToolGunConfigurationPacket
import org.bread_experts_group.breadmod.util.modifierMatches

// todo consolidate tool gun control functions and vals to this object
object ToolGunControlLogic {
    val toolGunBindList = mutableMapOf<ToolGunModeProvider.Control, KeyMapping>()

    val changeMode = KeyMapping(
        "controls.${BreadMod.ID}.$TOOL_GUN_DEF.change_mode",
        KeyConflictContext.GUI,
        KeyModifier.SHIFT,
        InputConstants.Type.MOUSE.getOrCreate(InputConstants.MOUSE_BUTTON_RIGHT),
        "controls.${BreadMod.ID}.category.$TOOL_GUN_DEF"
    )

    fun createMappingsForControls(prepared: List<ToolGunModeProvider.Control>): List<KeyMapping> {
        prepared.forEach {
            val mapping = if (it.modifier != "none") {
                KeyMapping(
                    it.nameKey,
                    KeyConflictContext.IN_GAME,
                    KeyModifier.valueFromString(it.modifier),
                    InputConstants.getKey(it.key),
                    it.categoryKey
                )
            } else {
                KeyMapping(
                    it.nameKey,
                    KeyConflictContext.IN_GAME,
                    InputConstants.getKey(it.key),
                    it.categoryKey
                )
            }
            toolGunBindList[it] = mapping
        }
        createdMappings = toolGunBindList.values.toList()
        return createdMappings
    }

    fun handleToolgunInput(
        player: LocalPlayer,
        itemHeld: ToolGunItem, stackHeld: ItemStack,
        key: InputConstants.Key, modifiers: Int,
        early: Boolean
    ): Boolean {
        val currentMode = itemHeld.getCurrentMode(stackHeld)

        if (!early && key == changeMode.key && modifierMatches(modifiers, changeMode.keyModifier)) {
            PacketDistributor.sendToServer(ToolGunConfigurationPacket(true))
            player.playSound(SoundEvents.DISPENSER_FAIL, 1.0f, 1.0f)
            return true
        } else {
            currentMode.keyBinds.forEach {
                toolGunBindList[it]?.let { bind ->
                    if (key == bind.key && modifierMatches(modifiers, bind.keyModifier)) {
                        PacketDistributor.sendToServer(ToolGunConfigurationPacket(false, it, early))

                        val mode = currentMode.mode
                        (if (early) mode::actionEarly else mode::action)(player.level(), player, stackHeld, it)
                        return true
                    }
                }
            }
        }
        return false
    }
}