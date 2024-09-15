package bread.mod.breadmod.datagen.loot

import bread.mod.breadmod.datagen.DataProviderScanner
import bread.mod.breadmod.util.ensureRegistrySupplierAndValue
import net.minecraft.world.item.BlockItem

// todo not working atm
/* Caused by: java.lang.IllegalStateException: Missing loottable 'minecraft:blocks/stone' for 'minecraft:stone' */
// breakpoint line 23
//class SmartBlockLootProvider(
//    val modID: String, forClassLoader: ClassLoader, forPackage: Package
//) {
//    private val scanner: LibraryScanner = LibraryScanner(forClassLoader, forPackage)
//
//    fun getProvider(registries: HolderLookup.Provider): BlockLootSubProvider {
//        return object : BlockLootSubProvider(emptySet<Item>(), FeatureFlags.REGISTRY.allFlags(), registries) {
//            override fun generate() {
//                scanner.getObjectPropertiesAnnotatedWith<DataGenerateLoot>().forEach { (property, data) ->
//                    val block = data.first.ensureRegistrySupplierAndValue<BlockItem>(property).get().block
//                    val annotations = data.second
//
//                    annotations.forEach {
//                        when (it.dropType) {
//                            DataGenerateLoot.Type.SELF -> dropSelf(block)
//                            DataGenerateLoot.Type.OTHER -> {} // todo
//                        }
//                    }
//                }
//            }
//        }
//    }
//}

abstract class SmartBlockLootProvider<T>(
    modID: String, forClassLoader: ClassLoader, forPackage: Package
) : DataProviderScanner<T>(modID, forClassLoader, forPackage) {
    protected fun getBlockLootMap(): Map<BlockItem, Array<DataGenerateLoot>> = buildMap {
        scanner.getObjectPropertiesAnnotatedWith<DataGenerateLoot>().forEach { (property, data) ->
            this[data.first.ensureRegistrySupplierAndValue<BlockItem>(property).get()] = data.second
        }
    }
}