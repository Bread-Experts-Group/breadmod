package bread.mod.breadmod.registry.block

import bread.mod.breadmod.ModMainCommon
import bread.mod.breadmod.block.BreadBlock
import bread.mod.breadmod.registry.item.ModItems.registerBlockItem
import dev.architectury.registry.registries.DeferredRegister
import dev.architectury.registry.registries.RegistrySupplier
import net.minecraft.core.registries.Registries
import net.minecraft.world.item.Item
import net.minecraft.world.level.block.Block

@Suppress("unused")
object ModBlocks {
    val BLOCK_REGISTRY: DeferredRegister<Block> = DeferredRegister.create(ModMainCommon.MOD_ID, Registries.BLOCK)

    val BREAD_BLOCK: RegistrySupplier<Block> = BLOCK_REGISTRY.registerBlockItem(
        "bread_block", { BreadBlock() }, Item.Properties()
    )
}