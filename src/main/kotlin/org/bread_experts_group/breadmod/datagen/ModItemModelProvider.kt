package org.bread_experts_group.breadmod.datagen

import net.minecraft.data.PackOutput
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.Item
import net.neoforged.neoforge.client.model.generators.ItemModelProvider
import net.neoforged.neoforge.common.data.ExistingFileHelper
import net.neoforged.neoforge.registries.DeferredItem
import org.bread_experts_group.breadmod.Breadmod
import org.bread_experts_group.breadmod.Breadmod.Companion.modLocation
import org.bread_experts_group.breadmod.registry.fluid.ModFluids
import org.bread_experts_group.breadmod.registry.item.ModItems

class ModItemModelProvider(
    packOutput: PackOutput,
    existingFileHelper: ExistingFileHelper
) : ItemModelProvider(packOutput, Breadmod.ID, existingFileHelper) {
    override fun registerModels() {
        singleItem(ModItems.FLOUR)
        singleItem(ModFluids.BREAD_LIQUID.bucket)
        singleItem(ModItems.TEST_RECORD)
        singleItem(ModItems.CHEF_HAT)
        singleItem(ModItems.TEST_BREAD)
        singleItem(ModItems.ULTIMATE_BREAD)
    }

    private fun <T : Item> singleItem(item: DeferredItem<T>) {
        withExistingParent(
            item.id.path,
            ResourceLocation.withDefaultNamespace("item/generated")
        ).texture(
            "layer0",
            modLocation("item/" + item.id.path)
        )
    }

    private fun <T : Item> handheldItem(item: DeferredItem<T>) {
        withExistingParent(
            item.id.path,
            ResourceLocation.withDefaultNamespace("item/handheld")
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