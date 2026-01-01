package org.bread_experts_group.breadmod_agent

import java.lang.classfile.ClassBuilder
import java.lang.classfile.ClassElement
import java.lang.classfile.CodeBuilder
import java.lang.classfile.CodeElement
import java.lang.classfile.CodeModel
import java.lang.classfile.MethodModel
import java.lang.constant.ClassDesc
import kotlin.reflect.KClass

object AgentUtil {
	fun parseClassName(className: String): String = className.replace('/', '.')

	fun ClassBuilder.modifyInit(
		classElement: ClassElement,
		transform: (CodeBuilder, CodeElement, Int) -> Unit
	): Boolean =
		if (classElement is MethodModel && classElement.methodName().equalsString("<init>")) {
			this.transformMethod(classElement) { methodBuilder, methodElement ->
				var index = 0
				if (methodElement is CodeModel) methodBuilder.transformCode(methodElement) { cB, cE ->
					transform(cB, cE, index++)
				} else methodBuilder.with(methodElement)
			}
			true
		} else false

	fun CodeBuilder.dumpOpcodeStack(codeElement: CodeElement, index: Int) {
		println("$index, $codeElement")
	}

	val Class<*>.classDesc: ClassDesc
		get() = ClassDesc.of(this.name)
	val KClass<*>.classDesc: ClassDesc
		get() {
			val declaring = this.java.declaringClass
			return if (declaring != null) ClassDesc.of(declaring.name + "$" + this.simpleName)
			else ClassDesc.of(this.java.name)
		}
}