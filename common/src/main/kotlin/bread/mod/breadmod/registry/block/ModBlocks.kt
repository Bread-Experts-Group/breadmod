package bread.mod.breadmod.registry.block

import bread.mod.breadmod.ModMainCommon
import bread.mod.breadmod.registry.item.ModItems
import bread.mod.breadmod.util.registerBlockItem
import dev.architectury.registry.registries.DeferredRegister
import dev.architectury.registry.registries.RegistrySupplier
import net.minecraft.core.registries.Registries
import net.minecraft.world.item.Item
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockBehaviour

@Suppress("unused")
object ModBlocks {
    val deferredRegister: DeferredRegister<Block> = DeferredRegister.create(ModMainCommon.MOD_ID, Registries.BLOCK)

    val BREAD_BLOCK: RegistrySupplier<Block> = deferredRegister.registerBlockItem(
        ModItems.deferredRegister,
        "bread_block",
        { Block(BlockBehaviour.Properties.of()) },
        Item.Properties()
    )
}