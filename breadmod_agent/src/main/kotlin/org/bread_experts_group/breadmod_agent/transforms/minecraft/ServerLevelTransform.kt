package org.bread_experts_group.breadmod_agent.transforms.minecraft

import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.Level
import net.minecraft.world.level.gameevent.GameEventDispatcher
import net.neoforged.neoforge.capabilities.CapabilityListenerHolder
import org.bread_experts_group.breadmod_agent.AgentUtil.classDesc
import org.bread_experts_group.breadmod_agent.transforms.ClassTransform
import java.lang.classfile.ClassBuilder
import java.lang.classfile.ClassElement
import java.lang.classfile.ClassFile
import java.lang.classfile.ClassFile.ACC_PUBLIC
import java.lang.constant.ClassDesc
import java.lang.constant.ConstantDescs
import java.lang.constant.MethodTypeDesc

class ServerLevelTransform(
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
				.iconst_0()
				.invokespecial(
					ClassDesc.of(Level::class.java.name),
					ConstantDescs.INIT_NAME,
					MethodTypeDesc.of(
						ConstantDescs.CD_void,
						ConstantDescs.CD_boolean
					)
				)
				.aload(0)
				.new_(CapabilityListenerHolder::class.classDesc)
				.dup()
				.invokespecial(
					CapabilityListenerHolder::class.classDesc,
					"<init>",
					Companion.DEFAULT_VOID
				)
				.putfield(
					ServerLevel::class.classDesc,
					"capListenerHolder",
					CapabilityListenerHolder::class.classDesc
				)
				.aload(0)
				.new_(GameEventDispatcher::class.classDesc)
				.dup()
				.aload(0)
				.invokespecial(
					GameEventDispatcher::class.classDesc,
					"<init>",
					MethodTypeDesc.of(
						ConstantDescs.CD_void,
						ServerLevel::class.classDesc
					)
				)
				.putfield(
					ServerLevel::class.classDesc,
					"gameEventDispatcher",
					GameEventDispatcher::class.classDesc
				)
				.return_()
		}
		classBuilder.with(classElement)
	}
}