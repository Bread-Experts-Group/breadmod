package org.bread_experts_group.breadmod.registry.menu

import net.minecraft.core.component.DataComponents
import net.minecraft.core.registries.Registries
import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.item.alchemy.PotionContents
import net.minecraft.world.item.component.DyedItemColor
import net.minecraft.world.level.block.Blocks
import net.neoforged.neoforge.registries.DeferredRegister
import org.bread_experts_group.breadmod.BreadMod
import org.bread_experts_group.breadmod.BreadMod.Companion.modTranslatable
import org.bread_experts_group.breadmod.datagen.lang.DataGenerateLanguage
import org.bread_experts_group.breadmod.registry.RegistryProvider
import org.bread_experts_group.breadmod.registry.block.ModBlocks
import org.bread_experts_group.breadmod.registry.item.IRegisterSpecialCreativeTab
import org.bread_experts_group.breadmod.registry.item.ModItems
import java.util.Optional
import java.util.function.Supplier

object ModCreativeTabs : RegistryProvider(BreadMod.ID, Registries.CREATIVE_MODE_TAB) {
	private val registry: DeferredRegister<CreativeModeTab> = this.getRegistry(Registries.CREATIVE_MODE_TAB)
	private fun constructTab(
		name: String,
		general: Boolean,
		constructor: CreativeModeTab.Builder.() -> Unit
	): Supplier<CreativeModeTab> {
		val builder = CreativeModeTab.builder()
		val registryObject = this.registry.register(name, builder::build)
		builder
			.title(modTranslatable("itemGroup", name))
			.displayItems { parameters, output ->
				for (deferredItem in ModItems.itemIterator()) {
					val item = deferredItem.get()
					if (item is IRegisterSpecialCreativeTab) {
						if (
							item.creativeModeTabs.contains(registryObject) &&
							item.displayInCreativeTab(parameters, output)
						) output.accept(item.defaultInstance)
						continue
					}
					output.accept(item.defaultInstance)
					parameters.holders.lookupOrThrow(Registries.POTION)
						.filterElements { potion -> potion.effects.isNotEmpty() }
						.listElements()
						.forEach { potion ->
							val dopedBread = ModItems.DOPED_BREAD.toStack()
							val color = PotionContents.getColor(potion.value().effects)
							dopedBread.set(
								DataComponents.POTION_CONTENTS,
								PotionContents(Optional.of(potion), Optional.of(color), listOf())
							)
							dopedBread.set(DataComponents.DYED_COLOR, DyedItemColor(color, false))
							output.accept(dopedBread)
						}
				}
			}
		constructor(builder)
		return registryObject
	}

	@DataGenerateLanguage(name = "Bread Mod")
	val MAIN_TAB: Supplier<CreativeModeTab> = this.constructTab("main", true) {
		this.icon { ModBlocks.BREAD_BLOCK.get().asItem().defaultInstance }
	}

	@DataGenerateLanguage(name = "Bread Mod: Experimental(!!)")
	val EXPERIMENTAL_TAB: Supplier<CreativeModeTab> = this.constructTab("experimental", false) {
		this.icon { Blocks.BARRIER.asItem().defaultInstance }
	}

	@DataGenerateLanguage(name = "Bread Mod: Specials")
	val SPECIALS_TAB: Supplier<CreativeModeTab> = this.constructTab("specials", false) {
		this.icon { ModBlocks.REINFORCED_BREAD_BLOCK.get().asItem().defaultInstance }
	}
}