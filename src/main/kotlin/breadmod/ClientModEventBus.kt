package breadmod

import breadmod.ModMain.ID
import breadmod.ModMain.modLocation
import breadmod.block.color.BlackbodyBlockColor
import breadmod.block.entity.renderer.BlackbodyRenderer
import breadmod.block.entity.renderer.SidedScreenRenderer
import breadmod.block.machine.entity.renderer.CreativeGeneratorRenderer
import breadmod.block.machine.entity.screen.DoughMachineScreen
import breadmod.block.machine.entity.screen.WheatCrusherScreen
import breadmod.block.storage.entity.renderer.EnergyBlockRenderer
import breadmod.datagen.tool_gun.BreadModToolGunModeProvider.Companion.TOOL_GUN_DEF
import breadmod.datagen.tool_gun.BreadModToolGunModeProvider.Control
import breadmod.entity.renderer.BreadBulletEntityRenderer
import breadmod.entity.renderer.PrimedHappyBlockRenderer
import breadmod.hud.ToolGunOverlay
import breadmod.item.armor.BreadArmorItem
import breadmod.item.armor.ArmorColor
import breadmod.item.screen.CertificateScreen
import breadmod.item.tool_gun.ToolGunItem.Companion.changeMode
import breadmod.registry.block.ModBlockEntities
import breadmod.registry.block.ModBlocks
import breadmod.registry.entity.ModEntityTypes.BREAD_BULLET_ENTITY
import breadmod.registry.entity.ModEntityTypes.HAPPY_BLOCK_ENTITY
import breadmod.registry.item.ModItems
import breadmod.registry.menu.ModMenuTypes
import com.mojang.blaze3d.vertex.DefaultVertexFormat
import com.mojang.blaze3d.vertex.VertexFormat
import net.minecraft.Util
import net.minecraft.client.KeyMapping
import net.minecraft.client.gui.screens.MenuScreens
import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.ShaderInstance
import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.client.renderer.item.ItemProperties
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.item.ItemStack
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.client.event.*
import net.minecraftforge.client.event.ModelEvent.RegisterAdditional
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay
import net.minecraftforge.client.model.generators.ModelProvider
import net.minecraftforge.client.settings.KeyConflictContext
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent
import java.util.function.Function


@Suppress("unused")
@Mod.EventBusSubscriber(modid = ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = [Dist.CLIENT])
object ClientModEventBus {
    @SubscribeEvent
    fun onClientSetup(event: FMLClientSetupEvent) {
        ModMain.LOGGER.info("Client setup")

        event.enqueueWork {
            ItemProperties.register(
                ModItems.BREAD_SHIELD.get(), modLocation("blocking")) { itemStack: ItemStack, _: ClientLevel?, livingEntity: LivingEntity?, _: Int ->
                if (livingEntity != null && livingEntity.isUsingItem && livingEntity.useItem == itemStack) 1.0f else 0.0f
            }

            ItemProperties.register(ModItems.CERTIFICATE.get(), modLocation("signed")) { stack, _, _, _ ->
                if (stack.tag != null && stack.tag!!.contains("author")) 1f else 0f
            }

            MenuScreens.register(ModMenuTypes.DOUGH_MACHINE.get()) { pMenu, pInventory, pTitle -> DoughMachineScreen(pMenu,pInventory,pTitle) }
            MenuScreens.register(ModMenuTypes.WHEAT_CRUSHER.get()) { pMenu, pInventory, pTitle -> WheatCrusherScreen(pMenu,pInventory,pTitle) }
            MenuScreens.register(ModMenuTypes.CERTIFICATE.get()) { pMenu, pInventory, pTitle -> CertificateScreen(pMenu, pInventory, pTitle) }

            // Power Generators
            //MenuScreens.register(ModMenuTypes.GENERATOR.get()) { pMenu, pInventory, pTitle -> GeneratorScreen(pMenu, pInventory, pTitle) }
        }
    }

    @SubscribeEvent
    fun registerBlockColors(event: RegisterColorHandlersEvent.Block) {
        event.register(BlackbodyBlockColor, ModBlocks.HEATING_ELEMENT_BLOCK.get().block)
    }
    @SubscribeEvent
    fun registerItemColors(event: RegisterColorHandlersEvent.Item) {
        event.register(ArmorColor, *ModItems.deferredRegister.entries.mapNotNull {
            val armorItem = it.get()
            if(armorItem is BreadArmorItem) armorItem else null
        }.toTypedArray())
    }

    @SubscribeEvent
    fun registerRenders(event: EntityRenderersEvent.RegisterRenderers) {
        event.registerEntityRenderer(HAPPY_BLOCK_ENTITY.get()) { pContext: EntityRendererProvider.Context ->
            PrimedHappyBlockRenderer(pContext) }
        event.registerEntityRenderer(BREAD_BULLET_ENTITY.get()) { pContext: EntityRendererProvider.Context ->
            BreadBulletEntityRenderer(pContext) }
    }

    @SubscribeEvent
    fun registerGuiOverlays(event: RegisterGuiOverlaysEvent) {
        event.registerBelow(VanillaGuiOverlay.DEBUG_TEXT.id(), "tool_gun_overlay", ToolGunOverlay())
    }

    @SubscribeEvent
    fun registerCustomModels(event: RegisterAdditional) { // ModelEvent
        event.register(modLocation( "${ModelProvider.ITEM_FOLDER}/$TOOL_GUN_DEF/item"))
        event.register(modLocation("${ModelProvider.ITEM_FOLDER}/$TOOL_GUN_DEF/coil"))
        event.register(modLocation("${ModelProvider.BLOCK_FOLDER}/generator_on"))
        event.register(modLocation("${ModelProvider.ITEM_FOLDER}/textureplane/textureplane_test"))
        event.register(modLocation("${ModelProvider.BLOCK_FOLDER}/toaster/handle"))
        event.register(modLocation("${ModelProvider.BLOCK_FOLDER}/creative_generator/creative_generator_star"))
        event.register(modLocation("${ModelProvider.BLOCK_FOLDER}/creative_generator"))

        event.register(modLocation("${ModelProvider.BLOCK_FOLDER}/sphere"))
    }

    @SubscribeEvent
    fun registerBlockEntityRenderers(event: EntityRenderersEvent.RegisterRenderers) {
        event.registerBlockEntityRenderer(ModBlockEntities.HEATING_ELEMENT.get()) { BlackbodyRenderer() }
        event.registerBlockEntityRenderer(ModBlockEntities.MONITOR.get()) { SidedScreenRenderer() }
        event.registerBlockEntityRenderer(ModBlockEntities.CREATIVE_GENERATOR.get()) { CreativeGeneratorRenderer() }
        event.registerBlockEntityRenderer(ModBlockEntities.ENERGY_STORAGE.get()) { EnergyBlockRenderer() }
    }

    val toolGunBindList = mutableMapOf<Control, KeyMapping?>()
    @SubscribeEvent
    fun registerBindings(event: RegisterKeyMappingsEvent) {
        event.register(changeMode)
    }

    fun createMappingsForControls(): List<KeyMapping> {
        toolGunBindList.filter { it.value == null }.keys.forEach {
            val mapping = if(it.modifier != null) {
                KeyMapping(
                    it.nameKey,
                    KeyConflictContext.IN_GAME,
                    it.modifier,
                    it.key(),
                    it.categoryKey
                )
            } else {
                KeyMapping(
                    it.nameKey,
                    KeyConflictContext.IN_GAME,
                    it.key(),
                    it.categoryKey
                )
            }
            toolGunBindList[it] = mapping
        }
        return toolGunBindList.values.mapNotNull { it }
    }

    @SubscribeEvent
    fun registerShaders(event: RegisterShadersEvent) {
        event.registerShader(
            ShaderInstance(
                event.resourceProvider,
                modLocation("bloom"),
                DefaultVertexFormat.BLOCK
            )
        ) { shader ->
            ModRenderTypes.bloomShader = shader
        }
    }

    /* https://gist.github.com/gigaherz/b8756ff463541f07a644ef8f14cb10f5 */
    @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS", "INACCESSIBLE_TYPE")
    object ModRenderTypes: RenderType(
        null, null, null,
        0, false, false,
        null, null
    ) {
        lateinit var bloomShader: ShaderInstance
        private val BLOOM_SHADER_STATE = ShaderStateShard { bloomShader }

        val BLOOM: Function<ResourceLocation, RenderType> = Util.memoize(::bloom)

        private fun bloom(location: ResourceLocation): RenderType {
            val cs = CompositeState.builder()
                .setShaderState(BLOOM_SHADER_STATE)
                .setTextureState(TextureStateShard(location, true, false))
                .setTransparencyState(NO_TRANSPARENCY)
                .setOverlayState(NO_OVERLAY)
                .createCompositeState(false)

            return create(
                "bloom",
                DefaultVertexFormat.BLOCK, VertexFormat.Mode.QUADS, 256,
                true, false, cs
            )
        }
    }
}