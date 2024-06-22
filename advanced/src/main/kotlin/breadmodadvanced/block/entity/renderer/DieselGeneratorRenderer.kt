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
//        pPoseStack.rotateAround(blockRotation, 0.0f, 0.0f, 0.0f)

        renderBlockModel(pPoseStack, pBuffer, pBlockEntity, doorModel, pPackedLight, pPackedOverlay)

        pBlockEntity.capabilityHolder.capabilityOrNull<FluidContainer>(ForgeCapabilities.FLUID_HANDLER)?.let { // todo figure out why this isn't returning any fluid or amount at all
            it.allTanks[0].let { tank ->
//                println(tank.space)
                if(tank.isEmpty) return
                val blockPos = pBlockEntity.blockPos
                val fluidTypeExtensions = IClientFluidTypeExtensions.of(tank.fluid.fluid)
                val stillFluidTexture = fluidTypeExtensions.getStillTexture(tank.fluid)?: return
                val fluidState = tank.fluid.fluid.defaultFluidState()
                val fluidSprite = instance.getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(stillFluidTexture)
                val fluidTint = fluidTypeExtensions.getTintColor(fluidState, pBlockEntity.level, blockPos)

                val fluidHeight: Float = ((tank.fluidAmount / tank.capacity * 0.5f) + 1.25f)
                val builder = pBuffer.getBuffer(ItemBlockRenderTypes.getRenderLayer(fluidState))

                if(tank.fluidAmount < tank.capacity) {
                    drawQuad(builder, pPoseStack, 0.25f, fluidHeight, 0.25f, 0.75f, fluidHeight, 0.75f, fluidSprite.u0, fluidSprite.v0, fluidSprite.v0, fluidSprite.v1, pPackedLight, fluidTint)
                }

                drawQuad(builder, pPoseStack, 0.25f, 0f, 0.25f, 0.75f, fluidHeight, 0.25f, fluidSprite.u0, fluidSprite.v0, fluidSprite.v0, fluidSprite.v1, pPackedLight, fluidTint)

                pPoseStack.pushPose()
                pPoseStack.mulPose(Axis.YP.rotationDegrees(180f))
                pPoseStack.translate(-1f, 0f, 0f)
                drawQuad(builder, pPoseStack, 0.25f, 0f, 0.75f, 0.75f, fluidHeight, 0.75f, fluidSprite.u0, fluidSprite.v0, fluidSprite.v0, fluidSprite.v1, pPackedLight, fluidTint)
                pPoseStack.popPose()

                pPoseStack.pushPose()
                pPoseStack.mulPose(Axis.YP.rotationDegrees(90f))
                pPoseStack.translate(-1f, 0f, 0f)
                drawQuad(builder, pPoseStack, 0.25f, 0f, 0.25f, 0.75f, fluidHeight, 0.25f, fluidSprite.u0, fluidSprite.v0, fluidSprite.v0, fluidSprite.v1, pPackedLight, fluidTint)
                pPoseStack.popPose()

                pPoseStack.pushPose()
                pPoseStack.mulPose(Axis.YP.rotationDegrees(90f))
                pPoseStack.translate(0f, 0f, -1f)
                drawQuad(builder, pPoseStack, 0.25f, 0f, 0.25f, 0.75f, fluidHeight, 0.25f, fluidSprite.u0, fluidSprite.v0, fluidSprite.v0, fluidSprite.v1, pPackedLight, fluidTint)
                pPoseStack.popPose()
            }
        }
    }
}