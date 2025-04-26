package org.bread_experts_group.breadmod.datagen

import net.minecraft.data.PackOutput
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.Item
import net.neoforged.neoforge.client.model.generators.ItemModelProvider
import net.neoforged.neoforge.common.data.ExistingFileHelper
import net.neoforged.neoforge.registries.DeferredItem
import org.bread_experts_group.breadmod.BreadMod
import org.bread_experts_group.breadmod.BreadMod.Companion.modLocation
import org.bread_experts_group.breadmod.registry.block.ModBlocks
import org.bread_experts_group.breadmod.registry.block.ModFluids
import org.bread_experts_group.breadmod.registry.item.ModItems

class ModItemModelProvider(
	packOutput: PackOutput,
	existingFileHelper: ExistingFileHelper
) : ItemModelProvider(packOutput, BreadMod.ID, existingFileHelper) {
	override fun registerModels() {
		this.singleItem(ModItems.FLOUR)
		this.singleItem(ModFluids.BREAD_LIQUID.bucket)
		this.singleItem(ModItems.RECORD_SECRET_HOPPIN)
		this.singleItem(ModItems.CHEF_HAT)
		this.singleItem(ModItems.OIL_DRUM)
		this.singleItem(ModItems.TEST_BREAD)
		this.singleItem(ModItems.ULTIMATE_BREAD)
		this.singleItem(ModItems.TOASTED_BREAD)
		this.singleItem(ModItems.BREAD_SLICE)
		this.singleItem(ModItems.TOAST_SLICE)
		this.singleItem(ModItems.DOUGH)
		this.singleItem(ModItems.DIE)
		this.singleItem(ModItems.BAGEL)
		this.singleItem(ModItems.HALF_BAGEL)
		this.singleItem(ModItems.ALUMINA)
		this.singleItem(ModItems.RF_BREAD_HELMET)
		this.singleItem(ModItems.RF_BREAD_CHESTPLATE)
		this.singleItem(ModItems.RF_BREAD_LEGGINGS)
		this.singleItem(ModItems.RF_BREAD_BOOTS)
		this.singleItem(ModItems.BREAD_AMULET)
		this.singleItem(ModItems.AMULET_OF_KEEPING)
		this.singleItem(ModItems.BREAD_PICKAXE)
		this.singleItem(ModItems.BREAD_SHOVEL)
		this.singleItem(ModItems.BREAD_AXE)
		this.singleItem(ModItems.BREAD_HOE)
		this.singleItem(ModItems.BREAD_SWORD)
		this.singleItem(ModItems.RF_BREAD_PICKAXE)
		this.singleItem(ModItems.RF_BREAD_SHOVEL)
		this.singleItem(ModItems.RF_BREAD_AXE)
		this.singleItem(ModItems.RF_BREAD_HOE)
		this.singleItem(ModItems.RF_BREAD_SWORD)
		this.singleItem(ModItems.CAPRISPIN)
		this.singleItem(ModItems.TOASTER_HEATING_ELEMENT)
		this.singleItem(ModItems.CREATURE)
		this.singleItem(ModBlocks.BREAD_DOOR)
		this.fenceInventory("bread_fence", this.modLoc("${BLOCK_FOLDER}/bread_block"))

		this.handheldItem(ModItems.WRENCH)
		this.handheldItem(ModItems.BULK_BLOCK_ITEM)
		this.handheldItem(ModItems.KNIFE)

		this.multiLayeredTexture(
			"breadmod:bread_boots",
			this.mcLoc("item/generated"),
			this.modLoc("item/bread_boots"),
			this.modLoc("item/bread_boots_overlay")
		)
		this.multiLayeredTexture(
			"breadmod:bread_leggings",
			this.mcLoc("item/generated"),
			this.modLoc("item/bread_leggings"),
			this.modLoc("item/bread_leggings_overlay")
		)
		this.multiLayeredTexture(
			"breadmod:bread_chestplate",
			this.mcLoc("item/generated"),
			this.modLoc("item/bread_chestplate"),
			this.modLoc("item/bread_chestplate_overlay")
		)
		this.multiLayeredTexture(
			"breadmod:bread_helmet",
			this.mcLoc("item/generated"),
			this.modLoc("item/bread_helmet"),
			this.modLoc("item/bread_helmet_overlay")
		)
		this.multiLayeredTexture(
			"breadmod:doped_bread",
			this.mcLoc("item/generated"),
			this.modLoc("item/doped_bread"),
			this.modLoc("item/doped_bread_overlay")
		)
	}

	private fun <T : Item> singleItem(item: DeferredItem<T>) {
		this.withExistingParent(
			item.id.path,
			ResourceLocation.withDefaultNamespace("item/generated")
		).texture(
			"layer0",
			modLocation("item/" + item.id.path)
		)
	}

	private fun <T : Item> handheldItem(item: DeferredItem<T>) {
		this.withExistingParent(
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
		this.withExistingParent(name, parent)
			.texture("layer0", texture)
			.texture("layer1", texture2)
	}
}