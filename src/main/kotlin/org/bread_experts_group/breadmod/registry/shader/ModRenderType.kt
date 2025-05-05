package org.bread_experts_group.breadmod.registry.shader

import com.mojang.blaze3d.vertex.VertexFormat
import com.mojang.blaze3d.vertex.VertexFormatElement
import net.minecraft.client.renderer.RenderStateShard
import net.minecraft.client.renderer.RenderStateShard.CULL
import net.minecraft.client.renderer.RenderStateShard.NO_TRANSPARENCY
import net.minecraft.client.renderer.RenderStateShard.ShaderStateShard
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.ShaderInstance

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
	var rainbowInstance: ShaderInstance? = null
	private val rainbowShader: RenderStateShard.ShaderStateShard = ShaderStateShard(this::rainbowInstance)
	val SPEED_VERTEX_ELEMENT: VertexFormatElement =
		VertexFormatElement.register(6, 0, VertexFormatElement.Type.FLOAT, VertexFormatElement.Usage.GENERIC, 1)
	val DIRECTION_VERTEX_ELEMENT: VertexFormatElement =
		VertexFormatElement.register(7, 0, VertexFormatElement.Type.FLOAT, VertexFormatElement.Usage.GENERIC, 2)
	val rainbowVertexFormat: VertexFormat = VertexFormat.builder()
		.add("Position", VertexFormatElement.POSITION)
		.add("UV0", VertexFormatElement.UV0)
		.add("Speed", this.SPEED_VERTEX_ELEMENT)
		.add("Direction", this.DIRECTION_VERTEX_ELEMENT)
		.build()
	private val solidTextureRenderType: RenderType = RenderType.create(
		"rainbow",
		this.rainbowVertexFormat,
		VertexFormat.Mode.QUADS,
		1536,
		true,
		false,
		RenderType.CompositeState.builder()
			.setCullState(CULL)
			.setTransparencyState(NO_TRANSPARENCY)
			.setShaderState(this.rainbowShader)
			.createCompositeState(false)
	)
	var astralInstance: ShaderInstance? = null
	private val astralShader: RenderStateShard.ShaderStateShard = ShaderStateShard(this::astralInstance)
	val astralVertexFormat: VertexFormat = VertexFormat.builder()
		.add("Position", VertexFormatElement.POSITION)
		.add("UV0", VertexFormatElement.UV0)
		.build()
	private val astralRenderType: RenderType = RenderType.create(
		"astral",
		this.astralVertexFormat,
		VertexFormat.Mode.QUADS,
		1536,
		false,
		false,
		RenderType.CompositeState.builder()
			.setCullState(CULL)
			.setTransparencyState(NO_TRANSPARENCY)
			.setShaderState(this.astralShader)
			.createCompositeState(false)
	)

	/**
	 * Rainbow shader.
	 */
	fun rainbow(): RenderType = this.solidTextureRenderType
//	/**
//	 * Astral Shader.
//	 */
//	fun astral(): RenderType = this.astralRenderType
}