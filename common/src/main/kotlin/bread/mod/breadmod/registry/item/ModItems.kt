package bread.mod.breadmod.registry.item

import bread.mod.breadmod.ModMainCommon
import bread.mod.breadmod.datagen.language.DataGenerateLanguage
import bread.mod.breadmod.datagen.model.item.DataGenerateItemModel
import bread.mod.breadmod.item.BreadShieldItem
import bread.mod.breadmod.item.DopedBreadItem
import bread.mod.breadmod.item.TestBreadItem
import bread.mod.breadmod.item.UltimateBreadItem
import dev.architectury.registry.registries.DeferredRegister
import dev.architectury.registry.registries.RegistrySupplier
import net.minecraft.core.registries.Registries
import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.Item
import net.minecraft.world.level.block.Block

object ModItems {
    val ITEM_REGISTRY: DeferredRegister<Item> = DeferredRegister.create(ModMainCommon.MOD_ID, Registries.ITEM)

    @DataGenerateItemModel
    @DataGenerateLanguage("en_us", "Test Bread")
    val TEST_BREAD: RegistrySupplier<Item> = ITEM_REGISTRY.register("test_bread") { TestBreadItem() }
    @DataGenerateItemModel
    @DataGenerateLanguage("en_us", "Ultimate Bread")
    val ULTIMATE_BREAD: RegistrySupplier<Item> = ITEM_REGISTRY.register("ultimate_bread") { UltimateBreadItem() }
    @DataGenerateLanguage("en_us", "Bread Shield")
    val BREAD_SHIELD: RegistrySupplier<BreadShieldItem> = ITEM_REGISTRY.register("bread_shield") { BreadShieldItem() }
    @DataGenerateItemModel
    @DataGenerateLanguage("en_us", "Doped Bread")
    val DOPED_BREAD: RegistrySupplier<DopedBreadItem> = ITEM_REGISTRY.register("doped_bread") { DopedBreadItem() }

    internal fun DeferredRegister<Block>.registerBlockItem(
        id: String,
        block: () -> Block,
        properties: Item.Properties
    ): RegistrySupplier<BlockItem> = this.register(id, block).let { blockSupply ->
        ITEM_REGISTRY.register(id) { BlockItem(blockSupply.get(), properties) }
    }

    internal fun DeferredRegister<Block>.registerBlockItem(
        id: String,
        block: () -> Block,
        item: (block: Block) -> BlockItem
    ): RegistrySupplier<BlockItem> = this.register(id, block).let { blockSupply ->
        ITEM_REGISTRY.register(id) { item(blockSupply.get()) }
    }
}