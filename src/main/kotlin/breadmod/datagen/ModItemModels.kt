package breadmod.datagen

import breadmod.BreadMod
import net.minecraft.data.PackOutput
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.Item
import net.minecraftforge.client.model.generators.ItemModelProvider
import net.minecraftforge.common.data.ExistingFileHelper
import net.minecraftforge.registries.RegistryObject

@Suppress("SpellCheckingInspection")
class ModItemModels(output: PackOutput?, modid: String?, existingFileHelper: ExistingFileHelper?) :
    ItemModelProvider(output, modid, existingFileHelper) {
    override fun registerModels() {
//        singleItem(ModItems.TEST_DISC)
//        singleItem(ModItems.TEST_BREAD)
        singleTexture("breadmod:test_bread", mcLoc("item/generated"), "layer0", modLoc("item/test_bread"))
        singleTexture("breadmod:bread_slice", mcLoc("item/generated"), "layer0", modLoc("item/bread_slice"))
        singleTexture("breadmod:music_disc_test", mcLoc("item/generated"), "layer0", modLoc("item/music_disc_test"))
        singleTexture("breadmod:bread_boots", mcLoc("item/generated"), "layer0", modLoc("item/bread_boots"))
        singleTexture("breadmod:bread_leggings", mcLoc("item/generated"), "layer0", modLoc("item/bread_leggings"))
        singleTexture("breadmod:bread_chestplate", mcLoc("item/generated"), "layer0", modLoc("item/bread_chestplate"))
        singleTexture("breadmod:bread_helmet", mcLoc("item/generated"), "layer0", modLoc("item/bread_helmet"))
    }

    private fun singleItem(item: RegistryObject<Item>) { // Figure out why this is causing an error when called
        withExistingParent(
            item.id.path,
            ResourceLocation("item/generated")
        ).texture(
            "layer0",
            ResourceLocation(BreadMod.ID, "item/" + item.id.path)
        )
    }
}