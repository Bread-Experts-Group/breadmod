package org.bread_experts_group.breadmod_agent.transforms.breadmod

import net.minecraft.server.level.ChunkMap
import net.minecraft.server.level.ServerChunkCache
import net.minecraft.server.level.ServerLevel
import org.bread_experts_group.breadmod_agent.AgentUtil.classDesc
import org.bread_experts_group.breadmod_agent.AgentUtil.modifyInit
import org.bread_experts_group.breadmod_agent.transforms.ClassTransform
import java.lang.classfile.ClassBuilder
import java.lang.classfile.ClassElement
import java.lang.classfile.ClassFile
import java.lang.constant.ConstantDescs
import java.lang.constant.MethodTypeDesc

class MicroLevelChunkMapTransform(
	classFile: ClassFile,
	inputBytes: ByteArray
) : ClassTransform(classFile, inputBytes) {
	override fun transform(): (ClassBuilder, ClassElement) -> Unit = { classBuilder, classElement ->
		val init = classBuilder.modifyInit(
			classElement
		) { codeBuilder, codeElement, index ->
			when (index) {
				15 -> codeBuilder
					.aload(0)
					.aload(1)
					.aload(2)
					.invokespecial(
						ChunkMap::class.classDesc,
						"<init>",
						MethodTypeDesc.of(
							ConstantDescs.CD_void,
							ServerLevel::class.classDesc,
							ServerChunkCache::class.classDesc
						)
					)
				else if (index !in 15 .. 31) -> codeBuilder.with(codeElement)
			}
		}
		if (!init) classBuilder.with(classElement)
	}
}