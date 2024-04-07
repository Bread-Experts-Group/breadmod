package breadmod.datagen

import breadmod.BreadMod
import breadmod.item.ModItems
import net.minecraft.data.PackOutput
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.Item
import net.minecraftforge.client.model.generators.ItemModelProvider
import net.minecraftforge.common.data.ExistingFileHelper
import net.minecraftforge.registries.RegistryObject

@Suppress("SpellCheckingInspection")
class ModItemModels(output: PackOutput, modid: String, existingFileHelper: ExistingFileHelper) :
    ItemModelProvider(output, modid, existingFileHelper) {
    override fun registerModels() {
        singleItem(ModItems.TEST_DISC)
        singleItem(ModItems.TEST_BREAD)
        singleItem(ModItems.BREAD_SWORD)
        singleItem(ModItems.BREAD_PICKAXE)
        singleItem(ModItems.BREAD_SHOVEL)
        singleItem(ModItems.BREAD_AXE)
        singleItem(ModItems.BREAD_HOE)
        singleItem(ModItems.BREAD_SLICE)
        singleItem(ModItems.TEST_DISC)
        multiTexture("breadmod:bread_boots", mcLoc("item/generated"), "layer0", modLoc("item/bread_boots"), "layer1", modLoc("item/bread_boots_overlay"))
        multiTexture("breadmod:bread_leggings", mcLoc("item/generated"), "layer0", modLoc("item/bread_leggings"), "layer1", modLoc("item/bread_leggings_overlay"))
        multiTexture("breadmod:bread_chestplate", mcLoc("item/generated"), "layer0", modLoc("item/bread_chestplate"), "layer1", modLoc("item/bread_chestplate_overlay"))
        multiTexture("breadmod:bread_helmet", mcLoc("item/generated"), "layer0", modLoc("item/bread_helmet"), "layer1", modLoc("item/bread_helmet_overlay"))
    }

    private fun <T: Item> singleItem(item: RegistryObject<T>) {
        withExistingParent(
            item.id.path,
            ResourceLocation("item/generated")
        ).texture(
            "layer0",
            ResourceLocation(BreadMod.ID, "item/" + item.id.path)
        )
    }

    private fun multiTexture(name: String, parent: ResourceLocation, textureKey: String, texture: ResourceLocation, textureKey2: String, texture2: ResourceLocation) {
        withExistingParent(name, parent)
            .texture(textureKey, texture)
            .texture(textureKey2, texture2)
    }
}