package bread.mod.breadmod.fabric.client

import bread.mod.breadmod.ModMainCommon
import bread.mod.breadmod.ModMainCommon.modLocation
import bread.mod.breadmod.client.render.entity.layers.ChefHatArmorLayer
import bread.mod.breadmod.registry.item.ModItems
import bread.mod.breadmod.util.render.itemColor
import bread.mod.breadmod.util.render.renderBuffer
import bread.mod.breadmod.util.render.rgMinecraft
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry
import net.fabricmc.fabric.api.client.rendering.v1.LivingEntityFeatureRendererRegistrationCallback
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents
import net.minecraft.client.model.EntityModel
import net.minecraft.client.renderer.entity.LivingEntityRenderer
import net.minecraft.client.renderer.entity.layers.RenderLayer
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.animal.Fox
import net.minecraft.world.entity.decoration.ArmorStand
import net.minecraft.world.entity.player.Player

/**
 * Mod Client initializer for Fabric.
 */
@Suppress("UNCHECKED_CAST")
class MainFabricClient : ClientModInitializer {

    /**
     * Initializes all client mod classes for breadmod.
     */
    override fun onInitializeClient() {
        // This entrypoint is suitable for setting up client-specific logic, such as rendering.
        ModMainCommon.initClient()

        ModelLoadingPlugin.register(AdditionalModelLoader())

        WorldRenderEvents.AFTER_TRANSLUCENT.register { renderContext ->
            val poseStack = renderContext.matrixStack() ?: return@register
            val camera = renderContext.camera()
            val partialTick = rgMinecraft.timer.realtimeDeltaTicks
            val levelRenderer = renderContext.worldRenderer()

            renderBuffer.removeIf { (mutableList, renderEvent) ->
                renderEvent.invoke(
                    mutableList,
                    poseStack,
                    camera,
                    partialTick,
                    levelRenderer
                )
            }
        }

        // Register model layers
        LivingEntityFeatureRendererRegistrationCallback.EVENT.register { _, renderer, event, _ ->
            addHatLayer<Player>(renderer, event)
            addHatLayer<ArmorStand>(renderer, event)
            addHatLayer<Fox>(renderer, event)
        }

        // Register item colors
        ColorProviderRegistry.ITEM.register(itemColor, ModItems.CHEF_HAT.get())
    }

    /**
     * Adds the chef hat armor layer to any living entity renderer
     *
     * @author Logan Mclean
     * @since 1.0.0
     */
    private fun <T : LivingEntity> addHatLayer(
        renderer: LivingEntityRenderer<*, *>,
        event: LivingEntityFeatureRendererRegistrationCallback.RegistrationHelper
    ) {
        val livingRenderer = renderer as LivingEntityRenderer<LivingEntity, EntityModel<LivingEntity>>
        event.register(ChefHatArmorLayer(livingRenderer) as RenderLayer<T, EntityModel<T>>)
    }

    private class AdditionalModelLoader : ModelLoadingPlugin {
        private val toolGunDef = "tool_gun"

        override fun onInitializeModelLoader(context: ModelLoadingPlugin.Context) {
            context.addModels(
                modLocation("item/$toolGunDef/item"),
                modLocation("item/$toolGunDef/coil"),
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
