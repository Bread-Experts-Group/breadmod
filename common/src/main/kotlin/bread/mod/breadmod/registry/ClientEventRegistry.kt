package bread.mod.breadmod.registry

import bread.mod.breadmod.client.gui.ToolGunOverlay
import bread.mod.breadmod.client.gui.WarOverlay
import bread.mod.breadmod.client.model.ChefHatModel
import bread.mod.breadmod.client.render.block.ToasterRenderer
import bread.mod.breadmod.client.render.entity.FakePlayerRenderer
import bread.mod.breadmod.client.render.entity.PrimedHappyBlockRenderer
import bread.mod.breadmod.client.render.item.ToolGunItemRenderer
import bread.mod.breadmod.client.sound.MachSoundInstance
import bread.mod.breadmod.command.client.AltToolGunModelCommand
import bread.mod.breadmod.event.BlockEntityWithoutLevelRendererEvent
import bread.mod.breadmod.item.armor.ChefHatItem
import bread.mod.breadmod.item.toolGun.ToolGunAnimationHandler
import bread.mod.breadmod.item.toolGun.ToolGunItem
import bread.mod.breadmod.registry.block.ModBlockEntityTypes
import bread.mod.breadmod.registry.entity.ModEntityTypes
import bread.mod.breadmod.registry.sound.ModSounds
import bread.mod.breadmod.util.render.machTrailMap
import bread.mod.breadmod.util.render.renderMachTrail
import bread.mod.breadmod.util.render.rgMinecraft
import com.mojang.blaze3d.platform.InputConstants
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import dev.architectury.event.EventResult
import dev.architectury.event.events.client.ClientCommandRegistrationEvent
import dev.architectury.event.events.client.ClientCommandRegistrationEvent.ClientCommandSourceStack
import dev.architectury.event.events.client.ClientGuiEvent
import dev.architectury.event.events.client.ClientRawInputEvent
import dev.architectury.event.events.client.ClientTickEvent
import dev.architectury.event.events.common.LifecycleEvent
import dev.architectury.registry.client.level.entity.EntityModelLayerRegistry
import dev.architectury.registry.client.level.entity.EntityRendererRegistry
import dev.architectury.registry.client.rendering.BlockEntityRendererRegistry
import net.minecraft.world.entity.EquipmentSlot

internal object ClientEventRegistry {
    // todo fix commands being broken on fabric (alternative: separate command registration for both platforms)
    fun registerClientCommands() {
        ClientCommandRegistrationEvent.EVENT.register { dispatcher, sourceStack ->
            dispatcher.register(
                LiteralArgumentBuilder.literal<ClientCommandSourceStack>("breadmod")
                    .then(AltToolGunModelCommand.register())
            )
        }
    }

    fun registerEntityRenderers() {
        EntityRendererRegistry.register(ModEntityTypes.HAPPY_BLOCK_ENTITY) { context -> PrimedHappyBlockRenderer(context) }
        EntityRendererRegistry.register(ModEntityTypes.FAKE_PLAYER) { context -> FakePlayerRenderer(context) }
    }

    fun registerBlockEntityRenderers() {
        LifecycleEvent.SETUP.register {
            BlockEntityRendererRegistry.register(ModBlockEntityTypes.TOASTER.get()) { context -> ToasterRenderer(context) }
        }
    }

    fun registerEntityLayers() {
        EntityModelLayerRegistry.register(ChefHatModel.HAT_LAYER) { ChefHatModel.createLayerDefinition() }
    }

    private val toolGunItemRenderer: ToolGunItemRenderer by lazy { ToolGunItemRenderer() }

    fun renderBEWLRs() {
        BlockEntityWithoutLevelRendererEvent.RENDER_ITEMS_EVENT.register { stack, displayContext, poseStack, bufferSource, packedLight, packedOverlay ->
            when (stack.item) {
                is ToolGunItem -> toolGunItemRenderer.renderByItem(
                    stack,
                    displayContext,
                    poseStack,
                    bufferSource,
                    packedLight,
                    packedOverlay
                )
            }
        }
    }

    private var sprintTimer = 0
    private val machOneSound: MachSoundInstance by lazy {
        MachSoundInstance(ModSounds.MACH_ONE.get(), 1..20, null)
    }
    private val machTwoSound: MachSoundInstance by lazy {
        MachSoundInstance(ModSounds.MACH_TWO.get(), 21..40, null)
    }
    private val machThreeSound: MachSoundInstance by lazy {
        MachSoundInstance(ModSounds.MACH_THREE.get(), 40..70, null)
    }
    private val machFourSound: MachSoundInstance by lazy {
        MachSoundInstance(ModSounds.MACH_FOUR.get(), 70..Int.MAX_VALUE, null)
    }

    fun registerMachTrailTicker() {
        ClientTickEvent.CLIENT_PRE.register { minecraft ->
            val player = minecraft.player ?: return@register
            val headSlot = player.getItemBySlot(EquipmentSlot.HEAD)
            val item = headSlot.item
            val soundManager = rgMinecraft.soundManager
            if (item is ChefHatItem && player.isSprinting) {
                machOneSound.timer = sprintTimer
                machTwoSound.timer = sprintTimer
                machThreeSound.timer = sprintTimer
                machFourSound.timer = sprintTimer

                when (sprintTimer) {
                    1 -> soundManager.play(machOneSound)
                    20 -> soundManager.play(machTwoSound)
                    41 -> soundManager.play(machThreeSound)
                    70 -> {
                        machFourSound.shouldLoop = true
                        soundManager.play(machFourSound)
                    }
                }

                sprintTimer++
                if (sprintTimer >= 20) {
                    renderMachTrail(player.gameProfile)
                }
            } else if (!player.isSprinting || item !is ChefHatItem) {
                sprintTimer = 0
                machFourSound.shouldLoop = false
                soundManager.stop(machOneSound)
                soundManager.stop(machTwoSound)
                soundManager.stop(machThreeSound)
                soundManager.stop(machFourSound)
            }
        }

        ClientTickEvent.CLIENT_PRE.register {
            if (machTrailMap.isNotEmpty()) {
                machTrailMap.forEach { (_, machTrailData) ->
                    machTrailData.tick()
                    if (!machTrailData.player.isSprinting) {
                        machTrailData.machFourSound.shouldLoop = false
                        rgMinecraft.soundManager.stop(machTrailData.machFourSound)
                        machTrailMap.remove(machTrailData.playerProfile)
                    }
                }
            }
        }
    }

// --Commented out by Inspection START (9/10/2024 03:50):
//    fun registerKeyEvent() {
//        ClientRawInputEvent.KEY_PRESSED.register { client, keyCode, scanCode, action, modifiers ->
//            println("${client.player}, ${InputConstants.getKey(keyCode, scanCode)}")
//
//            return@register EventResult.pass()
//        }
//    }
// --Commented out by Inspection STOP (9/10/2024 03:50)

    fun registerMouseEvent() {
        ClientRawInputEvent.MOUSE_CLICKED_POST.register { client, button, action, mods ->
            // todo uhh.. tool gun functionality yes
            if (button == InputConstants.MOUSE_BUTTON_RIGHT && action == InputConstants.PRESS) {
                ToolGunAnimationHandler.trigger()
            }

            return@register EventResult.pass()
        }
    }

    private val warOverlay = WarOverlay()
    private val toolGunOverlay = ToolGunOverlay()

    fun registerOverlays() = ClientGuiEvent.RENDER_HUD.register { guiGraphics, deltaTracker ->
        warOverlay.render(guiGraphics, deltaTracker)
        toolGunOverlay.render(guiGraphics, deltaTracker)
    }
}