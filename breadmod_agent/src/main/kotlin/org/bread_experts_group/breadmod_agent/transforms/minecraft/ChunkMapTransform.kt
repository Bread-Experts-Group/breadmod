package org.bread_experts_group.breadmod_agent.transforms.minecraft

import net.minecraft.server.level.ChunkMap
import net.minecraft.server.level.PlayerMap
import net.minecraft.server.level.ServerChunkCache
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.chunk.storage.ChunkStorage
import org.bread_experts_group.breadmod_agent.AgentUtil.classDesc
import org.bread_experts_group.breadmod_agent.transforms.ClassTransform
import java.lang.classfile.ClassBuilder
import java.lang.classfile.ClassElement
import java.lang.classfile.ClassFile
import java.lang.classfile.ClassFile.ACC_PUBLIC
import java.lang.constant.ConstantDescs
import java.lang.constant.MethodTypeDesc

class ChunkMapTransform(
	classFile: ClassFile,
	inputBytes: ByteArray
) : ClassTransform(classFile, inputBytes) {
	override fun transform(): (ClassBuilder, ClassElement) -> Unit = { classBuilder, classElement ->
		classBuilder.addMethod(
			ConstantDescs.INIT_NAME,
			MethodTypeDesc.of(
				ConstantDescs.CD_void,
				ServerLevel::class.classDesc,
				ServerChunkCache::class.classDesc
			),
			ACC_PUBLIC
		) { codeBuilder ->
			codeBuilder
				.aload(0)
				.invokespecial(
					ChunkStorage::class.classDesc,
					"<init>",
					MethodTypeDesc.of(ConstantDescs.CD_void)
				)
				.aload(0)
				.aload(1)
				.putfield(
					ChunkMap::class.classDesc,
					"level",
					ServerLevel::class.classDesc
				)
				.aload(0)
				.new_(PlayerMap::class.classDesc)
				.dup()
				.invokespecial(
					PlayerMap::class.classDesc,
					"<init>",
					MethodTypeDesc.of(ConstantDescs.CD_void)
				)
				.putfield(
					ChunkMap::class.classDesc,
					"playerMap",
					PlayerMap::class.classDesc
				)
				.return_()
		}
		classBuilder.with(classElement)
	}
}