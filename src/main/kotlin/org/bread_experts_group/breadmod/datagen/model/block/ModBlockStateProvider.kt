package org.bread_experts_group.breadmod.datagen.model.block

import net.minecraft.core.Direction
import net.minecraft.core.Direction.UP
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.data.PackOutput
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.ButtonBlock
import net.minecraft.world.level.block.DoorBlock
import net.minecraft.world.level.block.FenceBlock
import net.minecraft.world.level.block.PipeBlock
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.properties.AttachFace
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.level.block.state.properties.Property
import net.neoforged.neoforge.client.model.generators.BlockStateProvider
import net.neoforged.neoforge.client.model.generators.ConfiguredModel
import net.neoforged.neoforge.client.model.generators.ConfiguredModel.Builder
import net.neoforged.neoforge.client.model.generators.ModelFile
import net.neoforged.neoforge.client.model.generators.ModelProvider
import net.neoforged.neoforge.client.model.generators.MultiPartBlockStateBuilder
import net.neoforged.neoforge.client.model.generators.MultiPartBlockStateBuilder.PartBuilder
import net.neoforged.neoforge.common.data.ExistingFileHelper
import org.apache.logging.log4j.LogManager
import org.bread_experts_group.breadmod.BreadMod
import org.bread_experts_group.breadmod.datagen.getBlock
import org.bread_experts_group.breadmod.mixin.client.IMultiPartBlockStateBuilderAccessor
import org.bread_experts_group.breadmod.registry.Registry
import org.bread_experts_group.breadmod.registry.block.ModBlocks
import org.bread_experts_group.breadmod.registry.block.ModBlocks.asBlock
import org.bread_experts_group.breadmod.registry.block.actual.util.ModBlockStateProperties
import org.bread_experts_group.breadmod.util.reflect.LibraryScanner
import org.bread_experts_group.breadmod.util.reflect.LibraryScanner.Companion.getScanner

class ModBlockStateProvider(
	packOutput: PackOutput,
	private val existingFileHelper: ExistingFileHelper
) : BlockStateProvider(packOutput, BreadMod.Companion.ID, existingFileHelper) {
	private val registryScanner: LibraryScanner = Registry::class.java.`package`.getScanner()
	override fun registerStatesAndModels() {
		this.registryScanner.resolveAnnotationValuePairs<DataGenerateModelBlockAndItem>()
			.forEach { (annotation, data) ->
				val block = data.getBlock("Block model generation (block / item, cubeAll)")
				val location = BuiltInRegistries.BLOCK.getKey(block)
				val aPath = annotation.extendedPath
				val path = if (aPath.isNotEmpty()) "block/${aPath}/${location.path}"
				else "block/${location.path}"
				val texture = ResourceLocation.fromNamespaceAndPath(location.namespace, path)

				this.simpleBlockWithItem(
					block,
					this.models().cubeAll(location.path, texture).renderType(annotation.renderType)
				)
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
//		this.horizontalBlock(ModBlocks.MONITOR.asBlock()) {
//			val model = this.models().cube(
//				"breadmod:block/monitor",
//				this.modLoc("${ModelProvider.BLOCK_FOLDER}/monitor_top"),
//				this.modLoc("${ModelProvider.BLOCK_FOLDER}/monitor_top"),
//				this.modLoc("${ModelProvider.BLOCK_FOLDER}/monitor_face"),
//				this.modLoc("${ModelProvider.BLOCK_FOLDER}/monitor_top"),
//				this.modLoc("${ModelProvider.BLOCK_FOLDER}/monitor_side"),
//				this.modLoc("${ModelProvider.BLOCK_FOLDER}/monitor_side")
//			).renderType("minecraft:cutout")
//
//			return@horizontalBlock model
//		}
//		this.simpleBlockItem(
//			ModBlocks.MONITOR.asBlock(),
//			this.models().getBuilder("breadmod:block/monitor")
//		)
//		this.horizontalBlock(ModBlocks.RADIO_BLOCK.asBlock()) {
//			val model = this.models().orientable(
//				"breadmod:block/radio_block",
//				this.modLoc("${ModelProvider.BLOCK_FOLDER}/radio_block_side"),
//				this.modLoc("${ModelProvider.BLOCK_FOLDER}/radio_block"),
//				this.modLoc("${ModelProvider.BLOCK_FOLDER}/radio_block_side")
//			)
//
//			return@horizontalBlock model
//		}
//		this.simpleBlockItem(
//			ModBlocks.RADIO_BLOCK.asBlock(),
//			this.models().getBuilder("breadmod:block/radio_block")
//		)
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
		this.models().getExistingFile(this.modLoc("${ModelProvider.BLOCK_FOLDER}/cable/cable_part"))
		this.models().getExistingFile(this.modLoc("${ModelProvider.BLOCK_FOLDER}/cable/cable_core"))
//		this.getMultipartBuilder(ModBlocks.CABLE.asBlock())
//			.createPart(core).end()
//			.createSidedPart(connector)
//		this.simpleBlockItem(ModBlocks.CABLE.asBlock(), core)
		// Diesel Generator
//		this.getMultipartBuilder(ModBlocks.DIESEL_GENERATOR.asBlock())
//			.createSidedPart(this.blockBenchBlockModel("diesel_generator/diesel_generator"))
//			.createSidedConditionalPart(
//				this.blockBenchBlockModel("diesel_generator/diesel_generator_door"),
//				BlockStateProperties.OPEN,
//				false
//			)
//			.createSidedConditionalPart(
//				this.blockBenchBlockModel("diesel_generator/diesel_generator_door_open"),
//				BlockStateProperties.OPEN,
//				true
//			)
//			.createSidedConditionalPart(
//				this.blockBenchBlockModel("diesel_generator/battery_upgrade"),
//				BlockStateProperties.OPEN,
//				true,
//				{ it.condition(ModBlockStateProperties.UPGRADE_ONE, true) }
//			)
//			.createSidedConditionalPart(
//				this.blockBenchBlockModel("diesel_generator/charging_upgrade"),
//				BlockStateProperties.OPEN,
//				true,
//				{ it.condition(ModBlockStateProperties.UPGRADE_TWO, true) }
//			)
//			.createSidedConditionalPart(
//				this.blockBenchBlockModel("diesel_generator/turbo_upgrade"),
//				BlockStateProperties.OPEN,
//				true,
//				{ it.condition(ModBlockStateProperties.UPGRADE_THREE, true) }
//			)
//		this.simpleBlockItem(
//			ModBlocks.DIESEL_GENERATOR.asBlock(),
//			this.blockBenchItemModel("diesel_generator_item")
//		)
		// Item Pedestal
		this.simpleBlock(ModBlocks.ITEM_PEDESTAL.asBlock(), this.blockBenchBlockModel("item_pedestal"))
		this.simpleBlockItem(ModBlocks.ITEM_PEDESTAL.asBlock(), this.blockBenchBlockModel("item_pedestal"))
		// Toaster
		// todo toaster multipart
//		this.horizontalBlockBenchModel(ModBlocks.TOASTER.asBlock(), "toaster")
//		this.simpleBlockItem(ModBlocks.TOASTER.asBlock(), this.blockBenchItemModel("toaster_item"))
		// Wheat Crusher
		this.modLoc("${ModelProvider.BLOCK_FOLDER}/machine_top")
		this.modLoc("${ModelProvider.BLOCK_FOLDER}/machine_side")
		this.modLoc("${ModelProvider.BLOCK_FOLDER}/machine_back")
//		this.horizontalBlock(ModBlocks.WHEAT_CRUSHER.asBlock()) { state ->
//			val machineOn = if (state.getValue(BlockStateProperties.POWERED)) "_on" else ""
//			val model = this.models().cube(
//				"breadmod:block/wheat_crusher$machineOn",
//				machineTop,
//				machineTop,
//				this.modLoc("${ModelProvider.BLOCK_FOLDER}/wheat_crusher_front$machineOn"),
//				machineBack,
//				machineSide,
//				machineSide
//			)
//			return@horizontalBlock model
//		}
//		this.simpleBlockItem(
//			ModBlocks.WHEAT_CRUSHER.asBlock(),
//			this.models().getBuilder("breadmod:block/wheat_crusher")
//		)
		// Dough Machine
//		this.horizontalBlock(ModBlocks.DOUGH_MACHINE.asBlock()) { state ->
//			val machineOn = if (state.getValue(BlockStateProperties.POWERED)) "_on" else ""
//			val model = this.models().cube(
//				"breadmod:block/dough_machine$machineOn",
//				machineTop,
//				machineTop,
//				this.modLoc("${ModelProvider.BLOCK_FOLDER}/dough_machine_front$machineOn"),
//				machineBack,
//				machineSide,
//				machineSide
//			)
//			return@horizontalBlock model
//		}
//		this.simpleBlockItem(
//			ModBlocks.DOUGH_MACHINE.asBlock(),
//			this.models().getBuilder("breadmod:block/dough_machine")
//		)
		this.horizontalBlock(ModBlocks.GENERATOR.asBlock()) { state ->
			val active = if (state.getValue(BlockStateProperties.POWERED)) "_on" else ""
			this.blockBenchBlockModel("generator$active")
		}
		this.simpleBlockItem(
			ModBlocks.GENERATOR.asBlock(),
			this.models().getBuilder("breadmod:block/generator")
		)
//		this.horizontalBlock(ModBlocks.ENERGY_STORAGE.asBlock()) { state ->
//			val blockFolder = "${ModelProvider.BLOCK_FOLDER}/energy_storage"
//			val storedLevel = when (state.getValue(ModBlockStateProperties.STORAGE_LEVEL)) {
//				1    -> "_one"
//				2    -> "_two"
//				3    -> "_three"
//				4    -> "_four"
//				else -> ""
//			}
//			val model = this.models().orientableWithBottom(
//				"breadmod:block/energy_storage$storedLevel",
//				this.modLoc("$blockFolder/side"),
//				this.modLoc("$blockFolder/front$storedLevel"),
//				this.modLoc("$blockFolder/bottom"),
//				this.modLoc("$blockFolder/top")
//			)
//			return@horizontalBlock model
//		}
//		this.simpleBlockItem(
//			ModBlocks.ENERGY_STORAGE.asBlock(),
//			this.models().getBuilder("breadmod:block/energy_storage")
//		)
		this.simpleBlockItem(
			ModBlocks.FLOUR_LAYER_BLOCK.asBlock(),
			this.models().getBuilder("breadmod:block/flour_layer_1")
		)
//		this.horizontalBlockBenchModelWithItem(ModBlocks.KEYBOARD.asBlock(), "keyboard")
		this.horizontalBlockBenchModelWithItem(ModBlocks.WAR_TERMINAL.asBlock(), "war_terminal")
		this.horizontalBlockBenchModelWithItem(ModBlocks.NIKO_BLOCK.asBlock(), "niko_block")
		this.horizontalBlockBenchModelWithItem(ModBlocks.OMANEKO_BLOCK.asBlock(), "omaneko_block")
		this.horizontalBlockBenchModelWithItem(ModBlocks.RICARD_BLOCK.asBlock(), "ricard_block")
		this.horizontalBlockBenchModelWithItem(ModBlocks.UNFUNNYLAD_BLOCK.asBlock(), "unfunnylad_block")
//		this.horizontalBlockBenchModelWithItem(ModBlocks.MICROWAVE.asBlock(), "microwave")
//		this.simpleBlockItem(ModBlocks.MICROWAVE.asBlock(), this.blockBenchItemModel("microwave_item"))
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
			val half = state.getValue(ModBlockStateProperties.TRIPLE_BLOCK)
			val segment = when (half) {
				ModBlockStateProperties.TripleBlockHalf.UPPER -> "upper"
				ModBlockStateProperties.TripleBlockHalf.MIDDLE -> "middle"
				ModBlockStateProperties.TripleBlockHalf.LOWER -> "lower"
				else -> ""
			}
			val model = this.blockBenchBlockModel("double_or_nothing_$segment")
			return@horizontalBlock model
		}
		// Creative Generator
//		this.horizontalBlockBenchModel(ModBlocks.CREATIVE_GENERATOR.asBlock(), "creative_generator")
		// Coffee Machine
		this.horizontalBlockBenchModelWithItem(ModBlocks.COFFEE_MACHINE.asBlock(), "coffee_machine")
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

	private fun Builder<PartBuilder>.rotated(
		direction: Direction
	): Builder<PartBuilder> = this.let {
		if (direction.axis.isHorizontal) {
			this.rotationY((direction.toYRot().toInt() + 180) % 360)
		} else this.rotationX(if (direction == UP) 270 else 90)
	}

	@Suppress("CAST_NEVER_SUCCEEDS")
	private fun MultiPartBlockStateBuilder.getOwner() =
		(this as IMultiPartBlockStateBuilderAccessor).`breadmod$getOwner`()

	private fun <T : Comparable<T>> MultiPartBlockStateBuilder.createSidedConditionalPart(
		model: ModelFile,
		condition: Property<T>,
		value: T,
		additionalConditions: (PartBuilder) -> Unit = {},
		uvLock: Boolean = false,
		vararg sides: Direction = Direction.entries.toTypedArray()
	): MultiPartBlockStateBuilder {
		val properties = this.getOwner().stateDefinition.properties
		val directions =
			if (properties.contains(BlockStateProperties.HORIZONTAL_FACING))
				sides.filter { it.axis.isHorizontal }.toTypedArray() else sides
		LogManager.getLogger().info("BLOCK STATE: $properties")

		fun <T : Comparable<T>> part(prop: Property<T>, propVal: T, direction: Direction) =
			this.part()
				.modelFile(model)
				.uvLock(uvLock)
				.rotated(direction)
				.addModel()
				.condition(prop, propVal)
				.condition(condition, value)
		if (properties.containsAll(PipeBlock.PROPERTY_BY_DIRECTION.values)) {
			directions.forEach { direction ->
				val property = PipeBlock.PROPERTY_BY_DIRECTION[direction] ?: return@forEach
				part(property, true, direction).also(additionalConditions)
			}
		} else if (properties.contains(BlockStateProperties.FACING)) {
			directions.forEach { direction ->
				part(BlockStateProperties.FACING, direction, direction).also(additionalConditions)
			}
		} else if (properties.contains(BlockStateProperties.HORIZONTAL_FACING)) {
			directions.forEach { direction ->
				part(BlockStateProperties.HORIZONTAL_FACING, direction, direction).also(additionalConditions)
			}
		}
		return this
	}

	private fun <T : Comparable<T>> MultiPartBlockStateBuilder.conditionalPart(
		model: ModelFile,
		condition: Property<T>,
		value: T,
		additionalConditions: (PartBuilder) -> Unit = {}
	): MultiPartBlockStateBuilder =
		this.part().modelFile(model).addModel().condition(condition, value).also(additionalConditions).end()

	private fun MultiPartBlockStateBuilder.createPart(model: ModelFile): PartBuilder =
		this.part().modelFile(model).addModel()

	private fun MultiPartBlockStateBuilder.createSidedPart(
		model: ModelFile,
		uvLock: Boolean = false,
		vararg sides: Direction = Direction.entries.toTypedArray()
	): MultiPartBlockStateBuilder {
		val properties = this.getOwner().stateDefinition.properties
		val directions =
			if (properties.contains(BlockStateProperties.HORIZONTAL_FACING))
				sides.filter { it.axis.isHorizontal }.toTypedArray() else sides

		fun <T : Comparable<T>> part(prop: Property<T>, value: T, direction: Direction) =
			this.part().modelFile(model).uvLock(uvLock).rotated(direction).addModel().condition(prop, value)
		if (properties.containsAll(PipeBlock.PROPERTY_BY_DIRECTION.values)) {
			directions.forEach { direction ->
				val property = PipeBlock.PROPERTY_BY_DIRECTION[direction] ?: return@forEach
				part(property, true, direction)
			}
		} else if (properties.contains(BlockStateProperties.FACING)) {
			directions.forEach { direction ->
				part(BlockStateProperties.FACING, direction, direction)
			}
		} else if (properties.contains(BlockStateProperties.HORIZONTAL_FACING)) {
			directions.forEach { direction ->
				part(BlockStateProperties.HORIZONTAL_FACING, direction, direction)
			}
		}
		// this is just here to satisfy compiler requirements.
		return this
	}
}