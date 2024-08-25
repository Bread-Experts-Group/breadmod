package breadmod.item.tool_gun

import breadmod.ClientForgeEventBus.changeMode
import breadmod.ModMain.modTranslatable
import breadmod.datagen.tool_gun.BreadModToolGunModeProvider.Companion.TOOL_GUN_DEF
import breadmod.datagen.tool_gun.ModToolGunModeDataLoader
import breadmod.item.tool_gun.mode.ToolGunNoMode
import breadmod.client.render.tool_gun.ToolGunItemRenderer
import breadmod.registry.item.IRegisterSpecialCreativeTab
import breadmod.registry.menu.ModCreativeTabs
import breadmod.util.MapIterator
import net.minecraft.ChatFormatting
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.TooltipFlag
import net.minecraft.world.level.Level
import net.minecraftforge.client.extensions.common.IClientItemExtensions
import net.minecraftforge.registries.RegistryObject
import java.util.function.Consumer


internal class ToolGunItem : Item(Properties().stacksTo(1)), IRegisterSpecialCreativeTab {
    override fun appendHoverText(
        pStack: ItemStack,
        pLevel: Level?,
        pTooltipComponents: MutableList<Component>,
        pIsAdvanced: TooltipFlag
    ) {
        pTooltipComponents.add(
            modTranslatable("item", TOOL_GUN_DEF, "tooltip", "current_mode")
                .append(
                    (getCurrentMode(pStack).displayName.copy()
                        ?: Component.literal("???")).withStyle(ChatFormatting.GOLD)
                )
        )
        pTooltipComponents.add(
            changeMode.translatedKeyMessage.copy().withStyle(ChatFormatting.GREEN)
                .append(modTranslatable("item", TOOL_GUN_DEF, "tooltip", "mode_switch"))
        )
    }

    internal fun ensureCurrentMode(pStack: ItemStack): CompoundTag {
        if (!pStack.orCreateTag.contains(CURRENT_MODE_TAG)) {
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

    internal fun getCurrentMode(pStack: ItemStack): ModToolGunModeDataLoader.ToolgunMode {
        return try {
            ensureCurrentMode(pStack).let {
                ModToolGunModeDataLoader.modes[it.getString(MODE_NAMESPACE_TAG)]?.get(it.getString(MODE_NAME_TAG))?.first
                    ?: ToolGunNoMode
            }
        } catch (e: NoSuchElementException) {
            ToolGunNoMode
        }
    }

    override val creativeModeTabs: List<RegistryObject<CreativeModeTab>> = listOf(ModCreativeTabs.SPECIALS_TAB)

    override fun inventoryTick(pStack: ItemStack, pLevel: Level, pEntity: Entity, pSlotId: Int, pIsSelected: Boolean) {
        if (pEntity is Player) {
            val currentMode = getCurrentMode(pStack)

            if (pIsSelected) currentMode.mode.open(pLevel, pEntity, pStack, null)
            else currentMode.mode.close(pLevel, pEntity, pStack, null)
        }
    }

    override fun initializeClient(consumer: Consumer<IClientItemExtensions>) =
        consumer.accept(object : IClientItemExtensions {
            override fun getCustomRenderer(): BlockEntityWithoutLevelRenderer = ToolGunItemRenderer()
//        override fun getArmPose(entityLiving: LivingEntity, hand: InteractionHand, itemStack: ItemStack): ArmPose {
//            val armPose = IArmPoseTransformer { model, entity, arm ->
//                if(entity.isHolding(this@ToolGunItem)) {
//                    model.rightArm.yRot = -0.1F + model.head.yRot
//                    model.rightArm.xRot = ((-Math.PI / 2f) + model.head.xRot).toFloat()
//                }
//            }
//            return ArmPose.create("tool_gun", false, armPose)
//        }
        })

    internal companion object {
        const val CURRENT_MODE_TAG = "currentMode"
        const val MODE_NAMESPACE_TAG = "namespace"
        const val MODE_NAME_TAG = "name"

        const val NAMESPACE_ITERATOR_STATE_TAG = "namespaceIteratorState"
        const val MODE_ITERATOR_STATE_TAG = "modeIteratorState"
    }
}