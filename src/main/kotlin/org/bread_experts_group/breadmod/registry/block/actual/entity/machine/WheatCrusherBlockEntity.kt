package org.bread_experts_group.breadmod.registry.block.actual.entity.machine

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.HolderLookup
import net.minecraft.core.NonNullList
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.chat.Component
import net.minecraft.network.protocol.Packet
import net.minecraft.network.protocol.game.ClientGamePacketListener
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket
import net.minecraft.world.Container
import net.minecraft.world.ContainerHelper
import net.minecraft.world.MenuProvider
import net.minecraft.world.WorldlyContainer
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.entity.player.StackedContents
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.inventory.CraftingContainer
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.crafting.RecipeManager
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.HorizontalDirectionalBlock
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.neoforged.neoforge.energy.EnergyStorage
import net.neoforged.neoforge.items.wrapper.SidedInvWrapper
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.bread_experts_group.breadmod.BreadMod.Companion.modTranslatable
import org.bread_experts_group.breadmod.registry.block.ModBlockEntityTypes
import org.bread_experts_group.breadmod.registry.menu.actual.WheatCrusherMenu
import org.bread_experts_group.breadmod.registry.recipe.ModRecipeTypes
import org.bread_experts_group.breadmod.registry.recipe.actual.wheat_crushing.WheatCrusherRecipe
import org.bread_experts_group.breadmod.registry.recipe.actual.wheat_crushing.WheatCrusherRecipe.WheatCrusherInput
import java.util.*
import kotlin.math.max

private var debugMode = false

class WheatCrusherBlockEntity(
	pos : BlockPos, state : BlockState
) : BlockEntity(
	ModBlockEntityTypes.WHEAT_CRUSHER.get(),
	pos,
	state
), MenuProvider, CraftingContainer, WorldlyContainer {
	val logger : Logger = LogManager.getLogger()
	var progress : Int = 0
	var maxProgress : Int = 0
	private var energyDivision : Int? = null
	private var currentRecipe : Optional<WheatCrusherRecipe> = Optional.empty()
	private val recipeDial : RecipeManager.CachedCheck<WheatCrusherInput, WheatCrusherRecipe> by lazy {
		RecipeManager.createCheck(ModRecipeTypes.WHEAT_CRUSHING.get())
	}
	val energyHandler : EnergyStorage by lazy {
		object : EnergyStorage(100000) {
			// todo there's gotta be a better way to sync the energy every receive and extract cause this just seems
			//  hacky
			override fun receiveEnergy(toReceive : Int, simulate : Boolean) : Int {
				this@WheatCrusherBlockEntity.syncToClients()
				return super.receiveEnergy(toReceive, simulate)
			}
		}
	}
	val horizontal : Direction? = this.blockState.getValue(HorizontalDirectionalBlock.FACING)
	val sidedInvWrapper : SidedInvWrapper = SidedInvWrapper(this, this.horizontal)
	private var itemSlots : NonNullList<ItemStack> = NonNullList.withSize(2, ItemStack.EMPTY)
	// todo having syncToClients() not be present in extract and receive in the energy handler stops it from syncing
	//  every time it handles energy
	//  then the energy is only updated when tick() updates the block on client
	//  the caveat is having syncToClients() fire every energy event is potentially bogging down game resources and
	//  the meter in the gui being jittery
	private fun syncToClients() = this.level?.sendBlockUpdated(
		this.blockPos, this.blockState, this.blockState,
		Block.UPDATE_CLIENTS
	)

	fun tick(level : Level, pos : BlockPos, state : BlockState) {
		this.currentRecipe.ifPresentOrElse({ activeRecipe ->
			if (!this.inputStillValid(activeRecipe)) this.resetRecipe()
			val div =
				if (this.energyDivision == null) ((activeRecipe.recipeEnergy) / max(
					activeRecipe.recipeTime,
					1
				)).also { div -> this.energyDivision = div } else this.energyDivision
					?: return@ifPresentOrElse
			if ((div < 0) && (this.energyHandler.energyStored + div > this.energyHandler.maxEnergyStored))
				return@ifPresentOrElse
			val energy = this.energyHandler.extractEnergy(div, false)

			if (energy >= div && this.canFitResults()) {
				this.progress++
				level.setBlockAndUpdate(
					pos,
					state.setValue(
						BlockStateProperties.POWERED,
						true
					)
				)
				if (this.progress >= activeRecipe.recipeTime) {
					this.finalizeRecipe(level, activeRecipe)
					this.resetRecipe()
				}
			}
		}, {
			val check = this.recipeDial.getRecipeFor(
				WheatCrusherInput(
					this.getItem(0),
					this.getItem(0).count
				), level
			)

			check.ifPresent { present ->
				val recipe = present.value
				if (!this.canFitResults()) return@ifPresent
				this.currentRecipe = Optional.of(recipe)
				this.maxProgress = recipe.recipeTime

				if (debugMode) {
					try {
						this.logger.info("after getting recipe: $recipe")
						this.logger.info("recipe id: ${present.id.path}")
						this.logger.info("item requirement: ${present.value.recipeInput}")
						this.logger.info("item output: ${present.value.recipeOutput}")
						this.logger.info("time required: ${present.value.recipeTime}")
					} catch (e : Exception) {
						this.logger.error(e)
					}
				}
			}
			level.setBlockAndUpdate(
				pos,
				state.setValue(
					BlockStateProperties.POWERED,
					false
				)
			)
		})
	}

	private fun resetRecipe() {
		this.level?.setBlockAndUpdate(this.blockPos, this.blockState.setValue(BlockStateProperties.POWERED, false))
		this.currentRecipe = Optional.empty()
		this.maxProgress = 0; this.progress = -1
		this.energyDivision = null
	}

	private fun inputStillValid(recipe : WheatCrusherRecipe) : Boolean =
		this.getItem(0) == recipe.recipeInput || !this.getItem(0).isEmpty

	private fun canFitResults() : Boolean = this.getItem(1).count.let { it < this.maxStackSize } == true
	private fun finalizeRecipe(level : Level, recipe : WheatCrusherRecipe) {
		this.itemSlots[0].shrink(recipe.recipeInput.count)
		val assemble =
			recipe.assemble(WheatCrusherInput(this.getItem(0), recipe.recipeInput.count), level.registryAccess())
		if (this.itemSlots[1].isEmpty) this.itemSlots[1] =
			assemble.copyWithCount(recipe.recipeOutput.count) else this.itemSlots[1].grow(recipe.recipeOutput.count)
	}

	override fun getMaxStackSize() : Int = 64
	override fun saveAdditional(tag : CompoundTag, registries : HolderLookup.Provider) {
		super.saveAdditional(tag, registries)
		tag.put("energy", this.energyHandler.serializeNBT(registries))
		tag.putInt("progress", this.progress)
		tag.putInt("maxProgress", this.maxProgress)

		ContainerHelper.saveAllItems(tag, this.itemSlots, registries)
	}

	override fun loadAdditional(tag : CompoundTag, registries : HolderLookup.Provider) {
		super.loadAdditional(tag, registries)
		this.energyHandler.deserializeNBT(registries, tag.get("energy") ?: return)
		this.progress = tag.getInt("progress")
		this.maxProgress = tag.getInt("maxProgress")

		this.itemSlots = NonNullList.withSize(2, ItemStack.EMPTY)
		ContainerHelper.loadAllItems(tag, this.itemSlots, registries)
	}

	override fun clearContent() : Unit = this.itemSlots.forEach { it.count = 0 }
	override fun getContainerSize() : Int = this.itemSlots.size
	override fun isEmpty() : Boolean = this.itemSlots.any { !it.isEmpty }
	override fun getItem(slot : Int) : ItemStack = this.itemSlots[slot]
	override fun removeItem(slot : Int, pAmount : Int) : ItemStack = this.itemSlots[slot].split(pAmount)
	override fun removeItemNoUpdate(slot : Int) : ItemStack = this.itemSlots[slot].copyAndClear()
	override fun setItem(slot : Int, stack : ItemStack) {
		this.itemSlots[slot] = stack
	}

	override fun stillValid(player : Player) : Boolean = Container.stillValidBlockEntity(this, player)
	override fun fillStackedContents(contents : StackedContents) {
		for (stack : ItemStack in this.itemSlots) {
			contents.accountSimpleStack(stack)
		}
	}
	// allow every face of the block to receive and extract items
	override fun getSlotsForFace(side : Direction) : IntArray = intArrayOf(0, 1)
	override fun canPlaceItemThroughFace(index : Int, itemStack : ItemStack, direction : Direction?) : Boolean =
		if (direction != null) this.getSlotsForFace(direction).contains(index) && index == 0 else true

	override fun canTakeItemThroughFace(index : Int, stack : ItemStack, direction : Direction) : Boolean =
		this.getSlotsForFace(direction).contains(index) && index != 0

	override fun getWidth() : Int = 1
	override fun getHeight() : Int = 1
	override fun getItems() : MutableList<ItemStack> = this.itemSlots
	override fun getUpdateTag(registries : HolderLookup.Provider) : CompoundTag =
		super.getUpdateTag(registries).also { this.saveAdditional(it, registries) }

	override fun getUpdatePacket() : Packet<ClientGamePacketListener> =
		ClientboundBlockEntityDataPacket.create(this)

	override fun createMenu(containerId : Int, playerInventory : Inventory, player : Player) : AbstractContainerMenu =
		WheatCrusherMenu(containerId, playerInventory, this)

	override fun getDisplayName() : Component = modTranslatable("block", "wheat_crusher")
}