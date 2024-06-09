package breadmod.item

import breadmod.ModMain.modTranslatable
import breadmod.datagen.toolgun.ModToolgunModeDataLoader
import breadmod.item.rendering.ToolGunItemRenderer
import breadmod.registry.item.IRegisterSpecialCreativeTab
import breadmod.registry.screen.ModCreativeTabs
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerLevel
import net.minecraft.sounds.SoundEvents
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResultHolder
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.TooltipFlag
import net.minecraft.world.level.Level
import net.minecraftforge.client.extensions.common.IClientItemExtensions
import net.minecraftforge.registries.RegistryObject
import java.util.function.Consumer
import kotlin.random.Random


class ToolGunItem: Item(Properties().stacksTo(1)), IRegisterSpecialCreativeTab {
    override fun appendHoverText(
        pStack: ItemStack,
        pLevel: Level?,
        pTooltipComponents: MutableList<Component>,
        pIsAdvanced: TooltipFlag
    ) {
        ensureCurrentMode(pStack)
        pTooltipComponents.add(Component.literal("Current Mode: ${pStack.orCreateTag.getCompound(CURRENT_MODE_TAG).getString(MODE_NAME_TAG)}"))
        pTooltipComponents.add(modTranslatable("item", "toolgun", "tooltip", "mode_switch"))
    }

    private fun ensureCurrentMode(pStack: ItemStack) {
        if(!pStack.orCreateTag.contains(CURRENT_MODE_TAG)) {
            val namespace = ModToolgunModeDataLoader.modes.keys.randomOrNull() ?: return pStack.shrink(999)
            val name = ModToolgunModeDataLoader.modes[namespace]?.keys?.random() ?: throw IllegalStateException("Empty set!!!")
            pStack.orCreateTag.put(CURRENT_MODE_TAG, CompoundTag().also {
                it.putString(MODE_NAMESPACE_TAG, namespace)
                it.putString(MODE_NAME_TAG, name)
            })
        }
    }

    private fun getCurrentMode(pStack: ItemStack) = ensureCurrentMode(pStack).let {
        pStack.orCreateTag.getCompound(CURRENT_MODE_TAG).let {
            ModToolgunModeDataLoader.modes[it.getString(MODE_NAMESPACE_TAG)]?.get(it.getString(MODE_NAME_TAG))
        }
    }

    override val creativeModeTabs: List<RegistryObject<CreativeModeTab>> = listOf(ModCreativeTabs.SPECIALS_TAB)

    private val random = Random(-3428960)
    override fun use(pLevel: Level, pPlayer: Player, pUsedHand: InteractionHand): InteractionResultHolder<ItemStack> {
        val stack = pPlayer.getItemInHand(pUsedHand)
        if(pLevel is ServerLevel) {
//            pLevel.rayMarchEntity(pPlayer, pPlayer.position(), Vec3.directionFromRotation(pPlayer.xRot, pPlayer.yRot), 1000.0)?.let {
//                fun rand() = (random.nextDouble() - 0.5)*1.2
//                pLevel.sendParticles(ParticleTypes.END_ROD, it.entity.x, it.entity.y, it.entity.z, 40, rand(), random.nextDouble(), rand(), 1.0)
//                pLevel.playSound(null, pPlayer.blockPosition(), ModSounds.TOOL_GUN.get(), SoundSource.PLAYERS, 2.0f, 1f)
//                NETWORK.send(
//                    PacketDistributor.TRACKING_CHUNK.with { pLevel.getChunkAt(it.entity.blockPosition()) },
//                    BeamPacket(it.startPosition.toVector3f(), it.endPosition.toVector3f(), 0.1)
//                )
//
//                if(it.entity is ServerPlayer) it.entity.connection.disconnect(modTranslatable("item", "tool_gun", "player_left_game"))
//                else {
//                    it.entity.discard()
//                    pLevel.server.playerList.players.forEach { player -> player.sendSystemMessage(modTranslatable("item", "tool_gun", "entity_left_game", args = listOf(it.entity.name)).withStyle(ChatFormatting.YELLOW)) }
//                }
//            }
        } else if(pPlayer.isShiftKeyDown && pUsedHand == InteractionHand.MAIN_HAND) {
            pPlayer.playSound(SoundEvents.DISPENSER_FAIL, 1.0f, 1.0f)
            stack.orCreateTag.getString(CURRENT_MODE_TAG)
            pPlayer.cooldowns.addCooldown(this, 10)
        }

        return InteractionResultHolder.fail(pPlayer.getItemInHand(pUsedHand))
    }

    override fun initializeClient(consumer: Consumer<IClientItemExtensions>) = consumer.accept(object : IClientItemExtensions {
        override fun getCustomRenderer(): BlockEntityWithoutLevelRenderer = ToolGunItemRenderer()
    })

    companion object {
        internal const val CURRENT_MODE_TAG = "currentMode"
        internal const val MODE_NAMESPACE_TAG = "namespace"
        internal const val MODE_NAME_TAG = "name"
    }
}