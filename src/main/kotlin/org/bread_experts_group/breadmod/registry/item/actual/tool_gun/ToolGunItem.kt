package org.bread_experts_group.breadmod.registry.item.actual.tool_gun

import net.minecraft.ChatFormatting
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.TooltipFlag
import net.minecraft.world.level.Level
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions
import org.bread_experts_group.breadmod.BreadMod.Companion.modTranslatable
import org.bread_experts_group.breadmod.client.render.item.ToolGunItemRenderer
import org.bread_experts_group.breadmod.datagen.tool_gun.ToolGunModeDataLoader
import org.bread_experts_group.breadmod.registry.item.actual.tool_gun.ToolGunControlLogic.changeMode
import org.bread_experts_group.breadmod.registry.item.actual.tool_gun.mode.ToolGunNoMode
import org.bread_experts_group.breadmod.registry.component.ModDataComponents
import org.bread_experts_group.breadmod.registry.item.IRegisterSpecialCreativeTab
import org.bread_experts_group.breadmod.registry.menu.ModCreativeTabs
import java.util.function.Supplier

// todo complete re-implementation of tool gun features
class ToolGunItem : Item(Properties().stacksTo(1)), IRegisterSpecialCreativeTab {
    class ToolGunItemExtensions : IClientItemExtensions {
        override fun getCustomRenderer(): BlockEntityWithoutLevelRenderer = ToolGunItemRenderer()
    }

    override fun appendHoverText(
        stack: ItemStack,
        context: TooltipContext,
        tooltipComponents: MutableList<Component>,
        tooltipFlag: TooltipFlag
    ) {
        tooltipComponents.add(
            modTranslatable("item", TOOL_GUN_DEF, "tooltip", "current_mode")
                .append(
                    (getCurrentMode(stack).displayName.copy()
                        ?: Component.literal("???")).withStyle(ChatFormatting.GOLD)
                )
        )
        tooltipComponents.add(
            changeMode.translatedKeyMessage.copy().withStyle(ChatFormatting.GREEN)
                .append(modTranslatable("item", TOOL_GUN_DEF, "tooltip", "mode_switch"))
        )
    }

    fun getCurrentMode(stack: ItemStack): ToolGunModeDataLoader.ToolgunMode {
        return try {
            val data = stack.get(ModDataComponents.TOOL_GUN_DATA.get())
            ToolGunModeDataLoader.modes[data?.namespace]?.get(data?.name)?.first ?: ToolGunNoMode
        } catch (e: Exception) {
            ToolGunNoMode
        }
    }

//    override fun use(level: Level, player: Player, usedHand: InteractionHand): InteractionResultHolder<ItemStack> {
//        val stack = player.getItemInHand(usedHand)
//        val fakePlayerEntity = FakePlayer(level, player.x, player.y, player.z, player)
//        fakePlayerEntity.owner = player
//        fakePlayerEntity.yRot = player.yRot
//        fakePlayerEntity.yHeadRot = player.yHeadRot
//        fakePlayerEntity.setItemSlot(EquipmentSlot.HEAD, ModItems.CHEF_HAT.get().defaultInstance)
//        level.addFreshEntity(fakePlayerEntity)
//        if (level is ServerLevel) {
//            level.entityRaycast(
//                player,
//                player.position().add(0.0, player.eyeHeight.toDouble(), 0.0),
//                Vec3.directionFromRotation(player.xRot, player.yRot),
//                1000.0
//            )?.let {
//                val logger = LogManager.getLogger()
//                fun rand() = (player.random.nextDouble() - 0.5) * 1.2
//
//                logger.info("an entity was found: ${it.entity}")
//                level.sendParticles(
//                    ParticleTypes.END_ROD,
//                    it.entity.x, it.entity.y, it.entity.z, 60,
//                    rand(), player.random.nextDouble(), rand(), 1.0
//                )
//            }
//        } else if (level.isClientSide) {
//            addBeamTask(player.position().toVector3f(), player.position().plus(Vec3(0.0, -5.0, 0.0)).toVector3f(), 1.0f)
//        }
//        return InteractionResultHolder.fail(stack)
//    }

    override val creativeModeTabs: List<Supplier<CreativeModeTab>> = listOf(ModCreativeTabs.SPECIALS_TAB)

    override fun inventoryTick(stack: ItemStack, level: Level, entity: Entity, slotId: Int, isSelected: Boolean) {
        if (entity is Player) {
            val currentMode = getCurrentMode(stack)

            if (isSelected) currentMode.mode.open(level, entity, stack, null)
            else currentMode.mode.close(level, entity, stack, null)
        }
    }

    companion object {
        const val TOOL_GUN_DEF = "tool_gun"

//        const val CURRENT_MODE_TAG = "currentMode"
//        const val MODE_NAMESPACE_TAG = "namespace"
//        const val MODE_NAME_TAG = "name"
//
//        const val NAMESPACE_ITERATOR_STATE_TAG = "namespaceIteratorState"
//        const val MODE_ITERATOR_STATE_TAG = "modeIteratorState"
    }
}