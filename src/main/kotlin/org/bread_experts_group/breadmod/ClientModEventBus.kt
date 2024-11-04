package org.bread_experts_group.breadmod

import net.minecraft.client.model.EntityModel
import net.minecraft.client.renderer.entity.LivingEntityRenderer
import net.minecraft.client.resources.PlayerSkin
import net.minecraft.client.resources.model.ModelResourceLocation
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.LivingEntity
import net.neoforged.api.distmarker.Dist
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.client.event.*
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent
import net.neoforged.neoforge.client.gui.VanillaGuiLayers
import net.neoforged.neoforge.client.model.generators.ModelProvider
import org.bread_experts_group.breadmod.Breadmod.Companion.modLocation
import org.bread_experts_group.breadmod.block.BreadLiquidBlock
import org.bread_experts_group.breadmod.client.gui.ToolGunOverlay
import org.bread_experts_group.breadmod.client.gui.WarOverlay
import org.bread_experts_group.breadmod.client.model.ChefHatModel
import org.bread_experts_group.breadmod.client.render.entity.FakePlayerRenderer
import org.bread_experts_group.breadmod.client.render.entity.PrimedHappyBlockRenderer
import org.bread_experts_group.breadmod.client.render.entity.layers.ChefHatArmorLayer
import org.bread_experts_group.breadmod.client.screen.WheatCrusherScreen
import org.bread_experts_group.breadmod.item.toolGun.ToolGunItem
import org.bread_experts_group.breadmod.item.toolGun.ToolGunItem.Companion.TOOL_GUN_DEF
import org.bread_experts_group.breadmod.registry.entity.ModEntityTypes
import org.bread_experts_group.breadmod.registry.fluid.ModFluids
import org.bread_experts_group.breadmod.registry.item.ModItems
import org.bread_experts_group.breadmod.registry.menu.ModMenuTypes
import org.bread_experts_group.breadmod.util.itemColor

@Suppress("unused")
@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD, modid = Breadmod.ID, value = [Dist.CLIENT])
internal object ClientModEventBus {

    @SubscribeEvent
    fun registerClientExtensions(event: RegisterClientExtensionsEvent) {
        event.registerFluidType(BreadLiquidBlock.ClientExtensions, ModFluids.BREAD_LIQUID.type.get())
        event.registerItem(ToolGunItem.ToolGunItemExtensions(), ModItems.TOOL_GUN)
    }

    @SubscribeEvent
    fun registerRenderers(event: EntityRenderersEvent.RegisterRenderers) {
        event.registerEntityRenderer(ModEntityTypes.HAPPY_BLOCK_ENTITY.get(), ::PrimedHappyBlockRenderer)
        event.registerEntityRenderer(ModEntityTypes.FAKE_PLAYER.get(), ::FakePlayerRenderer)
    }

    @SubscribeEvent
    fun registerGuiLayers(event: RegisterGuiLayersEvent) {
        event.registerAboveAll(modLocation("war_overlay"), WarOverlay())
        event.registerBelow(VanillaGuiLayers.DEBUG_OVERLAY, modLocation("tool_gun_overlay"), ToolGunOverlay())
    }

    @SubscribeEvent
    fun registerItemColors(event: RegisterColorHandlersEvent.Item) {
        event.register(itemColor, ModItems.CHEF_HAT.get())
    }

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
    fun registerEntityLayers(event: EntityRenderersEvent.AddLayers) {
        for (skin: PlayerSkin.Model in event.skins) {
            val entity: LivingEntityRenderer<LivingEntity, EntityModel<LivingEntity>>? = event.getSkin(skin)
            entity?.addLayer(ChefHatArmorLayer(entity))
        }

        addHatLayer(EntityType.ZOMBIE, event)
        addHatLayer(EntityType.ARMOR_STAND, event)
        addHatLayer(EntityType.FOX, event)
    }

    @SubscribeEvent
    fun registerLayerDefinitions(event: EntityRenderersEvent.RegisterLayerDefinitions) {
        event.registerLayerDefinition(ChefHatModel.HAT_LAYER, ChefHatModel::createLayerDefinition)
    }

    @SubscribeEvent
    fun registerMenuScreens(event: RegisterMenuScreensEvent) {
        event.register(ModMenuTypes.WHEAT_CRUSHER.get(), ::WheatCrusherScreen)
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