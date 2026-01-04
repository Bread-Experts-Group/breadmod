package org.bread_experts_group.breadmod_agent.transforms.minecraft

import com.google.common.collect.Lists
import net.minecraft.util.RandomSource
import net.minecraft.world.level.Level
import net.minecraft.world.level.redstone.CollectingNeighborUpdater
import net.minecraft.world.level.redstone.NeighborUpdater
import net.neoforged.neoforge.attachment.AttachmentHolder
import org.bread_experts_group.breadmod_agent.AgentUtil.classDesc
import org.bread_experts_group.breadmod_agent.transforms.ClassTransform
import java.lang.classfile.ClassBuilder
import java.lang.classfile.ClassElement
import java.lang.classfile.ClassFile
import java.lang.classfile.ClassFile.ACC_PUBLIC
import java.lang.constant.ClassDesc
import java.lang.constant.ConstantDescs
import java.lang.constant.MethodTypeDesc

class LevelTransform(
	classFile: ClassFile,
	inputBytes: ByteArray
) : ClassTransform(classFile, inputBytes) {
	override fun transform(): (ClassBuilder, ClassElement) -> Unit = { classBuilder, classElement ->
		classBuilder.addMethod(
			ConstantDescs.INIT_NAME,
			MethodTypeDesc.of(
				ConstantDescs.CD_void,
				ConstantDescs.CD_boolean
			),
			ACC_PUBLIC
		) { codeBuilder ->
			codeBuilder
				.aload(0)
				.invokespecial(
					ClassDesc.of(AttachmentHolder::class.java.name),
					ConstantDescs.INIT_NAME,
					Companion.DEFAULT_VOID
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
					Companion.DEFAULT_VOID
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
					Companion.DEFAULT_VOID
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
				.aload(0)
				.new_(ArrayList::class.classDesc)
				.dup()
				.invokespecial(
					ArrayList::class.classDesc,
					ConstantDescs.INIT_NAME,
					Companion.DEFAULT_VOID
				)
				.putfield(
					Level::class.classDesc,
					"capturedBlockSnapshots",
					ArrayList::class.classDesc
				)
				.aload(0)
				.iload(1)
				.putfield(
					Level::class.classDesc,
					"isClientSide",
					ConstantDescs.CD_boolean
				)
				.return_()
		}
		classBuilder.with(classElement)
	}
}