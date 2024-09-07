package bread.mod.breadmod.registry.block

import bread.mod.breadmod.ModMainCommon
import bread.mod.breadmod.block.BreadBlock
import bread.mod.breadmod.datagen.DataGenerateLanguage
import bread.mod.breadmod.registry.item.ModItems.registerBlockItem
import dev.architectury.registry.registries.DeferredRegister
import dev.architectury.registry.registries.RegistrySupplier
import net.minecraft.core.registries.Registries
import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.Item
import net.minecraft.world.level.block.Block

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

    @DataGenerateLanguage("en_us", "Bread Block")
    @DataGenerateLanguage("es_es", "Bloque De Pan")
    val BREAD_BLOCK: RegistrySupplier<BlockItem> = BLOCK_REGISTRY.registerBlockItem(
        "bread_block", { BreadBlock() }, Item.Properties()
    )
}