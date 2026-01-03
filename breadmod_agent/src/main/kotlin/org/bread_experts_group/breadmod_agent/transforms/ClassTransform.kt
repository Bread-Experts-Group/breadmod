package org.bread_experts_group.breadmod_agent.transforms

import java.lang.classfile.ClassBuilder
import java.lang.classfile.ClassElement
import java.lang.classfile.ClassFile
import java.lang.classfile.CodeBuilder
import java.lang.constant.ConstantDescs
import java.lang.constant.MethodTypeDesc

abstract class ClassTransform(
	private val classFile: ClassFile,
	private val inputBytes: ByteArray
) {
	companion object {
		val DEFAULT_VOID: MethodTypeDesc = MethodTypeDesc.of(ConstantDescs.CD_void)
	}

	val existingElements: MutableList<String> = mutableListOf()

	fun parse(): ByteArray {
		val model = this.classFile.parse(this.inputBytes)
		return this.classFile.transformClass(model) { c, e ->
			this.transform().invoke(c, e)
		}
	}

	fun ClassBuilder.addMethod(
		methodName: String,
		methodDescriptor: MethodTypeDesc,
		flags: Int,
		builder: (CodeBuilder) -> Unit
	): ClassBuilder {
		if (methodName !in this@ClassTransform.existingElements) {
			this@ClassTransform.existingElements.add(methodName)
			this.withMethodBody(methodName, methodDescriptor, flags, builder)
		}
		return this
	}

	abstract fun transform(): (ClassBuilder, ClassElement) -> Unit
}