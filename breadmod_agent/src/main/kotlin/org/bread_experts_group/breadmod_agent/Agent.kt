package org.bread_experts_group.breadmod_agent

import net.minecraft.client.multiplayer.ClientChunkCache
import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.server.level.ChunkMap
import net.minecraft.server.level.ServerChunkCache
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.Level
import net.minecraft.world.level.chunk.ChunkSource
import net.minecraft.world.level.chunk.storage.ChunkStorage
import org.bread_experts_group.breadmod.experimental.physics_grid.backend.MicroLevelChunkMap
import org.bread_experts_group.breadmod.experimental.physics_grid.backend.client.ClientMicroLevel
import org.bread_experts_group.breadmod.experimental.physics_grid.backend.client.ClientMicroLevelChunkSource
import org.bread_experts_group.breadmod.experimental.physics_grid.backend.server.ServerMicroLevel
import org.bread_experts_group.breadmod.experimental.physics_grid.backend.server.ServerMicroLevelChunkSource
import org.bread_experts_group.breadmod_agent.AgentUtil.classDesc
import org.bread_experts_group.breadmod_agent.AgentUtil.modifyInit
import org.bread_experts_group.breadmod_agent.transforms.breadmod.ClientMicroLevelTransform
import org.bread_experts_group.breadmod_agent.transforms.breadmod.MicroLevelChunkMapTransform
import org.bread_experts_group.breadmod_agent.transforms.breadmod.ServerMicroLevelChunkSourceTransform
import org.bread_experts_group.breadmod_agent.transforms.breadmod.ServerMicroLevelTransform
import org.bread_experts_group.breadmod_agent.transforms.minecraft.ChunkMapTransform
import org.bread_experts_group.breadmod_agent.transforms.minecraft.ChunkStorageTransform
import org.bread_experts_group.breadmod_agent.transforms.minecraft.ClientLevelTransform
import org.bread_experts_group.breadmod_agent.transforms.minecraft.LevelTransform
import org.bread_experts_group.breadmod_agent.transforms.minecraft.ServerChunkCacheTransform
import org.bread_experts_group.breadmod_agent.transforms.minecraft.ServerLevelTransform
import java.lang.classfile.ClassFile
import java.lang.classfile.ClassFile.ACC_PUBLIC
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
					var hasClientChunkCacheConstructor = false
					return runCatching {
						when (AgentUtil.parseClassName(className)) {
							ServerLevel::class.java.name ->
								ServerLevelTransform(classFile, classfileBuffer).parse()
							ClientLevel::class.java.name ->
								ClientLevelTransform(classFile, classfileBuffer).parse()
							Level::class.java.name ->
								LevelTransform(classFile, classfileBuffer).parse()
							ServerChunkCache::class.java.name ->
								ServerChunkCacheTransform(classFile, classfileBuffer).parse()
							ChunkMap::class.java.name ->
								ChunkMapTransform(classFile, classfileBuffer).parse()
							ChunkStorage::class.java.name ->
								ChunkStorageTransform(classFile, classfileBuffer).parse()
							MicroLevelChunkMap::class.java.name ->
								MicroLevelChunkMapTransform(classFile, classfileBuffer).parse()
							ServerMicroLevelChunkSource::class.java.name ->
								ServerMicroLevelChunkSourceTransform(classFile, classfileBuffer).parse()
							ServerMicroLevel::class.java.name ->
								ServerMicroLevelTransform(classFile, classfileBuffer).parse()
							ClientMicroLevel::class.java.name ->
								ClientMicroLevelTransform(classFile, classfileBuffer).parse()
							ClientChunkCache::class.java.name -> {
								val model = classFile.parse(classfileBuffer)
								classFile.transformClass(model) { classBuilder, classElement ->
									if (!hasClientChunkCacheConstructor) {
										hasClientChunkCacheConstructor = true
										classBuilder.withMethodBody(
											"<init>",
											MethodTypeDesc.of(ConstantDescs.CD_void),
											ACC_PUBLIC
										) { codeBuilder ->
											codeBuilder
												.aload(0)
												.invokespecial(
													ChunkSource::class.classDesc,
													"<init>",
													MethodTypeDesc.of(ConstantDescs.CD_void)
												)
												.return_()
										}
									}
									classBuilder.with(classElement)
								}
							}
							ClientMicroLevelChunkSource::class.java.name -> {
								val model = classFile.parse(classfileBuffer)
								classFile.transformClass(model) { classBuilder, classElement ->
									val init = classBuilder.modifyInit(
										classElement
									) { codeBuilder, codeElement, index ->
										when (index) {
											10 -> codeBuilder
												.aload(0)
												.invokespecial(
													ClientChunkCache::class.classDesc,
													"<init>",
													MethodTypeDesc.of(ConstantDescs.CD_void)
												)
											else if (index > 10) -> codeBuilder.with(codeElement)
										}
									}
									if (!init) classBuilder.with(classElement)
								}
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