package org.bread_experts_group.breadmod.experimental.lidar.handler

import com.mojang.blaze3d.vertex.BufferBuilder
import com.mojang.blaze3d.vertex.ByteBufferBuilder
import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.vertex.VertexBuffer
import net.minecraft.client.Camera
import net.minecraft.client.renderer.RenderType

class SectionMesh(private val section: LidarSection) {
	private val renderType: RenderType = RenderType.debugQuads()
	private var byteBufferBuilder: ByteBufferBuilder? = null
	private var vertexBuffer: VertexBuffer? = null
	private var isCompiled: Boolean = false

	fun recompile() {
		this.vertexBuffer?.close()
		this.byteBufferBuilder?.close()
		this.byteBufferBuilder = null
		this.vertexBuffer = null
		this.isCompiled = false
	}

	fun getBuffer(): VertexBuffer =
		this.vertexBuffer ?: throw NullPointerException("Somehow got buffer before mesh was ready")

	fun ready(): Boolean = this.isCompiled

	fun compile(poseStack: PoseStack, camera: Camera) {
		if (this.isCompiled) return
		if (this.byteBufferBuilder == null) this.byteBufferBuilder = ByteBufferBuilder(this.renderType.bufferSize)
		if (this.vertexBuffer == null) this.vertexBuffer = VertexBuffer(VertexBuffer.Usage.STATIC)
		val bufferBuilder = BufferBuilder(
			this.byteBufferBuilder ?: return,
			this.renderType.mode,
			this.renderType.format
		)
		this.section.lidarBlocks.forEach { (pos, block) ->
			poseStack.pushPose()
			block.renderSides(poseStack, camera, bufferBuilder)
			poseStack.popPose()
		}
		val mesh = bufferBuilder.build() ?: return
		this.vertexBuffer?.let {
			it.bind()
			it.upload(mesh)
		}
		VertexBuffer.unbind()
		this.isCompiled = true
	}
}