package org.bread_experts_group.breadmod.registry.block.actual.entity.handler

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.ChatFormatting
import net.minecraft.core.HolderLookup
import net.minecraft.core.component.DataComponentMap
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.ListTag
import net.minecraft.nbt.Tag
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.chat.Component
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.material.Fluid
import net.minecraft.world.level.material.Fluids
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions
import net.neoforged.neoforge.common.util.INBTSerializable
import net.neoforged.neoforge.fluids.FluidStack
import net.neoforged.neoforge.fluids.capability.IFluidHandler
import org.bread_experts_group.breadmod.network.BreadModCodecs.BIG_DECIMAL_CODEC
import org.bread_experts_group.breadmod.network.BreadModCodecs.BIG_DECIMAL_STREAM_CODEC
import org.bread_experts_group.breadmod.network.BreadModCodecs.FLUID_ID_DESERIALIZER
import org.bread_experts_group.breadmod.network.BreadModCodecs.FLUID_ID_SERIALIZER
import org.bread_experts_group.breadmod.network.BreadModCodecs.compose
import org.bread_experts_group.breadmod.registry.block.actual.entity.BreadModBlockEntity
import org.bread_experts_group.breadmod.registry.block.actual.entity.handler.ExtendedFluidHandler.Tank.Companion.TANK_FLUID_ID_SERIALIZER
import org.bread_experts_group.breadmod.registry.component.ModDataComponents.TANKS
import org.bread_experts_group.breadmod.util.floatRoundEven
import org.bread_experts_group.breadmod.util.int
import org.bread_experts_group.breadmod.util.percentRoundEven
import java.math.BigDecimal
import java.math.RoundingMode
import kotlin.math.roundToInt

class ExtendedFluidHandler(
	vararg tanks: Tank
) : ParentedHandler<BreadModBlockEntity>, IFluidHandler, DataComponentSerializable, INBTSerializable<Tag> {
	override lateinit var parent: BreadModBlockEntity
	val tanks: MutableMap<Int, Tank> = mutableMapOf(*tanks.mapIndexed { index, tank -> index to tank }.toTypedArray())
	override fun getTanks(): Int = this.tanks.size
	override fun getFluidInTank(tank: Int): FluidStack = this.tanks[tank]?.fluidStack() ?: FluidStack.EMPTY
	override fun getTankCapacity(tank: Int): Int = if (this.tanks.containsKey(tank)) Int.MAX_VALUE else 0
	override fun isFluidValid(tank: Int, stack: FluidStack): Boolean = this.tanks[tank]?.validity
		?.invoke(stack.fluid, BigDecimal(stack.amount), stack.components) ?: true

	fun bigFill(
		fluid: Fluid,
		amount: BigDecimal,
		components: DataComponentMap,
		action: IFluidHandler.FluidAction
	): BigDecimal {
		val tank = this.tanks.firstNotNullOfOrNull { (_, tank) ->
			if (
				tank.validity.invoke(fluid, amount, components) &&
				(tank.fluid.isSame(Fluids.EMPTY) || (tank.fluid.isSame(fluid) && tank.amount < tank.capacity))
			) tank
			else null
		} ?: return BigDecimal.ZERO
		val transfer = minOf(tank.capacity - tank.amount, amount)
		if (action == IFluidHandler.FluidAction.EXECUTE) {
			tank.amount += transfer
			tank.fluid = fluid
			tank.components = components
			this.stateUpdated()
		}
		return transfer
	}

	override fun fill(
		resource: FluidStack,
		action: IFluidHandler.FluidAction
	): Int = this.bigFill(
		resource.fluid,
		BigDecimal(resource.amount),
		resource.components,
		action
	).int

	override fun drain(
		resource: FluidStack,
		action: IFluidHandler.FluidAction
	): FluidStack {
		TODO("Not yet implemented")
	}

	override fun drain(
		maxDrain: Int,
		action: IFluidHandler.FluidAction
	): FluidStack {
		return FluidStack.EMPTY
	}

	override fun serializeDataComponent(map: DataComponentMap.Builder) {
		val tanks = mutableListOf<Tank>()
		this.tanks.forEach { tanks.add(it.key, it.value) }
		map.set(TANKS, tanks)
	}

	override fun deserializeDataComponent(from: BlockEntity.DataComponentInput) {
		val tanks = from.get(TANKS)
		if (tanks != null) {
			this.tanks.clear()
			tanks.forEachIndexed { index, tank -> this.tanks[index] = tank }
			this.stateUpdated()
		}
	}

	val big100: BigDecimal = BigDecimal.valueOf(100)
	val percentGray: Component = Component.literal("%").withStyle(ChatFormatting.GRAY)
	val paraLeftDarkGray: Component = Component.literal("(").withStyle(ChatFormatting.DARK_GRAY)
	val paraRightDarkGray: Component = Component.literal(") ").withStyle(ChatFormatting.DARK_GRAY)
	val leftDarkGray: Component = Component.literal("[").withStyle(ChatFormatting.DARK_GRAY)
	val rightDarkGray: Component = Component.literal("]").withStyle(ChatFormatting.DARK_GRAY)
	val hashDarkGray: Component = Component.literal("#").withStyle(ChatFormatting.DARK_GRAY)
	val colonDarkGray: Component = Component.literal(": ").withStyle(ChatFormatting.DARK_GRAY)
	val pipeDarkGray: Component = Component.literal("|").withStyle(ChatFormatting.DARK_GRAY)
	override fun collectHoverText(tooltipComponents: MutableList<Component>) {
		this.tanks.forEach { (index, tank) ->
			val component = this.hashDarkGray.copy()
			component.append(Component.literal(index.toString()).withStyle(ChatFormatting.GRAY))
			component.append(this.colonDarkGray)
			component.append(Component.literal(" ${tank.amount} ").withStyle(ChatFormatting.BLUE))
			component.append(this.pipeDarkGray)
			component.append(Component.literal(" ${tank.capacity} ").withStyle(ChatFormatting.BLUE))
			component.append(this.paraLeftDarkGray)
			val percentage = tank.amount
				.divide(tank.capacity, percentRoundEven)
				.multiply(this.big100)
				.setScale(2, RoundingMode.HALF_EVEN)
			component.append(Component.literal(percentage.toString()).withStyle(ChatFormatting.BLUE))
			component.append(this.percentGray)
			component.append(this.paraRightDarkGray)
			component.append(this.leftDarkGray)
			val color = IClientFluidTypeExtensions.of(tank.fluid).tintColor
			component.append(tank.fluid.fluidType.description.copy().withColor(color))
			component.append(this.rightDarkGray)
			tooltipComponents.add(component)
		}
	}

	override fun serializeNBT(provider: HolderLookup.Provider): Tag {
		val list = ListTag()
		this.tanks.forEach { (_, tank) ->
			val compound = CompoundTag()
			compound.putString("capacity", tank.capacity.toString())
			if (tank.amount > BigDecimal.ZERO) {
				compound.putString("amount", tank.amount.toString())
				compound.putString("fluid", TANK_FLUID_ID_SERIALIZER(tank))
				// TODO components
			}
			list.add(compound)
		}
		return list
	}

	override fun deserializeNBT(provider: HolderLookup.Provider, nbt: Tag) {
		if (nbt !is ListTag) return
		nbt.forEachIndexed { index, tag ->
			if (tag !is CompoundTag) return@forEachIndexed
			try {
				val capacity = BigDecimal(tag.getString("capacity"))
				val tank = this.tanks.getOrPut(index) { Tank(capacity) }
				tank.fluid = BuiltInRegistries.FLUID.get(ResourceLocation.parse(tag.getString("fluid")))
				tank.amount = BigDecimal(tag.getString("amount"))
			} catch (_: Exception) {
			}
		}
	}

	data class Tank(
		var capacity: BigDecimal
	) {
		var validity: (Fluid, BigDecimal, DataComponentMap) -> Boolean = { _, _, _ -> true }
		var amount: BigDecimal = BigDecimal.ZERO
		var fluid: Fluid = Fluids.EMPTY
		var components: DataComponentMap = DataComponentMap.EMPTY

		fun fluidStack(): FluidStack {
			if (this.amount < BigDecimal.ONE) return FluidStack.EMPTY
			val percent = this.amount.divide(this.capacity, floatRoundEven).toFloat()
			val stack = FluidStack(this.fluid, (Int.MAX_VALUE * percent).roundToInt())
			stack.applyComponents(this.components)
			return stack
		}

		override fun toString(): String = "Tank[${this.amount} / ${this.capacity} [${this.fluid} | ${this.components}]]"

		companion object {
			val TANK_FLUID_ID_SERIALIZER: (Tank) -> String = FLUID_ID_SERIALIZER.compose(Tank::fluid)
			val TANK_DESERIALIZER: (BigDecimal, BigDecimal, String) -> Tank = { capacity, amount, fluidID ->
				val tank = Tank(capacity)
				tank.fluid = FLUID_ID_DESERIALIZER(fluidID)
				tank.amount = amount
				tank
			}
			val CODEC: Codec<Tank> = RecordCodecBuilder.create { inst ->
				inst.group(
					BIG_DECIMAL_CODEC.fieldOf("capacity").forGetter(Tank::capacity),
					BIG_DECIMAL_CODEC.fieldOf("amount").forGetter(Tank::amount),
					Codec.STRING.fieldOf("fluid_id").forGetter(this.TANK_FLUID_ID_SERIALIZER),
				).apply(inst, this.TANK_DESERIALIZER)
			}
			val STREAM_CODEC: StreamCodec<RegistryFriendlyByteBuf, Tank> = StreamCodec.composite(
				BIG_DECIMAL_STREAM_CODEC, Tank::capacity,
				BIG_DECIMAL_STREAM_CODEC, Tank::amount,
				ByteBufCodecs.STRING_UTF8, this.TANK_FLUID_ID_SERIALIZER,
				this.TANK_DESERIALIZER
			)
		}
	}
}