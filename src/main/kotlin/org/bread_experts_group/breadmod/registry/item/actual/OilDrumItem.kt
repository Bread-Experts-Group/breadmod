package org.bread_experts_group.breadmod.registry.item.actual

import net.minecraft.core.component.DataComponents
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResultHolder
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.ai.attributes.AttributeModifier
import net.minecraft.world.entity.ai.attributes.Attributes
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.ItemUtils
import net.minecraft.world.item.Rarity
import net.minecraft.world.item.UseAnim
import net.minecraft.world.item.alchemy.PotionContents
import net.minecraft.world.item.alchemy.Potions
import net.minecraft.world.level.Level
import net.neoforged.neoforge.network.PacketDistributor
import org.bread_experts_group.breadmod.BreadMod.Companion.modLocation
import org.bread_experts_group.breadmod.network.clientbound.GasGasGasSoundPacket

class OilDrumItem : Item(
	Item.Properties()
		.stacksTo(1)
		.rarity(Rarity.EPIC)
		.component(DataComponents.POTION_CONTENTS, PotionContents.EMPTY)
) {
	companion object {
		val DRUM_SPEED_ID: ResourceLocation = modLocation("drum", "speed")
	}

	override fun getUseDuration(stack: ItemStack, entity: LivingEntity): Int = 32
	override fun getUseAnimation(stack: ItemStack): UseAnim = UseAnim.DRINK
	override fun use(level: Level, player: Player, hand: InteractionHand): InteractionResultHolder<ItemStack> =
		ItemUtils.startUsingInstantly(level, player, hand)

	override fun getDefaultInstance(): ItemStack = super.getDefaultInstance().also {
		it.set(DataComponents.POTION_CONTENTS, PotionContents(Potions.WATER))
	}

	override fun finishUsingItem(stack: ItemStack, level: Level, livingEntity: LivingEntity): ItemStack {
		if (level.isClientSide) return ItemStack.EMPTY
		PacketDistributor.sendToPlayersInDimension(
			level as ServerLevel,
			GasGasGasSoundPacket(livingEntity.id)
		)
		Thread.ofVirtual().start {
			Thread.sleep(3600)
			val movementSpeed = livingEntity.attributes.getInstance(Attributes.MOVEMENT_SPEED)!!
			movementSpeed.addTransientModifier(
				AttributeModifier(Companion.DRUM_SPEED_ID, 10.0, AttributeModifier.Operation.ADD_VALUE)
			)
		}
		return ItemStack.EMPTY
	}
}