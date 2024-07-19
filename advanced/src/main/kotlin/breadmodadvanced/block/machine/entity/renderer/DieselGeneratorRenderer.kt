package breadmodadvanced.block.machine.entity.renderer

import breadmod.util.capability.FluidContainer
import breadmod.util.render.drawQuad
import breadmod.util.render.renderBlockModel
import breadmodadvanced.ModMainAdv.modLocation
import breadmodadvanced.block.machine.entity.DieselGeneratorBlockEntity
import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.math.Axis
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.ItemBlockRenderTypes
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer
import net.minecraft.core.Direction
import net.minecraft.world.inventory.InventoryMenu
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions
import net.minecraftforge.client.model.generators.ModelProvider
import net.minecraftforge.common.capabilities.ForgeCapabilities
import org.joml.Quaternionf

class DieselGeneratorRenderer: BlockEntityRenderer<DieselGeneratorBlockEntity> {
    private enum class DieselGeneratorUpgrades { BATTERY, CHARGING, TURBO }

    private val dieselGeneratorModels = "${ModelProvider.BLOCK_FOLDER}/diesel_generator"

    private val doorModelLocation = modLocation("$dieselGeneratorModels/diesel_generator_door")
    private val batteryUpgradeModelLoc = modLocation("$dieselGeneratorModels/diesel_generator_battery_upgrade")
    private val chargingUpgradeModelLoc = modLocation("$dieselGeneratorModels/diesel_generator_charging_upgrade")
    private val turboUpgradeModelLoc = modLocation("$dieselGeneratorModels/diesel_generator_turbo_upgrade")

    override fun render(
        pBlockEntity: DieselGeneratorBlockEntity,
        pPartialTick: Float,
        pPoseStack: PoseStack,
        pBuffer: MultiBufferSource,
        pPackedLight: Int,
        pPackedOverlay: Int
    ) {
        val instance = Minecraft.getInstance()
        val modelManager = instance.modelManager
//        val doorModel = modelManager.getModel(doorModelLocation)
        val blockRotation = pBlockEntity.blockState.getValue(BlockStateProperties.HORIZONTAL_FACING)

        val doorOpen = pBlockEntity.blockState.getValue(BlockStateProperties.OPEN)
        val doorAxis = if(doorOpen) Axis.YN.rotationDegrees(120f) else Axis.YN.rotationDegrees(0f)

        val batteryUpgrade = modelManager.getModel(batteryUpgradeModelLoc)
        val chargingUpgrade = modelManager.getModel(chargingUpgradeModelLoc)
        val turboUpgrade = modelManager.getModel(turboUpgradeModelLoc)

        // Door Rendering
        renderDoor(doorAxis, blockRotation, pPoseStack, pBuffer, pBlockEntity, pPackedLight, pPackedOverlay)


        // Fluid Tank Rendering
        pBlockEntity.capabilityHolder.capabilityOrNull<FluidContainer>(ForgeCapabilities.FLUID_HANDLER)?.let {
            val tank = it.allTanks[0]
            if(tank.isEmpty) return

            val blockPos = pBlockEntity.blockPos
            val fluidTypeExtensions = IClientFluidTypeExtensions.of(tank.fluid.fluid)
            val stillFluidTexture = fluidTypeExtensions.getStillTexture(tank.fluid)?: return
            val fluidState = tank.fluid.fluid.defaultFluidState()
            val fluidSprite = instance.getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(stillFluidTexture)
            val fluidTint = fluidTypeExtensions.getTintColor(fluidState, pBlockEntity.level, blockPos)

            // todo normalize texture size to not be squashed or stretched, refer to General#GuiGraphics.renderFluid for clues

            val fluidHeight: Float = ((tank.fluidAmount.toFloat() / tank.capacity.toFloat() * 0.2f) + 0.623f)
            val builder = pBuffer.getBuffer(ItemBlockRenderTypes.getRenderLayer(fluidState))

            val dv1 = (fluidSprite.v1 - fluidSprite.v0)
            val v1 = (fluidSprite.v0 + ((dv1 / 2f) * fluidHeight))
            val u1 = fluidSprite.u0 + ((fluidSprite.u1 - fluidSprite.u0) * 0.8F)

            // Top / pX0 = Right, pX1 = left, pZ0 = Front, pZ1 = Back
            if(tank.fluidAmount.toFloat() < tank.capacity.toFloat()) {
                pPoseStack.pushPose()
                rotateFluid(blockRotation, pPoseStack) // Rotate on East and West axis
                drawQuad(builder, pPoseStack, 0.005f, fluidHeight, 0.27f, 0.995f, fluidHeight, 0.75f, fluidSprite.u0, fluidSprite.v0, fluidSprite.u1, fluidSprite.v1, pPackedLight, fluidTint)
                pPoseStack.popPose()
            }

            // West / Right
            pPoseStack.pushPose()
            rotateFluid(blockRotation, pPoseStack)
            pPoseStack.mulPose(Axis.YP.rotationDegrees(90f))
            pPoseStack.translate(-1f, 0f, 0f)
            drawQuad(builder, pPoseStack, 0.25f, 0.57f, 0.0005f, 0.73f, fluidHeight, 0.005f, fluidSprite.u0, fluidSprite.v0, u1, v1, pPackedLight, fluidTint)
            pPoseStack.popPose()

            // East / Left
            pPoseStack.pushPose()
            rotateFluid(blockRotation, pPoseStack)
            pPoseStack.mulPose(Axis.YP.rotationDegrees(-90f))
            pPoseStack.translate(0f, 0f, -1f)
            drawQuad(builder, pPoseStack, 0.27f, 0.57f, 0.005f, 0.75f, fluidHeight, 0.005f, fluidSprite.u0, fluidSprite.v0, u1, v1, pPackedLight, fluidTint)
            pPoseStack.popPose()
        }
    }

    private fun rotateFluid(pBlockRotation: Direction, pPoseStack: PoseStack) {
        if(pBlockRotation == Direction.WEST || pBlockRotation == Direction.EAST) {
            pPoseStack.mulPose(Axis.YN.rotationDegrees(90f))
            pPoseStack.translate(0f, 0f, -1f)
        }
    }

    private fun rotateModel(pPoseStack: PoseStack, blockRotation: Direction) {
        when(blockRotation) {
            Direction.NORTH -> {}
            Direction.SOUTH -> { pPoseStack.mulPose(Axis.YN.rotationDegrees(180f)) }
            Direction.WEST -> { pPoseStack.mulPose(Axis.YN.rotationDegrees(-90f)) }
            Direction.EAST -> { pPoseStack.mulPose(Axis.YN.rotationDegrees(90f)) }
            else -> println("invalid rotation")
        }
    }

    private fun renderDoor(
        pDoorAxis: Quaternionf,
        blockRotation: Direction,
        pPoseStack: PoseStack,
        pBuffer: MultiBufferSource,
        pBlockEntity: DieselGeneratorBlockEntity,
        pPackedLight: Int,
        pPackedOverlay: Int
    ) {
        val doorModel = Minecraft.getInstance().modelManager.getModel(doorModelLocation)
        pPoseStack.pushPose()
        when(blockRotation) {
            Direction.NORTH -> pPoseStack.translate(0.625, 0.063, 0.0)
            Direction.SOUTH -> pPoseStack.translate(0.375, 0.0625, 1.0)
            Direction.WEST -> pPoseStack.translate(0.0, 0.0628, 0.375)
            Direction.EAST -> pPoseStack.translate(1.0, 0.0625, 0.625)
            else -> println("facing.. nowhere??")
        }
        rotateModel(pPoseStack, blockRotation)
        pPoseStack.mulPose(pDoorAxis)
        renderBlockModel(pPoseStack, pBuffer, pBlockEntity, doorModel, pPackedLight, pPackedOverlay)
        pPoseStack.popPose()
    }
}