package org.bread_experts_group.breadmod.registry.menu

import net.minecraft.core.registries.Registries
import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.level.block.Blocks
import net.neoforged.neoforge.registries.DeferredRegister
import org.bread_experts_group.breadmod.BreadMod
import org.bread_experts_group.breadmod.BreadMod.Companion.modTranslatable
import org.bread_experts_group.breadmod.datagen.lang.DataGenerateLanguage
import org.bread_experts_group.breadmod.registry.block.ModBlocks
import org.bread_experts_group.breadmod.registry.item.IRegisterSpecialCreativeTab
import org.bread_experts_group.breadmod.registry.item.ModItems
import java.util.function.Supplier

object ModCreativeTabs {
	val CREATIVE_TAB_REGISTRY : DeferredRegister<CreativeModeTab> =
		DeferredRegister.create(Registries.CREATIVE_MODE_TAB, BreadMod.ID)

	private fun constructTab(
		name : String,
		general : Boolean,
		constructor : CreativeModeTab.Builder.() -> Unit
	) : Supplier<CreativeModeTab> {
		val builder = CreativeModeTab.builder()
		val registryObject = this.CREATIVE_TAB_REGISTRY.register(name) { -> builder.build() }
		builder
			.title(modTranslatable("itemGroup", name))
			.displayItems { parameters, output ->
				ModItems.ITEM_REGISTRY.entries.forEach {
					val item = it.get()
					when {
						item is IRegisterSpecialCreativeTab -> if (item.creativeModeTabs.contains(registryObject))
							if (item.displayInCreativeTab(parameters, output)) output.accept(item.defaultInstance)
						general                             -> output.accept(item.defaultInstance)
					}
				}
			}
		constructor(builder)
		return registryObject
	}
	@DataGenerateLanguage("en_us", "Bread Mod")
	val MAIN_TAB : Supplier<CreativeModeTab> = this.constructTab("main", true) {
		this.icon { ModBlocks.BREAD_BLOCK.get().asItem().defaultInstance }
	}
	@DataGenerateLanguage("en_us", "Bread Mod: Experimental(!!)")
	val EXPERIMENTAL_TAB : Supplier<CreativeModeTab> = this.constructTab("experimental", false) {
		this.icon { Blocks.BARRIER.asItem().defaultInstance }
	}
	@DataGenerateLanguage("en_us", "Bread Mod: Specials")
	val SPECIALS_TAB : Supplier<CreativeModeTab> = this.constructTab("specials", false) {
		this.icon { ModBlocks.REINFORCED_BREAD_BLOCK.get().asItem().defaultInstance }
	}
}