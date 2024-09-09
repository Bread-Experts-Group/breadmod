package bread.mod.breadmod.fabric.datagen

import bread.mod.breadmod.ModMainCommon
import bread.mod.breadmod.datagen.ModRecipes
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider
import net.minecraft.data.recipes.RecipeOutput

internal class ModDataGenerator : DataGeneratorEntrypoint {
    override fun onInitializeDataGenerator(generator: FabricDataGenerator) {
        val pack = generator.createPack()

        SmartLanguageProviderFabric(
            ModMainCommon.MOD_ID,
            ModMainCommon::class.java.classLoader, ModMainCommon::class.java.`package`
        ).generate(pack)

        SmartModelProviderFabric(
            ModMainCommon.MOD_ID,
            ModMainCommon::class.java.classLoader, ModMainCommon::class.java.`package`
        ).generate(pack)

        SmartSoundProviderFabric(
            ModMainCommon.MOD_ID,
            ModMainCommon::class.java.classLoader, ModMainCommon::class.java.`package`
        ).generate(pack)

        pack.addProvider { output, registries -> object : FabricRecipeProvider(output, registries) {
            override fun buildRecipes(exporter: RecipeOutput) {
                ModRecipes.buildRecipes(exporter)
            }
        }}
    }
}