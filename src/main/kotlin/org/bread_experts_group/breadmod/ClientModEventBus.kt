package org.bread_experts_group.breadmod

import com.mojang.blaze3d.vertex.DefaultVertexFormat
import net.minecraft.client.model.EntityModel
import net.minecraft.client.renderer.ShaderInstance
import net.minecraft.client.renderer.entity.LivingEntityRenderer
import net.minecraft.client.renderer.item.ItemProperties
import net.minecraft.client.resources.PlayerSkin
import net.minecraft.client.resources.model.ModelResourceLocation
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.LivingEntity
import net.neoforged.api.distmarker.Dist
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent
import net.neoforged.neoforge.client.event.EntityRenderersEvent
import net.neoforged.neoforge.client.event.ModelEvent
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent
import net.neoforged.neoforge.client.event.RegisterShadersEvent
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent
import net.neoforged.neoforge.client.gui.VanillaGuiLayers
import net.neoforged.neoforge.client.model.generators.ModelProvider
import org.bread_experts_group.breadmod.BreadMod.Companion.modLocation
import org.bread_experts_group.breadmod.client.gui.CameraOverlay
import org.bread_experts_group.breadmod.client.gui.ToolGunOverlay
import org.bread_experts_group.breadmod.client.gui.WarOverlay
import org.bread_experts_group.breadmod.client.model.ChefHatModel
import org.bread_experts_group.breadmod.client.model.GluonGunBackpackModel
import org.bread_experts_group.breadmod.client.render.entity.FakePlayerRenderer
import org.bread_experts_group.breadmod.client.render.entity.PrimedHappyBlockRenderer
import org.bread_experts_group.breadmod.client.render.entity.block.ItemInWorldRenderer
import org.bread_experts_group.breadmod.client.render.entity.block.MicrowaveRenderer
import org.bread_experts_group.breadmod.client.render.entity.block.ToasterRenderer
import org.bread_experts_group.breadmod.client.render.entity.layers.ChefHatArmorLayer
import org.bread_experts_group.breadmod.client.render.entity.layers.GluonGunBackpackArmorLayer
import org.bread_experts_group.breadmod.client.screen.DoughMachineScreen
import org.bread_experts_group.breadmod.client.screen.WheatCrusherScreen
import org.bread_experts_group.breadmod.registry.recipe.actual.fluid_energy.test.FluidEnergyScreen
import org.bread_experts_group.breadmod.experimental.recipe.block.multi.fluid.MultiFluidScreen
import org.bread_experts_group.breadmod.experimental.recipe.block.multi.item.MultiItemScreen
import org.bread_experts_group.breadmod.experimental.recipe.block.single.fluid.SingleFluidScreen
import org.bread_experts_group.breadmod.experimental.recipe.block.single.fluid_item.SingleFluidItemScreen
import org.bread_experts_group.breadmod.experimental.recipe.block.single.item.SingleItemScreen
import org.bread_experts_group.breadmod.registry.KeyMappings.openModeGui
import org.bread_experts_group.breadmod.registry.KeyMappings.placeItemKey
import org.bread_experts_group.breadmod.registry.block.ModBlockEntityTypes
import org.bread_experts_group.breadmod.registry.block.ModFluids
import org.bread_experts_group.breadmod.registry.block.actual.BreadLiquidBlock
import org.bread_experts_group.breadmod.registry.entity.ModEntityTypes
import org.bread_experts_group.breadmod.registry.item.ModItems
import org.bread_experts_group.breadmod.registry.item.actual.armor.GluonGunBackpackItem
import org.bread_experts_group.breadmod.registry.item.actual.tool_gun.ToolGunItem
import org.bread_experts_group.breadmod.registry.item.actual.tool_gun.ToolGunItem.Companion.TOOL_GUN_DEF
import org.bread_experts_group.breadmod.registry.menu.ModMenuTypes
import org.bread_experts_group.breadmod.registry.shader.ModRenderType
import org.bread_experts_group.breadmod.util.render.itemColor

@Suppress("unused")
@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD, modid = BreadMod.ID, value = [Dist.CLIENT])
internal object ClientModEventBus {
	@SubscribeEvent
	fun clientSetup(event : FMLClientSetupEvent) {
		event.enqueueWork {
			ItemProperties.register(
				ModItems.BREAD_SHIELD.get(), modLocation("blocking")
			) { itemStack, _, livingEntity, _ ->
				if (livingEntity != null && livingEntity.isUsingItem && livingEntity.useItem == itemStack) 1f else 0f
			}
			// todo fix later
//            ItemProperties.register(ModItems.CERTIFICATE.get(), modLocation("signed")) { stack, _, _, _ ->
//                if (stack.tag != null && stack.tag!!.contains("author")) 1f else 0f
//            }
		}
	}
	@SubscribeEvent
	fun registerKeyMappings(event : RegisterKeyMappingsEvent) {
//        event.register(changeMode)
//        event.register(openGuiEditor)
		event.register(openModeGui)
		event.register(placeItemKey)
	}
	@SubscribeEvent
	fun registerShaders(event : RegisterShadersEvent) {
		event.registerShader(
			ShaderInstance(
				event.resourceProvider,
				modLocation("rendertype_solid_texture"),
				DefaultVertexFormat.BLOCK
			)
		) { ModRenderType.solidInstance = it }
	}
	@SubscribeEvent
	fun registerClientExtensions(event : RegisterClientExtensionsEvent) {
		event.registerFluidType(BreadLiquidBlock.ClientExtensions, ModFluids.BREAD_LIQUID.type.get())
		event.registerItem(ToolGunItem.ToolGunItemExtensions(), ModItems.TOOL_GUN)
		event.registerItem(GluonGunBackpackItem.GluonGunExtensions(), ModItems.GLUON_GUN)
	}
	@SubscribeEvent
	fun registerRenderers(event : EntityRenderersEvent.RegisterRenderers) {
		event.registerEntityRenderer(ModEntityTypes.HAPPY_BLOCK_ENTITY.get(), ::PrimedHappyBlockRenderer)
		event.registerEntityRenderer(ModEntityTypes.FAKE_PLAYER.get(), ::FakePlayerRenderer)
		event.registerBlockEntityRenderer(ModBlockEntityTypes.TOASTER.get(), ::ToasterRenderer)
		event.registerBlockEntityRenderer(ModBlockEntityTypes.MICROWAVE.get(), ::MicrowaveRenderer)
		event.registerBlockEntityRenderer(ModBlockEntityTypes.ITEM_IN_WORLD.get(), ::ItemInWorldRenderer)
	}
	@SubscribeEvent
	fun registerGuiLayers(event : RegisterGuiLayersEvent) {
		event.registerAboveAll(modLocation("war_overlay"), WarOverlay())
		event.registerAboveAll(modLocation("camera_overlay"), CameraOverlay())
		event.registerBelow(VanillaGuiLayers.DEBUG_OVERLAY, modLocation("tool_gun_overlay"), ToolGunOverlay())
	}
	@SubscribeEvent
	fun registerItemColors(event : RegisterColorHandlersEvent.Item) {
		event.register(
			itemColor,
			ModItems.CHEF_HAT.get(),
		)
	}

	private fun modModelLoc(id : String) = ModelResourceLocation.standalone(modLocation(id))
	@Suppress("UNCHECKED_CAST")
	/**
	 * Adds the chef hat armor layer to any living entity renderer
	 *
	 * @author Logan Mclean
	 * @since 1.0.0
	 * @throws IllegalArgumentException if [type] is not a [LivingEntityRenderer].
	 */
	private fun addHatLayer(type : EntityType<*>, event : EntityRenderersEvent.AddLayers) {
		if (event.getRenderer(type) is LivingEntityRenderer<*, *>) {
			val renderer = event.getRenderer(type) as LivingEntityRenderer<LivingEntity, EntityModel<LivingEntity>>
			renderer.addLayer(ChefHatArmorLayer(renderer))
		} else throw IllegalArgumentException("Expected LivingEntityRenderer, got ${event.getRenderer(type)}")
	}
	@SubscribeEvent
	fun registerAdditionalModels(event : ModelEvent.RegisterAdditional) {
		event.register(this.modModelLoc("${ModelProvider.ITEM_FOLDER}/$TOOL_GUN_DEF/item"))
		event.register(this.modModelLoc("${ModelProvider.ITEM_FOLDER}/$TOOL_GUN_DEF/coil"))
		event.register(this.modModelLoc("${ModelProvider.BLOCK_FOLDER}/generator_on"))
		event.register(this.modModelLoc("${ModelProvider.BLOCK_FOLDER}/toaster/handle"))
		event.register(this.modModelLoc("${ModelProvider.BLOCK_FOLDER}/creative_generator/creative_generator_star"))
		event.register(this.modModelLoc("${ModelProvider.BLOCK_FOLDER}/creative_generator"))
		event.register(this.modModelLoc("${ModelProvider.BLOCK_FOLDER}/sphere"))
		event.register(this.modModelLoc("${ModelProvider.ITEM_FOLDER}/$TOOL_GUN_DEF/alt/tool_gun_alt"))
		event.register(this.modModelLoc("${ModelProvider.BLOCK_FOLDER}/outline/outline_wrong"))
		event.register(this.modModelLoc("${ModelProvider.BLOCK_FOLDER}/outline/outline_right"))
		event.register(this.modModelLoc("${ModelProvider.BLOCK_FOLDER}/microwave/microwave_door"))
		event.register(this.modModelLoc("${ModelProvider.BLOCK_FOLDER}/microwave/microwave_plate"))
	}
	@SubscribeEvent
	fun registerEntityLayers(event : EntityRenderersEvent.AddLayers) {
		for (skin : PlayerSkin.Model in event.skins) {
			val entity : LivingEntityRenderer<LivingEntity, EntityModel<LivingEntity>>? = event.getSkin(skin)
			entity?.addLayer(ChefHatArmorLayer(entity))
			entity?.addLayer(GluonGunBackpackArmorLayer(entity))
		}
//        addHatLayer(EntityType.ZOMBIE, event)
		this.addHatLayer(EntityType.ARMOR_STAND, event)
		this.addHatLayer(EntityType.FOX, event)
	}
	@SubscribeEvent
	fun registerLayerDefinitions(event : EntityRenderersEvent.RegisterLayerDefinitions) {
		event.registerLayerDefinition(ChefHatModel.HAT_LAYER, ChefHatModel::createLayerDefinition)
		event.registerLayerDefinition(
			GluonGunBackpackModel.BACKPACK_LAYER,
			GluonGunBackpackModel::createLayerDefinition
		)
	}
	@SubscribeEvent
	fun registerMenuScreens(event : RegisterMenuScreensEvent) {
		event.register(ModMenuTypes.WHEAT_CRUSHER.get(), ::WheatCrusherScreen)
		event.register(ModMenuTypes.DOUGH_MACHINE.get(), ::DoughMachineScreen)
		// Experimental stuff
		event.register(ModMenuTypes.MULTI_ITEM.get(), ::MultiItemScreen)
		event.register(ModMenuTypes.MULTI_FLUID.get(), ::MultiFluidScreen)
		event.register(ModMenuTypes.SINGLE_ITEM.get(), ::SingleItemScreen)
		event.register(ModMenuTypes.SINGLE_FLUID.get(), ::SingleFluidScreen)
		event.register(ModMenuTypes.SINGLE_FLUID_ITEM.get(), ::SingleFluidItemScreen)

		event.register(ModMenuTypes.FLUID_ENERGY_TEST.get(), ::FluidEnergyScreen)
	}
}