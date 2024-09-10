package bread.mod.breadmod.fabric.client

import bread.mod.breadmod.ModMainCommon
import bread.mod.breadmod.ModMainCommon.modLocation
import bread.mod.breadmod.util.render.renderBuffer
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents

class MainFabricClient : ClientModInitializer {

    override fun onInitializeClient() {
        // This entrypoint is suitable for setting up client-specific logic, such as rendering.
        ModMainCommon.initClient()

        ModelLoadingPlugin.register(AdditionalModelLoader())

        WorldRenderEvents.END.register { renderContext ->
            val poseStack = renderContext.matrixStack() ?: return@register
            val camera = renderContext.camera()

            renderBuffer.removeIf { (mutableList, renderEvent) -> renderEvent.invoke(mutableList, poseStack, camera) }
        }
    }

    private class AdditionalModelLoader : ModelLoadingPlugin {
        private val toolGunDef = "tool_gun"

        override fun onInitializeModelLoader(context: ModelLoadingPlugin.Context) {
            context.addModels(
                modLocation("item/$toolGunDef/item"),
                modLocation("item/$toolGunDef/item"),
                modLocation("block/generator_on"),
                modLocation("block/toaster/handle"),
                modLocation("block/creative_generator/creative_generator_star"),
                modLocation("block/creative_generator"),
                modLocation("block/sphere"),
                // todo need to figure out how to register objs on fabric
//                modLocation("item/$toolGunDef/alt/tool_gun_alt")
            )
        }
    }
}
