package org.bread_experts_group.breadmod.registry.shader

import com.mojang.blaze3d.pipeline.RenderTarget
import com.mojang.blaze3d.systems.RenderSystem
import com.mojang.blaze3d.vertex.DefaultVertexFormat
import com.mojang.blaze3d.vertex.VertexFormat
import net.minecraft.Util
import net.minecraft.client.renderer.RenderStateShard
import net.minecraft.client.renderer.RenderStateShard.CULL
import net.minecraft.client.renderer.RenderStateShard.LIGHTMAP
import net.minecraft.client.renderer.RenderStateShard.NO_CULL
import net.minecraft.client.renderer.RenderStateShard.NO_TRANSPARENCY
import net.minecraft.client.renderer.RenderStateShard.TRANSLUCENT_TARGET
import net.minecraft.client.renderer.RenderStateShard.TRANSLUCENT_TRANSPARENCY
import net.minecraft.client.renderer.RenderStateShard.TextureStateShard
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.RenderType.SMALL_BUFFER_SIZE
import net.minecraft.client.renderer.ShaderInstance
import net.minecraft.resources.ResourceLocation
import java.util.function.Function

/**
 * ## SHADER UNIFORM AND IN/OUT INFO
 *
 * #### // ShaderInstance uniforms //
 *
 * - frustumMatrix -> uniform mat4 ModelViewMat
 * - projectionMatrix -> uniform mat4 ProjMat
 * - RenderSystem#getShaderColor -> uniform vec4 ColorModulator
 * - RenderSystem#getShaderGlintAlpha -> uniform float GlintAlpha
 * - RenderSystem#getShaderFogStart -> uniform float FogStart (almost 1 all the time)
 * - RenderSystem#getShaderFogEnd -> uniform float FogEnd
 * - RenderSystem#getShaderFogShape#getIndex -> uniform int FogShape
 * - RenderSystem#getTextureMatrix -> uniform mat4 TextureMat
 * - RenderSystem#getShaderGameTime -> uniform float GameTime
 * - Window#getWidth, Window#getHight -> uniform vec2 ScreenSize
 * - RenderSystem#getShaderLineWidth -> uniform float LineWidth
 *
 * #### // DefaultVertexFormat inputs //
 *
 * Samplers are obtained via the UV inputs of VertexFormat
 * - UV0 (texture UVs) -> uniform sampler2D Sampler0
 * - UV1 (packed overlay) -> uniform sampler2D Sampler1
 * - UV2 (packed light) -> uniform sampler2D Sampler2
 *
 * - POSITION -> in vec3 Position
 * - COLOR -> in vec4 Color
 * - NORMAL -> in vec3 Normal
 *
 * #### Custom uniforms can be created by extending ShaderInstance, creating the uniform inside the shader's json and populating a field with your uniform by calling getUniform
 */
@Suppress("INACCESSIBLE_TYPE")
object ModRenderType {
	lateinit var RAINBOW_INSTANCE: ShaderInstance
	lateinit var ASTRAL_INSTANCE: ShaderInstance
	lateinit var GLOW_INSTANCE: ShaderInstance
	lateinit var SUN_INSTANCE: ShaderInstance
	lateinit var TRANSLUCENT_TEX_INSTANCE: ShaderInstance
	lateinit var POSITION_TEX_COLOR_NO_CUTOUT_INSTANCE: ShaderInstance
	val RAINBOW: RenderType = RenderType.create(
		"rainbow",
		ModVertexFormats.RAINBOW_VERTEX_FORMAT,
		VertexFormat.Mode.QUADS,
		SMALL_BUFFER_SIZE,
		true,
		false,
		RenderType.CompositeState.builder()
			.setCullState(CULL)
			.setTransparencyState(NO_TRANSPARENCY)
			.setShaderState(ModStateShards.RAINBOW_SHARD)
			.createCompositeState(false)
	)
	val ASTRAL: RenderType = RenderType.create(
		"astral",
		ModVertexFormats.ASTRAL_VERTEX_FORMAT,
		VertexFormat.Mode.QUADS,
		SMALL_BUFFER_SIZE,
		false,
		false,
		RenderType.CompositeState.builder()
			.setCullState(CULL)
			.setTransparencyState(NO_TRANSPARENCY)
			.setShaderState(ModStateShards.ASTRAL_SHARD)
			.createCompositeState(false)
	)
	private val GLOW: Function<ResourceLocation, RenderType> = Util.memoize { texture ->
		val textureState = TextureStateShard(texture, false, false)
		RenderType.create(
			"glow",
			DefaultVertexFormat.BLOCK,
			VertexFormat.Mode.QUADS,
			SMALL_BUFFER_SIZE,
			true,
			true,
			RenderType.CompositeState.builder()
				.setShaderState(ModStateShards.GLOW_SHARD)
				.setTextureState(textureState)
				.setOutputState(ModStateShards.EMISSIVE_TARGET)
				.setTransparencyState(NO_TRANSPARENCY)
				.createCompositeState(false)
		)
	}
	private val SUN: Function<ResourceLocation, RenderType> = Util.memoize { texture ->
		val textureState = TextureStateShard(texture, false, false)
		RenderType.create(
			"sun",
			DefaultVertexFormat.BLOCK,
			VertexFormat.Mode.QUADS,
			SMALL_BUFFER_SIZE,
			true,
			true,
			RenderType.CompositeState.builder()
				.setShaderState(ModStateShards.SUN_SHARD)
				.setTextureState(textureState)
				.setTransparencyState(NO_TRANSPARENCY)
				.createCompositeState(false)
		)
	}
	private val TRANSLUCENT_TEX: Function<ResourceLocation, RenderType> = Util.memoize { texture ->
		val textureState = TextureStateShard(texture, false, false)
		RenderType.create(
			"translucent_tex",
			DefaultVertexFormat.BLOCK,
			VertexFormat.Mode.QUADS,
			SMALL_BUFFER_SIZE,
			true,
			true,
			RenderType.CompositeState.builder()
				.setLightmapState(LIGHTMAP)
				.setShaderState(ModStateShards.TRANSLUCENT_TEX_SHARD)
				.setTextureState(textureState)
				.setTransparencyState(TRANSLUCENT_TRANSPARENCY)
				.setOutputState(TRANSLUCENT_TARGET)
				.createCompositeState(false)
		)
	}
	private val RENDER_TARGET: Function<RenderTarget, RenderType> = Util.memoize { renderTarget ->
		RenderType.create(
			"render_target",
			DefaultVertexFormat.POSITION_TEX_COLOR,
			VertexFormat.Mode.QUADS,
			SMALL_BUFFER_SIZE,
			true,
			false,
			RenderType.CompositeState.builder()
				.setShaderState(ModStateShards.POSITION_TEX_COLOR_NO_CUTOUT)
				.setTransparencyState(RenderStateShard.NO_TRANSPARENCY)
				.setCullState(NO_CULL)
				.setTexturingState(RenderStateShard.TexturingStateShard("set_texture", {
					RenderSystem.setShaderTexture(0, renderTarget.colorTextureId)
				}, {}))
				.createCompositeState(false)
		)
	}

	fun renderTarget(target: RenderTarget): RenderType = this.RENDER_TARGET.apply(target)

	/**
	 * Glow Shader.
	 */
	fun glow(texture: ResourceLocation): RenderType = this.GLOW.apply(texture)

	/**
	 * Sun Shader.
	 */
	fun sun(texture: ResourceLocation): RenderType = this.SUN.apply(texture)

	/**
	 * Translucent tex Shader.
	 */
	fun translucentTex(texture: ResourceLocation): RenderType = this.TRANSLUCENT_TEX.apply(texture)
}