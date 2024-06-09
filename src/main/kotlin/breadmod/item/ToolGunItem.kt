package breadmod.item

import breadmod.ClientModEventBus.additionalBindList
import breadmod.ModMain
import breadmod.ModMain.modTranslatable
import breadmod.datagen.tool_gun.BreadModToolGunModeProvider.Companion.TOOL_GUN_DEF
import breadmod.datagen.tool_gun.ModToolGunModeDataLoader
import breadmod.item.rendering.ToolGunItemRenderer
import breadmod.network.PacketHandler.NETWORK
import breadmod.network.ToolGunUpdate
import breadmod.registry.item.IRegisterSpecialCreativeTab
import breadmod.registry.screen.ModCreativeTabs
import breadmod.util.MapIterator
import com.mojang.blaze3d.platform.InputConstants
import net.minecraft.ChatFormatting
import net.minecraft.client.KeyMapping
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerLevel
import net.minecraft.sounds.SoundEvents
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResultHolder
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
                .append(Component.literal(ensureCurrentMode(pStack).getString(MODE_NAME_TAG)).withStyle(ChatFormatting.GOLD))
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

    override fun use(pLevel: Level, pPlayer: Player, pUsedHand: InteractionHand): InteractionResultHolder<ItemStack> {
        val stack = pPlayer.getItemInHand(pUsedHand)

        if(pLevel is ServerLevel) getCurrentMode(stack).action(pPlayer, stack)
        return InteractionResultHolder.fail(pPlayer.getItemInHand(pUsedHand))
    }

    override fun inventoryTick(pStack: ItemStack, pLevel: Level, pEntity: Entity, pSlotId: Int, pIsSelected: Boolean) {
        if(changeMode.consumeClick() && changeMode.isDown) {
            NETWORK.sendToServer(ToolGunUpdate(pSlotId))
            pEntity.playSound(SoundEvents.DISPENSER_FAIL, 1.0f, 1.0f)
        }
    }

    override fun initializeClient(consumer: Consumer<IClientItemExtensions>) = consumer.accept(object : IClientItemExtensions {
        override fun getCustomRenderer(): BlockEntityWithoutLevelRenderer = ToolGunItemRenderer()
    })

    companion object {
        internal const val CURRENT_MODE_TAG = "currentMode"
        internal const val MODE_NAMESPACE_TAG = "namespace"
        internal const val MODE_NAME_TAG = "name"

        internal const val NAMESPACE_ITERATOR_STATE_TAG = "namespaceIteratorState"
        internal const val MODE_ITERATOR_STATE_TAG = "modeIteratorState"

        private val changeMode = KeyMapping(
            "controls.${ModMain.ID}.$TOOL_GUN_DEF.change_mode",
            KeyConflictContext.IN_GAME,
            KeyModifier.SHIFT,
            InputConstants.Type.MOUSE.getOrCreate(InputConstants.MOUSE_BUTTON_RIGHT),
            "controls.${ModMain.ID}.$TOOL_GUN_DEF"
        )

        init {
            additionalBindList.add(changeMode)
        }
    }
}