package breadmod.client.screen.tool_gun

import breadmod.ModMain.modLocation
import breadmod.ModMain.modTranslatable
import breadmod.client.gui.components.ScaledAbstractButton
import breadmod.client.gui.components.ScaledAbstractWidget
import breadmod.client.gui.components.TestButton
import breadmod.datagen.tool_gun.BreadModToolGunModeProvider.Companion.TOOL_GUN_DEF
import breadmod.item.tool_gun.mode.creator.ToolGunCreatorMode
import breadmod.item.tool_gun.mode.creator.ToolGunCreatorMode.Companion.constructEntity
import breadmod.item.tool_gun.mode.creator.getEntityFromString
import breadmod.item.tool_gun.mode.creator.mobEffectFromString
import breadmod.menu.item.ToolGunCreatorMenu
import breadmod.util.json
import breadmod.util.render.renderEntityInInventoryFollowsMouse
import breadmod.util.render.scaleFlat
import com.mojang.blaze3d.systems.RenderSystem
import com.mojang.blaze3d.vertex.PoseStack
import kotlinx.serialization.encodeToString
import net.minecraft.ChatFormatting
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.AbstractWidget
import net.minecraft.client.gui.components.Button
import net.minecraft.client.gui.components.Button.CreateNarration
import net.minecraft.client.gui.components.EditBox
import net.minecraft.client.gui.narration.NarrationElementOutput
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import net.minecraft.client.renderer.GameRenderer
import net.minecraft.client.renderer.RenderType
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.player.Inventory
import org.lwjgl.glfw.GLFW
import java.awt.Color
import java.io.File
import java.io.FileInputStream
import java.nio.file.Files
import java.nio.file.Path
import java.util.function.Consumer

class ToolGunCreatorScreen(
    pMenu: ToolGunCreatorMenu,
    pPlayerInventory: Inventory,
    pTitle: Component
) : AbstractContainerScreen<ToolGunCreatorMenu>(pMenu, pPlayerInventory, pTitle) {
    internal companion object {
        val TEXTURE = modLocation("textures", "gui", "item", TOOL_GUN_DEF, "creator_mode.png")
        val TEXTURE_ASSETS = modLocation("textures", "gui", "item", TOOL_GUN_DEF, "creator_mode_assets.png")
        val ICONS = ResourceLocation("minecraft", "textures/gui/icons.png")

        val DATA_FOLDER: Path = Files.createDirectories(ToolGunCreatorMode.DATA_PATH)
        val ENTITY_FILE: File = DATA_FOLDER.resolve("entity.json").toFile()

        var loadedEntity: ToolGunCreatorMode.CreatorEntity
        var lastSaved: String

        init {
            if (ENTITY_FILE.exists()) {
                FileInputStream(ENTITY_FILE).also {
                    loadedEntity = json.decodeFromString(it.readAllBytes().decodeToString())
                    it.close()
                }
            } else {
                loadedEntity = ToolGunCreatorMode.CreatorEntity(
                    null,
                    EntityType.PIG,
                    20.0, 0.25,
                    mutableMapOf(), 40.0
                )
            }
            lastSaved = json.encodeToString(loadedEntity)
        }

        // todo RecipeBookPage.java // RecipeBookComponent.java // RecipeButton.java
        var mobEffectPages = 1
        var currentMobEffectPage = 1

        /** holder for adding mob effect widgets */
        var mobEffectString: String = ""

        // First Int: Duration, Second Int: Amplifier
        /** Holder for mob effect widgets */
        val mobEffectWidgetHolder: MutableMap<String, MobEffectWidget> = mutableMapOf()

        /** Holder map to store an [AbstractWidget] linked to [Screen.MAIN] or [Screen.POTION]
         *  with a unique id
         * */
        val widgetMap: MutableMap<Pair<String, Enum<*>>, AbstractWidget> = mutableMapOf()

        var activeEditBox: EditBox? = null
        var currentTab: Enum<Screen> = Screen.MAIN
    }

    enum class SignType { ADD, SUBTRACT }
    enum class Screen { MAIN, POTION }

    private val entityX = 35
    private val entityY = 94

    init {
        imageWidth = 256
        imageHeight = 220
    }

    /**
     * [pX] and [pY] start at the top left of the gui, [pColor] defaults to White
     */
    private fun GuiGraphics.drawText(
        pText: Component,
        pX: Int,
        pY: Int,
        pColor: Int = Color.WHITE.rgb,
        pDropShadow: Boolean = false
    ) = drawString(font, pText, pX, pY, pColor, pDropShadow)

    // TODO rewrite
    private fun checkRequestedVsActual(): Boolean = true

    /**
     * Renders this [ToolGunCreatorScreen] to the screen.
     *
     */
    override fun render(pGuiGraphics: GuiGraphics, pMouseX: Int, pMouseY: Int, pPartialTick: Float) {
        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick)
        renderTooltip(pGuiGraphics, pMouseX, pMouseY)
    }

    // render >> renderBg
    override fun renderBg(pGuiGraphics: GuiGraphics, pPartialTick: Float, pMouseX: Int, pMouseY: Int) {
        val entity = constructEntity(loadedEntity, breadmod.util.render.minecraft.level ?: return)

        /** for scissor, subtract the width of your gui texture by the screen width and divide by 2 */
        val guiWidthOffset = (width - imageWidth) / 2

        /** for scissor, subtract the height of your gui texture by the screen height and divide by 2 */
        val guiHeightOffset = (height - imageHeight) / 2

        // Setup gui rendering
        RenderSystem.setShader { GameRenderer.getRendertypeTranslucentShader() }
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f)
        RenderSystem.setShaderTexture(0, TEXTURE)

        pGuiGraphics.blit(TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight)
        if (currentTab == Screen.MAIN) {
            pGuiGraphics.blit(TEXTURE_ASSETS, leftPos + 13, topPos + 23, 0, 0, 44, 77)
            pGuiGraphics.fill(RenderType.gui(), leftPos + 69, topPos + 11, leftPos + 70, topPos + 130, Color(26, 26, 26).rgb)
            pGuiGraphics.enableScissor(
                guiWidthOffset + 14, guiHeightOffset + 24,
                guiWidthOffset + 56, guiHeightOffset + 99
            )
            renderEntityInInventoryFollowsMouse(
                pGuiGraphics, leftPos + entityX, topPos + entityY, loadedEntity.scale,
                (leftPos + entityX) - pMouseX.toFloat(),
                (topPos + entityY - 50) - pMouseY.toFloat(),
                entity
            )
            pGuiGraphics.disableScissor()

            pGuiGraphics.drawCenteredString(
                font,
                entity.name.copy().withStyle(
                    if (checkRequestedVsActual()) ChatFormatting.GOLD else ChatFormatting.RED
                ),
                leftPos + 35, topPos + 14,
                Color.WHITE.rgb
            )
        } else {
            // todo potion tab assets
            pGuiGraphics.fill(RenderType.gui(), leftPos + 2, topPos + 25, leftPos + 254, topPos + 129, Color(26, 26, 26).rgb)
            pGuiGraphics.fill(RenderType.gui(), leftPos + 3, topPos + 26, leftPos + 253, topPos + 128, Color(20, 20, 20).rgb)
        }
    }

    override fun renderLabels(pGuiGraphics: GuiGraphics, pMouseX: Int, pMouseY: Int) {
        pGuiGraphics.drawText(title, 2, 2)
        pGuiGraphics.drawText(playerInventoryTitle, 2, 132)
        pGuiGraphics.drawText(modTranslatable(TOOL_GUN_DEF, "creator", "save_load"), 167, 132)
    }

    private fun createEditBox(
        pX: Int,
        pY: Int,
        pWidth: Int,
        pHeight: Int,
        defaultValue: String,
        responder: Consumer<String>
    ): EditBox {
        val editBox = CustomEditBox(pX, pY, pWidth, pHeight)
        editBox.setCanLoseFocus(true)
        editBox.setMaxLength(50)
        editBox.value = if (defaultValue != "") defaultValue else ""
        editBox.setResponder(responder)
        return editBox
    }

    private fun threeStageValueButtons(
        pair: Pair<String, Enum<*>>,
        pX: Int, pY: Int,
        mathType: SignType
    ) {
        when (mathType) {
            SignType.ADD -> {
                addToWidgetMap(
                    "${pair.first}_plus_one" to pair.second,
                    ValueModifierButton(pX, pY, 10, 10, 0.8, "+", pair.first, 1.0)
                )
                addToWidgetMap(
                    "${pair.first}_plus_ten" to pair.second,
                    ValueModifierButton(pX + 8, pY, 16, 10, 0.8, "++", pair.first, 10.0)
                )
                addToWidgetMap(
                    "${pair.first}_plus_hundred" to pair.second,
                    ValueModifierButton(pX + 2, pY + 8, 21, 10, 0.8, "+++", pair.first, 100.0)
                )
            }

            SignType.SUBTRACT -> {
                addToWidgetMap(
                    "${pair.first}_minus_one" to pair.second,
                    ValueModifierButton(pX, pY, 10, 10, 0.8, "-", pair.first, -1.0)
                )
                addToWidgetMap(
                    "${pair.first}_minus_ten" to pair.second,
                    ValueModifierButton(pX + 8, pY, 16, 10, 0.8, "--", pair.first, -10.0)
                )
                addToWidgetMap(
                    "${pair.first}_minus_hundred" to pair.second,
                    ValueModifierButton(pX + 2, pY + 8, 21, 10, 0.8, "---", pair.first, -100.0)
                )
            }
        }
    }

    private fun addToWidgetMap(pair: Pair<String, Enum<*>>, widget: AbstractWidget) {
        widgetMap[pair] = widget
    }

    /**
     * Sets widgets' visibility matching [id] (and widgets starting with [id]) in [widgetMap]
     */
    private fun setVisible(id: String, toggle: Boolean) {
        val filteredMap = widgetMap.filter { (key, _) -> key.first.startsWith(id) }
        filteredMap.forEach { (_, value) ->
            value.visible = toggle
        }
    }

    /**
     * Removes [id] and its associated children starting with [id] from [widgetMap]
     */
    private fun removeWidgetAndChildren(id: String) {
        val filteredMap = widgetMap.filter { (key, _) -> key.first.startsWith(id) }
        filteredMap.forEach { (key, value) ->
            if (value is MobEffectWidget) loadedEntity.effects.remove(key.first)
            value.visible = false
            widgetMap.remove(key)
        }
        initWidgetsFromMap()
    }

    /** Only called during gui init and adding potion effects.
     * Existing widgets are overridden when this function is called again. */
    private fun initWidgetsFromMap() {
        println("initializing widgetMap")
        clearWidgets()

        loadedEntity.effects.forEach { (key, value) ->
            mobEffectWidgetHolder[key] = MobEffectWidget(key to Screen.POTION, value)
            println(key)
        }
        println("total pages: $mobEffectPages")

        for (i in 0..<mobEffectWidgetHolder.size) {
            val list = mobEffectWidgetHolder.entries.elementAt(i)

            list.value.setPosition(leftPos + 5 + 83 * (i % 3), topPos + 32 + 50 * (i / 3))
            list.value.initWidget()
        }

//        (widgetMap["mob_effect_scroll_box" to Screen.POTION] as MobEffectScrollBox).initWidget()
        widgetMap.forEach { (key, value) ->
            if (value is ValueModifierWidget || value is MobEffectWidget) {
                addRenderableOnly(value)
            } else addRenderableWidget(value)
            if (currentTab == Screen.POTION && key.second != Screen.POTION) {
                setVisible(key.first, false)
//                value.visible = false
            } else if (currentTab == Screen.MAIN && key.second != Screen.MAIN) {
                setVisible(key.first, false)
//                value.visible = false
            }
//            println("key: $key, value: $value")
        }
    }

    override fun init() {
        super.init()
        // Main tab //

        createEditBox(leftPos + 100, topPos + 100, 100, 15, "") { string ->
            val toLoad = string.lowercase().replace(' ', '_')
            loadedEntity.type = getEntityFromString(toLoad)
            checkRequestedVsActual()
        }.also { addToWidgetMap("mob_selector" to Screen.MAIN, it) }

        addToWidgetMap(
            "potion_tab_button" to Screen.MAIN,
            GenericButton(leftPos + 175, topPos, 80, 10, Component.literal("Potion Effects")) {
                currentTab = Screen.POTION
                widgetMap.forEach { (key, value) ->
                    value.visible = currentTab == Screen.POTION && key.second == Screen.POTION
                }
            })

        // Entity Scale
        threeStageValueButtons("scale" to Screen.MAIN, leftPos + 5, topPos + 100, SignType.ADD)
        threeStageValueButtons("scale" to Screen.MAIN, leftPos + 38, topPos + 100, SignType.SUBTRACT)

        // Health modifier
        ValueModifierWidget(
            "health" to Screen.MAIN, leftPos + 72, topPos + 12, 1.0,
            ICONS, 52, 0, 9, 9
        )

//        addToWidgetMap("draggable" to Screen.MAIN,
//            DraggableWidget(leftPos + 20, topPos + 40, 40, 20, 0.5, Component.literal("this is a really long drag")))

        // Potion Effects Tab //

        addToWidgetMap(
            "page_button" to Screen.POTION,
            GenericButton(leftPos + 100, topPos + 10, 30, 10, Component.literal("page")) {
                if (mobEffectPages <= currentMobEffectPage) {
                    currentMobEffectPage = 1
                    initWidgetsFromMap()
                } else {
                    currentMobEffectPage++
                    initWidgetsFromMap()
                }
                println("current page: $currentMobEffectPage")
            })

        addToWidgetMap(
            "clear_effects" to Screen.POTION,
            GenericButton(leftPos + 140, topPos + 10, 30, 10, Component.literal("clear")) {
                loadedEntity.effects.clear()
                initWidgetsFromMap()
            })

        // Main tab button
        addToWidgetMap(
            "main_tab_button" to Screen.POTION,
            GenericButton(leftPos + 130, topPos, 30, 10, Component.literal("Main")) {
                currentTab = Screen.MAIN
                widgetMap.forEach { (key, value) ->
                    value.visible = currentTab == Screen.MAIN && key.second == Screen.MAIN
                }
            })

        // Add potion effect widget from edit box
        addToWidgetMap(
            "add_mob_effect" to Screen.POTION,
            GenericButton(leftPos + 84, topPos + 12, 23, 12, Component.literal("add")) {
                if (mobEffectFromString(id = mobEffectString) != null) {
                    mobEffectFromString(id = mobEffectString)?.let {
                        if (loadedEntity.effects[mobEffectString] == null) {
                            loadedEntity.effects[mobEffectString] = MobEffectInstance(it, 100, 1)
                            initWidgetsFromMap()
                        } else {
                            println("effect already exists in map!")
                        }
                    }
                } else {
                    println("mob effect is null!")
                }
                mobEffectString = ""
            })

        // mob effect edit box (clearly)
        createEditBox(
            leftPos + 2, topPos + 13, 80, 10, ""
        ) { string ->
            mobEffectString = string.lowercase().replace(' ', '_')
        }.also { addToWidgetMap("mob_effect_edit_box" to Screen.POTION, it) }

//        addToWidgetMap("mob_effect_scroll_box" to Screen.POTION,
//            MobEffectScrollBox(leftPos + 2, topPos + 26, 252, 103, Component.empty()))
        addToWidgetMap("test" to Screen.MAIN, TestButton(10, 10, 50, 20).also {
            it.addChild(TestButton(10, 20, 60, 20), "test_2")
//            it.move(0, 30)
        })

        initWidgetsFromMap()
    }

    override fun keyPressed(pKeyCode: Int, pScanCode: Int, pModifiers: Int): Boolean {
        val box = activeEditBox ?: return super.keyPressed(pKeyCode, pScanCode, pModifiers)
        if (pKeyCode == GLFW.GLFW_KEY_ESCAPE && shouldCloseOnEsc()) {
            breadmod.util.render.minecraft.player?.closeContainer()
        }

        return !box.keyPressed(pKeyCode, pScanCode, pModifiers) &&
                if (!box.canConsumeInput()) super.keyPressed(pKeyCode, pScanCode, pModifiers) else true
    }

    // todo could be turned into a gui dragging system? (strong maybe)
    override fun mouseDragged(pMouseX: Double, pMouseY: Double, pButton: Int, pDragX: Double, pDragY: Double): Boolean {
        return if (focused != null) {
            focused?.mouseDragged(pMouseX, pMouseY, pButton, pDragX, pDragY)
            true
        } else super.mouseDragged(pMouseX, pMouseY, pButton, pDragX, pDragY)
    }

    override fun shouldCloseOnEsc(): Boolean {
        val box = activeEditBox ?: return super.shouldCloseOnEsc()
        return !box.canConsumeInput()
    }

    override fun containerTick() {
        widgetMap.forEach { (_, value) ->
            if (value is EditBox) value.tick()
        }
    }

    override fun onClose() {
//        widgetMap.clear()
        activeEditBox = null
        super.onClose()
    }

    private fun AbstractWidget.scaleInternal(pGuiGraphics: GuiGraphics, pPoseStack: PoseStack, pX: Int, pY: Int, pScale: Double) {
        pPoseStack.pushPose()
        pPoseStack.translate(pX.toDouble(), pY.toDouble(), 0.0)
        pPoseStack.scaleFlat(pScale.toFloat())
        pGuiGraphics.fill(RenderType.gui(), 0, 0, width, height, Color(26, 26, 26).rgb)
        pGuiGraphics.fill(RenderType.gui(), 1, 1, width - 1, height - 1, Color(51, 51, 51).rgb)
    }

    // Custom widget classes past here //

    // todo custom texture and scale
    inner class CustomEditBox(
        pX: Int, pY: Int, pWidth: Int, pHeight: Int
    ) : EditBox(breadmod.util.render.minecraft.font, pX, pY, pWidth, pHeight, Component.empty()) {
        override fun tick() {
            super.tick()
            if (this.isFocused) {
                activeEditBox = this
            } else return
        }

        override fun keyPressed(pKeyCode: Int, pScanCode: Int, pModifiers: Int): Boolean {
            if ((pKeyCode == GLFW.GLFW_KEY_ENTER || pKeyCode == GLFW.GLFW_KEY_ESCAPE) && canConsumeInput()) {
                this.isFocused = false
            }
            return super.keyPressed(pKeyCode, pScanCode, pModifiers)
        }
    }

    // todo convert to be scalable using ScaledAbstractButton
    class GenericButton(
        pX: Int,
        pY: Int,
        pWidth: Int,
        pHeight: Int,
        pText: Component,
        onPress: OnPress
    ) : Button(pX, pY, pWidth, pHeight, pText, onPress, CreateNarration { it.get() })

    // todo custom button scale (DONE) and textures (TODO)
    // todo rework (left click increases, right click decreases, holding shift adds 10 multiplier,
    //      holding ctrl adds 100 multiplier)
    // todo key/mouseinput.modifiers, KeyModifier.CTRL -> you get your result
    class ValueModifierButton(
        pX: Int,
        pY: Int,
        pWidth: Int,
        pHeight: Int,
        pScale: Double,
        pText: String,
        private val pType: String,
        private val pValue: Double
    ) : ScaledAbstractButton(pX, pY, pWidth, pHeight, pScale, Component.literal(pText)) {
        override fun onPress() {
            when (pType) {
                "health" -> loadedEntity.health += pValue
                "speed" -> loadedEntity.speed += pValue
                "scale" -> loadedEntity.scale += pValue
            }
        }
    }

    // todo convert widgets in init to values like in MobEffectWidget
    /** Constructs a widget with six add/subtract buttons with a texture to signify what value it changes.
     *  Automatically adds itself to [widgetMap]
     */
    inner class ValueModifierWidget(
        pair: Pair<String, Enum<*>>,
        private val pX: Int,
        private val pY: Int,
        private val pScale: Double,
        private val pIconTexture: ResourceLocation,
        private val pUOffset: Int,
        private val pVOffset: Int,
        private val pUWidth: Int,
        private val pVHeight: Int
    ) : ScaledAbstractWidget(pX, pY, 80, 25, pScale, Component.empty()) {
        override fun renderWidget(pGuiGraphics: GuiGraphics, pMouseX: Int, pMouseY: Int, pPartialTick: Float) {
            val poseStack = pGuiGraphics.pose()
            scaleInternal(pGuiGraphics, poseStack, pX, pY, pScale)
            poseStack.scaleFlat(pScale.toFloat() - 0.4f)
            pGuiGraphics.drawCenteredString(font, loadedEntity.health.toString(), 66, 28, Color.WHITE.rgb)
            poseStack.scaleFlat(pScale.toFloat() + 4f)
            poseStack.translate(0.8, -0.2, 0.0)
            pGuiGraphics.blit(pIconTexture, 8, 0, pUOffset, pVOffset, pUWidth, pVHeight)
            poseStack.popPose()
        }

        init {
            addToWidgetMap(pair, this)
            threeStageValueButtons(pair, pX + 2, pY + 4, SignType.ADD)
            threeStageValueButtons(pair, pX + 57, pY + 4, SignType.SUBTRACT)
        }
    }
    // todo show null error if entered mob effect doesn't exist

    inner class MobEffectWidget(
        private val pair: Pair<String, Screen>,
        val instance: MobEffectInstance
    ) : AbstractWidget(0, 0, 80, 40, Component.empty()) {
        private lateinit var removeButton: GenericButton
        private lateinit var durationEditBox: EditBox
        private lateinit var amplifierEditBox: EditBox

        override fun updateWidgetNarration(pNarrationElementOutput: NarrationElementOutput) {
            defaultButtonNarrationText(pNarrationElementOutput)
        }

        fun initWidget() {
            addToWidgetMap(pair, this)
            durationEditBox = createEditBox(
                x + 2,
                y + height - 12,
                50, 10,
                loadedEntity.effects[pair.first]?.duration.toString()
            ) { string ->
                val number = try {
                    Integer.valueOf(string)
                } catch (e: NumberFormatException) {
                    100
                }

                val old = loadedEntity.effects[pair.first] ?: return@createEditBox
                loadedEntity.effects[pair.first] = MobEffectInstance(old.effect, number, old.amplifier)
            }
            addToWidgetMap("${pair.first}_duration" to pair.second, durationEditBox)
            amplifierEditBox = createEditBox(
                x + 58, y + height - 12,
                20, 10,
                loadedEntity.effects[pair.first]?.amplifier.toString()
            ) { string ->
                val number = try {
                    Integer.valueOf(string)
                } catch (e: NumberFormatException) {
                    1
                }

                val old = loadedEntity.effects[pair.first] ?: return@createEditBox
                loadedEntity.effects[pair.first] = MobEffectInstance(old.effect, old.duration, number)
            }
            addToWidgetMap("${pair.first}_amplifier" to pair.second, amplifierEditBox)
            removeButton = GenericButton(x + width - 11, y, 10, 10, Component.literal("x")) {
                println("removing ${pair.first}")
                removeWidgetAndChildren(pair.first)
            }
            addToWidgetMap("${pair.first}_remove" to pair.second, removeButton)
        }

        override fun renderWidget(pGuiGraphics: GuiGraphics, pMouseX: Int, pMouseY: Int, pPartialTick: Float) {
            val mobEffectTexture = breadmod.util.render.minecraft.mobEffectTextures.get(instance.effect)

            if (visible) {
                pGuiGraphics.fill(RenderType.gui(), x, y, x + width, y + height, Color(26, 26, 26).rgb)
                pGuiGraphics.fill(RenderType.gui(), x + 1, y + 1, x + width - 1, y + height - 1, Color(51, 51, 51).rgb)
                pGuiGraphics.blit(x + 2, y, 0, 18, 18, mobEffectTexture)
                pGuiGraphics.drawText(instance.effect.displayName, x + 2, y + 19, instance.effect.color)

                removeButton.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick)
                durationEditBox.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick)
                amplifierEditBox.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick)
            }
        }

        override fun mouseClicked(pMouseX: Double, pMouseY: Double, pButton: Int): Boolean {
            return if (visible) {
                if (durationEditBox.mouseClicked(pMouseX, pMouseY, pButton)) {
                    true
                } else if (amplifierEditBox.mouseClicked(pMouseX, pMouseY, pButton)) {
                    true
                } else if (removeButton.mouseClicked(pMouseX, pMouseY, pButton)) {
                    true
                } else false
            } else false
        }

        override fun keyPressed(pKeyCode: Int, pScanCode: Int, pModifiers: Int): Boolean {
            return if (visible) {
                if (durationEditBox.keyPressed(pKeyCode, pScanCode, pModifiers)) {
                    true
                } else if (amplifierEditBox.keyPressed(pKeyCode, pScanCode, pModifiers)) {
                    true
                } else false
            } else false
        }
    }
}