package bread.mod.breadmod.neoforge.datagen

import bread.mod.breadmod.datagen.model.item.DataGenerateHandheldItemModel
import bread.mod.breadmod.datagen.model.item.DataGenerateItemModel
import bread.mod.breadmod.datagen.model.item.SmartItemModelProvider
import com.google.gson.JsonElement
import net.minecraft.data.CachedOutput
import net.minecraft.data.DataProvider
import net.minecraft.data.PackOutput
import net.minecraft.data.models.model.ModelTemplates.FLAT_HANDHELD_ITEM
import net.minecraft.data.models.model.TextureMapping
import net.minecraft.resources.ResourceLocation
import net.neoforged.neoforge.client.model.generators.ItemModelProvider
import net.neoforged.neoforge.data.event.GatherDataEvent
import java.util.concurrent.CompletableFuture


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
                    private val modelTemplated = mutableMapOf<ResourceLocation, JsonElement>()

                    override fun registerModels() = getItemModelMap().forEach { (register, annotation) ->
                        when (annotation.first) {
                            is DataGenerateItemModel -> basicItem(register.get())
                            is DataGenerateHandheldItemModel -> FLAT_HANDHELD_ITEM.create(
                                register.id, TextureMapping.layer0(register.get())
                            ) { a, b -> modelTemplated[a] = b.get() }
                        }
                    }

                    override fun run(cachedOutput: CachedOutput): CompletableFuture<*> = CompletableFuture.allOf(
                        super.run(cachedOutput),
                        *modelTemplated.map { (target, model) ->
                            DataProvider.saveStable(
                                cachedOutput, model,
                                output
                                    .getOutputFolder(PackOutput.Target.RESOURCE_PACK)
                                    .resolve(target.namespace)
                                    .resolve("models")
                                    .resolve("item")
                                    .resolve("${target.path}.json")
                            )
                        }.toTypedArray()
                    )
                }
            )
        }
    }
}
