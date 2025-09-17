package org.bread_experts_group.breadmod.registry.recipe.actual.fluid_energy

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.core.component.DataComponentMap
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.core.registries.Registries
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.tags.TagKey
import net.minecraft.world.item.Item
import net.minecraft.world.item.Items
import net.minecraft.world.level.material.Fluid
import net.minecraft.world.level.material.Fluids
import org.bread_experts_group.breadmod.network.BreadModCodecs
import org.bread_experts_group.breadmod.util.logDebugInfo
import org.bread_experts_group.breadmod.util.ofOptional
import org.bread_experts_group.breadmod.util.toKotlinPair
import org.bread_experts_group.breadmod.util.toMojangPair
import java.math.BigDecimal
import java.util.Optional
import kotlin.jvm.optionals.getOrNull
import kotlin.reflect.KClass

@Suppress("UNCHECKED_CAST")
class InputOption<T : Any> private constructor(
	private val selectClass: KClass<T>,
	val left: Pair<TagKey<T>, BigDecimal>? = null,
	val right: BigDescriptor<T>? = null
) {
	companion object {
		fun <T : Any> tag(selectClass: KClass<T>, tagKey: Pair<TagKey<T>, BigDecimal>): InputOption<T> =
			InputOption(selectClass, tagKey)

		fun <T : Any> bigDescriptor(selectClass: KClass<T>, descriptor: BigDescriptor<T>): InputOption<T> =
			InputOption(selectClass, right = descriptor)

		val ITEM_CODEC: Codec<InputOption<Item>> =
			RecordCodecBuilder<RegistryFriendlyByteBuf, InputOption<Item>>.create { inst ->
				inst.group(
					Codec.pair(
						TagKey.codec(Registries.ITEM).fieldOf("tag").codec(),
						BreadModCodecs.BIG_DECIMAL_CODEC.fieldOf("count").codec()
					).optionalFieldOf("input_tag").forGetter { Optional.ofNullable(it.left?.toMojangPair()) },
					BreadModCodecs.BIG_DESCRIPTOR_ITEM_CODEC.optionalFieldOf("input_big_descriptor")
						.forGetter { Optional.ofNullable(it.right) }
				).apply(inst) { first, second ->
					InputOption(
						Item::class,
						first.getOrNull()?.toKotlinPair(),
						second.getOrNull()
					)
				}
			}
		val FLUID_CODEC: Codec<InputOption<Fluid>> =
			RecordCodecBuilder<RegistryFriendlyByteBuf, InputOption<Item>>.create { inst ->
				inst.group(
					Codec.pair(
						TagKey.codec(Registries.FLUID).fieldOf("tag").codec(),
						BreadModCodecs.BIG_DECIMAL_CODEC.fieldOf("amount").codec()
					).optionalFieldOf("input_tag").forGetter { Optional.ofNullable(it.left?.toMojangPair()) },
					BreadModCodecs.BIG_DESCRIPTOR_FLUID_CODEC.optionalFieldOf("input_big_descriptor")
						.forGetter { Optional.ofNullable(it.right) }
				).apply(inst) { first, second ->
					InputOption(
						Fluid::class,
						first.getOrNull()?.toKotlinPair(),
						second.getOrNull()
					)
				}
			}
		val ITEM_STREAM_CODEC: StreamCodec<RegistryFriendlyByteBuf, InputOption<Item>> = StreamCodec.composite(
			BreadModCodecs.pairStreamCodec(
				ByteBufCodecs.fromCodec(TagKey.codec(Registries.ITEM)),
				BreadModCodecs.BIG_DECIMAL_STREAM_CODEC
			).ofOptional(),
			{ Optional.ofNullable(it.left) },
			BreadModCodecs.BIG_DESCRIPTOR_ITEM_STREAM_CODEC.ofOptional(),
			{ Optional.ofNullable(it.right) },
			{ left, right ->
				InputOption(
					Item::class,
					left.getOrNull(),
					right.getOrNull()
				)
			}
		)
		val FLUID_STREAM_CODEC: StreamCodec<RegistryFriendlyByteBuf, InputOption<Fluid>> = StreamCodec.composite(
			BreadModCodecs.pairStreamCodec(
				ByteBufCodecs.fromCodec(TagKey.codec(Registries.FLUID)),
				BreadModCodecs.BIG_DECIMAL_STREAM_CODEC
			).ofOptional(),
			{ Optional.ofNullable(it.left) },
			BreadModCodecs.BIG_DESCRIPTOR_FLUID_STREAM_CODEC.ofOptional(),
			{ Optional.ofNullable(it.right) },
			{ left, right ->
				InputOption(
					Fluid::class,
					left.getOrNull(),
					right.getOrNull()
				)
			}
		)
	}

	private var tagItems: List<Item> = emptyList()
	private var tagFluids: List<Fluid> = emptyList()

	init {
		when (this.selectClass) {
			Item::class -> {
				if (this.left != null) {
					logDebugInfo("getting tag for ${this.left.first}")
					val itemTag = this.left.first as TagKey<Item>
					val tag = BuiltInRegistries.ITEM.getTag(itemTag)
					if (tag.isPresent) this.tagItems = tag.get().map { it.value() }
				}
			}
			Fluid::class -> {
				if (this.left != null) {
					val fluidTag = this.left.first as TagKey<Fluid>
					val tag = BuiltInRegistries.FLUID.getTag(fluidTag)
					if (tag.isPresent) this.tagFluids = tag.get().map { it.value() }
				}
			}
			else -> throw IllegalArgumentException("${this.selectClass} is not supported in InputOption!")
		}
	}

	override fun toString(): String = "InputOption[${this.selectClass}, ${this.left}, ${this.right}]"

	fun test(input: T): Boolean = when (this.selectClass) {
		Item::class -> {
			val item = input as Item
			val descriptor = this.right as? BigDescriptor<Item>
			if (this.tagItems.isNotEmpty() && this.left != null) this.tagItems.any { it == item }
			else if (this.right != null && descriptor != null) descriptor.value == item else false
		}
		Fluid::class -> {
			val fluid = input as Fluid
			val descriptor = this.right as? BigDescriptor<Fluid>
			if (this.tagFluids.isNotEmpty() && this.left != null) this.tagFluids.any { it == fluid }
			else if (this.right != null && descriptor != null) descriptor.value == fluid else false
		}
		else -> false
	}

	fun getTagItems(): List<Item> = this.tagItems
	fun getTagFluids(): List<Fluid> = this.tagFluids

	fun resolveInputItem(): Item =
		if (this.tagItems.isNotEmpty() && this.left != null) this.tagItems.first()
		else if (this.right != null && this.selectClass == Item::class) (this.right as BigDescriptor<Item>).value
		else Items.AIR

	fun resolveInputFluid(): Fluid =
		if (this.tagFluids.isNotEmpty() && this.left != null) this.tagFluids.first()
		else if (this.right != null && this.selectClass == Fluid::class) (this.right as BigDescriptor<Fluid>).value
		else Fluids.EMPTY

	fun testComponents(input: DataComponentMap): Boolean {
		if (this.right == null) return false
		val itemDescriptor = this.right as BigDescriptor<Item>

		if (itemDescriptor.components.isEmpty) return true
		itemDescriptor.components.all { iComp ->
			input.any { rComp ->
				return iComp.type == rComp.type && iComp.value == rComp.value
			}
		}
		return false
	}
}