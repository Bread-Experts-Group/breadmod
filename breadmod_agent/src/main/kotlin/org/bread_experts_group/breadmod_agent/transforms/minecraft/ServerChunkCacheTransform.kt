package org.bread_experts_group.breadmod_agent.transforms.minecraft

import net.minecraft.world.level.chunk.ChunkSource
import org.bread_experts_group.breadmod_agent.AgentUtil.classDesc
import org.bread_experts_group.breadmod_agent.transforms.ClassTransform
import java.lang.classfile.ClassBuilder
import java.lang.classfile.ClassElement
import java.lang.classfile.ClassFile
import java.lang.classfile.ClassFile.ACC_PUBLIC
import java.lang.constant.ConstantDescs

class ServerChunkCacheTransform(
	classFile: ClassFile,
	inputBytes: ByteArray
) : ClassTransform(classFile, inputBytes) {
	override fun transform(): (ClassBuilder, ClassElement) -> Unit = { classBuilder, classElement ->
		classBuilder.addMethod(
			ConstantDescs.INIT_NAME,
			Companion.DEFAULT_VOID,
			ACC_PUBLIC
		) { codeBuilder ->
			codeBuilder
				.aload(0)
				.invokespecial(
					ChunkSource::class.classDesc,
					"<init>",
					Companion.DEFAULT_VOID
				)
				.return_()
		}
		classBuilder.with(classElement)
	}
}