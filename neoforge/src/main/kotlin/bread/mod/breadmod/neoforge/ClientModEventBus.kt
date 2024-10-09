package bread.mod.breadmod.neoforge

import bread.mod.breadmod.ModMainCommon
import bread.mod.breadmod.ModMainCommon.modLocation
import bread.mod.breadmod.client.render.entity.layers.ChefHatArmorLayer
import bread.mod.breadmod.registry.item.ModItems
import bread.mod.breadmod.util.render.itemColor
import net.minecraft.client.model.EntityModel
import net.minecraft.client.renderer.entity.LivingEntityRenderer
import net.minecraft.client.resources.PlayerSkin
import net.minecraft.client.resources.model.ModelResourceLocation
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.LivingEntity
import net.neoforged.api.distmarker.Dist
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.client.event.EntityRenderersEvent
import net.neoforged.neoforge.client.event.ModelEvent
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent
import net.neoforged.neoforge.client.model.generators.ModelProvider

@Suppress("unused")
@EventBusSubscriber(modid = ModMainCommon.MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = [Dist.CLIENT])
internal object ClientModEventBus {
    const val TOOL_GUN_DEF = "tool_gun"

//    @SubscribeEvent
//    fun registerOverlays(event: RegisterGuiLayersEvent) {
//        event.registerAboveAll(ModMainCommon.modLocation("war_overlay"), WarOverlay())
//    }

    @SubscribeEvent
    fun registerAdditionalModels(event: ModelEvent.RegisterAdditional) {
        event.register(modModelLoc("${ModelProvider.ITEM_FOLDER}/$TOOL_GUN_DEF/item"))
        event.register(modModelLoc("${ModelProvider.ITEM_FOLDER}/$TOOL_GUN_DEF/coil"))
        event.register(modModelLoc("${ModelProvider.BLOCK_FOLDER}/generator_on"))
        event.register(modModelLoc("${ModelProvider.BLOCK_FOLDER}/toaster/handle"))
        event.register(modModelLoc("${ModelProvider.BLOCK_FOLDER}/creative_generator/creative_generator_star"))
        event.register(modModelLoc("${ModelProvider.BLOCK_FOLDER}/creative_generator"))
        event.register(modModelLoc("${ModelProvider.BLOCK_FOLDER}/sphere"))
        event.register(modModelLoc("${ModelProvider.ITEM_FOLDER}/$TOOL_GUN_DEF/alt/tool_gun_alt"))
        event.register(modModelLoc("${ModelProvider.BLOCK_FOLDER}/outline/outline_wrong"))
        event.register(modModelLoc("${ModelProvider.BLOCK_FOLDER}/outline/outline_right"))
    }

    @SubscribeEvent
    fun registerItemColors(event: RegisterColorHandlersEvent.Item) {
        event.register(itemColor, ModItems.CHEF_HAT.get())
    }

    @SubscribeEvent
    fun registerEntityLayers(event: EntityRenderersEvent.AddLayers) {
        for (skin: PlayerSkin.Model in event.skins) {
            val entity: LivingEntityRenderer<LivingEntity, EntityModel<LivingEntity>>? =
                event.getSkin(skin)

            entity?.addLayer(ChefHatArmorLayer(entity))
        }

        addHatLayer(EntityType.ZOMBIE, event)
        addHatLayer(EntityType.ARMOR_STAND, event)
        addHatLayer(EntityType.FOX, event)

//        for (renderer: EntityRenderer<*> in dispatcher.renderers.values) {
//            if (renderer !is LivingEntityRenderer<*, *>) return
//            if (renderer.model !is AgeableListModel) return
//            println(renderer)
//
//            val livingEntity = renderer as LivingEntityRenderer<LivingEntity, EntityModel<LivingEntity>>
//            livingEntity.addLayer(ChefHatArmorLayer(livingEntity))
//        }
    }

    @Suppress("UNCHECKED_CAST")
    /**
     * Adds the chef hat armor layer to any living entity renderer
     *
     * @author Logan Mclean
     * @since 1.0.0
     * @throws IllegalArgumentException if [type] is not [LivingEntityRenderer]
     */
    private fun addHatLayer(type: EntityType<*>, event: EntityRenderersEvent.AddLayers) {
        if (event.getRenderer(type) is LivingEntityRenderer<*, *>) {
            val renderer = event.getRenderer(type) as LivingEntityRenderer<LivingEntity, EntityModel<LivingEntity>>
            renderer.addLayer(ChefHatArmorLayer(renderer))
        } else throw IllegalArgumentException("Expected LivingEntityRenderer, got ${event.getRenderer(type)}")
    }

    private fun modModelLoc(id: String) =
        ModelResourceLocation.standalone(modLocation(id))
}