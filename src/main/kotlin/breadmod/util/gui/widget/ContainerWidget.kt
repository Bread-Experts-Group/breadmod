package breadmod.util.gui.widget

import breadmod.util.json
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
import kotlin.math.roundToInt

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
    private val childrenWidgets: MutableMap<AbstractWidget, Pair<Double, String?>>
) : AbstractWidget(pX, pY, pWidth, pHeight, pComponent) {
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
    fun addWidget(pWidget: AbstractWidget, pZIndex: Double, pTag: String): ContainerWidget = this.also {
        childrenWidgets[pWidget] = pZIndex to pTag
        associationMap[pTag] = pWidget
    }

    fun getTaggedWidget(pTag: String) = associationMap[pTag] ?: throw NoSuchElementException("No widget with tag $pTag")

    override fun renderWidget(pGuiGraphics: GuiGraphics, pMouseX: Int, pMouseY: Int, pPartialTick: Float) {
        val pose = pGuiGraphics.pose()

        childrenWidgets.forEach { (widget, pair) ->
            pose.pushPose()
            val translateX = this.x + widget.x.toDouble()
            val translateY = this.y + widget.y.toDouble()
            pose.translate(translateX, translateY, pair.first)
            pose.mulPoseMatrix(Matrix4f().rotateZ(pTilt))
            val mouseX = (pMouseX - translateX).roundToInt()
            val mouseY = (pMouseY - translateY).roundToInt()

            widget.render(pGuiGraphics, mouseX, mouseY, pPartialTick)

            pose.popPose()
        }
    }

    override fun updateWidgetNarration(pNarrationElementOutput: NarrationElementOutput) {}
}