package org.bread_experts_group.breadmod.registry

import com.mojang.blaze3d.platform.InputConstants
import com.mojang.blaze3d.systems.RenderSystem
import com.mojang.blaze3d.vertex.DefaultVertexFormat
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.screens.MenuScreens
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import net.minecraft.client.model.EntityModel
import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer
import net.minecraft.client.renderer.FogRenderer
import net.minecraft.client.renderer.ShaderInstance
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider
import net.minecraft.client.renderer.entity.LivingEntityRenderer
import net.minecraft.client.renderer.item.ItemProperties
import net.minecraft.client.resources.PlayerSkin
import net.minecraft.commands.Commands
import net.minecraft.core.RegistrySetBuilder
import net.minecraft.core.registries.Registries
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.Vec3
import net.neoforged.api.distmarker.Dist
import net.neoforged.bus.api.IEventBus
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent
import net.neoforged.fml.loading.FMLEnvironment
import net.neoforged.neoforge.capabilities.BlockCapability
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent
import net.neoforged.neoforge.client.event.ClientTickEvent
import net.neoforged.neoforge.client.event.EntityRenderersEvent
import net.neoforged.neoforge.client.event.InputEvent
import net.neoforged.neoforge.client.event.InputEvent.MouseScrollingEvent
import net.neoforged.neoforge.client.event.ModelEvent
import net.neoforged.neoforge.client.event.RegisterClientCommandsEvent
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent
import net.neoforged.neoforge.client.event.RegisterItemDecorationsEvent
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent
import net.neoforged.neoforge.client.event.RegisterShadersEvent
import net.neoforged.neoforge.client.event.RenderLevelStageEvent
import net.neoforged.neoforge.client.event.RenderLevelStageEvent.Stage.AFTER_LEVEL
import net.neoforged.neoforge.client.event.ScreenEvent
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent
import net.neoforged.neoforge.client.gui.VanillaGuiLayers
import net.neoforged.neoforge.client.model.generators.ModelProvider
import net.neoforged.neoforge.common.NeoForge
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider
import net.neoforged.neoforge.data.event.GatherDataEvent
import net.neoforged.neoforge.event.RegisterCommandsEvent
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent
import net.neoforged.neoforge.event.entity.living.LivingEquipmentChangeEvent
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent.EntityInteract
import net.neoforged.neoforge.event.server.ServerStoppingEvent
import net.neoforged.neoforge.event.tick.ServerTickEvent
import net.neoforged.neoforge.network.PacketDistributor
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent
import net.neoforged.neoforge.network.registration.PayloadRegistrar
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.bread_experts_group.breadmod.BreadMod
import org.bread_experts_group.breadmod.BreadMod.Companion.modLocation
import org.bread_experts_group.breadmod.BreadMod.Companion.modModelLoc
import org.bread_experts_group.breadmod.ModDataComponents
import org.bread_experts_group.breadmod.api.IToolGunMode
import org.bread_experts_group.breadmod.api.IToolGunModeRenderer
import org.bread_experts_group.breadmod.client.gui.overlays.InternetChatRelayOverlay
import org.bread_experts_group.breadmod.client.gui.overlays.ScreenBleedOverlay
import org.bread_experts_group.breadmod.client.gui.overlays.TestOverlay
import org.bread_experts_group.breadmod.client.gui.overlays.WarOverlay
import org.bread_experts_group.breadmod.client.gui.screens.BreadModScreen
import org.bread_experts_group.breadmod.client.model.ChefHatModel
import org.bread_experts_group.breadmod.client.model.ForkliftModel
import org.bread_experts_group.breadmod.client.model.GluonGunBackpackModel
import org.bread_experts_group.breadmod.client.render.RendererWithBEWLRLerpTicker
import org.bread_experts_group.breadmod.client.render.WarRenderer
import org.bread_experts_group.breadmod.client.render.buffer.MachTrailBufferTask.machTrailMap
import org.bread_experts_group.breadmod.client.render.buffer.RenderBuffer
import org.bread_experts_group.breadmod.client.render.entity.BigItemContainerRenderer
import org.bread_experts_group.breadmod.client.render.entity.FakePlayerRenderer
import org.bread_experts_group.breadmod.client.render.entity.ForkliftRenderer
import org.bread_experts_group.breadmod.client.render.entity.PrimedHappyBlockRenderer
import org.bread_experts_group.breadmod.client.render.entity.PrimedNukeBlockRenderer
import org.bread_experts_group.breadmod.client.render.entity.layers.ChefHatArmorLayer
import org.bread_experts_group.breadmod.client.render.entity.layers.GluonGunBackpackArmorLayer
import org.bread_experts_group.breadmod.client.render.item.CreativeGeneratorItemRenderer
import org.bread_experts_group.breadmod.client.render.item.ModelBlockItemRenderer
import org.bread_experts_group.breadmod.client.render.itemColor
import org.bread_experts_group.breadmod.client.render.localClient
import org.bread_experts_group.breadmod.client.render.scaleFlat
import org.bread_experts_group.breadmod.client.sound.BreadModTickingSoundInstance
import org.bread_experts_group.breadmod.command.client.InternetRelayChatCommand
import org.bread_experts_group.breadmod.command.client.PingCommand
import org.bread_experts_group.breadmod.command.server.ScreenBleedCommand
import org.bread_experts_group.breadmod.command.server.WarTimerCommand
import org.bread_experts_group.breadmod.data_holders.common.ToolGunData
import org.bread_experts_group.breadmod.data_holders.server.ScreenBleedData.Companion.screenBleedMap
import org.bread_experts_group.breadmod.data_holders.server.WarTimerData.Companion.warTimerMap
import org.bread_experts_group.breadmod.datagen.ModRecipeProvider
import org.bread_experts_group.breadmod.datagen.damage_type.ModDamageTypeProvider
import org.bread_experts_group.breadmod.datagen.lang.BaseLanguageProvider
import org.bread_experts_group.breadmod.datagen.lang.LanguageDataGenerator
import org.bread_experts_group.breadmod.datagen.loot.ModBlockLootProvider
import org.bread_experts_group.breadmod.datagen.model.block.ModBlockStateProvider
import org.bread_experts_group.breadmod.datagen.model.item.ModItemModelProvider
import org.bread_experts_group.breadmod.datagen.sound.ModSoundDefinitionsProvider
import org.bread_experts_group.breadmod.datagen.tag.ModTagProvider
import org.bread_experts_group.breadmod.event.InventoryChangeEvent
import org.bread_experts_group.breadmod.experimental.camera_viewer.CameraTexture
import org.bread_experts_group.breadmod.experimental.mirror.MirrorRenderer
import org.bread_experts_group.breadmod.experimental.mirror.MirrorTexture
import org.bread_experts_group.breadmod.experimental.physics_grid.ClearGridPacket
import org.bread_experts_group.breadmod.experimental.physics_grid.PhysicsGrid
import org.bread_experts_group.breadmod.network.clientbound.BeamPacket
import org.bread_experts_group.breadmod.network.clientbound.DoubleOrNothingPacket
import org.bread_experts_group.breadmod.network.clientbound.GasGasGasSoundPacket
import org.bread_experts_group.breadmod.network.clientbound.MachTrailPacket
import org.bread_experts_group.breadmod.network.clientbound.ScreenBleedSetPacket
import org.bread_experts_group.breadmod.network.clientbound.SpreadParticlesPacket
import org.bread_experts_group.breadmod.network.clientbound.physics_grid.EncapsulateSoundPhysicsGridPacket
import org.bread_experts_group.breadmod.network.clientbound.physics_grid.NewPhysicsGridPacket
import org.bread_experts_group.breadmod.network.clientbound.war_timer.WarTimerIncrement
import org.bread_experts_group.breadmod.network.clientbound.war_timer.WarTimerSet
import org.bread_experts_group.breadmod.network.clientbound.war_timer.WarTimerSynchronization
import org.bread_experts_group.breadmod.network.clientbound.war_timer.WarTimerToggle
import org.bread_experts_group.breadmod.network.serverbound.CreateModelBlockItemPacket
import org.bread_experts_group.breadmod.network.serverbound.GasGasGasNukePacket
import org.bread_experts_group.breadmod.network.serverbound.HitboxPacket
import org.bread_experts_group.breadmod.network.serverbound.PlaceItemInWorldPacket
import org.bread_experts_group.breadmod.network.serverbound.ToolGunDataSyncPacket
import org.bread_experts_group.breadmod.network.serverbound.ToolGunModeChangePacket
import org.bread_experts_group.breadmod.registry.KeyMappings.openModeGui
import org.bread_experts_group.breadmod.registry.KeyMappings.placeItemKey
import org.bread_experts_group.breadmod.registry.KeyMappings.toolGunAltFour
import org.bread_experts_group.breadmod.registry.KeyMappings.toolGunAltOne
import org.bread_experts_group.breadmod.registry.KeyMappings.toolGunAltThree
import org.bread_experts_group.breadmod.registry.KeyMappings.toolGunAltTwo
import org.bread_experts_group.breadmod.registry.attachment.ModAttachments
import org.bread_experts_group.breadmod.registry.block.ModBlocks
import org.bread_experts_group.breadmod.registry.block.ModFluids
import org.bread_experts_group.breadmod.registry.block.actual.BreadLiquidBlock
import org.bread_experts_group.breadmod.registry.block.actual.BreadModBlock
import org.bread_experts_group.breadmod.registry.block.actual.entity.BreadModBlockEntity
import org.bread_experts_group.breadmod.registry.block.handler.HitboxHandler
import org.bread_experts_group.breadmod.registry.block.handler.state.EnergyStorageStateHandler
import org.bread_experts_group.breadmod.registry.entity.ModEntityDataSerializers
import org.bread_experts_group.breadmod.registry.entity.ModEntityTypes
import org.bread_experts_group.breadmod.registry.entity.ModPainting
import org.bread_experts_group.breadmod.registry.entity.actual.FakePlayer
import org.bread_experts_group.breadmod.registry.item.EquipmentSlotListener
import org.bread_experts_group.breadmod.registry.item.IEntityInteractingItem
import org.bread_experts_group.breadmod.registry.item.IKeyboardItem
import org.bread_experts_group.breadmod.registry.item.IMouseItem
import org.bread_experts_group.breadmod.registry.item.ModItems
import org.bread_experts_group.breadmod.registry.item.ModRecords
import org.bread_experts_group.breadmod.registry.item.actual.armor.GluonGunBackpackItem
import org.bread_experts_group.breadmod.registry.item.actual.armor.ModArmorMaterials
import org.bread_experts_group.breadmod.registry.menu.BreadModMenu
import org.bread_experts_group.breadmod.registry.menu.ModCreativeTabs
import org.bread_experts_group.breadmod.registry.recipe.ModRecipeSerializers
import org.bread_experts_group.breadmod.registry.recipe.ModRecipeTypes
import org.bread_experts_group.breadmod.registry.shader.ModPostChains
import org.bread_experts_group.breadmod.registry.shader.ModRenderType
import org.bread_experts_group.breadmod.registry.shader.ModVertexFormats
import org.bread_experts_group.breadmod.registry.sound.ModSounds
import org.bread_experts_group.breadmod.registry.worldgen.dimensions.ModBiomes
import org.bread_experts_group.breadmod.registry.worldgen.dimensions.ModDimensions
import org.bread_experts_group.breadmod.registry.worldgen.dimensions.ModFeatures
import org.bread_experts_group.breadmod.registry.worldgen.dimensions.ModNoiseGenerators
import org.bread_experts_group.breadmod.registry.worldgen.dimensions.structures.ModPools
import org.bread_experts_group.breadmod.registry.worldgen.dimensions.structures.ModStructureSets
import org.bread_experts_group.breadmod.registry.worldgen.dimensions.structures.ModStructures
import org.bread_experts_group.breadmod.tool_gun.ToolGunItem
import org.bread_experts_group.breadmod.tool_gun.ToolGunItem.Companion.TOOL_GUN_DEF
import org.bread_experts_group.breadmod.tool_gun.gui.ToolGunOverlay
import org.bread_experts_group.breadmod.util.Color
import org.bread_experts_group.breadmod.util.block
import org.bread_experts_group.breadmod.util.getStackInPlayerHand
import org.bread_experts_group.breadmod.util.hitbox
import org.bread_experts_group.breadmod.util.rayCast
import org.bread_experts_group.breadmod.util.reflect.LibraryScanner.Companion.getScanner
import kotlin.reflect.full.primaryConstructor

@Suppress("KDocMissingDocumentation")
object Registry {
	val toolGunModes: MutableMap<ResourceLocation, IToolGunMode> = mutableMapOf()
	val toolGunRendererCache: MutableMap<ResourceLocation, IToolGunModeRenderer> = mutableMapOf()
	val itemRenderers: MutableMap<String, BlockEntityWithoutLevelRenderer> = mutableMapOf()
	val playingSounds: MutableMap<Vec3, BreadModTickingSoundInstance> = mutableMapOf()
	val logger: Logger = LogManager.getLogger("Bread Mod Registry")
	private val registerList: Array<RegistryProvider> = arrayOf(
		ModItems,
		ModBlocks,
		ModSounds,
		ModCreativeTabs,
		ModFluids,
		ModEntityTypes,
		ModEntityDataSerializers,
		ModArmorMaterials,
		ModDataComponents,
		ModAttachments,
		ModRecipeSerializers,
		ModRecipeTypes
	)

	fun registerAll(modBus: IEventBus) {
		this.registerList.forEach {
			for (registry in it) {
				this.logger.info("Pushing registry [${registry.registryName}]")
				registry.register(modBus)
			}
		}
		// Sided Event Registration
		when (FMLEnvironment.dist) {
			Dist.CLIENT -> {
				// Game Bus
				NeoForge.EVENT_BUS.addListener { event: ScreenEvent.Render.Post ->
					ScreenBleedOverlay.renderBleed(event.guiGraphics)
				}
				NeoForge.EVENT_BUS.addListener { event: RenderLevelStageEvent ->
					WarRenderer.render(event)
					RenderBuffer.handle(event)
					val player = localClient.player ?: return@addListener
					val stack = getStackInPlayerHand(player)
					if (stack.item is ToolGunItem) {
						val data = ToolGunData.get(stack)
						data.getMode().getCustomRenderer().renderLevelStageEvent(
							event,
							localClient.renderBuffers().bufferSource(),
							player,
							data
						)
					}

					HitboxHandler.hitboxes.forEach { (_, hitbox) ->
						hitbox.render(event, localClient.renderBuffers().bufferSource())
					}

					if (MirrorRenderer.blockEntities.isNotEmpty() && event.stage == AFTER_LEVEL) {
						MirrorRenderer.blockEntities.forEach { entity ->
							val texture = MirrorRenderer.textures.getOrPut(entity.blockPos) {
								MirrorTexture(modLocation("mirror_${MirrorTexture.counter++}"))
							}

							texture.bind()
							MirrorTexture.camera.setEntity(entity.level ?: return@forEach)
							texture.setupCamera(entity)

							MirrorTexture.textureTarget.clear(true)
							MirrorTexture.textureTarget.bindWrite(true)
							CameraTexture.targetBeingRendered = MirrorTexture.textureTarget

							RenderSystem.clear(16640, Minecraft.ON_OSX)
							FogRenderer.setupNoFog()
							RenderSystem.enableCull()

							texture.renderLevel(MirrorTexture.textureTarget)
							texture.writeToFrameBuffer(MirrorTexture.textureTarget)
							CameraTexture.targetBeingRendered = null
							localClient.mainRenderTarget.bindWrite(true)
						}
					}
				}
				NeoForge.EVENT_BUS.addListener { event: MouseScrollingEvent ->
					val player = localClient.player ?: return@addListener
					val level = player.level() as ClientLevel
					val stack = player.getItemInHand(player.usedItemHand)
					val item = stack.item
					if (item is IMouseItem) item.onMouseScroll(event, stack, level, player)
				}
				NeoForge.EVENT_BUS.addListener { event: InputEvent.Key ->
					val player = localClient.player ?: return@addListener
					val level = localClient.level ?: return@addListener
					val stack = player.getItemInHand(player.usedItemHand)
					val item = stack.item
					if (item is IKeyboardItem) item.onKeyboardPress(event, stack, player)
					if (event.action == InputConstants.PRESS) {
						if (event.key == KeyMappings.placeItemKey.key.value) {
							val hitResult = localClient.hitResult as? BlockHitResult ?: return@addListener
							if (level.getBlockState(hitResult.blockPos).isAir) return@addListener
							PacketDistributor.sendToServer(
								PlaceItemInWorldPacket(
									hitResult.blockPos,
									hitResult.direction
								)
							)
						}
					}
				}
				NeoForge.EVENT_BUS.addListener { event: InputEvent.MouseButton.Pre ->
					val player = localClient.player ?: return@addListener
					val level = player.level() as ClientLevel
					val stack = getStackInPlayerHand(player)
					val item = stack.item
					if (item is IMouseItem) item.onMouseInputPre(event, stack, level, player)
				}
				NeoForge.EVENT_BUS.addListener { event: InputEvent.MouseButton.Post ->
					val player = localClient.player ?: return@addListener
					val level = player.level() as ClientLevel
					val stack = getStackInPlayerHand(player)
					val item = stack.item
					if (item is IMouseItem) item.onMouseInputPost(event, stack, level, player)

					if (event.action == InputConstants.PRESS && event.button == InputConstants.MOUSE_BUTTON_RIGHT) {
						player.rayCast(10.0, hitbox(player))?.let { result ->
							val level = player.level()
							val state = level.getBlockState(result.blockPosition)
							val entity = level.getBlockEntity(result.hit.originBlockPos) as? BreadModBlockEntity
							val blockPos = result.hit.originBlockPos
							if (entity == null) {
								HitboxHandler.hitboxes.remove(result.hit.pos)
								player.displayClientMessage(
									Component.literal("entity does not exist, removing hitbox"),
									true
								)
								return@addListener
							}
							result.hit.onHitClient(level as ClientLevel, blockPos, state, player, entity)
							result.hit.onHitCommon(level, result.hit.originBlockPos, state, player, entity)
						}
						PacketDistributor.sendToServer(HitboxPacket())
					}
				}
				NeoForge.EVENT_BUS.addListener { _: ClientTickEvent.Pre ->
					if (!localClient.isPaused || !localClient.isLocalServer) {
						if (machTrailMap.isNotEmpty()) {
							machTrailMap.forEach { (_, machTrailData) ->
								machTrailData.tick()
								if (!machTrailData.targetPlayer.isSprinting || machTrailData.targetPlayer.attackAnim > 0f) {
									machTrailData.killAllSounds()
									machTrailMap.remove(machTrailData.targetPlayer)
									return@addListener
								}
							}
						}
					}
				}
				NeoForge.EVENT_BUS.addListener { _: ClientTickEvent.Post ->
					val inventory = (localClient.player ?: return@addListener).allSlots
					inventory.forEach {
						val renderer = IClientItemExtensions.of(it).customRenderer
						if (renderer is RendererWithBEWLRLerpTicker<*>) renderer.lerpTicker.tick()
					}
					this.playingSounds.values.forEach { it.tick(localClient.player ?: return@forEach) }
				}
				NeoForge.EVENT_BUS.addListener { event: RegisterClientCommandsEvent ->
					event.dispatcher.register(
						Commands.literal(BreadMod.ID)
							.then(InternetRelayChatCommand.register())
							.then(PingCommand.register())
					)
				}
				NeoForge.EVENT_BUS.addListener { event: LivingEquipmentChangeEvent ->
					val entity = event.entity
					val toStack = event.to
					val toItem = toStack.item as? EquipmentSlotListener
					val fromStack = event.from
					val fromItem = fromStack.item as? EquipmentSlotListener
					if (toStack.item is EquipmentSlotListener)
						toItem?.let {
							it.onItemEquipped(toStack, entity, entity.level(), event.slot)
							it.onEquipmentChange(fromStack, toStack, entity, entity.level(), event.slot)
						}
					fromItem?.let {
						it.onItemUnequipped(fromStack, entity, entity.level(), event.slot)
						it.onEquipmentChange(fromStack, toStack, entity, entity.level(), event.slot)
					}
				}
				NeoForge.EVENT_BUS.addListener { event: InventoryChangeEvent ->
//					LogManager.getLogger().info(event.stack.item)
				}
				// Mod Bus
				modBus.addListener { event: FMLClientSetupEvent ->
					event.enqueueWork {
						ItemProperties.register(
							ModItems.BREAD_SHIELD.get(), modLocation("blocking")
						) { itemStack, _, livingEntity, _ ->
							if (livingEntity != null && livingEntity.isUsingItem && livingEntity.useItem == itemStack)
								1f else 0f
						}
					}
				}
				modBus.addListener { event: RegisterItemDecorationsEvent ->
					event.register(ModItems.TOOL_GUN.asItem()) { guiGraphics, _, stack, xOffset, yOffset ->
						val mode = ToolGunData.get(stack).getMode()
						val poseStack = guiGraphics.pose()
						poseStack.pushPose()
						poseStack.scaleFlat(0.4f)
						poseStack.translate(xOffset * 2.5 + 2, yOffset * 2.5 + 22, 0.0)
						mode.getCustomRenderer().getModeWidget().icon.select({
							guiGraphics.renderFakeItem(it, 0, 0)
						}, {
							poseStack.pushPose()
							poseStack.scaleFlat(16f / it.textureWidth)
							it.blit(guiGraphics, 0, 0)
							poseStack.popPose()
						})
						poseStack.popPose()
						false
					}
				}
				modBus.addListener { event: RegisterKeyMappingsEvent ->
					event.register(openModeGui)
					event.register(toolGunAltOne)
					event.register(toolGunAltTwo)
					event.register(toolGunAltThree)
					event.register(toolGunAltFour)
					event.register(placeItemKey)
				}
				modBus.addListener { event: RegisterShadersEvent ->
					event.registerShader(
						ShaderInstance(
							event.resourceProvider,
							modLocation("rainbow"),
							ModVertexFormats.RAINBOW_VERTEX_FORMAT
						)
					) { ModRenderType.RAINBOW_INSTANCE = it }
					event.registerShader(
						ShaderInstance(
							event.resourceProvider,
							modLocation("astral"),
							ModVertexFormats.ASTRAL_VERTEX_FORMAT
						)
					) { ModRenderType.ASTRAL_INSTANCE = it }
					event.registerShader(
						ShaderInstance(
							event.resourceProvider,
							modLocation("glow"),
							DefaultVertexFormat.BLOCK
						)
					) { ModRenderType.GLOW_INSTANCE = it }
					event.registerShader(
						ShaderInstance(
							event.resourceProvider,
							modLocation("translucent_tex"),
							DefaultVertexFormat.BLOCK
						)
					) { ModRenderType.TRANSLUCENT_TEX_INSTANCE = it }
					event.registerShader(
						ShaderInstance(
							event.resourceProvider,
							modLocation("sun"),
							DefaultVertexFormat.BLOCK
						)
					) { ModRenderType.SUN_INSTANCE = it }
					event.registerShader(
						ShaderInstance(
							event.resourceProvider,
							modLocation("position_tex_color_no_cutout"),
							DefaultVertexFormat.POSITION_TEX_COLOR
						)
					) { ModRenderType.POSITION_TEX_COLOR_NO_CUTOUT_INSTANCE = it }
					ModPostChains.init(event.resourceProvider)
				}
				modBus.addListener { event: RegisterClientExtensionsEvent ->
					event.registerFluidType(BreadLiquidBlock.ClientExtensions, ModFluids.BREAD_LIQUID.type.get())
					event.registerItem(ToolGunItem.ToolGunItemExtensions, ModItems.TOOL_GUN)
					event.registerItem(GluonGunBackpackItem.GluonGunExtensions(), ModItems.GLUON_GUN)
					event.registerItem(object : IClientItemExtensions {
						val renderer: String = "creative_generator"
						override fun getCustomRenderer(): BlockEntityWithoutLevelRenderer =
							this@Registry.itemRenderers.getOrPut(this.renderer, ::CreativeGeneratorItemRenderer)
					}, ModBlocks.CREATIVE_GENERATOR.asItem())
					event.registerItem(object : IClientItemExtensions {
						val renderer: String = "model_item"
						override fun getCustomRenderer(): BlockEntityWithoutLevelRenderer =
							this@Registry.itemRenderers.getOrPut(this.renderer, ::ModelBlockItemRenderer)
					}, ModBlocks.MODEL_BLOCK.asItem())
				}
				modBus.addListener { event: EntityRenderersEvent.RegisterRenderers ->
					event.registerEntityRenderer(ModEntityTypes.BIG_ITEM_CONTAINER.get(), ::BigItemContainerRenderer)
					event.registerEntityRenderer(ModEntityTypes.HAPPY_BLOCK_ENTITY.get(), ::PrimedHappyBlockRenderer)
					event.registerEntityRenderer(ModEntityTypes.NUKE_BLOCK_ENTITY.get(), ::PrimedNukeBlockRenderer)
					event.registerEntityRenderer(ModEntityTypes.FAKE_PLAYER.get(), ::FakePlayerRenderer)
					event.registerEntityRenderer(ModEntityTypes.FORKLIFT.get(), ::ForkliftRenderer)

					for (deferredBlock in ModBlocks.blockIterator()) {
						val block = deferredBlock.get()
						if (block !is BreadModBlock) continue
						val renderer = block.ofRenderer() ?: continue
						@Suppress("UNCHECKED_CAST")
						event.registerBlockEntityRenderer(
							(block.blockEntityType ?: return@addListener).get(),
							renderer as (BlockEntityRendererProvider.Context) -> BlockEntityRenderer<BlockEntity>
						)
					}
				}
				modBus.addListener { event: RegisterGuiLayersEvent ->
					event.registerAboveAll(modLocation("war_overlay"), WarOverlay())
					event.registerAbove(VanillaGuiLayers.CHAT, modLocation("irc_overlay"), InternetChatRelayOverlay())
					event.registerAboveAll(modLocation("test_overlay"), TestOverlay())
					event.registerAboveAll(modLocation("screen_bleed_overlay"), ScreenBleedOverlay())
					event.registerBelow(
						VanillaGuiLayers.DEBUG_OVERLAY,
						modLocation("tool_gun_overlay"),
						ToolGunOverlay()
					)
				}
				modBus.addListener { event: RegisterColorHandlersEvent.Item ->
					event.register(
						itemColor,
						ModItems.CHEF_HAT.get(),
						ModItems.DOPED_BREAD.get(),
						ModItems.BREAD_CHESTPLATE.get(),
						ModItems.BREAD_HELMET.get(),
						ModItems.BREAD_LEGGINGS.get(),
						ModItems.BREAD_BOOTS.get()
					)
//					event.register({ stack, _ ->
//						val blockItem = stack.item as? BlockItem ?: return@register Color.WHITE
//						val cable = blockItem.block as? CableBlock ?: return@register Color.WHITE
//						cable.resolveColor()
//					}, ModBlocks.CABLE.asItem())
				}
				modBus.addListener { event: RegisterColorHandlersEvent.Block ->
					event.register({ _, getter, pos, _ ->
						if (getter != null && pos != null) {
							val entity = getter.getBlockEntity(pos) as? BreadModBlockEntity ?: return@register Color.RED
							val state = entity.getCapability(EnergyStorageStateHandler.BLOCK_VOID)
							state.get(EnergyStorageStateHandler.COLOR)
						} else Color.RED
					}, ModBlocks.ENERGY_STORAGE.block)
//					event.register({ state, _, _, _ ->
//						(state.block as? CableBlock ?: return@register Color.WHITE).resolveColor()
//					}, ModBlocks.CABLE.asBlock())
				}
				modBus.addListener { event: ModelEvent.RegisterAdditional ->
					event.register(modModelLoc("${ModelProvider.ITEM_FOLDER}/$TOOL_GUN_DEF/item"))
					event.register(modModelLoc("${ModelProvider.ITEM_FOLDER}/$TOOL_GUN_DEF/coil"))
					event.register(modModelLoc("${ModelProvider.BLOCK_FOLDER}/generator_on"))
					event.register(modModelLoc("${ModelProvider.BLOCK_FOLDER}/toaster/handle"))
					event.register(modModelLoc("${ModelProvider.BLOCK_FOLDER}/creative_generator_star"))
					event.register(modModelLoc("${ModelProvider.BLOCK_FOLDER}/creative_generator"))
					event.register(modModelLoc("${ModelProvider.ITEM_FOLDER}/$TOOL_GUN_DEF/alt/tool_gun_alt"))
					event.register(modModelLoc("${ModelProvider.BLOCK_FOLDER}/microwave/microwave_door"))
					event.register(modModelLoc("${ModelProvider.BLOCK_FOLDER}/microwave/microwave_plate"))
					event.register(modModelLoc("${ModelProvider.BLOCK_FOLDER}/axis"))
					val dieselGeneratorPath = "${ModelProvider.BLOCK_FOLDER}/diesel_generator"
					event.register(modModelLoc("$dieselGeneratorPath/diesel_generator_door"))
					event.register(modModelLoc("$dieselGeneratorPath/charging_upgrade"))
					event.register(modModelLoc("$dieselGeneratorPath/turbo_upgrade"))
					event.register(modModelLoc("$dieselGeneratorPath/battery_upgrade"))
				}
				modBus.addListener { event: EntityRenderersEvent.AddLayers ->
					@Suppress("UNCHECKED_CAST")
					fun addHatLayer(type: EntityType<*>, event: EntityRenderersEvent.AddLayers) {
						if (event.getRenderer(type) is LivingEntityRenderer<*, *>) {
							val renderer = event.getRenderer(type)
									as LivingEntityRenderer<LivingEntity, EntityModel<LivingEntity>>
							renderer.addLayer(ChefHatArmorLayer(renderer))
						} else throw IllegalArgumentException(
							"Expected LivingEntityRenderer, got ${event.getRenderer(type)}"
						)
					}

					for (skin: PlayerSkin.Model in event.skins) {
						val entity: LivingEntityRenderer<LivingEntity, EntityModel<LivingEntity>> =
							event.getSkin(skin) ?: return@addListener
						entity.addLayer(ChefHatArmorLayer(entity))
						entity.addLayer(GluonGunBackpackArmorLayer(entity))
					}
//                    addHatLayer(EntityType.ZOMBIE, event)
					addHatLayer(EntityType.ARMOR_STAND, event)
					addHatLayer(EntityType.FOX, event)
				}
				modBus.addListener { event: EntityRenderersEvent.RegisterLayerDefinitions ->
					event.registerLayerDefinition(ChefHatModel.HAT_LAYER, ChefHatModel::createLayerDefinition)
					event.registerLayerDefinition(
						GluonGunBackpackModel.BACKPACK_LAYER,
						GluonGunBackpackModel::createLayerDefinition
					)
					event.registerLayerDefinition(ForkliftModel.FORKLIFT_LAYER, ForkliftModel::createLayerDefinition)
				}
				modBus.addListener { event: RegisterMenuScreensEvent ->
					for (deferredBlock in ModBlocks.blockIterator()) {
						val block = deferredBlock.get()
						if (block !is BreadModBlock) continue
						if (block.ofMenu() == null) continue
						@Suppress("UNCHECKED_CAST")
						event.register(
							(block.menuType ?: return@addListener).get(),
							BreadModScreenConstructor() as MenuScreens.ScreenConstructor<
									AbstractContainerMenu, AbstractContainerScreen<AbstractContainerMenu>
									>
						)
					}
				}
			}
			Dist.DEDICATED_SERVER -> {
				// Nothing for dedicated servers yet ...
			}
			else -> throw UnsupportedOperationException()
		}
		// Common Event Registration
		// Game Bus
		NeoForge.EVENT_BUS.addListener { event: ServerTickEvent.Post ->
			PhysicsGrid.serverGrids.forEach { (_, grid) -> grid.tick(event.server) }
			warTimerMap.forEach { (player, data) -> data.tick(player) }
			screenBleedMap.forEach { (player, data) -> data.tick(player) }
		}
//		NeoForge.EVENT_BUS.addListener { event: ServerAboutToStartEvent ->
//			loadToolGunModes()
//		}
		NeoForge.EVENT_BUS.addListener { event: RegisterCommandsEvent ->
			event.dispatcher.register(
				Commands.literal(BreadMod.ID)
					.then(WarTimerCommand.register())
					.then(ScreenBleedCommand.register())
//					.then(Commands.literal("clearGrids").executes { PhysicsGridGlobals.grids.clear(); 1 })
			)
		}
		NeoForge.EVENT_BUS.addListener { event: EntityInteract ->
			val item = event.itemStack.item
			if (item is IEntityInteractingItem) item.onInteractWithEntity(
				event,
				event.entity,
				event.target,
				event.level,
				event.hand,
				event.pos,
				event.itemStack
			)
		}
		NeoForge.EVENT_BUS.addListener { _: ServerStoppingEvent ->
			if (FMLEnvironment.dist.isClient) {
				CameraTexture.textures.forEach { (_, texture) -> texture.close() }
				CameraTexture.textures.clear()
			}
		}
		// Mod Bus
		modBus.addListener { event: GatherDataEvent ->
			val generator = event.generator
			val packOutput = generator.packOutput
			val existingFileHelper = event.existingFileHelper
			val scanner = ModRecipeProvider::class.java.`package`.getScanner()
			// add all the bootstrap entries to the registry set builder
			val registrySetBuilder = RegistrySetBuilder()
				.add(Registries.PAINTING_VARIANT, ModPainting::bootstrap)
				.add(Registries.TEMPLATE_POOL, ModPools::bootstrap)
				.add(Registries.STRUCTURE, ModStructures::bootstrap)
				.add(Registries.STRUCTURE_SET, ModStructureSets::bootstrap)
				.add(Registries.NOISE_SETTINGS, ModNoiseGenerators::bootstrapNoiseGenerators)
				.add(Registries.CONFIGURED_FEATURE, ModFeatures::bootstrapConfiguredFeatures)
				.add(Registries.PLACED_FEATURE, ModFeatures::bootstrapPlacedFeatures)
				.add(Registries.BIOME, ModBiomes::bootstrapBiomes)
				.add(Registries.DIMENSION_TYPE, ModDimensions::bootstrapDimensionTypes)
				.add(Registries.LEVEL_STEM, ModDimensions::bootstrapLevelStems)
				.add(Registries.JUKEBOX_SONG, ModRecords::bootstrap)
			// bootstrap all the datapack entries and create the provider
			val datapackEntriesProvider = DatapackBuiltinEntriesProvider(
				packOutput, event.lookupProvider, registrySetBuilder, setOf(BreadMod.ID)
			)
			val lookupProvider = datapackEntriesProvider.registryProvider

			if (event.includeServer()) {
				this.logger.info("Server datagen")
				// actually run the provider for the datapack entries
				generator.addProvider(true, datapackEntriesProvider)
				generator.addProvider(true, ModSoundDefinitionsProvider(packOutput, existingFileHelper))
				generator.addProvider(
					true,
					ModBlockLootProvider(lookupProvider).construct(packOutput, lookupProvider)
				)
				generator.addProvider(true, ModRecipeProvider(packOutput, lookupProvider))
				generator.addProvider(true, ModDamageTypeProvider(packOutput))
				generator.addProvider(true, ModTagProvider(packOutput))
			}
			if (event.includeClient()) {
				this.logger.info("Client datagen")
				generator.addProvider(true, ModBlockStateProvider(packOutput, existingFileHelper))
				generator.addProvider(true, ModItemModelProvider(packOutput, existingFileHelper))
				scanner.getClassesAnnotatedWith(LanguageDataGenerator::class).forEach { clazz ->
					generator.addProvider(
						true,
						(clazz.primaryConstructor ?: return@forEach).call(packOutput) as BaseLanguageProvider
					)
				}
			}
		}
		modBus.addListener { event: RegisterPayloadHandlersEvent ->
			val registrar: PayloadRegistrar = event.registrar("1.4.0")
			// Clientbound packets
			WarTimerIncrement.register(registrar)
			WarTimerSet.register(registrar)
			WarTimerSynchronization.register(registrar)
			WarTimerToggle.register(registrar)
			MachTrailPacket.register(registrar)
			BeamPacket.register(registrar)
			NewPhysicsGridPacket.register(registrar)
			EncapsulateSoundPhysicsGridPacket.register(registrar)
			SpreadParticlesPacket.register(registrar)
			ScreenBleedSetPacket.register(registrar)
			GasGasGasSoundPacket.register(registrar)
			DoubleOrNothingPacket.register(registrar)
			// Serverbound packets
			ClearGridPacket.register(registrar)
			ToolGunModeChangePacket.register(registrar)
			ToolGunDataSyncPacket.register(registrar)
			PlaceItemInWorldPacket.register(registrar)
			GasGasGasNukePacket.register(registrar)
			HitboxPacket.register(registrar)
			CreateModelBlockItemPacket.register(registrar)
		}
		modBus.addListener { event: EntityAttributeCreationEvent ->
			event.put(ModEntityTypes.FAKE_PLAYER.get(), FakePlayer.createAttributes().build())
		}
		modBus.addListener { event: RegisterCapabilitiesEvent ->
			for (deferredBlock in ModBlocks.blockIterator()) {
				val block = deferredBlock.get()
				if (block !is BreadModBlock) continue
				for ((capability, _) in block.ofCapabilities()) {
					@Suppress("UNCHECKED_CAST")
					event.registerBlockEntity(
						capability as BlockCapability<Any, Any>,
						(block.blockEntityType ?: return@addListener).get()
					) { entity, context -> (entity as BreadModBlockEntity).getCapabilityOrNull(capability, context) }
				}
			}
		}
	}

	class BreadModScreenConstructor : MenuScreens.ScreenConstructor<BreadModMenu, BreadModScreen<*>> {
		override fun create(
			menu: BreadModMenu,
			inventory: Inventory,
			title: Component
		): BreadModScreen<*> = menu.ofScreen(title)
	}
}