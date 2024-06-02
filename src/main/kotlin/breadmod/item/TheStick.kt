package breadmod.item

import breadmod.ModMain.modTranslatable
import breadmod.registry.sound.ModSounds
import net.minecraft.ChatFormatting
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Rarity
import net.minecraft.world.item.TooltipFlag
import net.minecraft.world.level.Level
import kotlin.random.Random

@Suppress("SpellCheckingInspection")
class TheStick: Item(Properties().stacksTo(1).fireResistant().rarity(Rarity.EPIC)) {
    private val random = Random(592042)
    override fun interactLivingEntity(
        pStack: ItemStack,
        pAttacker: Player,
        pTarget: LivingEntity,
        pUsedHand: InteractionHand
    ): InteractionResult {
        val level = pAttacker.level()
        if (level is ServerLevel) {
            if (pTarget is ServerPlayer) pTarget.connection.disconnect(modTranslatable("item", "thestick", "playerkick"))
            else {
                pTarget.discard()
                level.server.playerList.players.forEach { it.sendSystemMessage(Component.translatable("item.breadmod.leftgame", pTarget.name).withStyle(ChatFormatting.YELLOW)) }
            }
            pTarget.playSound(ModSounds.TOOL_GUN.get(), 2.0f, 1f)
        } else {
            fun rand() = (random.nextDouble() - 0.5)*1.2
            repeat(40) { level.addParticle(ParticleTypes.FIREWORK, pTarget.x, pTarget.y, pTarget.z, rand(), random.nextDouble() + 0.1, rand()) }
        }
        return InteractionResult.sidedSuccess(level.isClientSide)
    }

    override fun appendHoverText(
        pStack: ItemStack,
        pLevel: Level?,
        pTooltipComponents: MutableList<Component>,
        pIsAdvanced: TooltipFlag
    ) {
        pTooltipComponents.addAll(arrayOf(
            Component.literal("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa").withStyle(ChatFormatting.OBFUSCATED),
            modTranslatable("item", "thestick", "tooltip").withStyle(ChatFormatting.RED),
            Component.literal("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa").withStyle(ChatFormatting.OBFUSCATED)))
    }
}