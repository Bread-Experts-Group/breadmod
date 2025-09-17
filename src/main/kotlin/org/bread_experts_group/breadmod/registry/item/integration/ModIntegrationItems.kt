package org.bread_experts_group.breadmod.registry.item.integration

import moze_intel.projecte.gameObjs.items.ItemPE
import net.neoforged.fml.ModList
import net.neoforged.neoforge.registries.DeferredItem
import org.bread_experts_group.breadmod.datagen.lang.DataGenerateLanguage
import org.bread_experts_group.breadmod.datagen.model.item.DataGenerateModelSingleItem
import org.bread_experts_group.breadmod.registry.item.ModItems
import org.bread_experts_group.breadmod.registry.item.integration.project_e.BreadOrbItem

object ModIntegrationItems {
	object ProjectEItems {
		@DataGenerateLanguage
		@DataGenerateLanguage(name = "An EMC battery made of... bread?", suffix = ".tooltip")
		@DataGenerateLanguage("en_jp", "パン玉")
		@DataGenerateModelSingleItem
		val BREAD_ORB: DeferredItem<ItemPE> = ModItems.registerItem("bread_orb", ::BreadOrbItem)
	}

	fun registerAll() {
		if (ModList.get().isLoaded("projecte")) ProjectEItems
	}
}