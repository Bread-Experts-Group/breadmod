package bread.mod.breadmod.neoforge.datagen

import bread.mod.breadmod.ModMainCommon.modLocation
import bread.mod.breadmod.datagen.model.item.DataGenerateItemModel
import bread.mod.breadmod.datagen.model.item.ItemModelType
import bread.mod.breadmod.datagen.model.item.SmartItemModelProvider
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.Item
import net.neoforged.neoforge.client.model.generators.ItemModelProvider
import net.neoforged.neoforge.data.event.GatherDataEvent


/**
 * An annotation-based item model provider.
 *
 * @property modID The mod ID to save item model definitions for.
 *
 * @see DataGenerateItemModel
 * @author Miko Elbrecht
 * @since 1.0.0
 */
class SmartItemModelProviderNeoForge(
    modID: String, forClassLoader: ClassLoader, forPackage: Package
) : SmartItemModelProvider<GatherDataEvent>(modID, forClassLoader, forPackage) {
    /**
     * Generates item model definition files according to [DataGenerateItemModel] use in the specified package.
     * @author Miko Elbrecht
     * @since 1.0.0
     */
    override fun generate(forEvent: GatherDataEvent) {
        if (forEvent.includeClient()) {
            forEvent.generator.addProvider(
                true,
                object : ItemModelProvider(forEvent.generator.packOutput, modID, forEvent.existingFileHelper) {
                    fun handheldItem(item: Item) {
                        val itemId = item.descriptionId.substringAfterLast('.')
                        withExistingParent(
                            itemId,
                            ResourceLocation.parse("item/handheld")
                        ).texture(
                            "layer0",
                            modLocation("item/$itemId")
                        )
                    }

                    fun overlayItem(item: Item) {
                        val itemId = item.descriptionId.substringAfterLast('.')
                        withExistingParent(
                            itemId,
                            ResourceLocation.parse("item/generated")
                        ).texture(
                            "layer0",
                            modLocation("item/$itemId")
                        ).texture(
                            "layer1",
                            modLocation("item/${itemId}_overlay")
                        )
                    }

                    override fun registerModels() = getItemModelMap().forEach { (item, annotation) ->
                        when (annotation.type) {
                            ItemModelType.HANDHELD -> handheldItem(item)
                            ItemModelType.STANDARD -> basicItem(item)
                            ItemModelType.WITH_OVERLAY -> overlayItem(item)
                        }
                    }
                }
            )
        }
    }
}
