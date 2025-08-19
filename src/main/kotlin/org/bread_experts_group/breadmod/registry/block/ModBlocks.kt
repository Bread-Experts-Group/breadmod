package org.bread_experts_group.breadmod.registry.block

import net.minecraft.ChatFormatting
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.player.Player
import net.minecraft.world.food.FoodProperties
import net.minecraft.world.inventory.MenuType
import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.item.Item
import net.minecraft.world.item.Item.Properties
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.item.Rarity
import net.minecraft.world.item.crafting.RecipeType
import net.minecraft.world.level.BlockAndTintGetter
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.DoorBlock
import net.minecraft.world.level.block.FenceBlock
import net.minecraft.world.level.block.SoundType
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockBehaviour
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.material.MapColor
import net.minecraft.world.level.material.PushReaction
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension
import net.neoforged.neoforge.registries.DeferredBlock
import net.neoforged.neoforge.registries.DeferredItem
import net.neoforged.neoforge.registries.DeferredRegister
import org.bread_experts_group.breadmod.ModDataComponents
import org.bread_experts_group.breadmod.api.ILightColored
import org.bread_experts_group.breadmod.datagen.lang.DataGenerateLanguage
import org.bread_experts_group.breadmod.datagen.loot.DataGenerateLootDropNothing
import org.bread_experts_group.breadmod.datagen.loot.DataGenerateLootDropSelf
import org.bread_experts_group.breadmod.datagen.model.block.DataGenerateModelBlockAndItem
import org.bread_experts_group.breadmod.datagen.model.item.DataGenerateModelSingleItem
import org.bread_experts_group.breadmod.datagen.tag.DataGenerateTagBlock
import org.bread_experts_group.breadmod.registry.RegistryProvider
import org.bread_experts_group.breadmod.registry.block.actual.BreadBlock
import org.bread_experts_group.breadmod.registry.block.actual.BreadModBlock
import org.bread_experts_group.breadmod.registry.block.actual.CableBlock
import org.bread_experts_group.breadmod.registry.block.actual.CharacterModelBlock
import org.bread_experts_group.breadmod.registry.block.actual.CoffeeMachineBlock
import org.bread_experts_group.breadmod.registry.block.actual.CreativeGeneratorBlock
import org.bread_experts_group.breadmod.registry.block.actual.DieselGeneratorBlock
import org.bread_experts_group.breadmod.registry.block.actual.DoubleOrNothingBlock
import org.bread_experts_group.breadmod.registry.block.actual.DoughMachineBlock
import org.bread_experts_group.breadmod.registry.block.actual.EnergyStorageBlock
import org.bread_experts_group.breadmod.registry.block.actual.FlammableBlock
import org.bread_experts_group.breadmod.registry.block.actual.FlourBlock
import org.bread_experts_group.breadmod.registry.block.actual.FlourLayeredBlock
import org.bread_experts_group.breadmod.registry.block.actual.GeneratorBlock
import org.bread_experts_group.breadmod.registry.block.actual.HappyBlock
import org.bread_experts_group.breadmod.registry.block.actual.HellNawButtonBlock
import org.bread_experts_group.breadmod.registry.block.actual.ItemInWorldBlock
import org.bread_experts_group.breadmod.registry.block.actual.ItemPedestalBlock
import org.bread_experts_group.breadmod.registry.block.actual.KeyboardBlock
import org.bread_experts_group.breadmod.registry.block.actual.MicrowaveBlock
import org.bread_experts_group.breadmod.registry.block.actual.MonitorBlock
import org.bread_experts_group.breadmod.registry.block.actual.NukeBlock
import org.bread_experts_group.breadmod.registry.block.actual.RadioBlock
import org.bread_experts_group.breadmod.registry.block.actual.RandomSoundBlock
import org.bread_experts_group.breadmod.registry.block.actual.ToasterBlock
import org.bread_experts_group.breadmod.registry.block.actual.WarTerminalBlock
import org.bread_experts_group.breadmod.registry.block.actual.WheatCrusherBlock
import org.bread_experts_group.breadmod.registry.block.actual.entity.BreadModBlockEntity
import org.bread_experts_group.breadmod.registry.block.actual.util.ModBlockSetTypes
import org.bread_experts_group.breadmod.registry.item.IRegisterSpecialCreativeTab
import org.bread_experts_group.breadmod.registry.item.ModItems
import org.bread_experts_group.breadmod.registry.menu.ModCreativeTabs
import org.bread_experts_group.breadmod.registry.recipe.actual.fluid_energy.test.FluidEnergyBlock
import org.bread_experts_group.breadmod.util.floatRoundEven
import java.awt.Color
import java.math.BigDecimal
import java.util.function.Supplier
import kotlin.math.roundToInt

object ModBlocks : RegistryProvider(
	Registries.BLOCK,
	Registries.MENU,
	Registries.BLOCK_ENTITY_TYPE
) {
	private val registry: DeferredRegister<Block> = this.getRegistry(Registries.BLOCK)
	private val menuRegistry: DeferredRegister<MenuType<*>> = this.getRegistry(Registries.MENU)
	private val blockEntityRegistry: DeferredRegister<BlockEntityType<*>> = this.getRegistry(
		Registries.BLOCK_ENTITY_TYPE
	)

	fun blockIterator(): Iterator<DeferredBlock<Block>> = object : Iterator<DeferredBlock<Block>> {
		val registryIterator = this@ModBlocks.registry.entries.iterator()
		override fun hasNext(): Boolean = this.registryIterator.hasNext()
		override fun next(): DeferredBlock<Block> {
			val holder = this.registryIterator.next()
			return DeferredBlock.createBlock(holder.id)
		}
	}

	fun getLocation(block: Block): ResourceLocation = BuiltInRegistries.BLOCK.getKey(block)

	@DataGenerateTagBlock(
		"minecraft:mineable/hoe",
		"minecraft:stone_ore_replaceables",
		"breadmod:mineable/knife",
		"c:storage_blocks/bread"
	)
	@DataGenerateLootDropSelf
	@DataGenerateModelBlockAndItem
	@DataGenerateLanguage
	val BREAD_BLOCK: DeferredItem<BlockItem> = this.registerBlockItem(
		"bread_block", ::BreadBlock, Properties().also {
			val breadFoodStats = Items.BREAD.getFoodProperties(Items.BREAD.defaultInstance, null) ?: return@also
			it.food(
				FoodProperties.Builder()
					.nutrition(breadFoodStats.nutrition * 9)
					.saturationModifier(breadFoodStats.saturation * 9)
					.build()
			)
		}
	)

	@DataGenerateTagBlock(
		"minecraft:mineable/pickaxe",
		"minecraft:beacon_base_blocks"
	)
	@DataGenerateLootDropSelf
	@DataGenerateModelBlockAndItem
	@DataGenerateLanguage
	val REINFORCED_BREAD_BLOCK: DeferredItem<BlockItem> = this.registerBlockItem(
		"reinforced_bread_block",
		{ Block(BlockBehaviour.Properties.ofFullCopy(Blocks.NETHERITE_BLOCK).strength(25f, 1200f)) },
		Properties().fireResistant()
	)

	@DataGenerateLootDropSelf
	@DataGenerateTagBlock("minecraft:mineable/pickaxe")
	@DataGenerateLanguage
	val MONITOR: DeferredItem<BlockItem> =
		this.registerBlockItem("monitor", ::MonitorBlock)

	@DataGenerateTagBlock(
		"minecraft:mineable/hoe",
		"c:storage_blocks/low_density_charcoal"
	)
	@DataGenerateLootDropSelf
	@DataGenerateModelBlockAndItem
	@DataGenerateLanguage(name = "Low-Density Charcoal Block")
	val LOW_DENSITY_CHARCOAL_BLOCK: DeferredItem<BlockItem> = this.registerBlockItem(
		"ld_charcoal_block",
		{ FlammableBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.BLACK_WOOL)) },
		{ block ->
			object : BlockItem(block, Properties()) {
				override fun getBurnTime(itemStack: ItemStack, recipeType: RecipeType<*>?): Int = 1600 * 4
			}
		}
	)

	@DataGenerateLootDropSelf
	@DataGenerateLanguage
	@DataGenerateLanguage(name = "Prolongs the inevitable.", suffix = ".tooltip")
	val WAR_TERMINAL: DeferredItem<BlockItem> = this.registerBlockItem("war_terminal", ::WarTerminalBlock)

	@DataGenerateLootDropSelf
	@DataGenerateModelBlockAndItem
	@DataGenerateLanguage
	@DataGenerateLanguage(name = "Uses the power of a die to make random noises.", suffix = ".tooltip")
	val RANDOM_SOUND_BLOCK: DeferredItem<BlockItem> = this.registerBlockItem("random_sound_block", ::RandomSoundBlock)

	@DataGenerateLootDropSelf
	@DataGenerateLanguage
	val RADIO_BLOCK: DeferredItem<BlockItem> = this.registerBlockItem("radio_block", ::RadioBlock)

	@DataGenerateLootDropSelf
	@DataGenerateTagBlock("c:storage_blocks/charcoal")
	@DataGenerateModelBlockAndItem
	@DataGenerateLanguage
	val CHARCOAL_BLOCK: DeferredItem<BlockItem> = this.registerBlockItem(
		"charcoal_block",
		{ FlammableBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.COAL_BLOCK)) },
		{ block ->
			object : BlockItem(block, Properties()) {
				override fun getBurnTime(itemStack: ItemStack, recipeType: RecipeType<*>?): Int = 1600 * 9
			}
		}
	)

	@DataGenerateLootDropSelf
	@DataGenerateLanguage
	val WHEAT_CRUSHER: DeferredItem<BlockItem> = this.registerBlockItem(
		"wheat_crusher",
		::WheatCrusherBlock,
		Properties()
	)

	@DataGenerateLootDropSelf
	@DataGenerateLanguage
	val DOUGH_MACHINE: DeferredItem<BlockItem> = this.registerBlockItem(
		"dough_machine",
		::DoughMachineBlock,
		Properties()
	)

	@DataGenerateLootDropSelf
	@DataGenerateLanguage
	val CREATIVE_GENERATOR: DeferredItem<BlockItem> = this.registerBlockItem(
		"creative_generator",
		::CreativeGeneratorBlock
	)

	@DataGenerateLootDropSelf
	@DataGenerateLanguage
	@DataGenerateLanguage(name = "I wouldn't cook charcoal in it..", suffix = ".tooltip")
	val TOASTER: DeferredItem<BlockItem> = this.registerBlockItem(
		"toaster",
		::ToasterBlock,
		Properties()
	)

	@DataGenerateLootDropSelf
	@DataGenerateLanguage
	val MICROWAVE: DeferredItem<BlockItem> = this.registerBlockItem(
		"microwave",
		::MicrowaveBlock,
		Properties()
	)

	@DataGenerateLootDropNothing
	@DataGenerateLanguage
	val ITEM_IN_WORLD_BLOCK: DeferredBlock<ItemInWorldBlock> = this.registerBlock("item_in_world", ::ItemInWorldBlock)

	@DataGenerateLanguage
	val ENERGY_STORAGE: DeferredItem<BlockItem> = this.registerBlockItem(
		"energy_storage",
		::EnergyStorageBlock
	) { block ->
		object : BlockItem(block, Item.Properties()) {
			override fun isBarVisible(stack: ItemStack): Boolean = true
			override fun getBarColor(stack: ItemStack): Int = ChatFormatting.RED.color ?: 0
			override fun getBarWidth(stack: ItemStack): Int {
				val amount = stack.getOrDefault(ModDataComponents.ENERGY, BigDecimal.ZERO)
				val capacity = stack.getOrDefault(ModDataComponents.ENERGY_CAPACITY, BigDecimal.ONE)
				return (amount.divide(capacity, floatRoundEven).toFloat() * 13).roundToInt()
			}
		}
	}

	@DataGenerateLootDropSelf
	@DataGenerateLanguage
	val GENERATOR: DeferredItem<BlockItem> = this.registerBlockItem("generator", ::GeneratorBlock)

	@DataGenerateLootDropSelf
	@DataGenerateLanguage
	val DIESEL_GENERATOR: DeferredItem<BlockItem> = this.registerBlockItem("diesel_generator", ::DieselGeneratorBlock)

	@DataGenerateLootDropSelf
	@DataGenerateLanguage
	val CABLE: DeferredItem<BlockItem> = this.registerBlockItem(
		"cable",
		{
			CableBlock()
		}
	)

	@DataGenerateTagBlock("minecraft:mineable/shovel")
	@DataGenerateModelBlockAndItem
	@DataGenerateLanguage
	val FLOUR_BLOCK: DeferredItem<BlockItem> = this.registerBlockItem("flour_block", ::FlourBlock)

	@DataGenerateTagBlock("minecraft:mineable/shovel")
	@DataGenerateLanguage(name = "Flour")
	val FLOUR_LAYER_BLOCK: DeferredItem<BlockItem> = this.registerBlockItem("flour_layer", ::FlourLayeredBlock)

	@DataGenerateLootDropSelf
	@DataGenerateModelBlockAndItem
	@DataGenerateLanguage
	val HAPPY_BLOCK: DeferredItem<BlockItem> = this.registerBlockItem("happy_block", ::HappyBlock)

	@DataGenerateLootDropSelf
	@DataGenerateLanguage
	val NUKE: DeferredItem<BlockItem> = this.registerBlockItem("nuke", ::NukeBlock)

	@DataGenerateLootDropSelf
	@DataGenerateLanguage
	val KEYBOARD: DeferredItem<BlockItem> = this.registerBlockItem(
		"keyboard",
		::KeyboardBlock,
		Properties().stacksTo(1)
	)

	@DataGenerateLootDropSelf
	@DataGenerateLanguage
	val HELL_NAW_BUTTON: DeferredItem<BlockItem> = this.registerBlockItem(
		"hell_naw_button",
		::HellNawButtonBlock,
		Properties()
	)

	@DataGenerateLootDropSelf
	@DataGenerateLanguage(name = "NIKO TENSHOT")
	val NIKO_BLOCK: DeferredItem<BlockItem> = this.registerBlockItem(
		"niko_block",
		::CharacterModelBlock,
		Properties().rarity(Rarity.EPIC)
	)

	@DataGenerateLootDropSelf
	@DataGenerateLanguage(name = "OMANEKO")
	val OMANEKO_BLOCK: DeferredItem<BlockItem> = this.registerBlockItem(
		"omaneko_block",
		::CharacterModelBlock,
		Properties().rarity(Rarity.EPIC)
	)

	@DataGenerateLootDropSelf
	@DataGenerateLanguage(name = "Ricard")
	val RICARD_BLOCK: DeferredItem<BlockItem> = this.registerBlockItem(
		"ricard_block",
		::CharacterModelBlock,
		Properties().rarity(Rarity.EPIC)
	)

	@DataGenerateLootDropSelf
	@DataGenerateLanguage(name = "Unfunnylad")
	val UNFUNNYLAD_BLOCK: DeferredItem<BlockItem> = this.registerBlockItem(
		"unfunnylad_block",
		::CharacterModelBlock,
		Properties().rarity(Rarity.EPIC)
	)

	@DataGenerateLootDropSelf
	@DataGenerateTagBlock("minecraft:fences")
	@DataGenerateLanguage
	val BREAD_FENCE: DeferredItem<BlockItem> = this.registerBlockItem(
		"bread_fence",
		{
			object : FenceBlock(
				Properties.of()
					.forceSolidOn()
					.sound(SoundType.GRASS)
					.strength(1.0F)
			) {
				override fun getFireSpreadSpeed(
					state: BlockState,
					level: BlockGetter,
					pos: BlockPos,
					direction: Direction
				): Int = 100

				override fun isFlammable(
					state: BlockState,
					level: BlockGetter,
					pos: BlockPos,
					direction: Direction
				): Boolean = true
			}
		}
	)

	@DataGenerateModelSingleItem
	@DataGenerateLanguage
	val BREAD_DOOR: DeferredItem<BlockItem> = this.registerBlockItem(
		"bread_door",
		{
			object : DoorBlock(
				ModBlockSetTypes.BREAD,
				Properties.of()
					.mapColor(MapColor.WOOD)
					.strength(1.0F)
					.pushReaction(PushReaction.DESTROY)
			) {
				override fun isFlammable(
					state: BlockState,
					level: BlockGetter,
					pos: BlockPos,
					direction: Direction
				): Boolean = true

				override fun canHarvestBlock(
					state: BlockState,
					level: BlockGetter,
					pos: BlockPos,
					player: Player
				): Boolean = !player.isCreative
			}
		}
	)

	@DataGenerateLanguage(name = "Double or Nothing")
	@DataGenerateLootDropSelf
	val DOUBLE_OR_NOTHING: DeferredItem<BlockItem> = this.registerBlockItem(
		"double_or_nothing",
		::DoubleOrNothingBlock
	)

	@DataGenerateLanguage
	@DataGenerateLootDropSelf
	// todo model, BE, BER, textures
	val COFFEE_MACHINE: DeferredItem<BlockItem> = this.registerBlockItem(
		"coffee_machine",
		::CoffeeMachineBlock
	)

	fun <T : Block> registerBlock(
		id: String,
		block: () -> T
	): DeferredBlock<T> {
		val holder = this.registry.register(id, Supplier {
			val actual = block()
			if (actual is BreadModBlock) {
				if (actual.shouldCreateEntity()) {
					@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
					actual.blockEntityType = this.blockEntityRegistry.register(id, Supplier {
						BlockEntityType.Builder.of(
							{ p, s -> actual.newBlockEntity(p, s) },
							actual
						).build(null)
					})
				}
				val menu = actual.ofMenu()
				if (menu != null) {
					actual.menuType = this.menuRegistry.register(id) { id: ResourceLocation ->
						IMenuTypeExtension.create { id, inventory, byteBuf ->
							@Suppress("UNCHECKED_CAST")
							menu(
								actual.menuType!!.get(), id, inventory,
								inventory.player
									.level()
									.getBlockEntity(
										byteBuf.readBlockPos(),
										actual.blockEntityType!!.get() as BlockEntityType<BreadModBlockEntity>
									)
									.get()
							)
						}
					}
				}
			}
			actual
		})
		return DeferredBlock.createBlock(holder.id)
	}

	private fun registerBlockItem(
		id: String,
		block: () -> Block,
		properties: Properties = Properties()
	): DeferredItem<BlockItem> = this.registerBlockItem(id, block) { blockItem -> BlockItem(blockItem, properties) }

	private fun <T : Block> registerBlockItem(
		id: String,
		block: () -> T,
		item: (block: T) -> BlockItem
	): DeferredItem<BlockItem> {
		val supplier = this.registerBlock(id, block)
		return ModItems.registerItem(id) { item(supplier.get()) }
	}

	private fun registerSimpleBlockItem(id: String): DeferredItem<BlockItem> =
		this.registerBlockItem(id, { Block(BlockBehaviour.Properties.of()) })

	private fun itemWithCreativeTab(
		block: Block,
		properties: Properties,
		creativeTabs: List<Supplier<CreativeModeTab>>
	): BlockItem = object : BlockItem(block, properties), IRegisterSpecialCreativeTab {
		override val creativeModeTabs: List<Supplier<CreativeModeTab>> = creativeTabs
	}

	// todo textures, renderer, BE logic
	@DataGenerateLootDropSelf
	@DataGenerateLanguage
	val ITEM_PEDESTAL: DeferredItem<BlockItem> = this.registerBlockItem(
		"item_pedestal",
		::ItemPedestalBlock,
		Properties()
	)

	@DataGenerateLootDropSelf
	@DataGenerateModelBlockAndItem
	@DataGenerateLanguage(name = "Fluid Energy Recipe")
	val FLUID_ENERGY: DeferredItem<BlockItem> = this.registerBlockItem(
		"fluid_energy_block",
		::FluidEnergyBlock
	) { block -> this.itemWithCreativeTab(block, Properties(), listOf(ModCreativeTabs.EXPERIMENTAL_TAB)) }

	// EXPERIMENTAL PAST THIS POINT
	@DataGenerateLootDropNothing
	@DataGenerateModelBlockAndItem
	@DataGenerateLanguage(name = "EXPERIMENTAL COLORED EMISSIVE LIGHT, RED")
	val COLORED_EMISSIVE_LIGHT_RED: DeferredItem<BlockItem> = this.registerBlockItem(
		"colored_emissive_light_red",
		{
			object : Block(Properties.of().lightLevel { _ -> 15 }), ILightColored {
				override fun getLightColor(
					level: BlockAndTintGetter,
					blockState: BlockState,
					position: BlockPos
				): Int = Color.RED.rgb
			}
		},
		{ block -> this.itemWithCreativeTab(block, Properties(), listOf(ModCreativeTabs.EXPERIMENTAL_TAB)) }
	)

	@DataGenerateLanguage(name = "basi0g01")
	@DataGenerateLootDropNothing
	@DataGenerateModelBlockAndItem("test_colors")
	val BASI_0G01: DeferredItem<BlockItem> = this.registerSimpleBlockItem("basi0g01")

	@DataGenerateLanguage(name = "basi0g02")
	@DataGenerateLootDropNothing
	@DataGenerateModelBlockAndItem("test_colors")
	val BASI_0G02: DeferredItem<BlockItem> = this.registerSimpleBlockItem("basi0g02")

	@DataGenerateLanguage(name = "basi0g04")
	@DataGenerateLootDropNothing
	@DataGenerateModelBlockAndItem("test_colors")
	val BASI_0G04: DeferredItem<BlockItem> = this.registerSimpleBlockItem("basi0g04")

	@DataGenerateLanguage(name = "basi0g08")
	@DataGenerateLootDropNothing
	@DataGenerateModelBlockAndItem("test_colors")
	val BASI_0G08: DeferredItem<BlockItem> = this.registerSimpleBlockItem("basi0g08")

	@DataGenerateLanguage(name = "basi0g16")
	@DataGenerateLootDropNothing
	@DataGenerateModelBlockAndItem("test_colors")
	val BASI_0G16: DeferredItem<BlockItem> = this.registerSimpleBlockItem("basi0g16")

	@DataGenerateLanguage(name = "basi2c08")
	@DataGenerateLootDropNothing
	@DataGenerateModelBlockAndItem("test_colors")
	val BASI_2C08: DeferredItem<BlockItem> = this.registerSimpleBlockItem("basi2c08")

	@DataGenerateLanguage(name = "basi2c16")
	@DataGenerateLootDropNothing
	@DataGenerateModelBlockAndItem("test_colors")
	val BASI_2C16: DeferredItem<BlockItem> = this.registerSimpleBlockItem("basi2c16")

	@DataGenerateLanguage(name = "basi3p01")
	@DataGenerateLootDropNothing
	@DataGenerateModelBlockAndItem("test_colors")
	val BASI_3P01: DeferredItem<BlockItem> = this.registerSimpleBlockItem("basi3p01")

	@DataGenerateLanguage(name = "basi3p02")
	@DataGenerateLootDropNothing
	@DataGenerateModelBlockAndItem("test_colors")
	val BASI_3P02: DeferredItem<BlockItem> = this.registerSimpleBlockItem("basi3p02")

	@DataGenerateLanguage(name = "basi3p04")
	@DataGenerateLootDropNothing
	@DataGenerateModelBlockAndItem("test_colors")
	val BASI_3P04: DeferredItem<BlockItem> = this.registerSimpleBlockItem("basi3p04")

	@DataGenerateLanguage(name = "basi3p08")
	@DataGenerateLootDropNothing
	@DataGenerateModelBlockAndItem("test_colors")
	val BASI_3P08: DeferredItem<BlockItem> = this.registerSimpleBlockItem("basi3p08")

	@DataGenerateLanguage(name = "basi4a08")
	@DataGenerateLootDropNothing
	@DataGenerateModelBlockAndItem("test_colors", "translucent")
	val BASI_4A08: DeferredItem<BlockItem> = this.registerSimpleBlockItem("basi4a08")

	@DataGenerateLanguage(name = "basi4a16")
	@DataGenerateLootDropNothing
	@DataGenerateModelBlockAndItem("test_colors", "translucent")
	val BASI_4A16: DeferredItem<BlockItem> = this.registerSimpleBlockItem("basi4a16")

	@DataGenerateLanguage(name = "basi6a08")
	@DataGenerateLootDropNothing
	@DataGenerateModelBlockAndItem("test_colors", "translucent")
	val BASI_6A08: DeferredItem<BlockItem> = this.registerSimpleBlockItem("basi6a08")

	@DataGenerateLootDropNothing
	@DataGenerateModelBlockAndItem
	@DataGenerateLanguage(name = "EXPERIMENTAL COLORED EMISSIVE LIGHT, GREEN")
	val COLORED_EMISSIVE_LIGHT_GREEN: DeferredItem<BlockItem> = this.registerBlockItem(
		"colored_emissive_light_green",
		{
			object : Block(Properties.of().lightLevel { _ -> 15 }), ILightColored {
				override fun getLightColor(
					level: BlockAndTintGetter,
					blockState: BlockState,
					position: BlockPos
				): Int = Color.GREEN.rgb
			}
		},
		{ block -> this.itemWithCreativeTab(block, Properties(), listOf(ModCreativeTabs.EXPERIMENTAL_TAB)) }
	)

	@DataGenerateLootDropNothing
	@DataGenerateModelBlockAndItem
	@DataGenerateLanguage(name = "EXPERIMENTAL COLORED EMISSIVE LIGHT, BLUE")
	val COLORED_EMISSIVE_LIGHT_BLUE: DeferredItem<BlockItem> = this.registerBlockItem(
		"colored_emissive_light_blue",
		{
			object : Block(Properties.of().lightLevel { _ -> 15 }), ILightColored {
				override fun getLightColor(
					level: BlockAndTintGetter,
					blockState: BlockState,
					position: BlockPos
				): Int = Color.BLUE.rgb
			}
		},
		{ block -> this.itemWithCreativeTab(block, Properties(), listOf(ModCreativeTabs.EXPERIMENTAL_TAB)) }
	)
//	@DataGenerateLootDropNothing
//	@DataGenerateModelBlockAndItem
//	@DataGenerateLanguage(name = "JadeFluidTank")
//	val JADE_FLUID_TANK: DeferredItem<BlockItem> = this.registerBlockItem(
//		"jade_fluid_tank",
//		::FluidTankJadeBlock
//	) { block -> this.itemWithCreativeTab(block, Properties(), listOf(ModCreativeTabs.EXPERIMENTAL_TAB)) }
}
