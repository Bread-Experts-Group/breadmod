package org.bread_experts_group.breadmod.registry.shader

import com.mojang.blaze3d.vertex.VertexFormat
import com.mojang.blaze3d.vertex.VertexFormatElement

object ModVertexFormats {
	val SPEED_VERTEX_ELEMENT: VertexFormatElement = VertexFormatElement.register(
		6, 0,
		VertexFormatElement.Type.FLOAT, VertexFormatElement.Usage.GENERIC, 1
	)
	val DIRECTION_VERTEX_ELEMENT: VertexFormatElement = VertexFormatElement.register(
		7, 0,
		VertexFormatElement.Type.FLOAT, VertexFormatElement.Usage.GENERIC, 2
	)
	val RAINBOW_VERTEX_FORMAT: VertexFormat = VertexFormat.builder()
		.add("Position", VertexFormatElement.POSITION)
		.add("UV0", VertexFormatElement.UV0)
		.add("Speed", this.SPEED_VERTEX_ELEMENT)
		.add("Direction", this.DIRECTION_VERTEX_ELEMENT)
		.build()
	val ASTRAL_VERTEX_FORMAT: VertexFormat = VertexFormat.builder()
		.add("Position", VertexFormatElement.POSITION)
		.add("UV0", VertexFormatElement.UV0)
		.build()
}