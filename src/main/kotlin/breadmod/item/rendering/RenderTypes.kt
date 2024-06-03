package breadmod.item.rendering

import breadmod.ModMain
import com.mojang.blaze3d.vertex.DefaultVertexFormat
import com.mojang.blaze3d.vertex.VertexFormat
import com.simibubi.create.AllSpecialTextures
import net.minecraft.client.renderer.RenderStateShard
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.ShaderInstance
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.client.event.RegisterShadersEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod.EventBusSubscriber
import java.io.IOException

/*
* Code credit goes to the Create team
* https://github.com/Creators-of-Create/Create
*/

// @RenderTypes.java in create

class RenderTypes(
    pName: String,
    pSetupState: Runnable,
    pClearState: Runnable
) : RenderStateShard(pName, pSetupState, pClearState) {
    val glowingShader: ShaderStateShard = ShaderStateShard { Shaders.glowingShader }

    private val outlineSolid = RenderType.create(createLayerName("outline_solid"), DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 256, false,
        false, RenderType.CompositeState.builder()
            .setShaderState(RENDERTYPE_ENTITY_SOLID_SHADER)
            .setTextureState(TextureStateShard(AllSpecialTextures.BLANK.location, false, false))
            .setCullState(CULL)
            .setLightmapState(LIGHTMAP)
            .setOverlayState(OVERLAY)
            .createCompositeState(false))


    private fun createLayerName(pName: String) = ModMain.ID + ":" + name

    @EventBusSubscriber(value = [Dist.CLIENT], bus = EventBusSubscriber.Bus.MOD)
    companion object Shaders {
        var glowingShader: ShaderInstance? = null

        @Throws(IOException::class)
        @SubscribeEvent
        fun onRegisterShaders(event: RegisterShadersEvent) {
            val resourceProvider = event.resourceProvider
            event.registerShader(ShaderInstance(resourceProvider, ModMain.modLocation("glowing_shader"), DefaultVertexFormat.NEW_ENTITY)) {
                shader: ShaderInstance -> glowingShader = shader
            }
        }
    }
}