package breadmod.block.storage.entity.renderer

import breadmod.block.machine.entity.AbstractMachineBlockEntity
import breadmod.util.capability.EnergyBattery
import breadmod.util.capability.FluidContainer
import breadmod.util.capability.IndexableItemHandler
import breadmod.util.render.renderText
import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.math.Axis
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer
import net.minecraft.core.Direction
import net.minecraft.network.chat.Component
import net.minecraft.world.item.ItemDisplayContext
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraftforge.common.capabilities.ForgeCapabilities
import java.awt.Color

@Suppress("unused")
abstract class BaseAbstractStorageBlockRenderer<T: AbstractMachineBlockEntity<T>>: BlockEntityRenderer<T> {
    private val instance: Minecraft = Minecraft.getInstance()
    private val fontRenderer = instance.font

    abstract override fun render(
        pBlockEntity: T, pPartialTick: Float, pPoseStack: PoseStack,
        pBuffer: MultiBufferSource, pPackedLight: Int, pPackedOverlay: Int
    )

    fun energyHandler(pBlockEntity: T) =
        pBlockEntity.capabilityHolder.capabilityOrNull<EnergyBattery>(ForgeCapabilities.ENERGY)
    fun fluidHandler(pBlockEntity: T) =
        pBlockEntity.capabilityHolder.capabilityOrNull<FluidContainer>(ForgeCapabilities.FLUID_HANDLER)
    fun itemHandler(pBlockEntity: T) =
        pBlockEntity.capabilityHolder.capabilityOrNull<IndexableItemHandler>(ForgeCapabilities.ITEM_HANDLER)

    // todo item rendering function with display context
//    fun renderItemOnSide(pPoseStack: PoseStack) {
//        val itemRender = Minecraft.getInstance().itemRenderer
//
//        itemRender.renderStatic(Items.BREAD, ItemDisplayContext.GUI)
//    }

    fun drawTextOnSide(
        pComponent: Component,
        pColor: Int,
        pBackgroundColor: Int,
        pDropShadow: Boolean,
        pPoseStack: PoseStack,
        pBuffer: MultiBufferSource,
        pBlockEntity: T,
        pScale: Float,
        pPosX: Double,
        pPosY: Double,
        pPosZ: Double = 0.0
    ) {
        val direction = pBlockEntity.blockState.getValue(BlockStateProperties.HORIZONTAL_FACING).opposite ?: return
        val yRot = direction.toYRot()
        pPoseStack.pushPose()
        when(direction) {
            Direction.NORTH -> {
                pPoseStack.translate(0f, 1f, 1.0001f)
                pPoseStack.translate(pPosX, -pPosY, pPosZ)
            }
            Direction.EAST -> {
                pPoseStack.mulPose(Axis.YN.rotationDegrees(90f))
                pPoseStack.mulPose(Axis.XN.rotationDegrees(-90f))
                pPoseStack.translate(0f, 0.0001f, -1f)
                pPoseStack.translate(pPosX, pPosZ, pPosY)
            }
            Direction.WEST -> {
                pPoseStack.mulPose(Axis.ZP.rotationDegrees(90f))
                pPoseStack.mulPose(Axis.YN.rotationDegrees(-90f))
                pPoseStack.translate(-1f, -1.0001f, 1f)
                pPoseStack.translate(pPosX, -pPosZ, -pPosY)
            }
            else -> {
                pPoseStack.mulPose(Axis.ZP.rotationDegrees(180f))
                pPoseStack.translate(-1f, -1f, -0.0001f)
                pPoseStack.translate(pPosX, pPosY, -pPosZ)
            }
        }
        pPoseStack.mulPose(Axis.XN.rotationDegrees(yRot))
        pPoseStack.scale(pScale, pScale, pScale)
        renderText(pComponent, pColor, pBackgroundColor, fontRenderer, pPoseStack, pBuffer, pDropShadow, Color(255, 255, 255, 0).rgb)
        pPoseStack.popPose()
    }
}