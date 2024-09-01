package breadmod.util.gui.widget

import breadmod.util.gui.widget.marker.*
import breadmod.util.json
import breadmod.util.render.mouseGuiX
import breadmod.util.render.mouseGuiY
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.*
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.AbstractWidget
import net.minecraft.client.gui.narration.NarrationElementOutput
import net.minecraft.network.chat.Component
import org.joml.Matrix4f
import kotlin.reflect.KClass
import kotlin.reflect.full.isSubclassOf

/**
 * A map of widgets usually contained in a [ContainerWidget].
 * @author Miko Elbrecht
 * @since 1.0.0
 */
typealias ContainedWidgets = MutableMap<AbstractWidget, Pair<Double, String?>>

/**
 * A widget that can contain other widgets.
 * @param pWidth The width of the widget.
 * @param pHeight The height of the widget.
 * @param pX The X position of the widget.
 * @param pY The Y position of the widget.
 * @param pComponent TODO something
 * @author Miko Elbrecht
 * @since 1.0
 */
@Serializable(with = ContainerWidget.Serializer::class)
open class ContainerWidget(
    pX: Int, pY: Int,
    pWidth: Int, pHeight: Int,
    private val pTilt: Float,
    pComponent: Component,
    private val childrenWidgets: ContainedWidgets,
    private val rootWidget: Boolean = false
) : AbstractWidget(pX, pY, pWidth, pHeight, pComponent), IWidgetOffsetAware {
    private val associationMap: MutableMap<String, AbstractWidget> = mutableMapOf()

    internal object Serializer : KSerializer<ContainerWidget> {
        override val descriptor: SerialDescriptor = buildClassSerialDescriptor("ContainerWidget") {
            element<Int>("x")
            element<Int>("y")
            element<Int>("width")
            element<Int>("height")
            element<Int>("tilt")
            element<Component>("component")
            element<List<AbstractWidget>>("widgets")
        }

        override fun deserialize(decoder: Decoder): ContainerWidget {
            require(decoder is JsonDecoder)
            val element = decoder.decodeJsonElement() as JsonObject
            return ContainerWidget(
                decoder.json.decodeFromJsonElement(element["x"]!!),
                decoder.json.decodeFromJsonElement(element["y"]!!),
                decoder.json.decodeFromJsonElement(element["width"]!!),
                decoder.json.decodeFromJsonElement(element["height"]!!),
                decoder.json.decodeFromJsonElement(element["tilt"]!!),
                decoder.json.decodeFromJsonElement(element["component"]!!),
                decoder.json.decodeFromJsonElement(element["widgets"]!!),
            )
        }

        override fun serialize(encoder: Encoder, value: ContainerWidget) {
            require(encoder is JsonEncoder)
            encoder.encodeJsonElement(buildJsonObject {
                put("x", json.encodeToJsonElement(value.x))
                put("y", json.encodeToJsonElement(value.y))
                put("width", json.encodeToJsonElement(value.width))
                put("height", json.encodeToJsonElement(value.height))
                put("component", json.encodeToJsonElement(value.message))
                putJsonArray("widgets") { value.childrenWidgets.forEach { add(json.encodeToJsonElement(it)) } }
            })
        }
    }

    init {
        childrenWidgets.forEach { (widget, pair) -> pair.second?.let { associationMap[it] = widget } }
    }

    /**
     * Adds a widget to this container.
     * @return This container.
     * @param pWidget The widget to add.
     * @param pZIndex The Z index (rendering order) of the widget.
     * @author Miko Elbrecht
     * @since 1.0
     */
    open fun addWidget(pWidget: AbstractWidget, pZIndex: Double = 0.0, pTag: String? = null): ContainerWidget = this.also {
        childrenWidgets[pWidget] = pZIndex to pTag
        if (pTag != null) associationMap[pTag] = pWidget
    }

    /**
     * Gets a widget by its tag.
     * @param pTag The tag of the widget to get.
     * @return The widget with the given tag.
     * @throws NoSuchElementException If no widget with the given tag exists in this container.
     * @author Miko Elbrecht
     * @since 1.0
     */
    fun getTaggedWidget(pTag: String): AbstractWidget = associationMap[pTag]
        ?: throw NoSuchElementException("No widget with tag $pTag")

    /**
     * Facilitates the rendering of this [ContainerWidget].
     * @see renderWidget
     * @author Miko Elbrecht
     * @since 1.0.0
     */
    override fun render(pGuiGraphics: GuiGraphics, pMouseX: Int, pMouseY: Int, pPartialTick: Float) {
        if (this.visible) {
            val pose = pGuiGraphics.pose()
            pose.pushPose()

            pose.mulPoseMatrix(Matrix4f().rotateX(pTilt))
            if (rootWidget) {
                pose.translate(x.toDouble(), y.toDouble(), 1.0)
                absoluteX = x
                absoluteY = y
            } else pose.translate(0.0, 0.0, 1.0)

            isHovered = (pMouseX >= x) &&
                    (pMouseY >= y) &&
                    (pMouseX < x + width) &&
                    (pMouseY < y + height)

            renderWidget(pGuiGraphics, pMouseX - x, pMouseY - y, pPartialTick)
            updateTooltip()

            pose.popPose()
        }
    }

    /**
     * Renders this [ContainerWidget] along with its children.
     * @param pGuiGraphics The graphics context to render with.
     * @param pMouseX The X position of the mouse.
     * @param pMouseY The Y position of the mouse.
     * @param pPartialTick The partial tick amount to render with.
     * @author Miko Elbrecht
     * @since 1.0
     */
    override fun renderWidget(pGuiGraphics: GuiGraphics, pMouseX: Int, pMouseY: Int, pPartialTick: Float) {
        val pose = pGuiGraphics.pose()
        childrenWidgets.forEach { (widget, pair) ->
            pose.pushPose()
            pose.translate(widget.x.toDouble(), widget.y.toDouble(), pair.first)

            if (widget is IWidgetOffsetAware) {
                widget.absoluteX = absoluteX + widget.x
                widget.absoluteY = absoluteY + widget.y
            }

            widget.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick)
            pose.popPose()
        }
    }

    private fun ContainerWidget.iterateLowHigh(
        pMouseX: Double, pMouseY: Double,
        pInterface: KClass<*>,
        pX: Int = this.x, pY: Int = this.y
    ): Pair<AbstractWidget, Pair<Double, Double>>? {
        return this.childrenWidgets.keys.firstNotNullOfOrNull { widget ->
            if (widget::class.isSubclassOf(pInterface) && widget.isMouseOver(pMouseX - pX, pMouseY - pY))
                widget to (pMouseX - pX to pMouseY - pY)
            else if (widget is ContainerWidget)
                widget.iterateLowHigh(pMouseX, pMouseY, pInterface, pX + widget.x, pY + widget.y)
            else null
        }
    }

    private fun getClickableWidget() =
        this.iterateLowHigh(
            mouseGuiX,
            mouseGuiY,
            IWidgetMouseClickSensitive::class
        )

    /**
     * Handles a mouse click event, distributing the event to [IWidgetMouseClickSensitive] widgets.
     * @param pMouseX The X position of the mouse.
     * @param pMouseY The Y position of the mouse.
     * @param pButton The button on the mouse that was clicked.
     * @return Whether the click was handled by a widget and should be consumed.
     * @author Miko Elbrecht
     * @since 1.0.0
     */
    override fun mouseClicked(pMouseX: Double, pMouseY: Double, pButton: Int): Boolean {
        val rx = getClickableWidget()
        return if (rx != null && rx.first.mouseClicked(rx.second.first, rx.second.second, pButton)) {
            clickFocused = rx.first
            true
        } else {
            clickFocused = null
            false
        }
    }

    /**
     * Handles a mouse release event, distributing the event to the previously clicked widget.
     * @param pMouseX The X position of the mouse.
     * @param pMouseY The Y position of the mouse.
     * @param pButton The button on the mouse that was clicked.
     * @return Whether the click was handled by a widget and should be consumed.
     * @author Miko Elbrecht
     * @since 1.0.0
     */
    override fun mouseReleased(pMouseX: Double, pMouseY: Double, pButton: Int): Boolean =
        if (clickFocused != null) {
            val state = clickFocused!!.mouseReleased(pMouseX, pMouseY, pButton)
            clickFocused = null
            state
        } else false

    private var clickFocused: AbstractWidget? = null

    /**
     * Handles a mouse movement event, distributing the event to [IWidgetMouseMovementSensitive] widgets.
     * @param pMouseX The X position of the mouse.
     * @param pMouseY The Y position of the mouse.
     * @author Miko Elbrecht
     * @since 1.0.0
     */
    override fun mouseMoved(pMouseX: Double, pMouseY: Double) {
        val rx = this.iterateLowHigh(pMouseX, pMouseY, IWidgetMouseMovementSensitive::class)
        rx?.first?.mouseMoved(rx.second.first, rx.second.second)
    }

    /**
     * Handles a mouse dragging event, distributing the event to [IWidgetMouseDragSensitive] widgets.
     * @param pMouseX The X position of the mouse.
     * @param pMouseY The Y position of the mouse.
     * @param pButton The button on the mouse that was clicked.
     * @param pDragX The X distance of the mouse drag.
     * @param pDragY The Y distance of the mouse drag.
     * @author Miko Elbrecht
     * @since 1.0.0
     */
    override fun mouseDragged(pMouseX: Double, pMouseY: Double, pButton: Int, pDragX: Double, pDragY: Double): Boolean {
        return if (dragFocused != null) {
            val shouldContinue = dragFocused!!.mouseDragged(pMouseX, pMouseY, pButton, pDragX, pDragY)
            if (!shouldContinue) {
                dragFocused = null
                false
            } else true
        } else {
            val rx = this.iterateLowHigh(pMouseX, pMouseY, IWidgetMouseDragSensitive::class)
            if (rx != null && rx.first.mouseDragged(rx.second.first, rx.second.second, pButton, pDragX, pDragY)) {
                dragFocused = rx.first
                true
            } else false
        }
    }

    private var dragFocused: AbstractWidget? = null

    private fun getKeyableWidget() =
        this.iterateLowHigh(
            mouseGuiX,
            mouseGuiY,
            IWidgetKeySensitive::class
        )

    /**
     * Handles a key press event, distributing the event to [IWidgetKeySensitive] widgets.
     * @param pButton The button on the keyboard that was pressed.
     * @param pScanCode The scan code of the key that was pressed.
     * @param pModifiers The bit-mapped modifiers that were active when the key was pressed.
     * @author Miko Elbrecht
     * @since 1.0.0
     */
    override fun keyPressed(pButton: Int, pScanCode: Int, pModifiers: Int): Boolean =
        getKeyableWidget()?.first?.keyPressed(pButton, pScanCode, pModifiers) ?: false

    /**
     * Handles a key press event, distributing the event to [IWidgetKeySensitive] widgets.
     * @param pButton The button on the keyboard that was pressed.
     * @param pScanCode The scan code of the key that was pressed.
     * @param pModifiers The bit-mapped modifiers that were active when the key was pressed.
     * @author Miko Elbrecht
     * @since 1.0.0
     */
    override fun keyReleased(pButton: Int, pScanCode: Int, pModifiers: Int): Boolean =
        getKeyableWidget()?.first?.keyReleased(pButton, pScanCode, pModifiers) ?: false

    /**
     * Updates the narration output of this [ContainerWidget]. (By default, nothing is added.)
     * @param pNarrationElementOutput The narration output to update.
     * @author Miko Elbrecht
     * @since 1.0.0
     */
    override fun updateWidgetNarration(pNarrationElementOutput: NarrationElementOutput) {}
    override var absoluteX: Int = 0
    override var absoluteY: Int = 0
}