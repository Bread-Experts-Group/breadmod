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
    private val childrenWidgets: ContainedWidgets
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
    open fun addWidget(pWidget: AbstractWidget, pZIndex: Double, pTag: String? = null): ContainerWidget = this.also {
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
        pose.pushPose()
        var aX = absoluteX + this.x
        var aY = absoluteY + this.y
        if (this.x > 0 && this.y > 0) pose.translate(this.x.toDouble(), this.y.toDouble(), 0.0)
        pose.mulPoseMatrix(Matrix4f().rotateX(pTilt))
        childrenWidgets.forEach { (widget, pair) ->
            pose.pushPose()
            // NOTE: This code is quite messy. We should probably clean it up later.
            if (widget !is ContainerWidget) {
                aX += widget.x
                aY += widget.y
                pose.translate(widget.x.toDouble(), widget.y.toDouble(), pair.first)
            }
            if (widget is IWidgetOffsetAware) {
                widget.absoluteX = aX
                widget.absoluteY = aY
            }
            widget.render(
                pGuiGraphics,
                pMouseX - this.x,
                pMouseY - this.y,
                pPartialTick
            )
            pose.popPose()
        }
        pose.popPose()
    }

    private fun ContainerWidget.iterateLowHigh(
        pMouseX: Double, pMouseY: Double,
        pInterface: KClass<*>,
        pX: Int = this.x, pY: Int = this.y
    ): Pair<AbstractWidget, Pair<Double, Double>>? {
        return this.childrenWidgets.keys.firstNotNullOfOrNull { widget ->
            if (widget is ContainerWidget)
                widget.iterateLowHigh(pMouseX, pMouseY, pInterface, pX + widget.x, pY + widget.y)
            else if (widget::class.isSubclassOf(pInterface) && widget.isMouseOver(pMouseX - pX, pMouseY - pY))
                widget to (pMouseX - pX to pMouseY - pY)
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
        return rx?.first?.mouseClicked(rx.second.first, rx.second.second, pButton) ?: false
    }

    /**
     * Handles a mouse release event, distributing the event to [IWidgetMouseClickSensitive] widgets.
     * @param pMouseX The X position of the mouse.
     * @param pMouseY The Y position of the mouse.
     * @param pButton The button on the mouse that was clicked.
     * @return Whether the click was handled by a widget and should be consumed.
     * @author Miko Elbrecht
     * @since 1.0.0
     */
    override fun mouseReleased(pMouseX: Double, pMouseY: Double, pButton: Int): Boolean {
        val rx = getClickableWidget()
        return rx?.first?.mouseReleased(rx.second.first, rx.second.second, pButton) ?: false
    }

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
        val rx = this.iterateLowHigh(pMouseX, pMouseY, IWidgetMouseDragSensitive::class)
        return rx?.first?.mouseDragged(rx.second.first, rx.second.second, pButton, pDragX, pDragY) ?: false
    }

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