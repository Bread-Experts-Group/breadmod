package bread.mod.breadmod.item.toolGun

import bread.mod.breadmod.entity.FakePlayer
import bread.mod.breadmod.registry.item.IRegisterSpecialCreativeTab
import bread.mod.breadmod.registry.item.ModItems
import bread.mod.breadmod.registry.menu.ModCreativeTabs
import bread.mod.breadmod.util.RaycastResult.Companion.entityRaycast
import bread.mod.breadmod.util.render.playerRenderTest
import dev.architectury.registry.registries.RegistrySupplier
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResultHolder
import net.minecraft.world.entity.EquipmentSlot
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.TooltipFlag
import net.minecraft.world.level.Level
import net.minecraft.world.phys.Vec3
import org.apache.logging.log4j.LogManager

// todo complete re-implementation of tool gun features
internal class ToolGunItem : Item(Properties().stacksTo(1)), IRegisterSpecialCreativeTab {
    override fun appendHoverText(
        stack: ItemStack,
        context: TooltipContext,
        tooltipComponents: MutableList<Component>,
        tooltipFlag: TooltipFlag
    ) {
//        tooltipComponents.add(
//            modTranslatable("item", TOOL_GUN_DEF, "tooltip", "current_mode")
//                .append(
//                    (getCurrentMode(pStack).displayName.copy()
//                        ?: Component.literal("???")).withStyle(ChatFormatting.GOLD)
//                )
//        )
//        tooltipComponents.add(
//            changeMode.translatedKeyMessage.copy().withStyle(ChatFormatting.GREEN)
//                .append(modTranslatable("item", TOOL_GUN_DEF, "tooltip", "mode_switch"))
//        )
    }

    override fun use(level: Level, player: Player, usedHand: InteractionHand): InteractionResultHolder<ItemStack> {
        val stack = player.getItemInHand(usedHand)
        val fakePlayerEntity = FakePlayer(level, player.x, player.y, player.z, player)
        fakePlayerEntity.owner = player
        fakePlayerEntity.yRot = player.yRot
        fakePlayerEntity.yHeadRot = player.yHeadRot
        fakePlayerEntity.setItemSlot(EquipmentSlot.HEAD, ModItems.CHEF_HAT.get().defaultInstance)
        level.addFreshEntity(fakePlayerEntity)
        if (level is ServerLevel) {
            level.entityRaycast(
                player,
                player.position().add(0.0, player.eyeHeight.toDouble(), 0.0),
                Vec3.directionFromRotation(player.xRot, player.yRot),
                1000.0
            )?.let {
                val logger = LogManager.getLogger()
                fun rand() = (player.random.nextDouble() - 0.5) * 1.2

                logger.info("an entity was found: ${it.entity}")
                level.sendParticles(
                    ParticleTypes.END_ROD,
                    it.entity.x, it.entity.y, it.entity.z, 60,
                    rand(), player.random.nextDouble(), rand(), 1.0
                )
            }
        } else if (level.isClientSide) {
//            addBeamTask(player.position().toVector3f(), player.position().plus(Vec3(0.0, -5.0, 0.0)).toVector3f(), 1.0f)
            playerRenderTest(
                player,
                player.x,
                player.y,
                player.z,
                -player.rotationVector.y,
                player.walkAnimation.position(),
//                        entity.getViewYRot(partialTick),
//                        entity.getViewXRot(partialTick),
//                        random.nextBoolean()
            )
        }
        return InteractionResultHolder.fail(stack)
    }

    override val creativeModeTabs: List<RegistrySupplier<CreativeModeTab>> = listOf(ModCreativeTabs.SPECIALS_TAB)

//    override fun inventoryTick(pStack: ItemStack, pLevel: Level, pEntity: Entity, pSlotId: Int, pIsSelected: Boolean) {
//        if (pEntity is Player) {
//            val currentMode = getCurrentMode(pStack)
//
//            if (pIsSelected) currentMode.mode.open(pLevel, pEntity, pStack, null)
//            else currentMode.mode.close(pLevel, pEntity, pStack, null)
//        }
//    }

//    override fun initializeClient(consumer: Consumer<IClientItemExtensions>) =
//        consumer.accept(object : IClientItemExtensions {
//            override fun getCustomRenderer(): BlockEntityWithoutLevelRenderer = ToolGunItemRenderer()
//        override fun getArmPose(entityLiving: LivingEntity, hand: InteractionHand, itemStack: ItemStack): ArmPose {
//            val armPose = IArmPoseTransformer { model, entity, arm ->
//                if(entity.isHolding(this@ToolGunItem)) {
//                    model.rightArm.yRot = -0.1F + model.head.yRot
//                    model.rightArm.xRot = ((-Math.PI / 2f) + model.head.xRot).toFloat()
//                }
//            }
//            return ArmPose.create("tool_gun", false, armPose)
//        }
//        })

    internal companion object {
        const val TOOL_GUN_DEF = "tool_gun"

        // --Commented out by Inspection START (9/10/2024 03:55):
//        // --Commented out by Inspection (9/10/2024 03:55):// --Commented out by Inspection (9/10/2024 03:55):const
//        val CURRENT_MODE_TAG = "currentMode"
//        // --Commented out by Inspection (9/10/2024 03:55):const val MODE_NAMESPACE_TAG = "namespace"
// --Commented out by Inspection STOP (9/10/2024 03:55)
//        const val MODE_NAME_TAG = "name"

// --Commented out by Inspection START (9/10/2024 03:55):
//        const val NAMESPACE_ITERATOR_STATE_TAG = "namespaceIteratorState"
//        const val MODE_ITERATOR_STATE_TAG = "modeIteratorState"
// --Commented out by Inspection STOP (9/10/2024 03:55)
    }
}