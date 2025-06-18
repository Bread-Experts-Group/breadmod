package org.bread_experts_group.breadmod.registry.item.integration.project_e

import moze_intel.projecte.api.capabilities.block_entity.IEmcStorage
import moze_intel.projecte.api.capabilities.block_entity.IEmcStorage.EmcAction
import moze_intel.projecte.api.capabilities.item.IItemEmcHolder
import moze_intel.projecte.gameObjs.items.IBarHelper
import moze_intel.projecte.gameObjs.items.ICapabilityAware
import moze_intel.projecte.gameObjs.items.ItemPE
import moze_intel.projecte.gameObjs.registries.PEDataComponentTypes
import net.minecraft.ChatFormatting
import net.minecraft.network.chat.Component
import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.item.CreativeModeTab.ItemDisplayParameters
import net.minecraft.world.item.CreativeModeTab.Output
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.TooltipFlag
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent
import org.bread_experts_group.breadmod.BreadMod.Companion.modTranslatable
import org.bread_experts_group.breadmod.registry.item.IRegisterSpecialCreativeTab
import org.bread_experts_group.breadmod.registry.menu.ModCreativeTabs
import java.util.function.Supplier
import kotlin.math.min

class BreadOrbItem : ItemPE(
	Properties().component(PEDataComponentTypes.STORED_EMC, 0L)
), IItemEmcHolder, IBarHelper, ICapabilityAware, IRegisterSpecialCreativeTab {
	private val maxEmc: Long = 45000L

	override fun isBarVisible(stack: ItemStack): Boolean = this.getStoredEmc(stack) > 0

	override fun insertEmc(stack: ItemStack, toInsert: Long, action: EmcAction): Long {
		if (toInsert < 0L) return this.extractEmc(stack, -toInsert, action)
		val max = this.getMaximumEmc(stack)
		val stored = this.getStoredEmc(stack)

		if (stored >= max) return 0L
		val toAdd = min(max - stored, toInsert)
		if (action.execute()) stack.set(PEDataComponentTypes.STORED_EMC, stored + toAdd)
		return toAdd
	}

	override fun extractEmc(stack: ItemStack, toExtract: Long, action: EmcAction): Long {
		if (toExtract < 0) return this.insertEmc(stack, -toExtract, action)
		val stored = this.getStoredEmc(stack)
		val toRemove = min(stored, toExtract)
		if (action.execute()) stack.set(PEDataComponentTypes.STORED_EMC, stored - toRemove)
		return toRemove
	}

	override fun getStoredEmc(stack: ItemStack): Long =
		stack.getOrDefault(PEDataComponentTypes.STORED_EMC, 0L)

	override fun getMaximumEmc(stack: ItemStack): Long = this.maxEmc

	override fun getWidthForBar(stack: ItemStack): Float {
		val emc = this.getStoredEmc(stack)
		if (emc == 0L) return 1f
		return (1 - emc / this.maxEmc.toDouble()).toFloat()
	}

	override fun appendHoverText(
		stack: ItemStack,
		context: TooltipContext,
		tooltipComponents: MutableList<Component>,
		tooltipFlag: TooltipFlag
	) {
		tooltipComponents.add(modTranslatable("item", "bread_orb", "tooltip").withStyle(ChatFormatting.GOLD))
	}

	override fun attachCapabilities(event: RegisterCapabilitiesEvent?) {
		// todo curious integration later
	}

	override val creativeModeTabs: List<Supplier<CreativeModeTab>> = listOf(ModCreativeTabs.MAIN_TAB)

	override fun displayInCreativeTab(parameters: ItemDisplayParameters, output: Output): Boolean {
		output.accept(ItemStack(this).also { this.insertEmc(it, 45000, IEmcStorage.EmcAction.EXECUTE) })
		return true
	}
}