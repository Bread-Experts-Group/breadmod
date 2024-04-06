package breadmod.entity.renderer

import breadmod.BreadMod
import net.minecraft.client.renderer.entity.EntityRenderer
import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.item.PrimedTnt

class PrimedHappyBlockRenderer(pContext: EntityRendererProvider.Context) : EntityRenderer<PrimedTnt>(pContext) {
    override fun getTextureLocation(pEntity: PrimedTnt): ResourceLocation {
        return texture
    }

    companion object {
        private val texture = ResourceLocation(BreadMod.ID, "textures/block/happy_block.png")
    }
}