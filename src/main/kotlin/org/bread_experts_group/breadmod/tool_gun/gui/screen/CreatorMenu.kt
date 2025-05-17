package org.bread_experts_group.breadmod.tool_gun.gui.screen

import net.minecraft.world.entity.player.Inventory
import org.bread_experts_group.breadmod.registry.menu.ModMenuTypes
import org.bread_experts_group.breadmod.registry.menu.actual.BMContainerMenu

class CreatorMenu(
	containerId: Int,
	inventory: Inventory,
) : BMContainerMenu.ToolGun(ModMenuTypes.CREATOR.get(), containerId, inventory)