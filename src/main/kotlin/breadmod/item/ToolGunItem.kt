package breadmod.item

import breadmod.ClientModEventBus.toolGunBindList
import breadmod.ModMain
import breadmod.ModMain.modTranslatable
import breadmod.datagen.tool_gun.BreadModToolGunModeProvider.Companion.TOOL_GUN_DEF
import breadmod.datagen.tool_gun.ModToolGunModeDataLoader
import breadmod.item.rendering.ToolGunItemRenderer
import breadmod.network.PacketHandler.NETWORK
import breadmod.network.ToolGunPacket
import breadmod.registry.item.IRegisterSpecialCreativeTab
import breadmod.registry.screen.ModCreativeTabs
import breadmod.util.MapIterator
import com.mojang.blaze3d.platform.InputConstants
import net.minecraft.ChatFormatting
import net.minecraft.client.KeyMapping
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.chat.Component
import net.minecraft.sounds.SoundEvents
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.TooltipFlag
import net.minecraft.world.level.Level
import net.minecraftforge.client.extensions.common.IClientItemExtensions
import net.minecraftforge.client.settings.KeyConflictContext
import net.minecraftforge.client.settings.KeyModifier
import net.minecraftforge.registries.RegistryObject
import java.util.function.Consumer


internal class ToolGunItem: Item(Properties().stacksTo(1)), IRegisterSpecialCreativeTab {
    override fun appendHoverText(
        pStack: ItemStack,
        pLevel: Level?,
        pTooltipComponents: MutableList<Component>,
        pIsAdvanced: TooltipFlag
    ) {
        pTooltipComponents.add(
            modTranslatable("item", TOOL_GUN_DEF, "tooltip", "current_mode")
                .append(getCurrentMode(pStack).displayName.copy().withStyle(ChatFormatting.GOLD))
        )
        pTooltipComponents.add(
            changeMode.translatedKeyMessage.copy().withStyle(ChatFormatting.GREEN).append(modTranslatable("item", TOOL_GUN_DEF, "tooltip", "mode_switch"))
        )
    }

    internal fun ensureCurrentMode(pStack: ItemStack): CompoundTag {
        if(!pStack.orCreateTag.contains(CURRENT_MODE_TAG)) {
            println(ModToolGunModeDataLoader.modes.size)
            val nextNamespace = MapIterator(ModToolGunModeDataLoader.modes).next()
            val nextMode = MapIterator(nextNamespace.value).next()

            val newTagData = CompoundTag().also {
                it.putString(MODE_NAMESPACE_TAG, nextNamespace.key)
                it.putString(MODE_NAME_TAG, nextMode.key)
                it.putInt(NAMESPACE_ITERATOR_STATE_TAG, 0)
                it.putInt(MODE_ITERATOR_STATE_TAG, 0)
            }
            pStack.orCreateTag.put(CURRENT_MODE_TAG, newTagData)
            return newTagData
        }
        return pStack.orCreateTag.getCompound(CURRENT_MODE_TAG)
    }

    internal fun getCurrentMode(pStack: ItemStack) = ensureCurrentMode(pStack).let {
        ModToolGunModeDataLoader.modes[it.getString(MODE_NAMESPACE_TAG)]!![it.getString(MODE_NAME_TAG)]!!
    }

    override val creativeModeTabs: List<RegistryObject<CreativeModeTab>> = listOf(ModCreativeTabs.SPECIALS_TAB)

    override fun inventoryTick(pStack: ItemStack, pLevel: Level, pEntity: Entity, pSlotId: Int, pIsSelected: Boolean) {
        if(pEntity is Player) {
            val currentMode = getCurrentMode(pStack)
            if(pLevel.isClientSide && !pIsSelected) {
                changeMode.consumeClick()
                currentMode.keyBinds.forEach { toolGunBindList[it]?.consumeClick() }
                currentMode.mode.close(pLevel, pEntity, pStack, null)
                return
            }

            if(pLevel.isClientSide && changeMode.consumeClick()) {
                NETWORK.sendToServer(ToolGunPacket(true, pSlotId))
                pEntity.playSound(SoundEvents.DISPENSER_FAIL, 1.0f, 1.0f)
                return
            } else {
                currentMode.mode.open(pLevel, pEntity, pStack, null)
                if(pLevel.isClientSide) {
                    currentMode.keyBinds.forEach {
                        val bind = toolGunBindList[it]
                        if(bind != null && bind.consumeClick()) {
                            NETWORK.sendToServer(ToolGunPacket(false, pSlotId, it))
                            currentMode.mode.action(pLevel, pEntity, pStack, it)
                        }
                    }
                }
            }
        }
    }

    override fun initializeClient(consumer: Consumer<IClientItemExtensions>) = consumer.accept(object : IClientItemExtensions {
        override fun getCustomRenderer(): BlockEntityWithoutLevelRenderer = ToolGunItemRenderer()
    })

    internal companion object {
        const val CURRENT_MODE_TAG = "currentMode"
        const val MODE_NAMESPACE_TAG = "namespace"
        const val MODE_NAME_TAG = "name"

        const val NAMESPACE_ITERATOR_STATE_TAG = "namespaceIteratorState"
        const val MODE_ITERATOR_STATE_TAG = "modeIteratorState"

        val changeMode by lazy {
            KeyMapping(
                "controls.${ModMain.ID}.$TOOL_GUN_DEF.change_mode",
                KeyConflictContext.IN_GAME,
                KeyModifier.SHIFT,
                InputConstants.Type.MOUSE.getOrCreate(InputConstants.MOUSE_BUTTON_RIGHT),
                "controls.${ModMain.ID}.category.$TOOL_GUN_DEF"
            )
        }
    }
}