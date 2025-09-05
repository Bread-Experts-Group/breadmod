package org.bread_experts_group.breadmod.registry.shader

import com.mojang.blaze3d.pipeline.RenderTarget
import net.minecraft.client.renderer.PostChain
import net.minecraft.server.packs.resources.ResourceProvider
import org.bread_experts_group.breadmod.BreadMod.Companion.modLocation
import org.bread_experts_group.breadmod.client.render.localClient

object ModPostChains {
	var ready: Boolean = false
		private set
	lateinit var bloom: PostChain
		private set
	lateinit var bloomEmissiveTarget: RenderTarget
		private set

	fun init(provider: ResourceProvider) {
		if (this.ready) this.bloom.close()
		this.bloom = this.newPostChain("bloom", provider)
		this.resize(localClient.window.width, localClient.window.height)
		this.bloomEmissiveTarget = this.bloom.getTempTarget("emissive")
		this.ready = true
	}

	fun resize(w: Int, h: Int) {
		this.bloom.resize(w, h)
	}

	private fun newPostChain(shader: String, provider: ResourceProvider): PostChain =
		PostChain(
			localClient.textureManager,
			provider,
			localClient.mainRenderTarget,
			modLocation("shaders/post/$shader.json")
		)
}