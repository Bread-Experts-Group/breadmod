package breadmod.compat.geckolib

import breadmod.ModMain.modLocation
import breadmod.item.ToolGunItem
import net.minecraft.resources.ResourceLocation
import software.bernie.geckolib.model.GeoModel

class ToolGunGeoModel : GeoModel<ToolGunItem>() {
    private val modelResource = modLocation("geo/tool_gun.geo.json")
    private val textureResource = modLocation("textures/item/tool_gun.png")
    private val animationResource = modLocation("animations/tool_gun.animation.json")

    override fun getTextureResource(animatable: ToolGunItem): ResourceLocation = textureResource
    override fun getModelResource(animatable: ToolGunItem): ResourceLocation = modelResource
    override fun getAnimationResource(animatable: ToolGunItem): ResourceLocation = animationResource
}