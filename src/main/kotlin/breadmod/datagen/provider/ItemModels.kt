package breadmod.datagen.provider

import breadmod.BreadMod
import net.minecraft.data.DataGenerator
import net.minecraftforge.client.model.generators.ItemModelProvider
import net.minecraftforge.common.data.ExistingFileHelper

class ItemModels (
    generator: DataGenerator,
    fileHelper: ExistingFileHelper,
) : ItemModelProvider(generator, BreadMod.ID, fileHelper) {
    override fun registerModels() {
        singleTexture("breadmod:test_bread", mcLoc("item/generated"), "layer0", modLoc("items/test_bread"))
        singleTexture("breadmod:music_disc_test", mcLoc("item/generated"), "layer0", modLoc("items/music_disc_test"))
        singleTexture("breadmod:bread_boots", mcLoc("item/generated"), "layer0", modLoc("items/bread_boots"))
        singleTexture("breadmod:bread_leggings", mcLoc("item/generated"), "layer0", modLoc("items/bread_leggings"))
        singleTexture("breadmod:bread_chestplate", mcLoc("item/generated"), "layer0", modLoc("items/bread_chestplate"))
        singleTexture("breadmod:bread_helmet", mcLoc("item/generated"), "layer0", modLoc("items/bread_helmet"))
        singleTexture("breadmod:bread_slice", mcLoc("item/generated"), "layer0", modLoc("items/bread_slice"))
    }
}