package breadmod.client.screen.tool_gun.creator

import breadmod.ModMain.modLocation
import breadmod.ModMain.modTranslatable
import breadmod.datagen.tool_gun.BreadModToolGunModeProvider.Companion.TOOL_GUN_DEF
import breadmod.item.tool_gun.mode.creator.*
import breadmod.menu.item.ToolGunCreatorMenu
import breadmod.network.PacketHandler
import breadmod.network.tool_gun.ToolGunCreatorDataPacket
import com.google.gson.JsonObject
import com.mojang.blaze3d.systems.RenderSystem
import moze_intel.projecte.gameObjs.registries.PEItems
import net.minecraft.ChatFormatting
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.AbstractButton
import net.minecraft.client.gui.components.AbstractWidget
import net.minecraft.client.gui.components.Button
import net.minecraft.client.gui.components.EditBox
import net.minecraft.client.gui.components.ImageButton
import net.minecraft.client.gui.components.Renderable
import net.minecraft.client.gui.narration.NarrationElementOutput
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import net.minecraft.client.gui.screens.inventory.InventoryScreen
import net.minecraft.client.renderer.GameRenderer
import net.minecraft.client.renderer.RenderType
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
import org.lwjgl.glfw.GLFW
import java.awt.Color
import java.nio.file.Files
import java.util.function.Consumer

/** probably the most mind-numbing class I've ever written, and it's still a work in progress...
 * @author chris
 * */

class ToolGunCreatorScreen(
    pMenu: ToolGunCreatorMenu,
    pPlayerInventory: Inventory,
    pTitle: Component
) : AbstractContainerScreen<ToolGunCreatorMenu>(pMenu, pPlayerInventory, pTitle) {
    companion object {
        private val TEXTURE = modLocation("textures", "gui", "item", TOOL_GUN_DEF, "creator_mode.png")
        private val TEXTURE_ASSETS = modLocation("textures", "gui", "item", TOOL_GUN_DEF, "creator_mode_assets.png")
        private val ICONS = ResourceLocation("minecraft", "textures/gui/icons.png")

        private val instance: Minecraft = Minecraft.getInstance()

        private var customEntityName: String = ""
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

        private val potionEffectInstanceMap: Nothing = TODO("set up map for holding potion effect instances" +
                " (the mob effect, duration, amplifier, tab, and system in place for not allowing more than one instance of a potion effect")

        var finalData: String = ""

        // todo revamp for save/load system
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

    /** ## It's short for ToolGunCreatorTabs. */
    private enum class TGCTabs { MAIN, POTION }
    private var currentTab: Enum<TGCTabs> = TGCTabs.MAIN
    private val entityX = 35
    private val entityY = 94
    private var entityScale = 32

    // todo this needs to be extended to support all widgets (AbstractWidget is probably good enough), and probably a separate map for holding potion effects
    /** map to initialize widgets specific to certain tabs */
    private val widgetMap: MutableMap<Pair<String, Enum<TGCTabs>>, AbstractWidget> = mutableMapOf()
    private var activeEditBox: EditBox? = null

    private val renderableMap: MutableMap<Pair<String, Enum<TGCTabs>>, PotionEffectGuiElement> = mutableMapOf()

    init {
        imageWidth = 256
        imageHeight = 220

        // set the entity type on gui init
        entityType = getEntityFromString(entityString)
    }

    private var alpha = 1.0f
    // todo set this up for a fading static texture
    private fun alphaTick() = if (alpha > 0f) alpha -= 0.01f else alpha = 1f

    private fun constructEntity(pLevel: Level): LivingEntity {
        val finalEntity = entityType?.create(pLevel) as LivingEntity

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

        if(customEntityName != "") { finalEntity.customName = Component.literal(customEntityName) }

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

        if(currentTab == TGCTabs.MAIN) {
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
        } else if(currentTab == TGCTabs.POTION) {
            // todo Gui.java@448 (code that will be immensely useful in rendering mob effect icons)
        }

        pGuiGraphics.blit(TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight)
    }

    override fun renderLabels(pGuiGraphics: GuiGraphics, pMouseX: Int, pMouseY: Int) {
        pGuiGraphics.drawText(title, 2, 2)
        pGuiGraphics.drawText(playerInventoryTitle, 2, 132)
        pGuiGraphics.drawText(modTranslatable("tool_gun", "creator", "save_load"), 167, 132)
    }

    // todo this needs to be changed to allow for dynamic potion effect edit boxes.
    // todo create confirmation button to apply value to whatever is in the responder parameter
    private fun createEditBox(
        pX: Int,
        pY: Int,
        pWidth: Int,
        pHeight: Int,
        tab: Enum<TGCTabs>,
        name: String,
        defaultValue: String,
        responder: Consumer<String>
    ): EditBox {
        val editBox = CustomEditBox(pX, pY, pWidth, pHeight)
        editBox.setCanLoseFocus(true)
        editBox.setMaxLength(50)
        editBox.value = if(defaultValue != "") defaultValue else ""
        editBox.setResponder(responder)
        widgetMap[name to tab] = editBox
        return editBox
    }

    private inner class CustomEditBox(
        pX: Int, pY: Int, pWidth: Int, pHeight: Int
    ): EditBox(font, pX, pY, pWidth, pHeight, Component.literal("A TEXT BOX")) {
        override fun tick() {
            super.tick()
            if(this.isFocused) {
                activeEditBox = this
            } else return
        }

        override fun keyPressed(pKeyCode: Int, pScanCode: Int, pModifiers: Int): Boolean {
            if((pKeyCode == GLFW.GLFW_KEY_ENTER || pKeyCode == GLFW.GLFW_KEY_ESCAPE) && canConsumeInput()) {
                this.isFocused = false
            }
            return super.keyPressed(pKeyCode, pScanCode, pModifiers)
        }
    }

    private fun valueModifier() {
        // todo graphics for modifying values (ex. health icon + buttons with box around it)
    }

    private fun valueModifierButtons(
        pair: Pair<String, Enum<TGCTabs>>,
        pX: Int, pY: Int,
        mathType: String
        ) {
        if(mathType == "+") {
            widgetMap["${pair.first}_plus_one" to pair.second] =
                ValueModifierButton(pX, pY, 10, 10, "+", pair.first, 1.0)
            widgetMap["${pair.first}_plus_ten" to pair.second] =
                ValueModifierButton(pX + 10, pY, 16, 10, "++", pair.first, 10.0)
            widgetMap["${pair.first}_plus_hundred" to pair.second] =
                ValueModifierButton(pX + 2, pY + 10, 22, 10, "+++", pair.first, 100.0)
        } else if(mathType == "-") {
            widgetMap["${pair.first}_minus_one" to pair.second] =
                ValueModifierButton(pX, pY, 10, 10, "-", pair.first, -1.0)
            widgetMap["${pair.first}_minus_ten" to pair.second] =
                ValueModifierButton(pX + 10, pY, 16, 10, "--", pair.first, -10.0)
            widgetMap["${pair.first}_minus_hundred" to pair.second] =
                ValueModifierButton(pX + 2, pY + 10, 22, 10, "---", pair.first, -100.0)
        }
    }

    override fun init() {
        super.init()
        createEditBox(
            leftPos + 100, topPos + 100, 100, 15,
            TGCTabs.MAIN, "mob_selector", entityString
        ) { string ->
            entityString = string.lowercase().replace(' ', '_')
            entityType = getEntityFromString(entityString)
            println(entityString)
        }
        
        widgetMap["potion_tab_button" to TGCTabs.MAIN] =
            TabButton(leftPos + 175, topPos, 80, 10, Component.literal("Potion Effects")) {
                currentTab = TGCTabs.POTION
                widgetMap.forEach { (key, value) -> value.visible = key.second == TGCTabs.POTION }
                renderableMap.forEach { (key, value) -> value.visible = key.second == TGCTabs.POTION }
                println(currentTab)
            }

        widgetMap["main_tab_button" to TGCTabs.POTION] =
            TabButton(leftPos + 130, topPos, 30, 10, Component.literal("Main")) {
                currentTab = TGCTabs.MAIN
                widgetMap.forEach { (key, value) -> value.visible = key.second == TGCTabs.MAIN }
                renderableMap.forEach { (key, value) -> value.visible = key.second == TGCTabs.MAIN }
                println(currentTab)
            }

        widgetMap["funny_button" to TGCTabs.MAIN] = FunnyButton(leftPos + 200, topPos + 80, 20, 20,
            0, 0, 20, 20,
            modLocation("textures", "block", "fish.gif"))

        widgetMap["json_button" to TGCTabs.MAIN] =
            JsonButton(leftPos + 120, topPos + 30, 80, 10, Component.literal("send to server"))

        valueModifierButtons("scale" to TGCTabs.MAIN, leftPos + 5, topPos + 100, "+")
        valueModifierButtons("scale" to TGCTabs.MAIN, leftPos + 38, topPos + 100, "-")

        renderableMap["test" to TGCTabs.POTION] = PotionEffectGuiElement(50, 50)

        renderableMap.forEach { (key, value) ->
            addRenderableOnly(value)
            if(key.second == TGCTabs.POTION) value.visible = false
        }

        widgetMap.forEach { (key, value) ->
            addRenderableWidget(value)
            if(key.second == TGCTabs.POTION) value.visible = false
        }
    }

    override fun rebuildWidgets() {
        widgetMap.clear()
        super.rebuildWidgets()
    }

    override fun keyPressed(pKeyCode: Int, pScanCode: Int, pModifiers: Int): Boolean {
        val box = activeEditBox ?: return super.keyPressed(pKeyCode, pScanCode, pModifiers)
        if (pKeyCode == GLFW.GLFW_KEY_ESCAPE && shouldCloseOnEsc()) {
            instance.player?.closeContainer()
        }

        return !box.keyPressed(pKeyCode, pScanCode, pModifiers) &&
                if(!box.canConsumeInput()) super.keyPressed(pKeyCode, pScanCode, pModifiers) else true
    }

    override fun shouldCloseOnEsc(): Boolean = activeEditBox?.canConsumeInput() != true

    override fun containerTick() {
        super.containerTick()
        widgetMap.forEach { (_, value) ->
            if(value is EditBox) value.tick()
        }
    }

    inner class ValueModifierButton(
        pX: Int,
        pY: Int,
        pWidth: Int,
        pHeight: Int,
        pText: String,
        private val pType: String,
        private val pValue: Double
    ): AbstractButton(pX, pY, pWidth, pHeight, Component.literal(pText)) {
        override fun updateWidgetNarration(pNarrationElementOutput: NarrationElementOutput) {}

        override fun onPress() {
            when(pType) {
                "health" -> entityHealth += pValue
                "speed" -> entitySpeed += pValue
                "scale" -> entityScale += pValue.toInt()
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
//        private val gson = Gson()

        private fun writeJson(): JsonObject = JsonObject().also {
//            it.addProperty("entity", ForgeRegistries.ENTITY_TYPES.getKey(entityType).toString())
            it.addProperty("entity", entityString)
            if(customEntityName != "") { it.addProperty("custom_entity_name", customEntityName) }
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

    // todo replace with direct call to ImageButton
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
}