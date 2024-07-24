package breadmod.block.storage.entity.renderer

import breadmod.block.machine.entity.AbstractMachineBlockEntity
import breadmod.block.util.translateOnBlockSide
import breadmod.util.capability.EnergyBattery
import breadmod.util.capability.FluidContainer
import breadmod.util.capability.IndexableItemHandler
import breadmod.util.render.renderText
import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.math.Axis
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer
import net.minecraft.network.chat.Component
import net.minecraftforge.common.capabilities.ForgeCapabilities
import java.awt.Color

@Suppress("unused")
abstract class BaseAbstractStorageBlockRenderer<T: AbstractMachineBlockEntity<T>>: BlockEntityRenderer<T> {
    val instance: Minecraft = Minecraft.getInstance()
    val fontRenderer = instance.font

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
        pPoseStack.pushPose()
        translateOnBlockSide(pBlockEntity, pPoseStack, pPosX, pPosY, pPosZ)
        pPoseStack.mulPose(Axis.XN.rotationDegrees(180f))
        pPoseStack.scale(pScale, pScale, pScale)
        renderText(pComponent, pColor, pBackgroundColor, fontRenderer, pPoseStack, pBuffer, pDropShadow, Color(255, 255, 255, 0).rgb)
        pPoseStack.popPose()
    }
}