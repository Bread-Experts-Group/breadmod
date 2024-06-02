package breadmod.item

import breadmod.ModMain.modTranslatable
import breadmod.registry.entity.ModEntityTypes
import breadmod.registry.sound.ModSounds
import net.minecraft.ChatFormatting
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Rarity
import net.minecraft.world.item.TooltipFlag
import net.minecraft.world.level.Level
import net.minecraft.world.phys.Vec3
import kotlin.random.Random

@Suppress("SpellCheckingInspection")
class TheStick: Item(Properties().stacksTo(1).fireResistant().rarity(Rarity.EPIC)) {
    private val random = Random(592042)
    override fun hurtEnemy(pStack: ItemStack, pTarget: LivingEntity, pAttacker: LivingEntity): Boolean {
        val level = pAttacker.level()
        if(level is ServerLevel) {
            if(pTarget is ServerPlayer) {
                pTarget.connection.disconnect(modTranslatable("item", "thestick", "playerkick"))
            } else {
                pTarget.playSound(ModSounds.TOOLGUN.get(), 2.0f, 1.0f)
                pTarget.remove(Entity.RemovalReason.DISCARDED)
                level.server.playerList.players.forEach {
                    it.sendSystemMessage(Component.translatable("item.breadmod.leftgame", pTarget.name).withStyle(ChatFormatting.YELLOW))
                }
                fun rand() = (random.nextDouble() - 0.5)*4
//                repeat(20) {
//                    val bullet = ModEntityTypes.BREAD_BULLET_ENTITY.get().create(level)
//                    if(bullet != null) {
//                        bullet.owner = pAttacker
//                        bullet.deltaMovement = Vec3(rand(), random.nextDouble() + 0.1, rand())
//                        bullet.moveTo(pTarget.x, pTarget.y, pTarget.z)
//                        level.addFreshEntity(bullet)
//                    }
//                }
            }
        }
        return super.hurtEnemy(pStack, pTarget, pAttacker)
    }

    override fun onLeftClickEntity(stack: ItemStack?, player: Player, entity: Entity): Boolean {
        repeat(40) {
            fun rand() = (random.nextDouble() - 0.5)*1.2
            player.level().addParticle(ParticleTypes.FIREWORK, entity.x, entity.y, entity.z, rand(), random.nextDouble() + 0.1, rand())
        }

        return super.onLeftClickEntity(stack, player, entity)
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