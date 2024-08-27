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
import net.minecraft.client.gui.narration.NarratedElementType
import net.minecraft.client.gui.narration.NarrationElementOutput
import net.minecraft.network.chat.Component
import org.joml.Matrix4f

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
class ContainerWidget(
    pWidth: Int, pHeight: Int,
    pX: Int, pY: Int,
    private val pTilt: Float,
    pComponent: Component,
    private val childrenWidgets: MutableMap<AbstractWidget, Double>
) : AbstractWidget(pWidth, pHeight, pX, pY, pComponent) {
    internal object Serializer : KSerializer<ContainerWidget> {
        override val descriptor: SerialDescriptor = buildClassSerialDescriptor("ContainerWidget") {
            element<Int>("width")
            element<Int>("height")
            element<Int>("x")
            element<Int>("y")
            element<Int>("tilt")
            element<Component>("component")
            element<List<AbstractWidget>>("widgets")
        }

        override fun deserialize(decoder: Decoder): ContainerWidget {
            require(decoder is JsonDecoder)
            val element = decoder.decodeJsonElement() as JsonObject
            return ContainerWidget(
                decoder.json.decodeFromJsonElement(element["width"]!!),
                decoder.json.decodeFromJsonElement(element["height"]!!),
                decoder.json.decodeFromJsonElement(element["x"]!!),
                decoder.json.decodeFromJsonElement(element["y"]!!),
                decoder.json.decodeFromJsonElement(element["tilt"]!!),
                decoder.json.decodeFromJsonElement(element["component"]!!),
                decoder.json.decodeFromJsonElement(element["widgets"]!!),
            )
        }

        override fun serialize(encoder: Encoder, value: ContainerWidget) {
            require(encoder is JsonEncoder)
            encoder.encodeJsonElement(buildJsonObject {
                put("width", json.encodeToJsonElement(value.width))
                put("height", json.encodeToJsonElement(value.height))
                put("x", json.encodeToJsonElement(value.x))
                put("y", json.encodeToJsonElement(value.y))
                put("component", json.encodeToJsonElement(value.message))
                putJsonArray("widgets") { value.childrenWidgets.forEach { add(json.encodeToJsonElement(it)) } }
            })
        }
    }

    /**
     * Adds a widget to this container.
     * @return This container.
     * @param pWidget The widget to add.
     * @param pZIndex The Z index (rendering order) of the widget.
     * @author Miko Elbrecht
     * @since 1.0
     */
    fun addWidget(pWidget: AbstractWidget, pZIndex: Double): ContainerWidget = this.also {
        childrenWidgets[pWidget] = pZIndex
    }

    override fun renderWidget(pGuiGraphics: GuiGraphics, pMouseX: Int, pMouseY: Int, pPartialTick: Float) {
        val pose = pGuiGraphics.pose()
        childrenWidgets.forEach { (widget, z) ->
            pose.pushPose()
            pose.translate(this.x + widget.x.toDouble(), this.y + widget.y.toDouble(), z)
            pose.mulPoseMatrix(Matrix4f().rotateZ(pTilt))
            widget.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick)
            pose.popPose()
        }
    }

    override fun updateWidgetNarration(pNarrationElementOutput: NarrationElementOutput) {
        pNarrationElementOutput.add(NarratedElementType.TITLE, "w43")
    }

    override fun clicked(pMouseX: Double, pMouseY: Double): Boolean = false
}