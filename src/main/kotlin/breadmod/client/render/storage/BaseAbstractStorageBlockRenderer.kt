package breadmod.client.render.storage

import breadmod.block.entity.machine.AbstractMachineBlockEntity
import breadmod.util.capability.EnergyBattery
import breadmod.util.capability.FluidContainer
import breadmod.util.capability.IndexableItemHandler
import breadmod.util.render.rgMinecraft
import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.gui.Font
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer
import net.minecraftforge.common.capabilities.ForgeCapabilities

@Suppress("unused")
abstract class BaseAbstractStorageBlockRenderer<T : AbstractMachineBlockEntity<T>> : BlockEntityRenderer<T> {
    val fontRenderer: Font = rgMinecraft.font

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
//        val itemRender = minecraft.itemRenderer
//
//        itemRender.renderStatic(Items.BREAD, ItemDisplayContext.GUI)
//    }
}