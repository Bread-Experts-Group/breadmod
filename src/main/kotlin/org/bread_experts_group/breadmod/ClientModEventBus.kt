package org.bread_experts_group.breadmod

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
import net.neoforged.neoforge.client.event.RegisterItemDecorationsEvent
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent
import net.neoforged.neoforge.client.event.RegisterShadersEvent
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent
import net.neoforged.neoforge.client.gui.VanillaGuiLayers
import net.neoforged.neoforge.client.model.generators.ModelProvider
import org.bread_experts_group.breadmod.BreadMod.Companion.modLocation
import org.bread_experts_group.breadmod.client.gui.overlays.CameraOverlay
import org.bread_experts_group.breadmod.client.gui.overlays.InternetChatRelayOverlay
import org.bread_experts_group.breadmod.client.gui.overlays.ScreenBleedOverlay
import org.bread_experts_group.breadmod.client.gui.overlays.TestOverlay
import org.bread_experts_group.breadmod.client.gui.overlays.ToolGunOverlay
import org.bread_experts_group.breadmod.client.gui.overlays.WarOverlay
import org.bread_experts_group.breadmod.client.gui.screens.DoughMachineScreen
import org.bread_experts_group.breadmod.client.gui.screens.WheatCrusherScreen
import org.bread_experts_group.breadmod.client.model.ChefHatModel
import org.bread_experts_group.breadmod.client.model.ForkliftModel
import org.bread_experts_group.breadmod.client.model.GluonGunBackpackModel
import org.bread_experts_group.breadmod.client.render.entity.FakePlayerRenderer
import org.bread_experts_group.breadmod.client.render.entity.ForkliftRenderer
import org.bread_experts_group.breadmod.client.render.entity.PrimedHappyBlockRenderer
import org.bread_experts_group.breadmod.client.render.entity.PrimedNukeBlockRenderer
import org.bread_experts_group.breadmod.client.render.entity.block.DoubleOrNothingRenderer
import org.bread_experts_group.breadmod.client.render.entity.block.EnergyStorageRenderer
import org.bread_experts_group.breadmod.client.render.entity.block.ItemInWorldRenderer
import org.bread_experts_group.breadmod.client.render.entity.block.MicrowaveRenderer
import org.bread_experts_group.breadmod.client.render.entity.block.MonitorRenderer
import org.bread_experts_group.breadmod.client.render.entity.block.ToasterRenderer
import org.bread_experts_group.breadmod.client.render.entity.layers.ChefHatArmorLayer
import org.bread_experts_group.breadmod.client.render.entity.layers.GluonGunBackpackArmorLayer
import org.bread_experts_group.breadmod.client.render.itemColor
import org.bread_experts_group.breadmod.client.render.scaleFlat
import org.bread_experts_group.breadmod.data_holders.common.ToolGunData
import org.bread_experts_group.breadmod.registry.KeyMappings.openModeGui
import org.bread_experts_group.breadmod.registry.KeyMappings.placeItemKey
import org.bread_experts_group.breadmod.registry.KeyMappings.toolGunAltFour
import org.bread_experts_group.breadmod.registry.KeyMappings.toolGunAltOne
import org.bread_experts_group.breadmod.registry.KeyMappings.toolGunAltThree
import org.bread_experts_group.breadmod.registry.KeyMappings.toolGunAltTwo
import org.bread_experts_group.breadmod.registry.block.ModBlockEntityTypes
import org.bread_experts_group.breadmod.registry.block.ModFluids
import org.bread_experts_group.breadmod.registry.block.actual.BreadLiquidBlock
import org.bread_experts_group.breadmod.registry.entity.ModEntityTypes
import org.bread_experts_group.breadmod.registry.item.ModItems
import org.bread_experts_group.breadmod.registry.item.actual.armor.GluonGunBackpackItem
import org.bread_experts_group.breadmod.registry.item.actual.tool_gun.ToolGunItem
import org.bread_experts_group.breadmod.registry.item.actual.tool_gun.ToolGunItem.Companion.TOOL_GUN_DEF
import org.bread_experts_group.breadmod.registry.menu.ModMenuTypes
import org.bread_experts_group.breadmod.registry.recipe.actual.fluid_energy.test.FluidEnergyScreen
import org.bread_experts_group.breadmod.registry.shader.ModRenderType

@Suppress("unused")
@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD, modid = BreadMod.ID, value = [Dist.CLIENT])
internal object ClientModEventBus {
	@SubscribeEvent
	fun clientSetup(event: FMLClientSetupEvent) {
		event.enqueueWork {
			ItemProperties.register(
				ModItems.BREAD_SHIELD.get(), modLocation("blocking")
			) { itemStack, _, livingEntity, _ ->
				if (livingEntity != null && livingEntity.isUsingItem && livingEntity.useItem == itemStack) 1f else 0f
			}
		}
	}

	@SubscribeEvent
	fun registerItemDecorations(event: RegisterItemDecorationsEvent) {
		event.register(ModItems.TOOL_GUN.asItem()) { guiGraphics, _, stack, xOffset, yOffset ->
			val (mode, _, _) = ToolGunData.get(stack)
			val poseStack = guiGraphics.pose()
			poseStack.pushPose()
			poseStack.scaleFlat(0.4f)
			poseStack.translate(xOffset * 2.5 + 2, yOffset * 2.5 + 22, 0.0)
			mode.getCustomRenderer().getModeWidget().icon.select({
				guiGraphics.renderFakeItem(it, 0, 0)
			}, {
				poseStack.pushPose()
				poseStack.scaleFlat(16f / it.textureWidth)
				it.blitTexture(guiGraphics, 0, 0)
				poseStack.popPose()
			})
			poseStack.popPose()
			false
		}
	}

	@SubscribeEvent
	fun registerKeyMappings(event: RegisterKeyMappingsEvent) {
		event.register(openModeGui)
		event.register(toolGunAltOne)
		event.register(toolGunAltTwo)
		event.register(toolGunAltThree)
		event.register(toolGunAltFour)
		event.register(placeItemKey)
	}

	@SubscribeEvent
	fun registerShaders(event: RegisterShadersEvent) {
		event.registerShader(
			ShaderInstance(
				event.resourceProvider,
				modLocation("rendertype_rainbow"),
				ModRenderType.rainbowVertexFormat
			)
		) { ModRenderType.rainbowInstance = it }
		event.registerShader(
			ShaderInstance(
				event.resourceProvider,
				modLocation("rendertype_astral"),
				ModRenderType.astralVertexFormat
			)
		) { ModRenderType.astralInstance = it }
	}

	@SubscribeEvent
	fun registerClientExtensions(event: RegisterClientExtensionsEvent) {
		event.registerFluidType(BreadLiquidBlock.ClientExtensions, ModFluids.BREAD_LIQUID.type.get())
		event.registerItem(ToolGunItem.ToolGunItemExtensions, ModItems.TOOL_GUN)
		event.registerItem(GluonGunBackpackItem.GluonGunExtensions(), ModItems.GLUON_GUN)
	}

	@SubscribeEvent
	fun registerRenderers(event: EntityRenderersEvent.RegisterRenderers) {
		event.registerEntityRenderer(ModEntityTypes.HAPPY_BLOCK_ENTITY.get(), ::PrimedHappyBlockRenderer)
		event.registerEntityRenderer(ModEntityTypes.NUKE_BLOCK_ENTITY.get(), ::PrimedNukeBlockRenderer)
		event.registerEntityRenderer(ModEntityTypes.FAKE_PLAYER.get(), ::FakePlayerRenderer)
		event.registerEntityRenderer(ModEntityTypes.FORKLIFT.get(), ::ForkliftRenderer)
		event.registerBlockEntityRenderer(ModBlockEntityTypes.TOASTER.get(), ::ToasterRenderer)
		event.registerBlockEntityRenderer(ModBlockEntityTypes.MICROWAVE.get(), ::MicrowaveRenderer)
		event.registerBlockEntityRenderer(ModBlockEntityTypes.ITEM_IN_WORLD.get(), ::ItemInWorldRenderer)
		event.registerBlockEntityRenderer(ModBlockEntityTypes.ENERGY_STORAGE.get(), ::EnergyStorageRenderer)
		event.registerBlockEntityRenderer(ModBlockEntityTypes.MONITOR.get(), ::MonitorRenderer)
		event.registerBlockEntityRenderer(ModBlockEntityTypes.DOUBLE_OR_NOTHING.get(), ::DoubleOrNothingRenderer)
	}

	@SubscribeEvent
	fun registerGuiLayers(event: RegisterGuiLayersEvent) {
		event.registerAboveAll(modLocation("war_overlay"), WarOverlay())
		event.registerAbove(VanillaGuiLayers.CHAT, modLocation("irc_overlay"), InternetChatRelayOverlay())
		event.registerAboveAll(modLocation("camera_overlay"), CameraOverlay())
		event.registerAboveAll(modLocation("test_overlay"), TestOverlay())
		event.registerAboveAll(modLocation("screen_bleed_overlay"), ScreenBleedOverlay())
		event.registerBelow(VanillaGuiLayers.DEBUG_OVERLAY, modLocation("tool_gun_overlay"), ToolGunOverlay())
	}

	@SubscribeEvent
	fun registerItemColors(event: RegisterColorHandlersEvent.Item) {
		event.register(
			itemColor,
			ModItems.CHEF_HAT.get(), ModItems.DOPED_BREAD.get()
		)
	}

	private fun modModelLoc(id: String) = ModelResourceLocation.standalone(modLocation(id))

	@Suppress("UNCHECKED_CAST")
	/**
	 * Adds the chef hat armor layer to any living entity renderer
	 *
	 * @author Logan Mclean
	 * @since 1.0.0
	 * @throws IllegalArgumentException if [type] is not a [LivingEntityRenderer].
	 */
	private fun addHatLayer(type: EntityType<*>, event: EntityRenderersEvent.AddLayers) {
		if (event.getRenderer(type) is LivingEntityRenderer<*, *>) {
			val renderer = event.getRenderer(type) as LivingEntityRenderer<LivingEntity, EntityModel<LivingEntity>>
			renderer.addLayer(ChefHatArmorLayer(renderer))
		} else throw IllegalArgumentException("Expected LivingEntityRenderer, got ${event.getRenderer(type)}")
	}

	@SubscribeEvent
	fun registerAdditionalModels(event: ModelEvent.RegisterAdditional) {
		event.register(this.modModelLoc("${ModelProvider.ITEM_FOLDER}/$TOOL_GUN_DEF/item"))
		event.register(this.modModelLoc("${ModelProvider.ITEM_FOLDER}/$TOOL_GUN_DEF/coil"))
		event.register(this.modModelLoc("${ModelProvider.BLOCK_FOLDER}/generator_on"))
		event.register(this.modModelLoc("${ModelProvider.BLOCK_FOLDER}/toaster/handle"))
		event.register(this.modModelLoc("${ModelProvider.BLOCK_FOLDER}/creative_generator/creative_generator_star"))
		event.register(this.modModelLoc("${ModelProvider.BLOCK_FOLDER}/creative_generator"))
		event.register(this.modModelLoc("${ModelProvider.ITEM_FOLDER}/$TOOL_GUN_DEF/alt/tool_gun_alt"))
		event.register(this.modModelLoc("${ModelProvider.BLOCK_FOLDER}/microwave/microwave_door"))
		event.register(this.modModelLoc("${ModelProvider.BLOCK_FOLDER}/microwave/microwave_plate"))
		event.register(this.modModelLoc("${ModelProvider.BLOCK_FOLDER}/axis"))
	}

	@SubscribeEvent
	fun registerEntityLayers(event: EntityRenderersEvent.AddLayers) {
		for (skin: PlayerSkin.Model in event.skins) {
			val entity: LivingEntityRenderer<LivingEntity, EntityModel<LivingEntity>> = event.getSkin(skin) ?: return
			entity.addLayer(ChefHatArmorLayer(entity))
			entity.addLayer(GluonGunBackpackArmorLayer(entity))
		}
//        addHatLayer(EntityType.ZOMBIE, event)
		this.addHatLayer(EntityType.ARMOR_STAND, event)
		this.addHatLayer(EntityType.FOX, event)
	}

	@SubscribeEvent
	fun registerLayerDefinitions(event: EntityRenderersEvent.RegisterLayerDefinitions) {
		event.registerLayerDefinition(ChefHatModel.HAT_LAYER, ChefHatModel::createLayerDefinition)
		event.registerLayerDefinition(
			GluonGunBackpackModel.BACKPACK_LAYER,
			GluonGunBackpackModel::createLayerDefinition
		)
		event.registerLayerDefinition(ForkliftModel.FORKLIFT_LAYER, ForkliftModel::createLayerDefinition)
	}

	@SubscribeEvent
	fun registerMenuScreens(event: RegisterMenuScreensEvent) {
		event.register(ModMenuTypes.WHEAT_CRUSHER.get(), ::WheatCrusherScreen)
		event.register(ModMenuTypes.DOUGH_MACHINE.get(), ::DoughMachineScreen)
		// Experimental stuff
		event.register(ModMenuTypes.FLUID_ENERGY_TEST.get(), ::FluidEnergyScreen)
	}
}