package breadmod.datagen

import breadmod.BreadMod
import breadmod.item.ModItems
import net.minecraft.data.PackOutput
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.Item
import net.minecraftforge.client.model.generators.ItemModelProvider
import net.minecraftforge.common.data.ExistingFileHelper
import net.minecraftforge.registries.RegistryObject

class ModItemModels(output: PackOutput?, modid: String?, existingFileHelper: ExistingFileHelper?) :
    ItemModelProvider(output, modid, existingFileHelper) {
    override fun registerModels() {
//        singleItem(ModItems.TEST_DISC)
//        singleItem(ModItems.TEST_BREAD)
        singleTexture("breadmod:test_bread", mcLoc("item/generated"), "layer0", modLoc("item/test_bread"))
        singleTexture("breadmod:music_disc_test", mcLoc("item/generated"), "layer0", modLoc("item/music_disc_test"))
    }

    private fun singleItem(item: RegistryObject<Item>) {
        withExistingParent(
            item.id.path,
            ResourceLocation("item/generated")
        ).texture(
            "layer0",
            ResourceLocation(BreadMod.ID, "item/" + item.id.path)
        )
    }
}