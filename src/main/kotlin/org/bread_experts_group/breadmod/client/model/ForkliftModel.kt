package org.bread_experts_group.breadmod.client.model

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.vertex.VertexConsumer
import com.mojang.math.Axis
import net.minecraft.client.model.EntityModel
import net.minecraft.client.model.geom.ModelLayerLocation
import net.minecraft.client.model.geom.ModelPart
import net.minecraft.client.model.geom.PartPose
import net.minecraft.client.model.geom.builders.CubeDeformation
import net.minecraft.client.model.geom.builders.CubeListBuilder
import net.minecraft.client.model.geom.builders.LayerDefinition
import net.minecraft.client.model.geom.builders.MeshDefinition
import net.minecraft.client.renderer.RenderType
import net.minecraft.resources.ResourceLocation
import org.bread_experts_group.breadmod.BreadMod.Companion.modLocation
import org.bread_experts_group.breadmod.client.render.localClient
import org.bread_experts_group.breadmod.registry.entity.actual.Forklift

class ForkliftModel(private val root: ModelPart) : EntityModel<Forklift>(RenderType::entityCutoutNoCull) {
	private val engine: ModelPart = this.root.getChild("engine")
	private val cage: ModelPart = this.root.getChild("cage")
	private val sideTrimLeft: ModelPart = this.root.getChild("side_trim_left")
	private val sideTrimRight: ModelPart = this.root.getChild("side_trim_right")
	private val fork: ModelPart = this.root.getChild("fork")
	private val seat: ModelPart = this.root.getChild("Seat")
	private val wheelFrontLeft: ModelPart = this.root.getChild("wheel_front_left")
	private val wheelFrontRight: ModelPart = this.root.getChild("wheel_front_right")
	private val wheelBackLeft: ModelPart = this.root.getChild("wheel_back_left")
	private val wheelBackRight: ModelPart = this.root.getChild("wheel_back_right")
	private val steeringWheel: ModelPart = this.root.getChild("steering_wheel")
	private val steeringShaft: ModelPart = this.root.getChild("steering_shaft")
	private val parts: ModelPart = this.root.getChild("parts")
	private val engineExhaust: ModelPart = this.root.getChild("engine_exhaust")

	override fun setupAnim(
		entity: Forklift,
		limbSwing: Float,
		limbSwingAmount: Float,
		ageInTicks: Float,
		netHeadYaw: Float,
		headPitch: Float
	) {
	}

	override fun renderToBuffer(
		poseStack: PoseStack,
		buffer: VertexConsumer,
		packedLight: Int,
		packedOverlay: Int,
		color: Int
	) {
		poseStack.pushPose()
		poseStack.translate(0f, 1.5f, 0f)
		poseStack.mulPose(Axis.XN.rotationDegrees(180f))
		this.engine.render(poseStack, buffer, packedLight, packedOverlay, color)
		this.cage.render(poseStack, buffer, packedLight, packedOverlay, color)
		this.sideTrimLeft.render(poseStack, buffer, packedLight, packedOverlay, color)
		this.sideTrimRight.render(poseStack, buffer, packedLight, packedOverlay, color)
		this.fork.render(poseStack, buffer, packedLight, packedOverlay, color)
		this.seat.render(poseStack, buffer, packedLight, packedOverlay, color)
		this.wheelFrontLeft.render(poseStack, buffer, packedLight, packedOverlay, color)
		this.wheelFrontRight.render(poseStack, buffer, packedLight, packedOverlay, color)
		this.wheelBackLeft.render(poseStack, buffer, packedLight, packedOverlay, color)
		this.wheelBackRight.render(poseStack, buffer, packedLight, packedOverlay, color)
		this.steeringWheel.render(poseStack, buffer, packedLight, packedOverlay, color)
		this.steeringShaft.render(poseStack, buffer, packedLight, packedOverlay, color)
		this.parts.render(poseStack, buffer, packedLight, packedOverlay, color)
		this.engineExhaust.render(poseStack, buffer, packedLight, packedOverlay, color)
		poseStack.popPose()
	}

	fun render(
		poseStack: PoseStack,
		packedLight: Int,
		packedOverlay: Int,
		color: Int
	): Unit = this.renderToBuffer(
		poseStack,
		localClient.renderBuffers().bufferSource().getBuffer(this.renderType(Companion.FORKLIFT_TEXTURE)),
		packedLight,
		packedOverlay,
		color
	)

	companion object {
		val FORKLIFT_LAYER: ModelLayerLocation = ModelLayerLocation(modLocation("forklift"), "main")
		val FORKLIFT_TEXTURE: ResourceLocation = modLocation("textures/entity/forklift.png")
		fun createLayerDefinition(): LayerDefinition =
			LayerDefinition.create(this.createMesh(), 128, 128)

		fun createMesh(): MeshDefinition {
			val meshDefinition = MeshDefinition()
			val root = meshDefinition.root
			root.addOrReplaceChild(
				"engine",
				CubeListBuilder.create().texOffs(56, 25)
					.addBox(-2.0f, 0.0f, -6.0f, 14.0f, 15.0f, 8.0f, CubeDeformation(0.0f)),
				PartPose.offset(-5.0f, 7.0f, 12.0f)
			)
			root.addOrReplaceChild(
				"cage",
				CubeListBuilder.create().texOffs(0, 105)
					.addBox(-11.0f, -17.0f, 18.0f, 10.0f, 2.0f, 2.0f, CubeDeformation(0.0f))
					.texOffs(100, 25).addBox(-1.0f, -17.0f, 18.0f, 2.0f, 20.0f, 2.0f, CubeDeformation(0.0f))
					.texOffs(64, 101).addBox(-13.0f, -17.0f, 18.0f, 2.0f, 20.0f, 2.0f, CubeDeformation(0.0f))
					.texOffs(56, 48).addBox(-1.0f, -17.0f, 1.0f, 2.0f, 2.0f, 17.0f, CubeDeformation(0.0f))
					.texOffs(0, 60).addBox(-13.0f, -17.0f, 1.0f, 2.0f, 2.0f, 17.0f, CubeDeformation(0.0f))
					.texOffs(94, 60).addBox(-11.0f, -17.0f, -1.0f, 10.0f, 2.0f, 2.0f, CubeDeformation(0.0f))
					.texOffs(108, 25).addBox(-13.0f, -17.0f, -1.0f, 2.0f, 17.0f, 2.0f, CubeDeformation(0.0f))
					.texOffs(24, 108).addBox(-1.0f, -17.0f, -1.0f, 2.0f, 17.0f, 2.0f, CubeDeformation(0.0f)),
				PartPose.offset(6.0f, 4.0f, -11.0f)
			)
			root.addOrReplaceChild(
				"side_trim_left",
				CubeListBuilder.create().texOffs(0, 0)
					.addBox(0.0f, -11.0f, -20.0f, 2.0f, 4.0f, 26.0f, CubeDeformation(0.0f))
					.texOffs(68, 79).addBox(0.0f, -7.0f, 3.0f, 2.0f, 5.0f, 3.0f, CubeDeformation(0.0f))
					.texOffs(44, 94).addBox(0.0f, -17.0f, -2.0f, 2.0f, 6.0f, 8.0f, CubeDeformation(0.0f))
					.texOffs(68, 87).addBox(0.0f, -7.0f, -14.0f, 2.0f, 5.0f, 9.0f, CubeDeformation(0.0f)),
				PartPose.offset(7.0f, 24.0f, 8.0f)
			)
			root.addOrReplaceChild(
				"side_trim_right",
				CubeListBuilder.create().texOffs(24, 94)
					.addBox(0.0f, -17.0f, -2.0f, 2.0f, 6.0f, 8.0f, CubeDeformation(0.0f))
					.texOffs(0, 30).addBox(0.0f, -11.0f, -20.0f, 2.0f, 4.0f, 26.0f, CubeDeformation(0.0f))
					.texOffs(0, 109).addBox(0.0f, -7.0f, 3.0f, 2.0f, 5.0f, 3.0f, CubeDeformation(0.0f))
					.texOffs(90, 87).addBox(0.0f, -7.0f, -14.0f, 2.0f, 5.0f, 9.0f, CubeDeformation(0.0f)),
				PartPose.offset(-9.0f, 24.0f, 8.0f)
			)
			root.addOrReplaceChild(
				"fork",
				CubeListBuilder.create().texOffs(0, 79)
					.addBox(15.0f, 0.0f, -33.0f, 3.0f, 1.0f, 14.0f, CubeDeformation(0.0f))
					.texOffs(34, 79).addBox(4.0f, 0.0f, -33.0f, 3.0f, 1.0f, 14.0f, CubeDeformation(0.0f))
					.texOffs(110, 52).addBox(6.5f, -1.0f, -19.0f, 3.0f, 2.0f, 2.0f, CubeDeformation(0.0f))
					.texOffs(110, 56).addBox(12.5f, -1.0f, -19.0f, 3.0f, 2.0f, 2.0f, CubeDeformation(0.0f))
					.texOffs(52, 108).addBox(9.5f, -11.0f, -19.0f, 3.0f, 12.0f, 2.0f, CubeDeformation(0.0f))
					.texOffs(32, 108).addBox(15.0f, -11.0f, -19.0f, 3.0f, 12.0f, 2.0f, CubeDeformation(0.0f))
					.texOffs(110, 48).addBox(12.5f, -11.0f, -19.0f, 3.0f, 2.0f, 2.0f, CubeDeformation(0.0f))
					.texOffs(108, 44).addBox(6.5f, -11.0f, -19.0f, 3.0f, 2.0f, 2.0f, CubeDeformation(0.0f))
					.texOffs(42, 108).addBox(4.0f, -11.0f, -19.0f, 3.0f, 12.0f, 2.0f, CubeDeformation(0.0f)),
				PartPose.offset(-11.0f, 21.0f, 5.0f)
			)
			root.addOrReplaceChild(
				"Seat",
				CubeListBuilder.create().texOffs(38, 67)
					.addBox(-5.0f, -8.0f, -4.0f, 10.0f, 2.0f, 10.0f, CubeDeformation(0.0f))
					.texOffs(0, 94).addBox(-5.0f, -17.0f, 4.0f, 10.0f, 9.0f, 2.0f, CubeDeformation(0.0f)),
				PartPose.offset(0.0f, 19.0f, 0.0f)
			)
			root.addOrReplaceChild(
				"wheel_front_left",
				CubeListBuilder.create().texOffs(94, 48)
					.addBox(7.0f, -6.0f, -13.0f, 2.0f, 6.0f, 6.0f, CubeDeformation(0.0f)),
				PartPose.offset(0.0f, 24.0f, 0.0f)
			)
			root.addOrReplaceChild(
				"wheel_front_right",
				CubeListBuilder.create().texOffs(88, 101)
					.addBox(-9.0f, -6.0f, -13.0f, 2.0f, 6.0f, 6.0f, CubeDeformation(0.0f)),
				PartPose.offset(0.0f, 24.0f, 0.0f)
			)
			root.addOrReplaceChild(
				"wheel_back_left",
				CubeListBuilder.create().texOffs(72, 101)
					.addBox(7.0f, -6.0f, 4.0f, 2.0f, 6.0f, 6.0f, CubeDeformation(0.0f)),
				PartPose.offset(0.0f, 24.0f, 0.0f)
			)
			root.addOrReplaceChild(
				"wheel_back_right",
				CubeListBuilder.create().texOffs(104, 101)
					.addBox(-9.0f, -6.0f, 4.0f, 2.0f, 6.0f, 6.0f, CubeDeformation(0.0f)),
				PartPose.offset(0.0f, 24.0f, 0.0f)
			)
			val steeringWheel = root.addOrReplaceChild(
				"steering_wheel",
				CubeListBuilder.create(),
				PartPose.offset(0.0f, 24.0f, 0.0f)
			)
			steeringWheel.addOrReplaceChild(
				"cube_r1",
				CubeListBuilder.create().texOffs(38, 60)
					.addBox(-3.0f, -2.5f, 4.0f, 6.0f, 5.0f, 1.0f, CubeDeformation(0.0f)),
				PartPose.offsetAndRotation(0.0f, -18.5f, -10.0f, 0.6109f, 0.0f, 0.0f)
			)
			val steeringShaft = root.addOrReplaceChild(
				"steering_shaft",
				CubeListBuilder.create(),
				PartPose.offset(0.0f, 5.5f, -10.0f)
			)
			steeringShaft.addOrReplaceChild(
				"cube_r2",
				CubeListBuilder.create().texOffs(10, 109)
					.addBox(-1.0f, -0.5f, 0.0f, 2.0f, 1.0f, 4.0f, CubeDeformation(0.0f)),
				PartPose.offsetAndRotation(0.0f, 0.0f, 0.0f, 0.6109f, 0.0f, 0.0f)
			)
			root.addOrReplaceChild(
				"parts",
				CubeListBuilder.create().texOffs(78, 67)
					.addBox(-7.0f, -1.5f, -2.0f, 14.0f, 18.0f, 2.0f, CubeDeformation(0.0f))
					.texOffs(56, 0).addBox(-7.0f, 7.5f, 0.0f, 14.0f, 9.0f, 16.0f, CubeDeformation(0.0f)),
				PartPose.offset(0.0f, 5.5f, -10.0f)
			)
			root.addOrReplaceChild(
				"engine_exhaust",
				CubeListBuilder.create().texOffs(110, 64)
					.addBox(-1.0f, -3.0f, -1.0f, 2.0f, 3.0f, 2.0f, CubeDeformation(0.0f)),
				PartPose.offset(-5.0f, 7.0f, 12.0f)
			)

			return meshDefinition
		}
	}
}