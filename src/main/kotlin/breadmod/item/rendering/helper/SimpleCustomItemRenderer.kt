package breadmod.item.rendering.helper

import it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet
import net.minecraft.world.item.Item
import net.minecraftforge.client.extensions.common.IClientItemExtensions
import net.minecraftforge.registries.ForgeRegistries
import java.util.function.Consumer

class SimpleCustomItemRenderer(private var renderer: AbstractRenderedItemModelRenderer) : IClientItemExtensions {

    override fun getCustomRenderer(): AbstractRenderedItemModelRenderer = renderer

    companion object {
        fun create(item: Item, renderer: AbstractRenderedItemModelRenderer): SimpleCustomItemRenderer {
            CustomRenderedItemRegister.register(item)
            return SimpleCustomItemRenderer(renderer)
        }
    }

    object CustomRenderedItemRegister {
        private val items: MutableSet<Item> = ReferenceOpenHashSet()
        private var itemsFiltered = false

        fun register(pItem: Item) { items.add(pItem) }
        fun forEach(pConsumer: Consumer<Item>) {
            if(!itemsFiltered) {
                val iterator = items.iterator()
                while (iterator.hasNext()) {
                    val item = iterator.next()
                    if (!ForgeRegistries.ITEMS.containsValue(item) || IClientItemExtensions.of(item)
                            .customRenderer !is AbstractRenderedItemModelRenderer
                    ) { iterator.remove() }
                }
                itemsFiltered = true
            }
            items.forEach(pConsumer)
        }
    }
}