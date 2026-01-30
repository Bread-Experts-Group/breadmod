package org.bread_experts_group.breadmod.client.render

import com.mojang.blaze3d.vertex.BufferBuilder
import com.mojang.blaze3d.vertex.ByteBufferBuilder
import com.mojang.blaze3d.vertex.MeshData
import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.vertex.VertexBuffer
import net.minecraft.client.Camera
import net.minecraft.client.DeltaTracker
import net.minecraft.client.renderer.RenderType
import org.joml.Matrix4f

/**
 * Self-contained "Mesh" class for rendering a large amount of objects as one mesh, Like vanilla's section rendering.
 */
abstract class CompilableMesh {
	protected val meshes: MutableMap<RenderType, MeshData> = mutableMapOf()
	protected val bufferBuilders: MutableMap<RenderType, BufferBuilder> = mutableMapOf()
	protected val vertexBuffers: MutableMap<RenderType, VertexBuffer> = mutableMapOf()
	protected var isCompiled: Boolean = false

	protected fun getOrBeginBufferBuilder(renderType: RenderType): BufferBuilder =
		this.bufferBuilders.getOrPut(renderType) {
			val byteBufferBuilder = ByteBufferBuilder(renderType.bufferSize)
			BufferBuilder(byteBufferBuilder, renderType.mode, renderType.format)
		}

	protected fun getOrBeginVertexBuffer(renderType: RenderType): VertexBuffer =
		this.vertexBuffers.getOrPut(renderType) { VertexBuffer(VertexBuffer.Usage.STATIC) }

	/**
	 * Closes all compiled meshes and active vertex buffers.
	 */
	fun close() {
		this.meshes.values.forEach(MeshData::close)
		this.vertexBuffers.values.forEach(VertexBuffer::close)
	}

	fun isReady(): Boolean = this.isCompiled

	fun recompile() {
		this.close()
		this.vertexBuffers.clear()
		this.bufferBuilders.clear()
		this.meshes.clear()
		this.isCompiled = false
	}

	open fun render(
		poseStack: PoseStack,
		camera: Camera,
		modelViewMatrix: Matrix4f,
		projectionMatrix: Matrix4f,
		deltaTracker: DeltaTracker
	): Unit = throw UnsupportedOperationException("Cannot call, render method is not overridden.")

	open fun render(
		renderType: RenderType,
		poseStack: PoseStack,
		camera: Camera,
		modelViewMatrix: Matrix4f,
		projectionMatrix: Matrix4f,
		deltaTracker: DeltaTracker
	): Unit = throw UnsupportedOperationException("Cannot call, render method is not overridden.")

	abstract fun compile(poseStack: PoseStack, camera: Camera)
}