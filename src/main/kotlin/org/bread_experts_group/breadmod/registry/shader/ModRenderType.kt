package org.bread_experts_group.breadmod.registry.shader

import com.mojang.blaze3d.vertex.DefaultVertexFormat
import com.mojang.blaze3d.vertex.VertexFormat
import com.mojang.blaze3d.vertex.VertexFormatElement
import net.minecraft.client.renderer.RenderStateShard
import net.minecraft.client.renderer.RenderStateShard.CULL
import net.minecraft.client.renderer.RenderStateShard.LIGHTMAP
import net.minecraft.client.renderer.RenderStateShard.NO_TRANSPARENCY
import net.minecraft.client.renderer.RenderStateShard.ShaderStateShard
import net.minecraft.client.renderer.RenderStateShard.TRANSLUCENT_TARGET
import net.minecraft.client.renderer.RenderStateShard.TRANSLUCENT_TRANSPARENCY
import net.minecraft.client.renderer.RenderStateShard.TextureStateShard
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.RenderType.SMALL_BUFFER_SIZE
import net.minecraft.client.renderer.ShaderInstance
import net.minecraft.resources.ResourceLocation

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
 */
@Suppress("INACCESSIBLE_TYPE")
object ModRenderType {
	val SPEED_VERTEX_ELEMENT: VertexFormatElement = VertexFormatElement.register(
		6, 0,
		VertexFormatElement.Type.FLOAT, VertexFormatElement.Usage.GENERIC, 1
	)
	val DIRECTION_VERTEX_ELEMENT: VertexFormatElement = VertexFormatElement.register(
		7, 0,
		VertexFormatElement.Type.FLOAT, VertexFormatElement.Usage.GENERIC, 2
	)
	val rainbowVertexFormat: VertexFormat = VertexFormat.builder()
		.add("Position", VertexFormatElement.POSITION)
		.add("UV0", VertexFormatElement.UV0)
		.add("Speed", this.SPEED_VERTEX_ELEMENT)
		.add("Direction", this.DIRECTION_VERTEX_ELEMENT)
		.build()
	var rainbowInstance: ShaderInstance? = null
	private val rainbowShader: ShaderStateShard = ShaderStateShard(this::rainbowInstance)
	val rainbow: RenderType by lazy {
		RenderType.create(
			"rainbow",
			this.rainbowVertexFormat,
			VertexFormat.Mode.QUADS,
			SMALL_BUFFER_SIZE,
			true,
			false,
			RenderType.CompositeState.builder()
				.setCullState(CULL)
				.setTransparencyState(NO_TRANSPARENCY)
				.setShaderState(this.rainbowShader)
				.createCompositeState(false)
		)
	}
	val astralVertexFormat: VertexFormat = VertexFormat.builder()
		.add("Position", VertexFormatElement.POSITION)
		.add("UV0", VertexFormatElement.UV0)
		.build()
	var astralInstance: ShaderInstance? = null
	private val astralShader: ShaderStateShard = ShaderStateShard(this::astralInstance)
	val astral: RenderType by lazy {
		RenderType.create(
			"astral",
			this.astralVertexFormat,
			VertexFormat.Mode.QUADS,
			SMALL_BUFFER_SIZE,
			false,
			false,
			RenderType.CompositeState.builder()
				.setCullState(CULL)
				.setTransparencyState(NO_TRANSPARENCY)
				.setShaderState(this.astralShader)
				.createCompositeState(false)
		)
	}
	var glowInstance: ShaderInstance? = null
	private val glowShader: ShaderStateShard = ShaderStateShard(this::glowInstance)

	/**
	 * Glow Shader.
	 */
	fun glow(texture: ResourceLocation): RenderType {
		val textureState = TextureStateShard(texture, false, false)
		return RenderType.create(
			"glow",
			DefaultVertexFormat.BLOCK,
			VertexFormat.Mode.QUADS,
			SMALL_BUFFER_SIZE,
			true,
			true,
			RenderType.CompositeState.builder()
				.setShaderState(this.glowShader)
				.setTextureState(textureState)
				.setTransparencyState(NO_TRANSPARENCY)
				.setWriteMaskState(RenderStateShard.COLOR_WRITE)
				.createCompositeState(false)
		)
	}

	var translucentTexInstance: ShaderInstance? = null
	private val translucentTexTexShader: ShaderStateShard = ShaderStateShard(this::translucentTexInstance)

	/**
	 * Translucent tex Shader.
	 */
	fun translucentTex(texture: ResourceLocation): RenderType {
		val textureState = TextureStateShard(texture, false, false)
		return RenderType.create(
			"translucent_tex",
			DefaultVertexFormat.BLOCK,
			VertexFormat.Mode.QUADS,
			SMALL_BUFFER_SIZE,
			true,
			true,
			RenderType.CompositeState.builder()
				.setLightmapState(LIGHTMAP)
				.setShaderState(this.translucentTexTexShader)
				.setTextureState(textureState)
				.setTransparencyState(TRANSLUCENT_TRANSPARENCY)
				.setOutputState(TRANSLUCENT_TARGET)
				.createCompositeState(false)
		)
	}
}