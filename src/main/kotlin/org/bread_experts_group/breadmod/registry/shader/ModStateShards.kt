package org.bread_experts_group.breadmod.registry.shader

import net.minecraft.client.renderer.RenderStateShard.OutputStateShard
import net.minecraft.client.renderer.RenderStateShard.ShaderStateShard
import org.bread_experts_group.breadmod.client.render.localClient

object ModStateShards {
	val EMISSIVE_TARGET: OutputStateShard = OutputStateShard("emissive_target", {
		if (ModPostChains.ready) {
			ModPostChains.emissiveTarget.copyDepthFrom(localClient.mainRenderTarget)
			ModPostChains.emissiveTarget.bindWrite(false)
		}
	}, {
		if (ModPostChains.ready) localClient.mainRenderTarget.bindWrite(true)
	})
	val LIDAR_TARGET: OutputStateShard = OutputStateShard("emissive_target", {
		if (ModPostChains.ready) {
//			ModPostChains.lidarTarget.copyDepthFrom(localClient.mainRenderTarget)
			ModPostChains.lidarTarget.bindWrite(false)
		}
	}, {
		if (ModPostChains.ready) localClient.mainRenderTarget.bindWrite(true)
	})
	val SUN_SHARD: ShaderStateShard = ShaderStateShard(ModRenderType::SUN_INSTANCE)
	val GLOW_SHARD: ShaderStateShard = ShaderStateShard(ModRenderType::GLOW_INSTANCE)
	val ASTRAL_SHARD: ShaderStateShard = ShaderStateShard(ModRenderType::ASTRAL_INSTANCE)
	val RAINBOW_SHARD: ShaderStateShard = ShaderStateShard(ModRenderType::RAINBOW_INSTANCE)
	val TRANSLUCENT_TEX_SHARD: ShaderStateShard = ShaderStateShard(ModRenderType::TRANSLUCENT_TEX_INSTANCE)
	val POSITION_TEX_COLOR_NO_CUTOUT: ShaderStateShard =
		ShaderStateShard(ModRenderType::POSITION_TEX_COLOR_NO_CUTOUT_INSTANCE)
}