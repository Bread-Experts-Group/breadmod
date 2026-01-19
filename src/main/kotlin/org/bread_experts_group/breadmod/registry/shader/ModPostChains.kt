package org.bread_experts_group.breadmod.registry.shader

import com.mojang.blaze3d.pipeline.RenderTarget
import net.minecraft.client.renderer.PostChain
import net.minecraft.server.packs.resources.ResourceProvider
import org.bread_experts_group.breadmod.BreadMod.Companion.modLocation
import org.bread_experts_group.breadmod.client.render.localClient

object ModPostChains {
	var ready: Boolean = false
		private set
	lateinit var bloomChain: PostChain
		private set
	lateinit var emissiveTarget: RenderTarget
		private set
	lateinit var lidarChain: PostChain
		private set
	lateinit var lidarTarget: RenderTarget
		private set

	fun init(provider: ResourceProvider) {
		if (this.ready) {
			this.bloomChain.close()
			this.lidarChain.close()
		}

		this.bloomChain = this.newPostChain("bloom", provider)
		this.lidarChain = this.newPostChain("lidar", provider)
		this.emissiveTarget = this.bloomChain.getTempTarget("emissive")
		this.lidarTarget = this.lidarChain.getTempTarget("lidar")

		this.resize(localClient.window.width, localClient.window.height)
		this.ready = true
	}

	fun resize(w: Int, h: Int) {
		this.bloomChain.resize(w, h)
		this.lidarChain.resize(w, h)
	}

	private fun newPostChain(shader: String, provider: ResourceProvider): PostChain =
		PostChain(
			localClient.textureManager,
			provider,
			localClient.mainRenderTarget,
			modLocation("shaders/post/$shader.json")
		)
}