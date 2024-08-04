package breadmod.client.render.entity

import breadmod.ModMain
import breadmod.entity.BreadBulletEntity
import net.minecraft.client.renderer.entity.ArrowRenderer
import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.resources.ResourceLocation

class BreadBulletEntityRenderer(pContext: EntityRendererProvider.Context) : ArrowRenderer<BreadBulletEntity>(pContext) {
    override fun getTextureLocation(pEntity: BreadBulletEntity): ResourceLocation =
        ModMain.modLocation("textures","entity","projectile","bread_bullet.png")
}