package org.bread_experts_group.breadmod_agent

import com.google.common.collect.Lists
import net.minecraft.server.level.ServerChunkCache
import net.minecraft.server.level.ServerLevel
import net.minecraft.util.RandomSource
import net.minecraft.world.level.Level
import net.minecraft.world.level.chunk.ChunkSource
import net.minecraft.world.level.gameevent.GameEventDispatcher
import net.minecraft.world.level.redstone.CollectingNeighborUpdater
import net.minecraft.world.level.redstone.NeighborUpdater
import net.neoforged.neoforge.attachment.AttachmentHolder
import net.neoforged.neoforge.capabilities.CapabilityListenerHolder
import org.bread_experts_group.breadmod.experimental.physics_grid.backend.MicroLevelChunkSource
import org.bread_experts_group.breadmod.experimental.physics_grid.backend.ServerMicroLevel
import org.bread_experts_group.breadmod_agent.AgentUtil.classDesc
import org.bread_experts_group.breadmod_agent.AgentUtil.modifyInit
import java.lang.classfile.ClassFile
import java.lang.classfile.ClassFile.ACC_PUBLIC
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
					var hasServerChunkCacheConstructor = false
					return runCatching {
						when (AgentUtil.parseClassName(className)) {
							ServerLevel::class.java.name -> {
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
												.aload(0)
												.new_(CapabilityListenerHolder::class.classDesc)
												.dup()
												.invokespecial(
													CapabilityListenerHolder::class.classDesc,
													"<init>",
													MethodTypeDesc.of(ConstantDescs.CD_void)
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
									}
									classBuilder.with(classElement)
								}
							}
							Level::class.java.name -> {
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
												.aload(0)
												.invokestatic(
													RandomSource::class.classDesc,
													"create",
													MethodTypeDesc.of(RandomSource::class.classDesc),
													true
												)
												.putfield(
													Level::class.classDesc,
													"random",
													RandomSource::class.classDesc
												)
												.aload(0)
												.invokestatic(
													RandomSource::class.classDesc,
													"createThreadSafe",
													MethodTypeDesc.of(RandomSource::class.classDesc),
													true
												)
												.putfield(
													Level::class.classDesc,
													"threadSafeRandom",
													RandomSource::class.classDesc
												)
												.aload(0)
												.new_(CollectingNeighborUpdater::class.classDesc)
												.dup()
												.aload(0)
												.loadConstant(100000)
												.invokespecial(
													CollectingNeighborUpdater::class.classDesc,
													"<init>",
													MethodTypeDesc.of(
														ConstantDescs.CD_void,
														Level::class.classDesc,
														ConstantDescs.CD_int
													)
												)
												.putfield(
													Level::class.classDesc,
													"neighborUpdater",
													NeighborUpdater::class.classDesc
												)
												.aload(0)
												.new_(ArrayList::class.classDesc)
												.dup()
												.invokespecial(
													ArrayList::class.classDesc,
													"<init>",
													MethodTypeDesc.of(ConstantDescs.CD_void)
												)
												.putfield(
													Level::class.classDesc,
													"freshBlockEntities",
													ArrayList::class.classDesc
												)
												.aload(0)
												.new_(ArrayList::class.classDesc)
												.dup()
												.invokespecial(
													ArrayList::class.classDesc,
													"<init>",
													MethodTypeDesc.of(ConstantDescs.CD_void)
												)
												.putfield(
													Level::class.classDesc,
													"pendingFreshBlockEntities",
													ArrayList::class.classDesc
												)
												.aload(0)
												.invokestatic(
													Lists::class.classDesc,
													"newArrayList",
													MethodTypeDesc.of(ArrayList::class.classDesc)
												)
												.putfield(
													Level::class.classDesc,
													"pendingBlockEntityTickers",
													ConstantDescs.CD_List
												)
												.aload(0)
												.invokestatic(
													Lists::class.classDesc,
													"newArrayList",
													MethodTypeDesc.of(ArrayList::class.classDesc)
												)
												.putfield(
													Level::class.classDesc,
													"blockEntityTickers",
													ConstantDescs.CD_List
												)
												.return_()
										}
									}
									classBuilder.with(classElement)
								}
							}
							ServerChunkCache::class.java.name -> {
								val model = classFile.parse(classfileBuffer)
								classFile.transformClass(model) { classBuilder, classElement ->
									if (!hasServerChunkCacheConstructor) {
										hasServerChunkCacheConstructor = true
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
							MicroLevelChunkSource::class.java.name -> {
								val model = classFile.parse(classfileBuffer)
								classFile.transformClass(model) { classBuilder, classElement ->
									val init = classBuilder.modifyInit(
										classElement
									) { codeBuilder, codeElement, index ->
										when (index) {
											9 -> codeBuilder
												.aload(0)
												.invokespecial(
													ServerChunkCache::class.classDesc,
													"<init>",
													MethodTypeDesc.of(ConstantDescs.CD_void)
												)
											else if (index !in 9 .. 24) -> codeBuilder.with(codeElement)
										}
									}
									if (!init) classBuilder.with(classElement)
								}
							}
							ServerMicroLevel::class.java.name -> {
								val model = classFile.parse(classfileBuffer)
								classFile.transformClass(model) { classBuilder, classElement ->
									val init = classBuilder.modifyInit(
										classElement
									) { codeBuilder, codeElement, index ->
										when (index) {
											20 -> codeBuilder
												.aload(0)
												.invokespecial(
													ServerLevel::class.classDesc,
													ConstantDescs.INIT_NAME,
													MethodTypeDesc.of(ConstantDescs.CD_void)
												)
											else if (index !in 20 .. 34) -> codeBuilder.with(codeElement)
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