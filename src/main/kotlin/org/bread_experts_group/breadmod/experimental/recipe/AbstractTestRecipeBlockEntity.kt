package org.bread_experts_group.breadmod.experimental.recipe

import net.minecraft.core.BlockPos
import net.minecraft.core.HolderLookup
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.protocol.Packet
import net.minecraft.network.protocol.game.ClientGamePacketListener
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket
import net.minecraft.world.MenuProvider
import net.minecraft.world.item.crafting.Recipe
import net.minecraft.world.item.crafting.RecipeInput
import net.minecraft.world.item.crafting.RecipeManager
import net.minecraft.world.item.crafting.RecipeType
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import java.util.*

abstract class AbstractTestRecipeBlockEntity<INPUT : RecipeInput, RECIPE : Recipe<INPUT>>(
	pos: BlockPos,
	state: BlockState,
	type: BlockEntityType<*>,
	recipeType: RecipeType<RECIPE>
) : BlockEntity(type, pos, state), MenuProvider {
	/**
	 * Counts up by 1 every tick when the recipe is valid.
	 * The recipe is completed when this is above or equal to the max recipe time.
	 */
	var progress: Int = 0

	/**
	 * Not used in recipe logic, only as a visual indicator in guis for the player
	 */
	var maxProgress: Int = 0
	var currentRecipe: Optional<RECIPE> = Optional.empty()
	val recipeDial: RecipeManager.CachedCheck<INPUT, RECIPE> by lazy {
		RecipeManager.createCheck(recipeType)
	}

	abstract fun tick(level: Level, tPos: BlockPos, tState: BlockState)
	abstract fun finalizeRecipe(recipe: RECIPE, level: Level)
	fun syncToClients(): Unit? =
		this.level?.sendBlockUpdated(this.blockPos, this.blockState, this.blockState, Block.UPDATE_ALL)

	/**
	 * Resets the current recipe.
	 */
	fun resetRecipe() {
		this.currentRecipe = Optional.empty()
		this.maxProgress = 0; this.progress = 0
	}

	override fun saveAdditional(tag: CompoundTag, registries: HolderLookup.Provider) {
		super.saveAdditional(tag, registries)
		tag.putInt("progress", this.progress)
		tag.putInt("maxProgress", this.maxProgress)
	}

	override fun loadAdditional(tag: CompoundTag, registries: HolderLookup.Provider) {
		super.loadAdditional(tag, registries)
		this.progress = tag.getInt("progress")
		this.maxProgress = tag.getInt("maxProgress")
	}

	override fun getUpdateTag(registries: HolderLookup.Provider): CompoundTag =
		super.getUpdateTag(registries).also { this.saveAdditional(it, registries) }

	override fun getUpdatePacket(): Packet<ClientGamePacketListener> =
		ClientboundBlockEntityDataPacket.create(this)
}