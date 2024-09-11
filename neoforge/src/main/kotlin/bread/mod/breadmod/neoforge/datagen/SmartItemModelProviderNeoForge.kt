package bread.mod.breadmod.neoforge.datagen

import bread.mod.breadmod.ModMainCommon.modLocation
import bread.mod.breadmod.datagen.model.item.DataGenerateHandheldItemModel
import bread.mod.breadmod.datagen.model.item.DataGenerateItemModel
import bread.mod.breadmod.datagen.model.item.SmartItemModelProvider
import dev.architectury.registry.registries.RegistrySupplier
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.Item
import net.neoforged.neoforge.client.model.generators.ItemModelProvider
import net.neoforged.neoforge.data.event.GatherDataEvent


/**
 * An annotation-based block model provider.
 *
 * @property modID The mod ID to save block model definitions for.
 *
 * @see bread.mod.breadmod.datagen.model.block
 * @author Miko Elbrecht
 * @since 1.0.0
 */
class SmartItemModelProviderNeoForge(
    modID: String, forClassLoader: ClassLoader, forPackage: Package
) : SmartItemModelProvider<GatherDataEvent>(modID, forClassLoader, forPackage) {
    /**
     * Generates block model definition files according to annotations in [bread.mod.breadmod.datagen.model.block]
     * use in the specified package.
     * @author Miko Elbrecht
     * @since 1.0.0
     */
    override fun generate(forEvent: GatherDataEvent) {
        if (forEvent.includeClient()) {
            forEvent.generator.addProvider(
                true,
                object : ItemModelProvider(forEvent.generator.packOutput, modID, forEvent.existingFileHelper) {
                    fun handheldItem(item: RegistrySupplier<Item>) {
                        withExistingParent(
                            item.id.path,
                            ResourceLocation.fromNamespaceAndPath("minecraft", "item/handheld")
                        ).texture(
                            "layer0",
                            modLocation("item/" + item.id.path)
                        )
                    }

                    override fun registerModels() = getItemModelMap().forEach { (register, annotation) ->
                        when (annotation.first) {
                            is DataGenerateItemModel -> basicItem(register.get())
                            is DataGenerateHandheldItemModel -> handheldItem(register)
                        }
                    }
                }
            )
        }
    }
}
