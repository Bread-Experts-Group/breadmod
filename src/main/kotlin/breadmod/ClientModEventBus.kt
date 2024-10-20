package breadmod

import breadmod.ClientForgeEventBus.changeMode
import breadmod.ClientForgeEventBus.createdMappings
import breadmod.ClientForgeEventBus.openGuiEditor
import breadmod.ModMain.ID
import breadmod.ModMain.modLocation
import breadmod.client.gui.ToolGunOverlay
import breadmod.client.gui.WarOverlay
import breadmod.client.render.*
import breadmod.client.render.entity.BreadBulletEntityRenderer
import breadmod.client.render.entity.PrimedHappyBlockRenderer
import breadmod.client.render.storage.EnergyStorageRenderer
import breadmod.client.screen.CertificateItemScreen
import breadmod.client.screen.DoughMachineScreen
import breadmod.client.screen.WheatCrusherScreen
import breadmod.client.screen.sound_block.SoundBlockScreenFactory
import breadmod.client.screen.tool_gun.creator.ToolGunCreatorSpawnMenuFactory
import breadmod.datagen.tool_gun.BreadModToolGunModeProvider.Companion.TOOL_GUN_DEF
import breadmod.datagen.tool_gun.BreadModToolGunModeProvider.Control
import breadmod.item.armor.ArmorColor
import breadmod.item.armor.BreadArmorItem
import breadmod.registry.block.ModBlockEntityTypes
import breadmod.registry.entity.ModEntityTypes.BREAD_BULLET_ENTITY
import breadmod.registry.entity.ModEntityTypes.HAPPY_BLOCK_ENTITY
import breadmod.registry.item.ModItems
import breadmod.registry.menu.ModMenuTypes
import com.mojang.blaze3d.platform.InputConstants
import net.minecraft.client.KeyMapping
import net.minecraft.client.gui.screens.MenuScreens
import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider
import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.client.renderer.item.ItemProperties
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.item.ItemStack
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.client.event.EntityRenderersEvent
import net.minecraftforge.client.event.ModelEvent.RegisterAdditional
import net.minecraftforge.client.event.RegisterColorHandlersEvent
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent
import net.minecraftforge.client.event.RegisterKeyMappingsEvent
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay
import net.minecraftforge.client.model.generators.ModelProvider
import net.minecraftforge.client.settings.KeyConflictContext
import net.minecraftforge.client.settings.KeyModifier
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent


@Suppress("unused")
@Mod.EventBusSubscriber(modid = ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = [Dist.CLIENT])
object ClientModEventBus {
    //val BLURRED_RENDER_TARGET = TextureTarget(rgMinecraft.window.width, rgMinecraft.window.height, true, Minecraft.ON_OSX)

    @SubscribeEvent
    fun onClientSetup(event: FMLClientSetupEvent) {
        ModMain.LOGGER.info("Client setup")

        event.enqueueWork {
            ItemProperties.register(
                ModItems.BREAD_SHIELD.get(), modLocation("blocking")
            ) { itemStack: ItemStack, _: ClientLevel?, livingEntity: LivingEntity?, _: Int ->
                if (livingEntity != null && livingEntity.isUsingItem && livingEntity.useItem == itemStack) 1.0f else 0.0f
            }

            ItemProperties.register(ModItems.CERTIFICATE.get(), modLocation("signed")) { stack, _, _, _ ->
                if (stack.tag != null && stack.tag!!.contains("author")) 1f else 0f
            }

            MenuScreens.register(ModMenuTypes.DOUGH_MACHINE.get()) { pMenu, pInventory, pTitle ->
                DoughMachineScreen(
                    pMenu,
                    pInventory,
                    pTitle
                )
            }
            MenuScreens.register(ModMenuTypes.WHEAT_CRUSHER.get()) { pMenu, pInventory, pTitle ->
                WheatCrusherScreen(
                    pMenu,
                    pInventory,
                    pTitle
                )
            }
            MenuScreens.register(ModMenuTypes.CERTIFICATE.get()) { pMenu, pInventory, pTitle ->
                CertificateItemScreen(
                    pMenu,
                    pInventory,
                    pTitle
                )
            }
            MenuScreens.register(ModMenuTypes.TOOL_GUN_CREATOR.get()) { pMenu, pInventory, pTitle ->
                ToolGunCreatorSpawnMenuFactory.create(
                    pMenu,
                    pInventory,
                    pTitle
                )
            }
            MenuScreens.register(ModMenuTypes.SOUND_BLOCK.get()) { pMenu, pInventory, pTitle ->
                SoundBlockScreenFactory.create(
                    pMenu,
                    pInventory,
                    pTitle
                )
            }

            // Power Generators
            //MenuScreens.register(ModMenuTypes.GENERATOR.get()) { pMenu, pInventory, pTitle -> GeneratorScreen(pMenu, pInventory, pTitle) }
        }
    }


    @SubscribeEvent
    fun registerItemColors(event: RegisterColorHandlersEvent.Item) {
        event.register(ArmorColor, *ModItems.deferredRegister.entries.mapNotNull {
            val armorItem = it.get()
            if (armorItem is BreadArmorItem) armorItem else null
        }.toTypedArray())
    }

    @SubscribeEvent
    fun registerRenders(event: EntityRenderersEvent.RegisterRenderers) {
        event.registerEntityRenderer(HAPPY_BLOCK_ENTITY.get()) { pContext: EntityRendererProvider.Context ->
            PrimedHappyBlockRenderer(pContext)
        }
        event.registerEntityRenderer(BREAD_BULLET_ENTITY.get()) { pContext: EntityRendererProvider.Context ->
            BreadBulletEntityRenderer(pContext)
        }
    }

    @SubscribeEvent
    fun registerGuiOverlays(event: RegisterGuiOverlaysEvent) {
        event.registerBelow(VanillaGuiOverlay.DEBUG_TEXT.id(), "tool_gun_overlay", ToolGunOverlay())
        event.registerAboveAll("war_overlay", WarOverlay())
    }

    @SubscribeEvent
    fun registerCustomModels(event: RegisterAdditional) { // ModelEvent
        event.register(modLocation("${ModelProvider.ITEM_FOLDER}/$TOOL_GUN_DEF/item"))
        event.register(modLocation("${ModelProvider.ITEM_FOLDER}/$TOOL_GUN_DEF/coil"))
        event.register(modLocation("${ModelProvider.BLOCK_FOLDER}/generator_on"))
        event.register(modLocation("${ModelProvider.BLOCK_FOLDER}/toaster/handle"))
        event.register(modLocation("${ModelProvider.BLOCK_FOLDER}/creative_generator/creative_generator_star"))
        event.register(modLocation("${ModelProvider.BLOCK_FOLDER}/creative_generator"))

        event.register(modLocation("${ModelProvider.BLOCK_FOLDER}/sphere"))
        event.register(modLocation("${ModelProvider.ITEM_FOLDER}/$TOOL_GUN_DEF/alt/tool_gun_alt"))
    }

    @SubscribeEvent
    fun registerBlockEntityRenderers(event: EntityRenderersEvent.RegisterRenderers) {
        event.registerBlockEntityRenderer(ModBlockEntityTypes.MONITOR.get()) { SidedScreenRenderer() }
        event.registerBlockEntityRenderer(ModBlockEntityTypes.CREATIVE_GENERATOR.get()) { CreativeGeneratorRenderer() }
        event.registerBlockEntityRenderer(ModBlockEntityTypes.ENERGY_STORAGE.get()) { EnergyStorageRenderer() }
        event.registerBlockEntityRenderer(ModBlockEntityTypes.TOASTER.get()) { ToasterRenderer() }
        event.registerBlockEntityRenderer(ModBlockEntityTypes.SOUND_BLOCK.get()) { SoundBlockRenderer() }
//        event.registerBlockEntityRenderer(ModBlockEntityTypes.FLUID_STORAGE.get()) { FluidStorageRenderer() }

        val genericMachineRenderer = { _: BlockEntityRendererProvider.Context -> GenericMachineBlockEntityRenderer() }
        event.registerBlockEntityRenderer(ModBlockEntityTypes.DOUGH_MACHINE.get(), genericMachineRenderer)
        event.registerBlockEntityRenderer(ModBlockEntityTypes.WHEAT_CRUSHER.get(), genericMachineRenderer)
    }

    @SubscribeEvent
    fun registerKeyMappings(event: RegisterKeyMappingsEvent) {
        event.register(changeMode)
        event.register(openGuiEditor)
    }

    val toolGunBindList = mutableMapOf<Control, KeyMapping>()
    fun createMappingsForControls(prepared: List<Control>): List<KeyMapping> {
        prepared.forEach {
            val mapping = if (it.modifier != null) {
                KeyMapping(
                    it.nameKey,
                    KeyConflictContext.IN_GAME,
                    KeyModifier.valueFromString(it.modifier),
                    InputConstants.getKey(it.key),
                    it.categoryKey
                )
            } else {
                KeyMapping(
                    it.nameKey,
                    KeyConflictContext.IN_GAME,
                    InputConstants.getKey(it.key),
                    it.categoryKey
                )
            }
            toolGunBindList[it] = mapping
        }
        createdMappings = toolGunBindList.values.toList()
        return createdMappings
    }

//    @SubscribeEvent
//    fun registerShaders(event: RegisterShadersEvent) {
//        event.registerShader(
//            ShaderInstance(
//                event.resourceProvider,
//                modLocation("gui_blur"),
//                DefaultVertexFormat.POSITION_COLOR
//            )
//        ) { shader ->
//            ModRenderTypes.guiBlurShader = shader
//        }
//    }
//
//    /* https://gist.github.com/gigaherz/b8756ff463541f07a644ef8f14cb10f5 */
//    @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS", "INACCESSIBLE_TYPE")
//    object ModRenderTypes : RenderType(
//        null, null, null,
//        0, false, false,
//        null, null
//    ) {
//        lateinit var guiBlurShader: ShaderInstance
//        private val GUI_BLUR_SHADER_STATE = ShaderStateShard { guiBlurShader }
//
//        //val GUI_BLUR: Function<ResourceLocation, RenderType> = Util.memoize(::guiBlur)
//        val GUI_BLUR = guiBlur()
//
//        private fun guiBlur(): RenderType {
//            val cs = CompositeState.builder()
//                .setShaderState(GUI_BLUR_SHADER_STATE)
//                .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
//                .setDepthTestState(LEQUAL_DEPTH_TEST)
//                .createCompositeState(false)
//
//            return create(
//                "gui_blur",
//                DefaultVertexFormat.POSITION_COLOR, VertexFormat.Mode.QUADS, 256,
//                true, false, cs
//            )
//        }
//    }
}