package org.bread_experts_group.breadmod.experimental.tool_gun_mode

import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.level.block.Blocks
import org.bread_experts_group.breadmod.BreadMod.Companion.modLocation

class ExplodeWidget : ModeWidget(Component.literal("Explode"), Blocks.TNT.asItem().defaultInstance) {
    override val previewImage: ResourceLocation
        get() = modLocation("textures", "gui", "tool_gun", "exploder.png")
    override val description: Component
        get() = Component.literal("just a test description")
}