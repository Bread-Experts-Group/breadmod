package org.bread_experts_group.breadmod.client.render.item

import net.minecraft.client.player.LocalPlayer
import net.minecraft.client.renderer.MultiBufferSource
import net.neoforged.neoforge.client.event.RenderLevelStageEvent

/**
 * Hooks into RenderLevelStageEvent for rending in-world objects / effects while holding the item.
 */
interface IRenderingItem {
	fun renderLevelStageEvent(event: RenderLevelStageEvent, bufferSource: MultiBufferSource, player: LocalPlayer)
}