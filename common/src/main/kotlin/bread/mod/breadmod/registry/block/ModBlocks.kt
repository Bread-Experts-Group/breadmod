package bread.mod.breadmod.registry.block

import bread.mod.breadmod.ModMainCommon
import bread.mod.breadmod.block.BreadBlock
import bread.mod.breadmod.block.RandomSoundBlock
import bread.mod.breadmod.block.SoundBlock
import bread.mod.breadmod.block.WarTerminalBlock
import bread.mod.breadmod.block.util.FlammableBlock
import bread.mod.breadmod.datagen.model.block.DataGenerateCustomBlockModel
import bread.mod.breadmod.datagen.language.DataGenerateLanguage
import bread.mod.breadmod.datagen.model.block.BlockModelType
import bread.mod.breadmod.datagen.model.block.DataGenerateBlockAndItemModel
import bread.mod.breadmod.datagen.tag.DataGenerateTags
import bread.mod.breadmod.datagen.tag.TagTypes
import bread.mod.breadmod.item.util.FuelItem
import bread.mod.breadmod.registry.item.IRegisterSpecialCreativeTab
import bread.mod.breadmod.registry.item.ModItems.registerBlockItem
import bread.mod.breadmod.registry.menu.ModCreativeTabs
import dev.architectury.registry.registries.DeferredRegister
import dev.architectury.registry.registries.RegistrySupplier
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.registries.Registries
import net.minecraft.world.food.FoodProperties
import net.minecraft.world.food.Foods
import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.item.Item
import net.minecraft.world.item.Rarity
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.state.BlockBehaviour
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.phys.shapes.VoxelShape

/**
 * Blocks for the base bread mod.
 * @author Miko Elbrecht, Logan McLean
 * @since 1.0.0
 */
object ModBlocks {
    /**
     * The deferred register for blocks.
     * @author Logan McLean
     * @since 1.0.0
     */
    val BLOCK_REGISTRY: DeferredRegister<Block> = DeferredRegister.create(ModMainCommon.MOD_ID, Registries.BLOCK)

    @DataGenerateTags(TagTypes.MINEABLE_WITH_HOE)
    @FlammableBlock(30, 60)
    @DataGenerateBlockAndItemModel
    @DataGenerateLanguage("en_us", "Bread Block")
    @DataGenerateLanguage("es_es", "Bloque De Pan")
    val BREAD_BLOCK: RegistrySupplier<BlockItem> = BLOCK_REGISTRY.registerBlockItem(
        "bread_block", { BreadBlock() },
        Item.Properties()
            .food(
                FoodProperties.Builder()
                    .nutrition(Foods.BREAD.nutrition * 9)
                    .saturationModifier(Foods.BREAD.saturation * 9)
                    .build()
            )
    )

    @DataGenerateBlockAndItemModel
    @DataGenerateLanguage("en_us", "Reinforced Bread Block")
    val REINFORCED_BREAD_BLOCK = BLOCK_REGISTRY.registerBlockItem(
        "reinforced_bread_block",
        { Block(BlockBehaviour.Properties.ofFullCopy(
            Blocks.NETHERITE_BLOCK).strength(25f, 1200f))
        },
        Item.Properties()
    )

    @DataGenerateCustomBlockModel(true)
    @DataGenerateLanguage("en_us", "OMANEKO")
    val OMANEKO_BLOCK = BLOCK_REGISTRY.registerBlockItem(
        "omaneko_block", { CharacterModelBlock() }, Item.Properties().rarity(Rarity.EPIC)
    )

    @DataGenerateCustomBlockModel(true)
    @DataGenerateLanguage("en_us", "Niko Tenshot")
    val NIKO_BLOCK = BLOCK_REGISTRY.registerBlockItem(
        "niko_block", { CharacterModelBlock() }, Item.Properties().rarity(Rarity.EPIC)
    )

    @DataGenerateCustomBlockModel(true)
    @DataGenerateLanguage("en_us", "Ricardetex-Infinious")
    val RICARD_BLOCK = BLOCK_REGISTRY.registerBlockItem(
        "ricard_block", { CharacterModelBlock() }, Item.Properties().rarity(Rarity.EPIC)
    )

    @DataGenerateCustomBlockModel(true)
    @DataGenerateLanguage("en_us", "Unfunnylad")
    val UNFUNNYLAD_BLOCK = BLOCK_REGISTRY.registerBlockItem(
        "unfunnylad_block", { CharacterModelBlock() }, Item.Properties().rarity(Rarity.EPIC)
    )

    @DataGenerateTags(TagTypes.MINEABLE_WITH_PICKAXE)
    @FuelItem((80 * 20) * 4)
    @DataGenerateBlockAndItemModel
    @DataGenerateLanguage("en_us", "Charcoal Block")
    val CHARCOAL_BLOCK = BLOCK_REGISTRY.registerBlockItem(
        "charcoal_block", {
            Block(BlockBehaviour.Properties.ofFullCopy(Blocks.COAL_BLOCK).ignitedByLava())
        },
        Item.Properties()
    )

    // todo need single texture rotatable block generation
    @DataGenerateCustomBlockModel(true, BlockModelType.HORIZONTAL_FACING)
    @DataGenerateLanguage("en_us", "War Terminal")
    val WAR_TERMINAL = BLOCK_REGISTRY.registerBlockItem(
        "war_terminal", { WarTerminalBlock() }, Item.Properties())

    @DataGenerateBlockAndItemModel
    @DataGenerateLanguage("en_us", "Random Sound Generator")
    val RANDOM_SOUND_BLOCK = BLOCK_REGISTRY.registerBlockItem(
        "random_sound_block", { RandomSoundBlock() }, Item.Properties()
    )

    @DataGenerateCustomBlockModel(false, BlockModelType.ORIENTABLE)
    @DataGenerateLanguage("en_us", "Sound Block")
    val SOUND_BLOCK = BLOCK_REGISTRY.registerBlockItem(
        "sound_block", { SoundBlock() }, { block ->
            object : BlockItem(block, Properties()), IRegisterSpecialCreativeTab {
                override val creativeModeTabs: List<RegistrySupplier<CreativeModeTab>> = listOf(ModCreativeTabs.SPECIALS_TAB)
            }
        }
    )

    private class CharacterModelBlock : Block(Properties.of().noOcclusion()) {
        override fun createBlockStateDefinition(pBuilder: StateDefinition.Builder<Block, BlockState>) {
            pBuilder.add(BlockStateProperties.HORIZONTAL_FACING)
        }

        override fun getStateForPlacement(pContext: BlockPlaceContext): BlockState = defaultBlockState()
            .setValue(BlockStateProperties.HORIZONTAL_FACING, pContext.horizontalDirection)

        @Deprecated(
            "Deprecated in Java", ReplaceWith(
                "super.getShape(pState, pLevel, pPos, pContext)",
                "net.minecraft.world.level.block.Block"
            )
        )
        override fun getShape(
            pState: BlockState,
            pLevel: BlockGetter,
            pPos: BlockPos,
            pContext: CollisionContext
        ): VoxelShape = when (pState.getValue(BlockStateProperties.HORIZONTAL_FACING)) {
            Direction.NORTH, Direction.SOUTH -> box(0.0, 0.0, 3.0, 16.0, 20.0, 13.0)
            else -> box(3.0, 0.0, 0.0, 13.0, 20.0, 16.0)
        }
    }
}