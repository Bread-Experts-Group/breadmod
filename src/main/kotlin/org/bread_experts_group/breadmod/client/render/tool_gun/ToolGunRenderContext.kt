package org.bread_experts_group.breadmod.client.render.tool_gun

import net.minecraft.client.gui.Font
import net.minecraft.client.renderer.block.BlockRenderDispatcher
import net.minecraft.client.renderer.block.ModelBlockRenderer
import net.minecraft.client.renderer.entity.EntityRenderDispatcher
import net.minecraft.client.renderer.entity.ItemRenderer
import net.minecraft.client.resources.model.ModelManager
import org.bread_experts_group.breadmod.client.render.localClient

class ToolGunRenderContext(
	val modelManager: ModelManager,
	val itemRenderer: ItemRenderer,
	val blockModelRenderer: ModelBlockRenderer,
	val blockRenderDispatcher: BlockRenderDispatcher,
	val entityRenderDispatcher: EntityRenderDispatcher,
	val font: Font
) {
	var shouldRecoil: Boolean = true
	var shouldRenderCoil: Boolean = true
	var shouldRenderMainBody: Boolean = true
	var shouldRenderScreenContents: Boolean = true

	companion object {
		fun init(): ToolGunRenderContext = ToolGunRenderContext(
			localClient.modelManager,
			localClient.itemRenderer,
			localClient.blockRenderer.modelRenderer,
			localClient.blockRenderer,
			localClient.entityRenderDispatcher,
			localClient.font
		)
	}
}