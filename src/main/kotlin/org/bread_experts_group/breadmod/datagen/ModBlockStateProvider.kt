package org.bread_experts_group.breadmod.datagen

import net.minecraft.core.Direction
import net.minecraft.data.PackOutput
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.ButtonBlock
import net.minecraft.world.level.block.DoorBlock
import net.minecraft.world.level.block.FenceBlock
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.properties.AttachFace
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.neoforged.neoforge.client.model.generators.BlockStateProvider
import net.neoforged.neoforge.client.model.generators.ConfiguredModel
import net.neoforged.neoforge.client.model.generators.ModelFile
import net.neoforged.neoforge.client.model.generators.ModelProvider
import net.neoforged.neoforge.common.data.ExistingFileHelper
import org.bread_experts_group.breadmod.BreadMod
import org.bread_experts_group.breadmod.registry.block.ModBlocks
import org.bread_experts_group.breadmod.registry.block.ModBlocks.asBlock

class ModBlockStateProvider(
	packOutput : PackOutput,
	private val existingFileHelper : ExistingFileHelper
) : BlockStateProvider(packOutput, BreadMod.ID, existingFileHelper) {
	override fun registerStatesAndModels() {
		this.blockWithItem(ModBlocks.BREAD_BLOCK.asBlock())
		this.blockWithItem(ModBlocks.REINFORCED_BREAD_BLOCK.asBlock())
		this.blockWithItem(ModBlocks.FLOUR_BLOCK.asBlock())
		this.blockWithItem(ModBlocks.LOW_DENSITY_CHARCOAL_BLOCK.asBlock())
		this.blockWithItem(ModBlocks.HAPPY_BLOCK.asBlock())
		this.blockWithItem(ModBlocks.CHARCOAL_BLOCK.asBlock())
		this.blockWithItem(ModBlocks.RANDOM_SOUND_BLOCK.asBlock())

		this.blockWithItem(ModBlocks.FLUID_ENERGY.asBlock())
		this.blockWithItem(ModBlocks.MULTI_ITEM_TEST.asBlock())
		this.blockWithItem(ModBlocks.MULTI_FLUID_TEST.asBlock())
		this.blockWithItem(ModBlocks.SINGLE_ITEM_TEST.asBlock())
		this.blockWithItem(ModBlocks.SINGLE_FLUID_TEST.asBlock())
		this.blockWithItem(ModBlocks.SINGLE_FLUID_ITEM_TEST.asBlock())
		this.blockWithItem(ModBlocks.COLORED_EMISSIVE_LIGHT_RED.asBlock())
		this.blockWithItem(ModBlocks.COLORED_EMISSIVE_LIGHT_GREEN.asBlock())
		this.blockWithItem(ModBlocks.COLORED_EMISSIVE_LIGHT_BLUE.asBlock())
		this.blockWithItem(ModBlocks.JADE_FLUID_TANK.asBlock())

		this.directionalBlock(ModBlocks.MONITOR.get().block) {
			val name = "breadmod:block/monitor"
			val model = this.models().cube(
				name,
				this.modLoc("${ModelProvider.BLOCK_FOLDER}/monitor_top"),
				this.modLoc("${ModelProvider.BLOCK_FOLDER}/monitor_top"),
				this.modLoc("${ModelProvider.BLOCK_FOLDER}/monitor_face"),
				this.modLoc("${ModelProvider.BLOCK_FOLDER}/monitor_top"),
				this.modLoc("${ModelProvider.BLOCK_FOLDER}/monitor_side"),
				this.modLoc("${ModelProvider.BLOCK_FOLDER}/monitor_side")
			)

			return@directionalBlock model
		}
		this.simpleBlockItem(
			ModBlocks.MONITOR.get().block,
			this.models().getBuilder("breadmod:block/monitor")
		)

		this.horizontalBlock(ModBlocks.SOUND_BLOCK.asBlock()) {
			val name = "breadmod:block/sound_block"
			val model = this.models().orientable(
				name,
				this.modLoc("${ModelProvider.BLOCK_FOLDER}/sound_block_side"),
				this.modLoc("${ModelProvider.BLOCK_FOLDER}/sound_block"),
				this.modLoc("${ModelProvider.BLOCK_FOLDER}/sound_block_side")
			)

			return@horizontalBlock model
		}
		this.simpleBlockItem(
			ModBlocks.SOUND_BLOCK.asBlock(),
			this.models().getBuilder("breadmod:block/sound_block")
		)

		this.getVariantBuilder(ModBlocks.FLOUR_LAYER_BLOCK.get().block).forAllStates { state ->
			val layer = state.getValue(BlockStateProperties.LAYERS)
			ConfiguredModel.builder()
				.modelFile(
					this.models().getBuilder("breadmod:block/flour_layer_${layer}")
						.parent(
							this.models().withExistingParent(
								ModBlocks.getLocation(ModBlocks.FLOUR_LAYER_BLOCK.get().block).path,
								this.mcLoc("${ModelProvider.BLOCK_FOLDER}/thin_block")
							)
						)
						.texture("texture", this.modLoc("${ModelProvider.BLOCK_FOLDER}/flour_block"))
						.texture("particle", this.modLoc("${ModelProvider.BLOCK_FOLDER}/flour_block"))
						.element()
						.from(0F, 0F, 0F)
						.to(16F, 2F * layer, 16F)
						.allFaces { d, u ->
							u.uvs(0F, if (d.axis.isVertical) 0F else 16F - (2 * layer), 16F, 16F)
							u.texture("#texture")
							if (d != Direction.UP) u.cullface(d)
						}
						.end()
				)
				.build()
		}
		val machineTop = this.modLoc("${ModelProvider.BLOCK_FOLDER}/machine_top")
		val machineSide = this.modLoc("${ModelProvider.BLOCK_FOLDER}/machine_side")
		val machineBack = this.modLoc("${ModelProvider.BLOCK_FOLDER}/machine_back")

		this.horizontalBlock(ModBlocks.WHEAT_CRUSHER.asBlock()) { state ->
			val machineOn = if (state.getValue(BlockStateProperties.POWERED)) "_on" else ""
			val name = "breadmod:block/wheat_crusher$machineOn"
			val model = this.models().cube(
				name,
				machineTop,
				machineTop,
				this.modLoc("${ModelProvider.BLOCK_FOLDER}/wheat_crusher_front$machineOn"),
				machineBack,
				machineSide,
				machineSide
			)
			return@horizontalBlock model
		}
		this.simpleBlockItem(
			ModBlocks.WHEAT_CRUSHER.asBlock(),
			this.models().getBuilder("breadmod:block/wheat_crusher")
		)

		this.horizontalBlock(ModBlocks.DOUGH_MACHINE.asBlock()) { state ->
			val machineOn = if (state.getValue(BlockStateProperties.POWERED)) "_on" else ""
			val name = "breadmod:block/dough_machine$machineOn"
			val model = this.models().cube(
				name,
				machineTop,
				machineTop,
				this.modLoc("${ModelProvider.BLOCK_FOLDER}/dough_machine_front$machineOn"),
				machineBack,
				machineSide,
				machineSide
			)
			return@horizontalBlock model
		}
		this.simpleBlockItem(
			ModBlocks.DOUGH_MACHINE.asBlock(),
			this.models().getBuilder("breadmod:block/dough_machine")
		)

		this.simpleBlockItem(
			ModBlocks.FLOUR_LAYER_BLOCK.get().block,
			this.models().getBuilder("breadmod:block/flour_layer_1")
		)

		this.horizontalBlockBenchModelWithItem(ModBlocks.KEYBOARD.asBlock(), "keyboard")
		this.horizontalBlockBenchModelWithItem(ModBlocks.WAR_TERMINAL.asBlock(), "war_terminal")
		this.horizontalBlockBenchModelWithItem(ModBlocks.NIKO_BLOCK.asBlock(), "niko_block")
		this.horizontalBlockBenchModelWithItem(ModBlocks.OMANEKO_BLOCK.asBlock(), "omaneko_block")
		this.horizontalBlockBenchModelWithItem(ModBlocks.RICARD_BLOCK.asBlock(), "ricard_block")
		this.horizontalBlockBenchModelWithItem(ModBlocks.UNFUNNYLAD_BLOCK.asBlock(), "unfunnylad_block")
		this.horizontalBlockBenchModelWithItem(ModBlocks.TOASTER.asBlock(), "toaster")
		this.horizontalBlockBenchModelWithItem(ModBlocks.MICROWAVE.asBlock(), "microwave")

		this.doorBlockWithRenderType(
			ModBlocks.BREAD_DOOR.asBlock() as DoorBlock,
			this.modLoc("${ModelProvider.BLOCK_FOLDER}/bread_door_bottom"),
			this.modLoc("${ModelProvider.BLOCK_FOLDER}/bread_door_top"),
			"minecraft:cutout"
		)

		this.fenceBlockWithRenderType(
			ModBlocks.BREAD_FENCE.asBlock() as FenceBlock,
			this.modLoc("${ModelProvider.BLOCK_FOLDER}/bread_block"),
			"minecraft:cutout"
		)
		// Hell Naw button
		this.getVariantBuilder(ModBlocks.HELL_NAW_BUTTON.asBlock() as ButtonBlock).forAllStates { state : BlockState ->
			val facing = state.getValue(ButtonBlock.FACING)
			val face = state.getValue(ButtonBlock.FACE)
			val powered = state.getValue(ButtonBlock.POWERED)
			ConfiguredModel.builder()
				.modelFile(
					if (powered) this.blockBenchModel("hell_naw_button_pressed")
					else this.blockBenchModel("hell_naw_button")
				)
				.rotationX(if (face == AttachFace.FLOOR) 0 else if (face == AttachFace.WALL) 90 else 180)
				.rotationY((if (face == AttachFace.CEILING) facing else facing.opposite).toYRot().toInt())
				.build()
		}

		this.simpleBlockItem(
			ModBlocks.HELL_NAW_BUTTON.get().block,
			this.models().getBuilder("breadmod:block/hell_naw_button")
		)
	}

	private fun blockWithItem(blockRegistryObject : Block) {
		this.simpleBlockWithItem(blockRegistryObject, this.cubeAll(blockRegistryObject))
	}

	private fun blockBenchModel(model : String) : ModelFile.ExistingModelFile =
		ModelFile.ExistingModelFile(this.modLoc("${ModelProvider.BLOCK_FOLDER}/$model"), this.existingFileHelper)

	private fun horizontalBlockBenchModelWithItem(block : Block, model : String) {
		this.horizontalBlock(block, this.blockBenchModel(model))
		this.simpleBlockItem(block, this.blockBenchModel(model))
	}
}