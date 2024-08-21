package breadmod.client.gui.screens

import breadmod.client.gui.components.CustomAbstractWidget
import net.minecraft.client.gui.components.AbstractWidget
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.inventory.AbstractContainerMenu

/**
 * Custom implementation of [AbstractContainerScreen] with a widget id system.
 */
abstract class BreadModContainerScreen <T: AbstractContainerMenu>(
    pMenu: T,
    pPlayerInventory: Inventory,
    pTitle: Component
) : AbstractContainerScreen<T>(pMenu, pPlayerInventory, pTitle) {
    /**
     * Holder map for widgets.
     */
    protected val widgetMap: MutableMap<Pair<String, WidgetType>, AbstractWidget> = mutableMapOf()

    enum class WidgetType {
        /** A widget that can be interacted with, does not render. */
        NOT_RENDERABLE,
        /** A widget that can be interacted with, renders on screen. */
        RENDERABLE,
        /** A widget that only renders on screen, cannot be interacted with. */
        RENDERABLE_ONLY
    }

    fun addWidgetToMap(id: String, type: WidgetType, widget: AbstractWidget) {
        widgetMap[id to type] = widget
        when (type) {
            WidgetType.NOT_RENDERABLE -> addWidget(widget)
            WidgetType.RENDERABLE -> addRenderableWidget(widget)
            WidgetType.RENDERABLE_ONLY -> addRenderableOnly(widget)
        }
    }
}