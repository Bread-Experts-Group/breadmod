package org.bread_experts_group.breadmod.datagen.tool_gun

import net.minecraft.core.HolderLookup
import net.minecraft.data.PackOutput
import net.minecraft.network.chat.Component
import net.minecraft.world.item.Items
import org.bread_experts_group.breadmod.BreadMod
import org.bread_experts_group.breadmod.client.gui.ModTextureLocations
import org.bread_experts_group.breadmod.client.tool_gun_mode.ModeWidget
import org.bread_experts_group.breadmod.registry.item.actual.tool_gun.mode.ExplodeMode
import java.util.concurrent.CompletableFuture

internal class ModToolGunModeProvider(
	output : PackOutput,
	lookupProvider : CompletableFuture<HolderLookup.Provider>
) : ToolGunModeProvider(output, lookupProvider, BreadMod.ID) {
	override fun addModes() {
		// Testing Method
		this.addEmptyMode()

		this.addMode(
			BreadMod.ID,
			"explode",
			Component.literal("explode"),
			Component.literal("tooltip"),
			ExplodeMode(),
			ModeWidget.Builder()
				.icon(Items.TNT.defaultInstance)
				.previewImage(ModTextureLocations.EXPLODE_PREVIEW)
				.name("Explode Mode")
				.description(
					"BOOM BOOM BOOM, I CAN'T SINGING THIS BLOODY TUNE TUNE TUNE IT'S GONNA MAKE MY BRAIN GO BOOM BOOM BOOM-"
				)
				.build()

		)
	}
}