package breadmod.client.render.storage

import breadmod.block.entity.storage.FluidStorageBlockEntity
import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.renderer.MultiBufferSource

class FluidStorageRenderer : BaseAbstractStorageBlockRenderer<FluidStorageBlockEntity>() {
    override fun render(
        pBlockEntity: FluidStorageBlockEntity,
        pPartialTick: Float,
        pPoseStack: PoseStack,
        pBuffer: MultiBufferSource,
        pPackedLight: Int,
        pPackedOverlay: Int
    ) {
        TODO("Not yet implemented")
    }
}