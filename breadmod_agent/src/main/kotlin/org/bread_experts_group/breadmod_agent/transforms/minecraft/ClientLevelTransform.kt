package org.bread_experts_group.breadmod_agent.transforms.minecraft

import net.minecraft.client.Minecraft
import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.client.multiplayer.prediction.BlockStatePredictionHandler
import net.minecraft.client.renderer.LevelRenderer
import net.minecraft.world.level.Level
import org.bread_experts_group.breadmod_agent.AgentUtil.classDesc
import org.bread_experts_group.breadmod_agent.transforms.ClassTransform
import java.lang.classfile.ClassBuilder
import java.lang.classfile.ClassElement
import java.lang.classfile.ClassFile
import java.lang.classfile.ClassFile.ACC_PUBLIC
import java.lang.constant.ClassDesc
import java.lang.constant.ConstantDescs
import java.lang.constant.MethodTypeDesc

class ClientLevelTransform(
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
				.iconst_1()
				.invokespecial(
					ClassDesc.of(Level::class.java.name),
					ConstantDescs.INIT_NAME,
					MethodTypeDesc.of(
						ConstantDescs.CD_void,
						ConstantDescs.CD_boolean
					)
				)
				.aload(0)
				.new_(BlockStatePredictionHandler::class.classDesc)
				.dup()
				.invokespecial(
					BlockStatePredictionHandler::class.classDesc,
					ConstantDescs.INIT_NAME,
					Companion.DEFAULT_VOID
				)
				.putfield(
					ClientLevel::class.classDesc,
					"blockStatePredictionHandler",
					BlockStatePredictionHandler::class.classDesc
				)
				.aload(0)
				.invokestatic(
					Minecraft::class.classDesc,
					"getInstance",
					MethodTypeDesc.of(Minecraft::class.classDesc)
				)
				.getfield(
					Minecraft::class.classDesc,
					"levelRenderer",
					LevelRenderer::class.classDesc
				)
				.putfield(
					ClientLevel::class.classDesc,
					"levelRenderer",
					LevelRenderer::class.classDesc
				)
				.aload(0)
				.invokestatic(
					Minecraft::class.classDesc,
					"getInstance",
					MethodTypeDesc.of(Minecraft::class.classDesc)
				)
				.putfield(
					ClientLevel::class.classDesc,
					"minecraft",
					Minecraft::class.classDesc
				)
				.return_()
		}
		classBuilder.with(classElement)
	}
}