package breadmod.client.screen.tool_gun

import breadmod.ModMain.modLocation
import breadmod.ModMain.modTranslatable
import breadmod.client.gui.components.ScaledAbstractButton
import breadmod.client.gui.components.ScaledAbstractWidget
import breadmod.datagen.tool_gun.BreadModToolGunModeProvider.Companion.TOOL_GUN_DEF
import breadmod.item.tool_gun.mode.creator.*
import breadmod.menu.item.ToolGunCreatorMenu
import breadmod.network.PacketHandler
import breadmod.network.tool_gun.ToolGunCreatorDataPacket
import breadmod.util.render.scaleFlat
import com.google.gson.JsonObject
import com.mojang.blaze3d.systems.RenderSystem
import moze_intel.projecte.gameObjs.registries.PEItems
import net.minecraft.ChatFormatting
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.*
import net.minecraft.client.gui.components.Button.CreateNarration
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

/** A horrifying amalgamation of many inner classes and one gui (will have proper docs soon... maybe) */
class ToolGunCreatorScreen(
    pMenu: ToolGunCreatorMenu,
    pPlayerInventory: Inventory,
    pTitle: Component
) : AbstractContainerScreen<ToolGunCreatorMenu>(pMenu, pPlayerInventory, pTitle) {

    companion object {
        private val TEXTURE = modLocation("textures", "gui", "item", TOOL_GUN_DEF, "creator_mode.png")
        private val TEXTURE_ASSETS = modLocation("textures", "gui", "item", TOOL_GUN_DEF, "creator_mode_assets.png")
        private val ICONS = ResourceLocation("minecraft", "textures/gui/icons.png")

        private var customEntityName: String = ""
        private var entityString: String = "zombie"
        private var entityType: EntityType<*>? = getEntityFromString(entityString)

        private var entityHealth: Double = 20.0
        private var entitySpeed: Double = 5.0

        // First Int: Duration, Second Int: Amplifier
        private var entityEffects: MutableMap<String, Triple<MobEffect, Int, Int>> = mutableMapOf(
            "jump" to Triple(MobEffects.JUMP, 500, 2)
        )

        private var helmetSlot: ItemStack = Items.DIAMOND_HELMET.defaultInstance
        private var chestplateSlot: ItemStack = Items.DIAMOND_CHESTPLATE.defaultInstance
        private var leggingsSlot: ItemStack = Items.DIAMOND_LEGGINGS.defaultInstance
        private var bootsSlot: ItemStack = Items.DIAMOND_BOOTS.defaultInstance

        private var mainHandSlot: ItemStack = PEItems.RED_MATTER_AXE.get().defaultInstance
        private var offHandSlot: ItemStack = ItemStack.EMPTY

        /** holder for mob effects */
        val mobEffectInstanceMap: MutableMap<String, Triple<MobEffect, Int, Int>> = mutableMapOf()

        /** holder for adding mob effect widgets */
        private var mobEffectString: String = ""

        /** Holder map to store an [AbstractWidget] linked to [SignType.MAIN] or [SignType.POTION]
         *  with a unique id
         * */
        private val widgetMap: MutableMap<Pair<String, Enum<SignType>>, AbstractWidget> = mutableMapOf()

        /** json data bridge for the creator mode renderer */
        var finalData: String = ""

        private var activeEditBox: EditBox? = null
        private var currentTab: Enum<SignType> = SignType.MAIN
        private var entityScale = 32
    }

    private val instance: Minecraft = Minecraft.getInstance()
    enum class SignType { MAIN, POTION, ADD, SUBTRACT }
    private val entityX = 35
    private val entityY = 94

    init {
        imageWidth = 256
        imageHeight = 220

        // set the entity type on gui init
        entityType = getEntityFromString(entityString)
        entityScale = 32
    }

    private var staticAlpha = 1.0f
    // todo set this up for a fading static texture
    private fun alphaTick() = if (staticAlpha > 0f) staticAlpha -= 0.01f else staticAlpha = 1f

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
        entityEffects.forEach { (_, value) ->
            finalEntity.addEffect(MobEffectInstance(value.first, value.second, value.third))
        }

        if(customEntityName != "") { finalEntity.customName = Component.literal(customEntityName) }

        return finalEntity
    }

    private fun matchEntityTypeToString(): Boolean {
        val level = instance.level ?: return false
        val entity = constructEntity(level)
        val formattedName = entity.name.string.lowercase().replace(' ', '_')
//        return entity.name.string == entityString
        return formattedName == entityString
    }

    /**
     * [pX] and [pY] start at the top left of the gui, [pColor] defaults to White
     */
    private fun GuiGraphics.drawText(pText: String, pX: Int, pY: Int, pColor: Int = Color.WHITE.rgb, pDropShadow: Boolean = false) =
        drawString(font, Component.literal(pText), pX,  pY, pColor, pDropShadow)
    private fun GuiGraphics.drawText(pText: Component, pX: Int, pY: Int, pColor: Int = Color.WHITE.rgb, pDropShadow: Boolean = false) =
        drawString(font, pText, pX, pY, pColor, pDropShadow)

    // todo revamp for save/load system
    private fun setPath(path: String) = FMLPaths.GAMEDIR.get().resolve(path).toAbsolutePath()
    private fun createPathAndFile() {
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
        val formattedName = entity.name.copy().string.lowercase().replace(' ', '_')

        // Setup gui rendering
        RenderSystem.setShader { GameRenderer.getRendertypeTranslucentShader() }
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, staticAlpha)
        RenderSystem.setShaderTexture(0, TEXTURE)

        if(currentTab == SignType.MAIN) {
            pGuiGraphics.blit(TEXTURE_ASSETS, leftPos + 14, topPos + 24, 0, 0, 42, 75)
            InventoryScreen.renderEntityInInventoryFollowsMouse(
                pGuiGraphics, leftPos + entityX, topPos + entityY, entityScale,
                (leftPos + entityX) - pMouseX.toFloat(),
                (topPos + entityY - 50) - pMouseY.toFloat(),
                entity
            )
            pGuiGraphics.pose().translate(0.0, 0.0, 130.0)
            pGuiGraphics.drawCenteredString(
                font,
                if(matchEntityTypeToString()) Component.translatable(entity.name.copy().string)
                    .withStyle(if(matchEntityTypeToString()) ChatFormatting.GOLD else ChatFormatting.RED) else
                    modTranslatable(TOOL_GUN_DEF,"creator", "invalid_entity",
                        args = listOf(entityString, formattedName)
                    ).withStyle(if(matchEntityTypeToString()) ChatFormatting.GOLD else ChatFormatting.RED),
                leftPos + 35, topPos + 14,
                Color.WHITE.rgb
            )
        } else if(currentTab == SignType.POTION) {
            // todo Gui.java@448 (code that will be immensely useful in rendering mob effect icons)
        }

        pGuiGraphics.blit(TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight)
    }

    override fun renderLabels(pGuiGraphics: GuiGraphics, pMouseX: Int, pMouseY: Int) {
        pGuiGraphics.drawText(title, 2, 2)
        pGuiGraphics.drawText(playerInventoryTitle, 2, 132)
        pGuiGraphics.drawText(modTranslatable(TOOL_GUN_DEF, "creator", "save_load"), 167, 132)
    }

    // todo this needs to be changed to allow for dynamic potion effect edit boxes.
    // todo create confirmation button to apply value to whatever is in the responder parameter
    private fun createEditBox(
        pX: Int,
        pY: Int,
        pWidth: Int,
        pHeight: Int,
        pair: Pair<String, Enum<SignType>>,
        defaultValue: String,
        responder: Consumer<String>
    ): EditBox {
        val editBox = CustomEditBox(pX, pY, pWidth, pHeight)
        editBox.setCanLoseFocus(true)
        editBox.setMaxLength(50)
        editBox.value = if(defaultValue != "") defaultValue else ""
        editBox.setResponder(responder)
        addToWidgetMap(pair, editBox)
        return editBox
    }

    private fun threeStageValueButtons(
        pair: Pair<String, Enum<SignType>>,
        pX: Int, pY: Int,
        mathType: Enum<SignType>
    ) {
        when(mathType) {
            SignType.ADD -> {
                addToWidgetMap("${pair.first}_plus_one" to pair.second,
                    ValueModifierButton(pX, pY, 10, 10, 0.8, "+", pair.first, 1.0))
                addToWidgetMap("${pair.first}_plus_ten" to pair.second,
                    ValueModifierButton(pX + 8, pY, 16, 10, 0.8, "++", pair.first, 10.0))
                addToWidgetMap("${pair.first}_plus_hundred" to pair.second,
                    ValueModifierButton(pX + 2, pY + 8, 21, 10, 0.8, "+++", pair.first, 100.0))
            }
            SignType.SUBTRACT -> {
                addToWidgetMap("${pair.first}_minus_one" to pair.second,
                    ValueModifierButton(pX, pY, 10, 10, 0.8, "-", pair.first, -1.0))
                addToWidgetMap("${pair.first}_minus_ten" to pair.second,
                    ValueModifierButton(pX + 8, pY, 16, 10, 0.8, "--", pair.first, -10.0))
                addToWidgetMap("${pair.first}_minus_hundred" to pair.second,
                    ValueModifierButton(pX + 2, pY + 8, 21, 10, 0.8, "---", pair.first, -100.0))
            }
        }
    }

    private fun addToWidgetMap(pair: Pair<String, Enum<SignType>>, widget: AbstractWidget) {
        widgetMap[pair] = widget
    }
    private fun removeFromWidgetMap(pair: Pair<String, Enum<SignType>>) {
        widgetMap.remove(pair).also {
            if (it != null) {
                removeWidget(it)
                it.visible = false
            }
        }
    }

    /** Only called during gui init and adding potion effects.
     * Existing widgets are simply overridden when this function is called again. */
    private fun initWidgetMap() {
        println("initializing widgetMap")
        // make sure to flush duplicate renderable entries in the list after adding potion effect widgets
        clearWidgets()
        widgetMap.forEach { (key, value) ->
            if(value is MobEffectGuiElement || value is ValueModifierButtonsWithGui) {
                addRenderableOnly(value)
            } else addRenderableWidget(value)
            if(currentTab == SignType.POTION && key.second != SignType.POTION) {
                value.visible = false
            } else if(currentTab == SignType.MAIN && key.second != SignType.MAIN) {
                value.visible = false
            }
//            println("key: $key, value: $value")
        }
    }

    override fun init() {
        super.init()
        // Main tab //

        createEditBox(
            leftPos + 100, topPos + 100, 100, 15,
            "mob_selector" to SignType.MAIN, entityString
        ) { string ->
            entityString = string.lowercase().replace(' ', '_')
            entityType = getEntityFromString(entityString)
            matchEntityTypeToString()
//            println(entityString)
        }

        addToWidgetMap("potion_tab_button" to SignType.MAIN,
            GenericButton(leftPos + 175, topPos, 80, 10, Component.literal("Potion Effects")) {
                currentTab = SignType.POTION
                widgetMap.forEach { (key, value) ->
                    value.visible = currentTab == SignType.POTION && key.second == SignType.POTION
                }
                println(currentTab)
            })

        addToWidgetMap("funny_button" to SignType.MAIN,
            FunnyButton(leftPos + 200, topPos + 80, 20, 20,
                0, 0, 20, 20,
                modLocation("textures", "block", "fish.gif")))

        // Fire entity to server-side
        addToWidgetMap("json_button" to SignType.MAIN,
            JsonButton(leftPos + 169, topPos + 205, 82, 10,
                Component.literal("send to server"))
        )


        // Entity Scale
        threeStageValueButtons("scale" to SignType.MAIN, leftPos + 5, topPos + 100, SignType.ADD)
        threeStageValueButtons("scale" to SignType.MAIN, leftPos + 38, topPos + 100, SignType.SUBTRACT)

        // Health modifier
        ValueModifierButtonsWithGui("health" to SignType.MAIN, leftPos + 72, topPos + 12, 1.0)

        // Potion Effects Tab //

        // Main tab button
        addToWidgetMap("main_tab_button" to SignType.POTION,
            GenericButton(leftPos + 130, topPos, 30, 10, Component.literal("Main")) {
                currentTab = SignType.MAIN
                widgetMap.forEach { (key, value) ->
                    value.visible = currentTab == SignType.MAIN && key.second == SignType.MAIN
                }
//                println(currentTab)
            })

        // Add potion effect widget from edit box
//        addToWidgetMap("add_mob_effect" to SignType.POTION,
//            GenericButton(leftPos + 150, topPos + 20, 80, 10, Component.literal("add effect")) {
//                if(mobEffectFromString(id = mobEffectString) != null) {
//                    println(mobEffectFromString(id = mobEffectString))
//                    MobEffectGuiElement(mobEffectString to SignType.POTION, leftPos + 20, topPos + 30)
//                } else {
//                    println("mob effect is null!")
//                }
//                mobEffectString = ""
//            })

        addToWidgetMap("add_mob_effect" to SignType.POTION,
            GenericButton(leftPos + 150, topPos + 40, 80, 10, Component.literal("add effect")) {
                if(mobEffectFromString(id = mobEffectString) != null) {
                    println(mobEffectFromString(id = mobEffectString))
                    MobEffectGuiElement(mobEffectString to SignType.POTION, leftPos + 20, topPos + 30)
                } else {
                    println("mob effect is null!")
                }
                mobEffectString = ""
            })

        // Potion effect adder box
        createEditBox(
            leftPos + 50, topPos + 10, 100, 15,
            "mob_effect_edit_box" to SignType.POTION, ""
        ) { string ->
            mobEffectString = string.lowercase().replace(' ', '_')
//            println(potionEffectString)
        }

//        widgetMap["health" to SignType.MAIN] =
//            ValueModifierButton.ValueModifierButtonsWithGui("health" to SignType.MAIN, 20, 10)

//        widgetMap["test" to SignType.POTION] = PotionEffectGuiElement(50, 50)

//        renderableMap.forEach { (key, value) ->
//            addRenderableOnly(value)
//            if(key.second == SignType.POTION) value.visible = false
//        }

//        PotionEffectGuiElement("potion_test" to SignType.POTION, leftPos + 10, topPos + 30)

        initWidgetMap()
    }

    override fun keyPressed(pKeyCode: Int, pScanCode: Int, pModifiers: Int): Boolean {
        val box = activeEditBox ?: return super.keyPressed(pKeyCode, pScanCode, pModifiers)
        if (pKeyCode == GLFW.GLFW_KEY_ESCAPE && shouldCloseOnEsc()) {
            instance.player?.closeContainer()
        }

        return !box.keyPressed(pKeyCode, pScanCode, pModifiers) &&
                if(!box.canConsumeInput()) super.keyPressed(pKeyCode, pScanCode, pModifiers) else true
    }

    // todo could be turned into a gui dragging system? (strong maybe)
    override fun mouseDragged(pMouseX: Double, pMouseY: Double, pButton: Int, pDragX: Double, pDragY: Double): Boolean {
//        println("mouse dragging: X:$pMouseX, y:$pMouseY")
        return super.mouseDragged(pMouseX, pMouseY, pButton, pDragX, pDragY)
    }

    override fun shouldCloseOnEsc(): Boolean {
        val box = activeEditBox ?: return super.shouldCloseOnEsc()
        return !box.canConsumeInput()
    }

    override fun containerTick() {
        super.containerTick()
        widgetMap.forEach { (_, value) ->
            if(value is EditBox) value.tick()
        }
    }

    override fun onClose() {
//        widgetMap.clear()
        activeEditBox = null
        super.onClose()
    }

    // Custom widget classes past here

    // todo custom texture and scale
    inner class CustomEditBox(
        pX: Int, pY: Int, pWidth: Int, pHeight: Int
    ): EditBox(instance.font, pX, pY, pWidth, pHeight, Component.literal("A TEXT BOX")) {

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

    // todo replace with direct call to ImageButton
    // todo custom button scale and textures
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

    // todo convert to be scalable using ScaledAbstractButton or ScaledButton (wip)
    inner class GenericButton(
        pX: Int,
        pY: Int,
        pWidth: Int,
        pHeight: Int,
        pText: Component,
        onPress: OnPress
    ): Button(pX, pY, pWidth, pHeight, pText, onPress, CreateNarration { it.get() })

    // todo add parameters for automatically adding resize widgets and adding onto a future potion effects map
    // todo rebuilding potion widgets from entityEffects after removing another potion widget

    // todo automatically adding valid mob effect to mobEffectInstanceMap with mob effect name as id
    // todo setting text color to mob effect color
    // todo render calls for rendering the mob effect and bg of that effect instance (Gui.java@448)
    // todo automatically adding created effect instance into entityEffect with a system in place for not allowing more than one instance of a potion effect
    // todo show null error if entered mob effect doesn't exist
    // todo add and remove buttons for deleting the mob effect instance (along with removing that instance from entityEffect)
    // todo convert to using scaling

    inner class MobEffectGuiElement(
        private val pair: Pair<String, Enum<SignType>>,
        private val pX: Int,
        private val pY: Int
    ): AbstractWidget(pX, pY, 300, 50, Component.empty()) {
        override fun updateWidgetNarration(pNarrationElementOutput: NarrationElementOutput) {}
        override fun renderWidget(pGuiGraphics: GuiGraphics, pMouseX: Int, pMouseY: Int, pPartialTick: Float) {
            if(visible) {
                pGuiGraphics.fill(RenderType.endPortal(), pX, pY, pX + 100, pY + 50, Color.RED.rgb)
                pGuiGraphics.drawText(pair.first, pX + 30, pY + 5, Color.RED.rgb)
            }
        }

        // todo work on logic to remove this and any child widget matching pair.first
        private fun removeThisWidget(pair: Pair<String, Enum<SignType>>) {
//            widgetMap.forEach { (key, value) -> // todo throws ConcurrentModificationException
//              if(pair.first in key.first) {
//                    println("key: $key, value: $value")
//                    widgetMap.remove(pair.first to pair.second)
//                    removeWidget(value)
//                }
//            }
            removeFromWidgetMap(pair)
            removeFromWidgetMap("${pair.first}_plus_one" to pair.second)
            removeFromWidgetMap("${pair.first}_plus_ten" to pair.second)
            removeFromWidgetMap("${pair.first}_plus_hundred" to pair.second)
            removeFromWidgetMap("${pair.first}_minus_one" to pair.second)
            removeFromWidgetMap("${pair.first}_minus_ten" to pair.second)
            removeFromWidgetMap("${pair.first}_minus_hundred" to pair.second)
            removeFromWidgetMap("${pair.first}_remove" to pair.second)
        }

        init {
            addToWidgetMap(pair, this)
            threeStageValueButtons(pair, pX, pY, SignType.ADD)
            threeStageValueButtons(pair, pX + 100, pY, SignType.SUBTRACT)
            // todo convert to ScaledButton
            addToWidgetMap("${pair.first}_remove" to pair.second,
                GenericButton(pX, pY + 30, 20, 10, Component.literal("remove")) {
                    println("removing ${pair.first}")
                    removeThisWidget(pair)
                })
            initWidgetMap()
        }
    }

    // todo custom button scale and textures
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
                entityEffects.forEach { (_, value) ->
                    effectObject.add(effectToString(value.first), JsonObject().also { currentEffect ->
                        currentEffect.addProperty("duration", value.second)
                        currentEffect.addProperty("amplifier", value.third)
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

    // todo custom button scale (DONE) and textures (TODO)
    inner class ValueModifierButton(
        pX: Int,
        pY: Int,
        pWidth: Int,
        pHeight: Int,
        pScale: Double,
        pText: String,
        private val pType: String,
        private val pValue: Double
    ): ScaledAbstractButton(pX, pY, pWidth, pHeight, pScale, Component.literal(pText)) {
        override fun onPress() {
            when(pType) {
                "health" -> entityHealth += pValue
                "speed" -> entitySpeed += pValue
                "scale" -> entityScale += pValue.toInt()
            }
        }
    }

    /** Constructs a widget with 6 add/subtract buttons with a texture to signify what value it changes.
     *  Automatically adds itself to [widgetMap]
     */
    inner class ValueModifierButtonsWithGui(
        pair: Pair<String, Enum<SignType>>,
        private val pX: Int,
        private val pY: Int,
        private val pScale: Double
    ): ScaledAbstractWidget(pX, pY, 80, 25, pScale, Component.empty()) {
        override fun renderWidget(pGuiGraphics: GuiGraphics, pMouseX: Int, pMouseY: Int, pPartialTick: Float) {
            val poseStack = pGuiGraphics.pose()
            poseStack.pushPose()
            poseStack.translate(pX.toDouble(), pY.toDouble(), 0.0)
            poseStack.scaleFlat(pScale.toFloat())
            pGuiGraphics.fill(RenderType.gui(), 0, 0, width, height, Color(26, 26, 26).rgb)
            pGuiGraphics.fill(RenderType.gui(), 1, 1, width - 1, height - 1, Color(51, 51, 51).rgb)
            poseStack.scaleFlat(pScale.toFloat() - 0.4f)
            pGuiGraphics.drawCenteredString(font, entityHealth.toString(), 66, 28, Color.WHITE.rgb)
            poseStack.scaleFlat(pScale.toFloat() + 4f)
            poseStack.translate(0.8, -0.2, 0.0)
            // todo parameters for choosing icons
            pGuiGraphics.blit(ICONS, 8, 0, 52, 0, 9, 9)
            poseStack.popPose()
            // 52, 0, 61, 9
        }

        init {
            addToWidgetMap(pair, this)
            threeStageValueButtons(pair, pX + 2, pY + 4, SignType.ADD)
            threeStageValueButtons(pair, pX + 57, pY + 4, SignType.SUBTRACT)
        }
    }
}