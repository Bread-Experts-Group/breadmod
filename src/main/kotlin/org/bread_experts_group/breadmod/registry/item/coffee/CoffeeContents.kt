package org.bread_experts_group.breadmod.registry.item.coffee

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.StreamCodec
import net.minecraft.util.FastColor
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.item.ItemStack
import org.bread_experts_group.breadmod.ModDataComponents
import org.bread_experts_group.breadmod.registry.item.ModItems
import org.bread_experts_group.breadmod.util.Color
import org.bread_experts_group.breadmod.util.toList

data class CoffeeContents(val effects: List<MobEffectInstance>) {
	companion object {
		val CODEC: Codec<CoffeeContents> = RecordCodecBuilder.create { inst ->
			inst.group(
				MobEffectInstance.CODEC.listOf().fieldOf("effects").forGetter(CoffeeContents::effects)
			).apply(inst, ::CoffeeContents)
		}
		val STREAM_CODEC: StreamCodec<RegistryFriendlyByteBuf, CoffeeContents> = StreamCodec.composite(
			MobEffectInstance.STREAM_CODEC.toList(), CoffeeContents::effects,
			::CoffeeContents
		)
		val EMPTY: CoffeeContents = CoffeeContents(listOf())

		fun createBlendStack(list: List<MobEffectInstance>): ItemStack {
			val stack = ModItems.COFFEE_BLEND.toStack()
			stack.set(ModDataComponents.COFFEE_CONTENTS, CoffeeContents(list))
			return stack
		}
	}

	fun getColor(): Int {
		if (this.effects.isEmpty()) return Color.WHITE
		var i = 0
		var j = 0
		var k = 0
		var l = 0

		for (mobEffectInstance in this.effects) {
			if (mobEffectInstance.isVisible) {
				val i1 = mobEffectInstance.effect.value().color
				val j1 = mobEffectInstance.amplifier + 1
				i += j1 * FastColor.ARGB32.red(i1)
				j += j1 * FastColor.ARGB32.green(i1)
				k += j1 * FastColor.ARGB32.blue(i1)
				l += j1
			}
		}

		return Color.color(i / l, j / l, k / l)
	}

	fun applyToEntity(entity: LivingEntity) {
		this.effects.forEach(entity::addEffect)
	}
}