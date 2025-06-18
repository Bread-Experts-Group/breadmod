package org.bread_experts_group.breadmod.client.render.entity.block

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.math.Axis
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context
import net.minecraft.client.resources.model.BakedModel
import net.minecraft.core.Direction
import net.minecraft.core.Direction.EAST
import net.minecraft.core.Direction.NORTH
import net.minecraft.core.Direction.SOUTH
import net.minecraft.core.Direction.WEST
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import org.bread_experts_group.breadmod.client.render.getFluidSpriteAndTint
import org.bread_experts_group.breadmod.client.render.getModel
import org.bread_experts_group.breadmod.client.render.localClient
import org.bread_experts_group.breadmod.client.render.rotate
import org.bread_experts_group.breadmod.client.render.translateDiv16
import org.bread_experts_group.breadmod.registry.block.actual.entity.machine.DieselGeneratorBlockEntity
import org.bread_experts_group.breadmod.util.toYRotFixed

// todo the rest of the models (upgrades, door)
class DieselGeneratorRenderer(context: Context) : BreadModBER<DieselGeneratorBlockEntity>(context) {
	private val doorModel: BakedModel = localClient.getModel("block/diesel_generator/diesel_generator_door")

	override fun render(
		blockEntity: DieselGeneratorBlockEntity,
		partialTick: Float,
		poseStack: PoseStack,
		bufferSource: MultiBufferSource,
		packedLight: Int,
		packedOverlay: Int
	) {
		val blockRotation = blockEntity.blockState.getValue(BlockStateProperties.HORIZONTAL_FACING)
		poseStack.pushPose()
		poseStack.mulPose(Axis.YP.rotationDegrees(blockRotation.toYRotFixed()))
		this.renderDoor(blockEntity, blockRotation, poseStack, bufferSource, packedOverlay)
		poseStack.popPose()
	}

	private fun renderDoor(
		blockEntity: DieselGeneratorBlockEntity,
		rotation: Direction,
		poseStack: PoseStack,
		bufferSource: MultiBufferSource,
		packedOverlay: Int
	) {
		poseStack.pushPose()
		when (rotation) {
			NORTH -> poseStack.translateDiv16(9.5, 1.0, 0.5)
			SOUTH -> poseStack.translateDiv16(-6.5, 1.0, -15.5)
			WEST  -> poseStack.translateDiv16(-6.5, 1.0, 0.5)
			EAST  -> poseStack.translateDiv16(9.5, 1.0, -15.5)
			else  -> {}
		}
		if (blockEntity.doorOpen) poseStack.rotate(Axis.YN, 135f)
		this.renderModel(blockEntity, this.doorModel, poseStack, bufferSource, packedOverlay)
		poseStack.popPose()
	}

	private fun renderFluid(
		blockEntity: DieselGeneratorBlockEntity,
		poseStack: PoseStack,
		rotation: Direction,
		bufferSource: MultiBufferSource
	) {
		val tank = blockEntity.fluidHandler.getUnit(0)
		tank.amount.divide(tank.capacity).toFloat()
		val (_, _) = getFluidSpriteAndTint(tank.fluid, false)
	}
}