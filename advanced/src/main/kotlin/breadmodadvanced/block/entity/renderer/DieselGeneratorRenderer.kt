package breadmodadvanced.block.entity.renderer

import breadmod.util.capability.FluidContainer
import breadmod.util.render.drawQuad
import breadmod.util.render.renderBlockModel
import breadmodadvanced.ModMainAdv
import breadmodadvanced.block.entity.DieselGeneratorBlockEntity
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

class DieselGeneratorRenderer: BlockEntityRenderer<DieselGeneratorBlockEntity> {
    private val doorModelLocation = ModMainAdv.modLocation("${ModelProvider.BLOCK_FOLDER}/diesel_generator/diesel_generator_door")

    override fun render(
        pBlockEntity: DieselGeneratorBlockEntity,
        pPartialTick: Float,
        pPoseStack: PoseStack,
        pBuffer: MultiBufferSource,
        pPackedLight: Int,
        pPackedOverlay: Int
    ) {
        val instance = Minecraft.getInstance()
        val renderer = instance.blockRenderer
        val doorModel = instance.modelManager.getModel(doorModelLocation)
        val blockRotation = pBlockEntity.blockState.getValue(BlockStateProperties.HORIZONTAL_FACING)

        // Door Rendering
        pPoseStack.pushPose()
        when(blockRotation) { // TODO Set up rotations and translations for door, upgrade cards, and fluid, set up functions for transforming separate models
            Direction.NORTH -> {
//                println("facing north")
                pPoseStack.translate(0.625, 0.063, 0.0)
                // todo set up toggle property in block to.. toggle the door being open or closed
                pPoseStack.mulPose(Axis.YN.rotationDegrees(120f))
            }
            Direction.SOUTH -> {
//                println("facing south")
                pPoseStack.translate(0.375, 0.0625, 1.0)
                pPoseStack.mulPose(Axis.YN.rotationDegrees(180f))
                pPoseStack.mulPose(Axis.YN.rotationDegrees(120f))
            }
            Direction.WEST -> {
//                println("facing west")
                pPoseStack.translate(0.0, 0.0628, 0.375)
                pPoseStack.mulPose(Axis.YN.rotationDegrees(-90f))
                pPoseStack.mulPose(Axis.YN.rotationDegrees(120f))
            }
            Direction.EAST -> {
//                println("facing east")
                pPoseStack.translate(1.0, 0.0625, 0.625)
                pPoseStack.mulPose(Axis.YN.rotationDegrees(90f))
                pPoseStack.mulPose(Axis.YN.rotationDegrees(120f))
            }
            else -> println("facing.. nowhere??")
        }
        renderBlockModel(pPoseStack, pBuffer, pBlockEntity, doorModel, pPackedLight, pPackedOverlay)

        // Fluid Tank Rendering
        pPoseStack.popPose()
        pBlockEntity.capabilityHolder.capabilityOrNull<FluidContainer>(ForgeCapabilities.FLUID_HANDLER)?.let {
            it.allTanks[0].let { tank ->
                if(tank.isEmpty) return
                val blockPos = pBlockEntity.blockPos
                val fluidTypeExtensions = IClientFluidTypeExtensions.of(tank.fluid.fluid)
                val stillFluidTexture = fluidTypeExtensions.getStillTexture(tank.fluid)?: return
                val fluidState = tank.fluid.fluid.defaultFluidState()
                val fluidSprite = instance.getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(stillFluidTexture)
                val fluidTint = fluidTypeExtensions.getTintColor(fluidState, pBlockEntity.level, blockPos)

                val fluidHeight: Float = ((tank.fluidAmount.toFloat() / tank.capacity.toFloat() * 0.2f) + 0.623f)
                val builder = pBuffer.getBuffer(ItemBlockRenderTypes.getRenderLayer(fluidState))

                // Top / pX0 = Right, pX1 = left, pZ0 = Front, pZ1 = Back
                if(tank.fluidAmount.toFloat() < tank.capacity.toFloat()) {
                    pPoseStack.pushPose()
                    rotateModel(blockRotation, pPoseStack) // Rotate on East and West axis
                    drawQuad(builder, pPoseStack, 0.005f, fluidHeight, 0.27f, 0.995f, fluidHeight, 0.75f, fluidSprite.u0, fluidSprite.v0, fluidSprite.u1, fluidSprite.v1, pPackedLight, fluidTint)
                    pPoseStack.popPose()
                }

                // West / Right
                pPoseStack.pushPose()
                rotateModel(blockRotation, pPoseStack)
                pPoseStack.mulPose(Axis.YP.rotationDegrees(90f))
                pPoseStack.translate(-1f, 0f, 0f)
                drawQuad(builder, pPoseStack, 0.25f, 0.57f, 0.0005f, 0.73f, fluidHeight, 0.005f, fluidSprite.u0, fluidSprite.v0, fluidSprite.u1, fluidSprite.v1, pPackedLight, fluidTint)
                pPoseStack.popPose()

                // East / Left
                pPoseStack.pushPose()
                rotateModel(blockRotation, pPoseStack)
                pPoseStack.mulPose(Axis.YP.rotationDegrees(-90f))
                pPoseStack.translate(0f, 0f, -1f)
                drawQuad(builder, pPoseStack, 0.27f, 0.57f, 0.005f, 0.75f, fluidHeight, 0.005f, fluidSprite.u0, fluidSprite.v0, fluidSprite.u1, fluidSprite.v1, pPackedLight, fluidTint)
                pPoseStack.popPose()
            }
        }
    }

    private fun rotateModel(pBlockRotation: Direction, pPoseStack: PoseStack) {
        if(pBlockRotation == Direction.WEST || pBlockRotation == Direction.EAST) {
            pPoseStack.mulPose(Axis.YN.rotationDegrees(90f))
            pPoseStack.translate(0f, 0f, -1f)
        }
    }
}