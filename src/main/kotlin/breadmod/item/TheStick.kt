package breadmod.item

import breadmod.ModMain.modTranslatable
import net.minecraft.ChatFormatting
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.TooltipFlag
import net.minecraft.world.level.Level

class TheStick: Item(Properties()) {

    override fun hurtEnemy(pStack: ItemStack, pTarget: LivingEntity, pAttacker: LivingEntity): Boolean {
        val chat = pAttacker as ServerPlayer
//        chat.sendChatMessage(OutgoingChatMessage.create(PlayerChatMessage.system("test")), false, ChatType.bind(ChatType.CHAT, pAttacker))
        if(pTarget is ServerPlayer){
            pTarget.connection.disconnect(modTranslatable("item", "thestick", "playerkick"))
        } else {
            chat.sendSystemMessage(Component.translatable("item.breadmod.leftgame", pTarget.name).withStyle(ChatFormatting.YELLOW))
            pTarget.remove(Entity.RemovalReason.DISCARDED)
        }
        return super.hurtEnemy(pStack, pTarget, pAttacker)
    }

    override fun appendHoverText(
        pStack: ItemStack,
        pLevel: Level?,
        pTooltipComponents: MutableList<Component>,
        pIsAdvanced: TooltipFlag
    ) {
//        pTooltipComponents.add(
//            Component.literal("aaa").withStyle(ChatFormatting.OBFUSCATED)
//                .append(modTranslatable("item", "thestick", "tooltip"))
//                .append(Component.literal("aaa").withStyle(ChatFormatting.OBFUSCATED)))
        pTooltipComponents.addAll(arrayOf(
            Component.literal("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa").withStyle(ChatFormatting.OBFUSCATED),
            modTranslatable("item", "thestick", "tooltip").withStyle(ChatFormatting.RED),
            Component.literal("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa").withStyle(ChatFormatting.OBFUSCATED)))
    }
}