package bread.mod.breadmod.datagen.tag

import bread.mod.breadmod.datagen.DataProviderScanner
import net.minecraft.world.item.Item
import net.minecraft.world.level.block.Block

abstract class SmartTagProvider<T> (
    modID: String, forClassLoader: ClassLoader, forPackage: Package
) : DataProviderScanner<T>(modID, forClassLoader, forPackage) {
    protected fun getBlockTags(): Map<Block, DataGenerateTags> = buildMap {
        scanner.getObjectPropertiesAnnotatedWith<DataGenerateTags>().forEach { (value, annotation) ->
            this[value.get() as Block] = annotation[0]
        }
    }
    protected fun getItemTags(): Map<Item, DataGenerateTags> = buildMap {
        scanner.getObjectPropertiesAnnotatedWith<DataGenerateTags>().forEach { (value, annotation) ->
            this[value.get() as Item] = annotation[0]
        }
    }
}