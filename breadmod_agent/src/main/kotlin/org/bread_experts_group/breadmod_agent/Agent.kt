package org.bread_experts_group.breadmod_agent

import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.Level
import net.neoforged.neoforge.attachment.AttachmentHolder
import java.lang.classfile.ClassFile
import java.lang.classfile.ClassFile.ACC_PUBLIC
import java.lang.classfile.CodeModel
import java.lang.classfile.MethodModel
import java.lang.constant.ClassDesc
import java.lang.constant.ConstantDescs
import java.lang.constant.MethodTypeDesc
import java.lang.instrument.ClassFileTransformer
import java.lang.instrument.Instrumentation
import java.security.ProtectionDomain

@Suppress("Unused")
class Agent {
	companion object {
		@JvmStatic
		fun premain(agentArgs: String?, instrumentation: Instrumentation) {
			println("THE BREADMOD AGENT HAS INFECTED THE GAME")
			val classFile = ClassFile.of()
			instrumentation.addTransformer(object : ClassFileTransformer {
				override fun transform(
					module: Module?,
					loader: ClassLoader?,
					className: String,
					classBeingRedefined: Class<*>?,
					protectionDomain: ProtectionDomain,
					classfileBuffer: ByteArray
				): ByteArray? {
					var hasServerLevelConstructor = false
					var hasLevelConstructor = false
					return runCatching {
						when (className) {
							"net/minecraft/server/level/ServerLevel" -> {
								val model = classFile.parse(classfileBuffer)
								classFile.transformClass(model) { classBuilder, classElement ->
									if (!hasServerLevelConstructor) {
										hasServerLevelConstructor = true
										classBuilder.withMethodBody(
											ConstantDescs.INIT_NAME,
											MethodTypeDesc.of(ConstantDescs.CD_void),
											ACC_PUBLIC
										) { codeBuilder ->
											codeBuilder
												.aload(0)
												.invokespecial(
													ClassDesc.of(Level::class.java.name),
													ConstantDescs.INIT_NAME,
													MethodTypeDesc.of(ConstantDescs.CD_void)
												)
												.return_()
										}
									}
									classBuilder.with(classElement)
								}
							}
							"net/minecraft/world/level/Level" -> {
								val model = classFile.parse(classfileBuffer)
								classFile.transformClass(model) { classBuilder, classElement ->
									if (!hasLevelConstructor) {
										hasLevelConstructor = true
										classBuilder.withMethodBody(
											ConstantDescs.INIT_NAME,
											MethodTypeDesc.of(ConstantDescs.CD_void),
											ACC_PUBLIC
										) { codeBuilder ->
											codeBuilder
												.aload(0)
												.invokespecial(
													ClassDesc.of(AttachmentHolder::class.java.name),
													ConstantDescs.INIT_NAME,
													MethodTypeDesc.of(ConstantDescs.CD_void)
												)
												.return_()
										}
									}
									classBuilder.with(classElement)
								}
							}
							"org/bread_experts_group/breadmod/experimental/physics_grid/ServerMicroLevel" -> {
								val model = classFile.parse(classfileBuffer)
								var index = 0
								val bytes = classFile.transformClass(model) { classBuilder, classElement ->
									if (classElement is MethodModel &&
										classElement.methodName().equalsString("<init>")
									) {
										classBuilder.transformMethod(classElement) { methodBuilder, methodElement ->
											if (methodElement is CodeModel) methodBuilder.transformCode(methodElement) { codeBuilder, codeElement ->
												when (index) {
													16 -> codeBuilder
														.aload(0)
														.invokespecial(
															ClassDesc.of(ServerLevel::class.java.name),
															ConstantDescs.INIT_NAME,
															MethodTypeDesc.of(ConstantDescs.CD_void)
														)
													else if (index !in 16 .. 30) -> codeBuilder.with(codeElement)
												}
												index++
											} else methodBuilder.with(methodElement)
										}
									} else classBuilder.with(classElement)
								}
								bytes
							}
							else -> null
						}
					}.onFailure {
						it.printStackTrace()
					}.getOrNull()
				}
			}, false)
		}
	}
}