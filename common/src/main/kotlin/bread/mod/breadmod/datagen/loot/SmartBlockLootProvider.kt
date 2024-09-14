package bread.mod.breadmod.datagen.loot

import bread.mod.breadmod.reflection.LibraryScanner
import bread.mod.breadmod.util.ensureRegistrySupplierAndValue
import net.minecraft.core.HolderLookup
import net.minecraft.data.loot.BlockLootSubProvider
import net.minecraft.world.flag.FeatureFlags
import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.Item

// todo not working atm
/* Caused by: java.lang.IllegalStateException: Missing loottable 'minecraft:blocks/stone' for 'minecraft:stone' */
// breakpoint line 23
class SmartBlockLootProvider(
    val modID: String, forClassLoader: ClassLoader, forPackage: Package
) {
    private val scanner: LibraryScanner = LibraryScanner(forClassLoader, forPackage)

    fun getProvider(registries: HolderLookup.Provider): BlockLootSubProvider {
        return object : BlockLootSubProvider(emptySet<Item>(), FeatureFlags.REGISTRY.allFlags(), registries) {
            override fun generate() {
                scanner.getObjectPropertiesAnnotatedWith<DataGenerateLoot>().forEach { (property, data) ->
                    val block = data.first.ensureRegistrySupplierAndValue<BlockItem>(property).get().block
                    val annotations = data.second

                    annotations.forEach {
                        when (it.dropType) {
                            DataGenerateLoot.Type.SELF -> dropSelf(block)
                            DataGenerateLoot.Type.OTHER -> {} // todo
                        }
                    }
                }
            }
        }
    }
}