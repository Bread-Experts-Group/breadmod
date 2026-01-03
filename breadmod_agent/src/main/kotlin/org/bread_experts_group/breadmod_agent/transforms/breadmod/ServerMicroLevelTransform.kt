package org.bread_experts_group.breadmod_agent.transforms.breadmod

import net.minecraft.server.level.ServerLevel
import org.bread_experts_group.breadmod_agent.AgentUtil.classDesc
import org.bread_experts_group.breadmod_agent.AgentUtil.modifyInit
import org.bread_experts_group.breadmod_agent.transforms.ClassTransform
import java.lang.classfile.ClassBuilder
import java.lang.classfile.ClassElement
import java.lang.classfile.ClassFile
import java.lang.constant.ConstantDescs
import java.lang.constant.MethodTypeDesc

class ServerMicroLevelTransform(
	classFile: ClassFile,
	inputBytes: ByteArray
) : ClassTransform(classFile, inputBytes) {
	override fun transform(): (ClassBuilder, ClassElement) -> Unit = { classBuilder, classElement ->
		val init = classBuilder.modifyInit(
			classElement
		) { codeBuilder, codeElement, index ->
			when (index) {
				28 -> codeBuilder
					.aload(0)
					.invokespecial(
						ServerLevel::class.classDesc,
						ConstantDescs.INIT_NAME,
						MethodTypeDesc.of(ConstantDescs.CD_void)
					)
				else if (index > 28) -> codeBuilder.with(codeElement)
			}
		}
		if (!init) classBuilder.with(classElement)
	}
}