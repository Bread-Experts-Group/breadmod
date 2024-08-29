package breadmod.datagen

import breadmod.ModMain
import breadmod.ModMain.modLocation
import breadmod.registry.block.ModBlocks
import breadmod.registry.fluid.ModFluids
import breadmod.registry.item.ModItems
import net.minecraft.data.PackOutput
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.Item
import net.minecraftforge.client.model.generators.ItemModelProvider
import net.minecraftforge.client.model.generators.ModelProvider
import net.minecraftforge.common.data.ExistingFileHelper
import net.minecraftforge.registries.RegistryObject

internal class ModItemModelProvider(
    packOutput: PackOutput,
    existingFileHelper: ExistingFileHelper
) : ItemModelProvider(packOutput, ModMain.ID, existingFileHelper) {
    override fun registerModels() {
        singleItem(ModItems.TEST_DISC)
        singleItem(ModItems.TEST_BREAD)
        handheldItem(ModItems.BREAD_SWORD)
        handheldItem(ModItems.BREAD_PICKAXE)
        handheldItem(ModItems.BREAD_SHOVEL)
        handheldItem(ModItems.BREAD_AXE)
        handheldItem(ModItems.BREAD_HOE)
        singleItem(ModItems.BREAD_SLICE)
        handheldItem(ModItems.RF_BREAD_SHOVEL)
        handheldItem(ModItems.RF_BREAD_AXE)
        handheldItem(ModItems.RF_BREAD_HOE)
        handheldItem(ModItems.RF_BREAD_SWORD)
        handheldItem(ModItems.RF_BREAD_PICKAXE)
        singleItem(ModItems.TEST_DISC)
        singleItem(ModItems.DOUGH)
        singleItem(ModItems.FLOUR)
        singleItem(ModItems.DIE)
        singleItem(ModItems.ULTIMATE_BREAD)
        singleItem(ModFluids.BREAD_LIQUID.bucket)
        singleItem(ModItems.BASIC_BREAD_AMULET)
//        singleItem(ModItems.REINFORCED_BREAD_AMULET)
//        singleItem(ModItems.INDESTRUCTIBLE_BREAD_AMULET)
        handheldItem(ModItems.KNIFE)
        singleItem(ModItems.RF_BREAD_HELMET)
        singleItem(ModItems.RF_BREAD_CHESTPLATE)
        singleItem(ModItems.RF_BREAD_LEGGINGS)
        singleItem(ModItems.RF_BREAD_BOOTS)
        singleItem(ModBlocks.BREAD_DOOR)
        singleItem(ModItems.CREATURE)
        singleItem(ModItems.CAPRISPIN)
        singleItem(ModItems.TOAST_SLICE)
        handheldItem(ModItems.BREAD_GUN_ITEM)
        singleItem(ModItems.TOASTER_HEATING_ELEMENT)
        singleItem(ModItems.TOASTED_BREAD)
        handheldItem(ModItems.WRENCH)
        singleItem(ModItems.BAGEL)
        singleItem(ModItems.HALF_BAGEL)

        fenceInventory("bread_fence", modLoc("${ModelProvider.BLOCK_FOLDER}/bread_block"))
        ModItems.PROJECT_E?.also {
            singleItem(it.BREAD_ORB_ITEM)
        }
        multiLayeredTexture(
            "breadmod:bread_boots",
            mcLoc("item/generated"),
            modLoc("item/bread_boots"),
            modLoc("item/bread_boots_overlay")
        )
        multiLayeredTexture(
            "breadmod:bread_leggings",
            mcLoc("item/generated"),
            modLoc("item/bread_leggings"),
            modLoc("item/bread_leggings_overlay")
        )
        multiLayeredTexture(
            "breadmod:bread_chestplate",
            mcLoc("item/generated"),
            modLoc("item/bread_chestplate"),
            modLoc("item/bread_chestplate_overlay")
        )
        multiLayeredTexture(
            "breadmod:bread_helmet",
            mcLoc("item/generated"),
            modLoc("item/bread_helmet"),
            modLoc("item/bread_helmet_overlay")
        )
        multiLayeredTexture(
            "breadmod:doped_bread",
            mcLoc("item/generated"),
            modLoc("item/doped_bread"),
            modLoc("item/doped_bread_overlay")
        )
    }

    private fun <T : Item> singleItem(item: RegistryObject<T>) {
        withExistingParent(
            item.id.path,
            ResourceLocation("item/generated")
        ).texture(
            "layer0",
            modLocation("item/" + item.id.path)
        )
    }

    private fun <T : Item> handheldItem(item: RegistryObject<T>) {
        withExistingParent(
            item.id.path,
            ResourceLocation("item/handheld")
        ).texture(
            "layer0",
            modLocation("item/" + item.id.path)
        )
    }

    private fun multiLayeredTexture(
        name: String,
        parent: ResourceLocation,
        texture: ResourceLocation,
        texture2: ResourceLocation
    ) {
        withExistingParent(name, parent)
            .texture("layer0", texture)
            .texture("layer1", texture2)
    }
}