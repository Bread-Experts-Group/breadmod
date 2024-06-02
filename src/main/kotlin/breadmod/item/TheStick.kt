package breadmod.item

import breadmod.ModMain.modTranslatable
import breadmod.entity.BreadBulletEntity
import net.minecraft.ChatFormatting
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.LivingEntity
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
                pTarget.remove(Entity.RemovalReason.DISCARDED)
                level.server.playerList.players.forEach {
                    it.sendSystemMessage(Component.translatable("item.breadmod.leftgame", pTarget.name).withStyle(ChatFormatting.YELLOW))
                }
                repeat(20) {
                    val bullet = BreadBulletEntity(level, pAttacker)
                    fun rand() = (random.nextDouble() - 0.5)*10
                    bullet.deltaMovement = Vec3(rand(), rand(), rand())
                    bullet.moveTo(pTarget.x, pTarget.y, pTarget.z)
                    level.addFreshEntity(bullet)
                }
            }
        }
        return super.hurtEnemy(pStack, pTarget, pAttacker)
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