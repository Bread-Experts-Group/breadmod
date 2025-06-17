package org.bread_experts_group.breadmod.datagen.model.block

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
import org.bread_experts_group.breadmod.datagen.getBlock
import org.bread_experts_group.breadmod.registry.Registry
import org.bread_experts_group.breadmod.registry.block.ModBlocks
import org.bread_experts_group.breadmod.registry.block.ModBlocks.asBlock
import org.bread_experts_group.breadmod.registry.block.actual.DoubleOrNothingBlock
import org.bread_experts_group.breadmod.registry.block.actual.util.ModBlockStateProperties
import org.bread_experts_group.breadmod.util.reflect.LibraryScanner
import org.bread_experts_group.breadmod.util.reflect.LibraryScanner.Companion.getScanner

class ModBlockStateProvider(
	packOutput: PackOutput,
	private val existingFileHelper: ExistingFileHelper
) : BlockStateProvider(packOutput, BreadMod.Companion.ID, existingFileHelper) {
	private val registryScanner: LibraryScanner = Registry::class.java.`package`.getScanner()
	override fun registerStatesAndModels() {
		this.registryScanner.resolveAnnotationValuePairs<DataGenerateModelBlockAndItem>().forEach { (_, data) ->
			val block = data.getBlock("Block model generation (block / item, cubeAll)")
			this.simpleBlockWithItem(block, this.cubeAll(block))
		}
		this.simpleBlock(
			ModBlocks.NUKE.asBlock(),
			this.models().cubeBottomTop(
				"breadmod:block/nuke",
				this.modLoc("${ModelProvider.BLOCK_FOLDER}/nuke"),
				this.modLoc("${ModelProvider.BLOCK_FOLDER}/nuke_bottom"),
				this.modLoc("${ModelProvider.BLOCK_FOLDER}/nuke_top"),
			)
		)
		this.simpleBlockItem(
			ModBlocks.NUKE.asBlock(),
			this.models().getBuilder("breadmod:block/nuke")
		)

		this.horizontalBlock(ModBlocks.MONITOR.asBlock()) {
			val model = this.models().cube(
				"breadmod:block/monitor",
				this.modLoc("${ModelProvider.BLOCK_FOLDER}/monitor_top"),
				this.modLoc("${ModelProvider.BLOCK_FOLDER}/monitor_top"),
				this.modLoc("${ModelProvider.BLOCK_FOLDER}/monitor_face"),
				this.modLoc("${ModelProvider.BLOCK_FOLDER}/monitor_top"),
				this.modLoc("${ModelProvider.BLOCK_FOLDER}/monitor_side"),
				this.modLoc("${ModelProvider.BLOCK_FOLDER}/monitor_side")
			).renderType("minecraft:cutout")

			return@horizontalBlock model
		}
		this.simpleBlockItem(
			ModBlocks.MONITOR.asBlock(),
			this.models().getBuilder("breadmod:block/monitor")
		)

		this.horizontalBlock(ModBlocks.SOUND_BLOCK.asBlock()) {
			val model = this.models().orientable(
				"breadmod:block/sound_block",
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

		this.getVariantBuilder(ModBlocks.FLOUR_LAYER_BLOCK.asBlock()).forAllStates { state ->
			val layer = state.getValue(BlockStateProperties.LAYERS)
			ConfiguredModel.builder()
				.modelFile(
					this.models().getBuilder("breadmod:block/flour_layer_${layer}")
						.parent(
							this.models().withExistingParent(
								ModBlocks.getLocation(ModBlocks.FLOUR_LAYER_BLOCK.asBlock()).path,
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
		val connector = this.models().getExistingFile(this.modLoc("${ModelProvider.BLOCK_FOLDER}/cable/cable_part"))
		val core = this.models().getExistingFile(this.modLoc("${ModelProvider.BLOCK_FOLDER}/cable/cable_core"))
		this.getMultipartBuilder(ModBlocks.CABLE.asBlock())
			.part().modelFile(core).addModel().end()
			.part().modelFile(connector).uvLock(false).addModel()
			.condition(ModBlockStateProperties.NORTH, true).end()
			.part().modelFile(connector).uvLock(false).rotationY(90).addModel()
			.condition(ModBlockStateProperties.EAST, true).end()
			.part().modelFile(connector).uvLock(false).rotationY(180).addModel()
			.condition(ModBlockStateProperties.SOUTH, true).end()
			.part().modelFile(connector).uvLock(false).rotationY(270).addModel()
			.condition(ModBlockStateProperties.WEST, true).end()
			.part().modelFile(connector).uvLock(false).rotationX(270).addModel()
			.condition(ModBlockStateProperties.UP, true).end()
			.part().modelFile(connector).uvLock(false).rotationX(90).addModel()
			.condition(ModBlockStateProperties.DOWN, true).end()
		this.simpleBlockItem(ModBlocks.CABLE.asBlock(), core)
		val machineTop = this.modLoc("${ModelProvider.BLOCK_FOLDER}/machine_top")
		val machineSide = this.modLoc("${ModelProvider.BLOCK_FOLDER}/machine_side")
		val machineBack = this.modLoc("${ModelProvider.BLOCK_FOLDER}/machine_back")
		this.horizontalBlock(ModBlocks.WHEAT_CRUSHER.asBlock()) { state ->
			val machineOn = if (state.getValue(BlockStateProperties.POWERED)) "_on" else ""
			val model = this.models().cube(
				"breadmod:block/wheat_crusher$machineOn",
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
			val model = this.models().cube(
				"breadmod:block/dough_machine$machineOn",
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

		this.horizontalBlock(ModBlocks.GENERATOR.asBlock()) { state ->
			val active = if (state.getValue(BlockStateProperties.POWERED)) "_on" else ""
			this.blockBenchBlockModel("generator$active")
		}
		this.simpleBlockItem(
			ModBlocks.GENERATOR.asBlock(),
			this.models().getBuilder("breadmod:block/generator")
		)

		this.horizontalBlockBenchModelWithItem(
			ModBlocks.DIESEL_GENERATOR.asBlock(),
			"diesel_generator/diesel_generator"
		)

		this.horizontalBlock(ModBlocks.ENERGY_STORAGE.asBlock()) { state ->
			val blockFolder = "${ModelProvider.BLOCK_FOLDER}/energy_storage"
			val storedLevel = when (state.getValue(ModBlockStateProperties.STORAGE_LEVEL)) {
				1    -> "_one"
				2    -> "_two"
				3    -> "_three"
				4    -> "_four"
				else -> ""
			}
			val model = this.models().orientableWithBottom(
				"breadmod:block/energy_storage$storedLevel",
				this.modLoc("$blockFolder/side"),
				this.modLoc("$blockFolder/front$storedLevel"),
				this.modLoc("$blockFolder/bottom"),
				this.modLoc("$blockFolder/top")
			)
			return@horizontalBlock model
		}
		this.simpleBlockItem(
			ModBlocks.ENERGY_STORAGE.asBlock(),
			this.models().getBuilder("breadmod:block/energy_storage")
		)

		this.simpleBlockItem(
			ModBlocks.FLOUR_LAYER_BLOCK.asBlock(),
			this.models().getBuilder("breadmod:block/flour_layer_1")
		)

		this.horizontalBlockBenchModelWithItem(ModBlocks.KEYBOARD.asBlock(), "keyboard")
		this.horizontalBlockBenchModelWithItem(ModBlocks.WAR_TERMINAL.asBlock(), "war_terminal")
		this.horizontalBlockBenchModelWithItem(ModBlocks.NIKO_BLOCK.asBlock(), "niko_block")
		this.horizontalBlockBenchModelWithItem(ModBlocks.OMANEKO_BLOCK.asBlock(), "omaneko_block")
		this.horizontalBlockBenchModelWithItem(ModBlocks.RICARD_BLOCK.asBlock(), "ricard_block")
		this.horizontalBlockBenchModelWithItem(ModBlocks.UNFUNNYLAD_BLOCK.asBlock(), "unfunnylad_block")

		this.horizontalBlockBenchModel(ModBlocks.TOASTER.asBlock(), "toaster")
		this.simpleBlockItem(ModBlocks.TOASTER.asBlock(), this.blockBenchItemModel("toaster_item"))
		this.horizontalBlockBenchModelWithItem(ModBlocks.MICROWAVE.asBlock(), "microwave")
		this.simpleBlockItem(ModBlocks.MICROWAVE.asBlock(), this.blockBenchItemModel("microwave_item"))

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
		this.getVariantBuilder(ModBlocks.HELL_NAW_BUTTON.asBlock() as ButtonBlock).forAllStates { state: BlockState ->
			val facing = state.getValue(ButtonBlock.FACING)
			val face = state.getValue(ButtonBlock.FACE)
			val powered = state.getValue(ButtonBlock.POWERED)
			ConfiguredModel.builder()
				.modelFile(
					if (powered) this.blockBenchBlockModel("hell_naw_button_pressed")
					else this.blockBenchBlockModel("hell_naw_button")
				)
				.rotationX(if (face == AttachFace.FLOOR) 0 else if (face == AttachFace.WALL) 90 else 180)
				.rotationY((if (face == AttachFace.CEILING) facing else facing.opposite).toYRot().toInt())
				.build()
		}

		this.simpleBlockItem(
			ModBlocks.HELL_NAW_BUTTON.asBlock(),
			this.models().getBuilder("breadmod:block/hell_naw_button")
		)
		// Double or Nothing
		this.horizontalBlock(ModBlocks.DOUBLE_OR_NOTHING.asBlock()) { state ->
			val half = state.getValue(DoubleOrNothingBlock.Companion.TRIPLE_HALF)
			val segment = when (half) {
				ModBlockStateProperties.TripleBlockHalf.UPPER  -> "upper"
				ModBlockStateProperties.TripleBlockHalf.MIDDLE -> "middle"
				ModBlockStateProperties.TripleBlockHalf.LOWER  -> "lower"
				else                                           -> ""
			}
			val model = this.blockBenchBlockModel("double_or_nothing_$segment")
			return@horizontalBlock model
		}
		// Creative Generator
		this.horizontalBlockBenchModel(ModBlocks.CREATIVE_GENERATOR.asBlock(), "creative_generator")
	}

	private fun blockBenchBlockModel(model: String): ModelFile.ExistingModelFile =
		ModelFile.ExistingModelFile(this.modLoc("${ModelProvider.BLOCK_FOLDER}/$model"), this.existingFileHelper)

	private fun blockBenchItemModel(model: String): ModelFile.ExistingModelFile =
		ModelFile.ExistingModelFile(this.modLoc("${ModelProvider.ITEM_FOLDER}/$model"), this.existingFileHelper)

	private fun horizontalBlockBenchModel(block: Block, model: String): Unit =
		this.horizontalBlock(block, this.blockBenchBlockModel(model))

	private fun horizontalBlockBenchModelWithItem(block: Block, model: String) {
		this.horizontalBlockBenchModel(block, model)
		this.simpleBlockItem(block, this.blockBenchBlockModel(model))
	}
}