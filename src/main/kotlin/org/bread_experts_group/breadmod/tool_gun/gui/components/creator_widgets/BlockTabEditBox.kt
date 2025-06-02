package org.bread_experts_group.breadmod.tool_gun.gui.components.creator_widgets

import com.mojang.blaze3d.platform.InputConstants
import net.minecraft.client.gui.components.EditBox
import net.minecraft.core.BlockPos
import net.minecraft.core.component.TypedDataComponent
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.state.BlockState
import net.neoforged.neoforge.capabilities.Capabilities
import org.apache.logging.log4j.LogManager
import org.bread_experts_group.breadmod.client.render.localClient
import org.bread_experts_group.breadmod.util.getCapability
import kotlin.jvm.optionals.getOrNull

class BlockTabEditBox(
	private val container: BlockTab
) : EditBox(
	localClient.font,
	container.x + 155,
	container.y + 204,
	100,
	20,
	Component.literal("test")
) {
	init {
		this.setMaxLength(100)
	}

	private fun stateFromString(): BlockState =
		BuiltInRegistries.BLOCK.getOptional(ResourceLocation.tryParse(this.value)).getOrNull()
			?.defaultBlockState() ?: Blocks.AIR.defaultBlockState()

	override fun keyPressed(keyCode: Int, scanCode: Int, modifiers: Int): Boolean {
		if (keyCode == InputConstants.KEY_RETURN) {
			this.container.killDataWidgets()
			this.container.blockEntity = null
			this.container.screen.currentBlock = this.stateFromString()
			this.container.setBlockEntityFromState()
			this.container.populateStatePropertyWidgets()
			this.container.populateBlockEntityDataWidgets()
			this.container.populateCapabilityWidgets()
			this.container.blockEntity?.let { entity ->
				val data = entity.saveCustomOnly(this.container.level.registryAccess())
				data.tags.forEach { (key, tag) ->
					if (tag is CompoundTag) {
						tag.tags.forEach { (cKey, cTag) ->
							LogManager.getLogger().info("parent: $key, $cKey, ${cTag.type}")
						}
					}
					LogManager.getLogger().info("$key, ${tag.type}")
				}
				LogManager.getLogger().info(
					this.container.level.getCapability(
						Capabilities.EnergyStorage.BLOCK,
						BlockPos.ZERO,
						this.container.screen.currentBlock,
						entity
					)?.maxEnergyStored
				)
				entity.components().forEach<TypedDataComponent<*>>(LogManager.getLogger()::info)
			}
		}
		return super.keyPressed(keyCode, scanCode, modifiers)
	}
}