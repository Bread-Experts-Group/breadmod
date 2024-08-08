package breadmod.client.screen.tool_gun

import breadmod.ModMain.modLocation
import breadmod.ModMain.modTranslatable
import breadmod.datagen.tool_gun.BreadModToolGunModeProvider.Companion.TOOL_GUN_DEF
import breadmod.item.tool_gun.mode.creator.*
import breadmod.menu.item.ToolGunCreatorMenu
import breadmod.network.PacketHandler
import breadmod.network.tool_gun.ToolGunCreatorDataPacket
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.mojang.blaze3d.systems.RenderSystem
import moze_intel.projecte.gameObjs.registries.PEItems
import net.minecraft.ChatFormatting
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.AbstractButton
import net.minecraft.client.gui.components.Button
import net.minecraft.client.gui.components.EditBox
import net.minecraft.client.gui.components.ImageButton
import net.minecraft.client.gui.narration.NarrationElementOutput
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import net.minecraft.client.gui.screens.inventory.InventoryScreen
import net.minecraft.client.renderer.GameRenderer
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.effect.MobEffect
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.effect.MobEffects
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.ai.attributes.Attributes
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.level.Level
import net.minecraftforge.fml.loading.FMLPaths
import java.awt.Color
import java.nio.file.Files

@Suppress("Unused")
class ToolGunCreatorScreen(
    pMenu: ToolGunCreatorMenu,
    pPlayerInventory: Inventory,
    pTitle: Component
) : AbstractContainerScreen<ToolGunCreatorMenu>(pMenu, pPlayerInventory, pTitle) {
    companion object {
        private val TEXTURE = modLocation("textures", "gui", "item", TOOL_GUN_DEF, "creator_mode.png")
        private val TEXTURE_ASSETS = modLocation("textures", "gui", "item", TOOL_GUN_DEF, "creator_mode_assets.png")
        private val instance: Minecraft = Minecraft.getInstance()

        private var customEntityName: String? = "jim"
        private var entityString: String = "zombie"
        private var entityType: EntityType<*>? = getEntityFromString(entityString)

        private var entityHealth: Double = 20.0
        private var entitySpeed: Double = 5.0

        // First Int: Duration, Second Int: Amplifier
        private var entityEffect: MutableList<Triple<MobEffect, Int, Int>> = mutableListOf(
            Triple(MobEffects.HARM, 1000, 10),
            Triple(MobEffects.JUMP, 500, 2)
        )

        private var helmetSlot: ItemStack = Items.DIAMOND_HELMET.defaultInstance
        private var chestplateSlot: ItemStack = Items.DIAMOND_CHESTPLATE.defaultInstance
        private var leggingsSlot: ItemStack = Items.DIAMOND_LEGGINGS.defaultInstance
        private var bootsSlot: ItemStack = Items.DIAMOND_BOOTS.defaultInstance

        private var mainHandSlot: ItemStack = PEItems.RED_MATTER_AXE.get().defaultInstance
        private var offHandSlot: ItemStack = ItemStack.EMPTY

        var finalData: String = ""

        private fun setPath(path: String) = FMLPaths.GAMEDIR.get().resolve(path).toAbsolutePath()
        fun createPathAndFile() {
            if(!Files.exists(setPath("breadmod/tool_gun"))) {
                println("directory does not exist, creating")
                Files.createDirectories(setPath("breadmod/tool_gun"))
            }
            val path = setPath("breadmod/tool_gun")
            val data = finalData.encodeToByteArray()
            if(!Files.exists(setPath("breadmod/tool_gun/mob.json"))) {
                println("writing file")
                Files.write(setPath("breadmod/tool_gun/mob.json"), data)
            }

            println(path.toString())
        }
    }

    enum class Tabs { MAIN, POTION }

    private var currentTab: Enum<Tabs> = Tabs.MAIN
    private val entityX = 35
    private val entityY = 94
    private var entityScale = 32

    init {
        imageWidth = 256
        imageHeight = 220
    }

    private var alpha = 1.0f
    // todo set this up for a fading static texture
    private fun alphaTick() = if (alpha > 0f) alpha -= 0.01f else alpha = 1f

    private fun updateEntityType() = entityType == getEntityFromString(entityString)

    private fun constructEntity(pLevel: Level): LivingEntity {
        val finalEntity = entityType?.create(pLevel) as LivingEntity

        updateEntityType()

        // Health
        finalEntity.getAttribute(Attributes.MAX_HEALTH)?.baseValue = entityHealth
        finalEntity.health = entityHealth.toFloat()

        // Speed
        finalEntity.getAttribute(Attributes.MOVEMENT_SPEED)?.baseValue = entitySpeed
        finalEntity.speed = entitySpeed.toFloat()

        // Armor Slots
        finalEntity.getSlot(HELMET_SLOT).set(helmetSlot)
        finalEntity.getSlot(CHESTPLATE_SLOT).set(chestplateSlot)
        finalEntity.getSlot(LEGGINGS_SLOT).set(leggingsSlot)
        finalEntity.getSlot(BOOTS_SLOT).set(bootsSlot)

        // Item Slots
        finalEntity.getSlot(MAINHAND_SLOT).set(mainHandSlot)
        finalEntity.getSlot(OFFHAND_SLOT).set(offHandSlot)

        // Potion Effects
        entityEffect.forEach { (effect, duration, amplifier) ->
            finalEntity.addEffect(MobEffectInstance(effect, duration, amplifier))
        }

        customEntityName?.let { finalEntity.customName = Component.literal(it) }

        return finalEntity
    }

    /**
     * [pX] and [pY] start at the top left of the gui, [pColor] defaults to White
     */
    private fun GuiGraphics.drawText(pText: String, pX: Int, pY: Int, pColor: Int = Color.WHITE.rgb, pDropShadow: Boolean = false) =
        drawString(font, Component.literal(pText), pX,  pY, pColor, pDropShadow)
    private fun GuiGraphics.drawText(pText: Component, pX: Int, pY: Int, pColor: Int = Color.WHITE.rgb, pDropShadow: Boolean = false) =
        drawString(font, pText, pX, pY, pColor, pDropShadow)

    override fun render(pGuiGraphics: GuiGraphics, pMouseX: Int, pMouseY: Int, pPartialTick: Float) {
        val poseStack = pGuiGraphics.pose()
        poseStack.pushPose()
        poseStack.translate(0.0, 0.0, -130.0)
        renderBackground(pGuiGraphics)
        poseStack.translate(0.0, 0.0, 130.0)
        poseStack.popPose()
        renderTooltip(pGuiGraphics, pMouseX, pMouseY)

        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick)
    }

    // render >> renderBg
    override fun renderBg(pGuiGraphics: GuiGraphics, pPartialTick: Float, pMouseX: Int, pMouseY: Int) {
        val level = instance.level ?: return
        val entity = constructEntity(level)

        // Setup gui rendering
        RenderSystem.setShader { GameRenderer.getRendertypeTranslucentShader() }
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, alpha)
        RenderSystem.setShaderTexture(0, TEXTURE)

        if(currentTab == Tabs.MAIN) {
            pGuiGraphics.blit(TEXTURE_ASSETS, leftPos + 14, topPos + 24, 0, 0, 42, 75)
            InventoryScreen.renderEntityInInventoryFollowsMouse(
                pGuiGraphics, leftPos + entityX, topPos + entityY, entityScale,
                (leftPos + entityX) - pMouseX.toFloat(),
                (topPos + entityY - 50) - pMouseY.toFloat(),
                entity
            )
            pGuiGraphics.pose().translate(0.0, 0.0, 130.0)
            pGuiGraphics.drawString(
                font,
                Component.translatable(entity.name.copy().string)
                    .withStyle(ChatFormatting.GOLD),
                leftPos + 13, topPos + 14,
                Color.WHITE.rgb
            )
        } else if(currentTab == Tabs.POTION) {

        }


        pGuiGraphics.blit(TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight)
    }

    override fun renderLabels(pGuiGraphics: GuiGraphics, pMouseX: Int, pMouseY: Int) {
        pGuiGraphics.drawText(title, 2, 2)
        pGuiGraphics.drawText(playerInventoryTitle, 2, 132)
        pGuiGraphics.drawText(modTranslatable("tool_gun", "creator", "save_load"), 167, 132)
    }

    override fun init() {
        super.init()
        // todo figure out how to set the active state of buttons to false when changing tabs
        if(currentTab == Tabs.MAIN) {
            addRenderableWidget(AddSubtractButton(leftPos + 100, topPos + 30, 10, 10, "+", "scale"))
            addRenderableWidget(AddSubtractButton(leftPos + 100, topPos + 40, 10, 10, "-", "scale"))
            addRenderableWidget(
                JsonButton(leftPos + 120, topPos + 30, 80, 10, Component.literal("send to server"))
            )
            addRenderableWidget(
                FunnyButton(leftPos + 200, topPos + 80, 20, 20,
                    0, 0, 20, 20,
                    modLocation("textures", "block", "fish.gif"))
            )
            addRenderableWidget(MobSelector(leftPos + 100, topPos + 100, 100, 15, Component.literal("mob")))
        }

        addRenderableWidget(TabButton(leftPos + 175, topPos, 80, 10, Component.literal("Potion Effects")) {
            currentTab = Tabs.POTION
            rebuildWidgets()
            println(currentTab)
        })
        addRenderableWidget(TabButton(leftPos + 140, topPos, 30, 10, Component.literal("Main")) {
            currentTab = Tabs.MAIN
            rebuildWidgets()
            println(currentTab)
        })
    }

    inner class AddSubtractButton(
        pX: Int,
        pY: Int,
        pWidth: Int,
        pHeight: Int,
        private val pText: String,
        private val value: String
    ): AbstractButton(pX, pY, pWidth, pHeight, Component.literal(pText)) {
        override fun updateWidgetNarration(pNarrationElementOutput: NarrationElementOutput) {}

        override fun onPress() {
            when(pText) {
                "+" -> {
                    when (value) {
                        "health" -> entityHealth += 1.0
                        "speed" -> entitySpeed += 1.0
                        "scale" -> entityScale += 1
                    }
                }
                "++" -> {
                    when (value) {
                        "health" -> entityHealth += 10.0
                        "speed" -> entitySpeed += 10.0
                        "scale" -> entityScale += 10
                    }
                }
                "+++" -> {
                    when (value) {
                        "health" -> entityHealth += 100.0
                        "speed" -> entitySpeed += 100.0
                        "scale" -> entityScale += 100
                    }
                }
                "-" -> {
                    when (value) {
                        "health" -> entityHealth -= 1.0
                        "speed" -> entitySpeed -= 1.0
                        "scale" -> entityScale -= 1
                    }
                }
                "--" -> {
                    when (value) {
                        "health" -> entityHealth -= 10.0
                        "speed" -> entitySpeed -= 10.0
                        "scale" -> entityScale -= 10
                    }
                }
                "---" -> {
                    when (value) {
                        "health" -> entityHealth -= 100.0
                        "speed" -> entitySpeed -= 100.0
                        "scale" -> entityScale -= 100
                    }
                }
            }
        }

        override fun isFocused(): Boolean = false
    }

    inner class TabButton(
        pX: Int,
        pY: Int,
        pWidth: Int,
        pHeight: Int,
        pText: Component,
        onPress: OnPress
    ): Button(pX, pY, pWidth, pHeight, pText, onPress, CreateNarration { it.get() })

    inner class JsonButton(
        pX: Int,
        pY: Int,
        pWidth: Int,
        pHeight: Int,
        pText: Component
    ): AbstractButton(pX, pY, pWidth, pHeight, pText) {
        private val gson = Gson()

        private fun writeJson(): JsonObject = JsonObject().also {
//            it.addProperty("entity", ForgeRegistries.ENTITY_TYPES.getKey(entityType).toString())
            it.addProperty("entity", entityString)
            if(customEntityName != null) { it.addProperty("custom_entity_name", customEntityName) }
            it.addProperty("entity_health", entityHealth)
            it.addProperty("entity_speed", entitySpeed)
            it.add("effects", JsonObject().also { effectObject ->
                entityEffect.forEach { (effect, duration, amplifier) ->
                    effectObject.add(effectToString(effect), JsonObject().also { currentEffect ->
                        currentEffect.addProperty("duration", duration)
                        currentEffect.addProperty("amplifier", amplifier)
                    })
                }
            })
            it.addProperty("helmet", itemToString(helmetSlot.item))
            it.addProperty("chestplate", itemToString(chestplateSlot.item))
            it.addProperty("leggings", itemToString(leggingsSlot.item))
            it.addProperty("boots", itemToString(bootsSlot.item))
            it.addProperty("main_hand", itemToString(mainHandSlot.item))
            it.addProperty("off_hand", itemToString(offHandSlot.item))
        }

        override fun updateWidgetNarration(pNarrationElementOutput: NarrationElementOutput) {}

        override fun onPress() {
            val json = writeJson()

            println("firing string to server")
            println(json.toString())
            finalData = json.toString()
            PacketHandler.NETWORK.sendToServer(ToolGunCreatorDataPacket(json.toString()))

            // todo this stuff below needs to be converted into a save/load system for storing on the player's computer
//            val jsonByteArray = json.toString().encodeToByteArray()
//
//            val filePath: Path
//            if(!Files.exists(FMLPaths.GAMEDIR.get().resolve("breadmod/tool_gun/mob.json").toAbsolutePath())) {
//                Files.write(FMLPaths.GAMEDIR.get().resolve("breadmod/tool_gun/mob.json").toAbsolutePath(), jsonByteArray)
//                println("wrote json file to path")
//                return
//            } else filePath = FMLPaths.GAMEDIR.get().resolve("breadmod/tool_gun/mob.json").toAbsolutePath()
//
//            val jsonReader = JsonReader(FileReader(filePath.toFile()))
//            val jsonData: JsonObject = gson.fromJson(jsonReader, JsonObject::class.java)
        }
    }

    inner class FunnyButton(
        pX: Int,
        pY: Int,
        pWidth: Int,
        pHeight: Int,
        pXTexStart: Int,
        pYTexStart: Int,
        pTextureWidth: Int,
        pTextureHeight: Int,
        pResourceLocation: ResourceLocation
    ): ImageButton(
        pX, pY,
        pWidth, pHeight,
        pXTexStart, pYTexStart,
        0,
        pResourceLocation, pTextureWidth, pTextureHeight,
        { createPathAndFile() }
    )

    inner class MobSelector(
        pX: Int,
        pY: Int,
        pWidth: Int,
        pHeight: Int,
        pMessage: Component
    ): EditBox(font, pX, pY, pWidth, pHeight, pMessage) {
        override fun setValue(pText: String) {
            super.setValue(pText)
            entityString = value
        }
    }
}