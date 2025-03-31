package org.bread_experts_group.breadmod.registry.shader

import com.mojang.blaze3d.vertex.DefaultVertexFormat
import com.mojang.blaze3d.vertex.VertexFormat
import net.minecraft.Util
import net.minecraft.client.renderer.RenderStateShard.CULL
import net.minecraft.client.renderer.RenderStateShard.LIGHTMAP
import net.minecraft.client.renderer.RenderStateShard.NO_TRANSPARENCY
import net.minecraft.client.renderer.RenderStateShard.ShaderStateShard
import net.minecraft.client.renderer.RenderStateShard.TextureStateShard
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.ShaderInstance
import net.minecraft.resources.ResourceLocation
import java.util.function.Function

@Suppress("INACCESSIBLE_TYPE")
object ModRenderType {
	var solidInstance: ShaderInstance? = null
	private val solidTextureShader = ShaderStateShard(this::solidInstance)
	private val solidTextureRenderType: Function<ResourceLocation, RenderType> =
		Util.memoize { location: ResourceLocation ->
			val compositeState: RenderType.CompositeState = RenderType.CompositeState.builder()
				.setLightmapState(LIGHTMAP)
				.setCullState(CULL)
				.setTransparencyState(NO_TRANSPARENCY)
				.setShaderState(this.solidTextureShader)
				.setTextureState(TextureStateShard(location, false, true))
				.createCompositeState(true)
			RenderType.create(
				"solid_texture",
				DefaultVertexFormat.BLOCK,
				VertexFormat.Mode.QUADS,
				4194304,
				true,
				false,
				compositeState
			)
		}

	/**
	 * Replica of [RenderType.solid] that takes in a [textureLocation]
	 */
	fun solidTextured(textureLocation: ResourceLocation): RenderType =
		this.solidTextureRenderType.apply(textureLocation)
}