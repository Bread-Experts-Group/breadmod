package breadmodadvanced.registry.item

import breadmodadvanced.ModMainAdv
import net.minecraft.world.item.Item
import net.minecraftforge.registries.DeferredRegister
import net.minecraftforge.registries.ForgeRegistries

object ModItemsAdv {
    val deferredRegister: DeferredRegister<Item> = DeferredRegister.create(ForgeRegistries.ITEMS, ModMainAdv.ID)
    fun getLocation(item: Item) = ForgeRegistries.ITEMS.getKey(item)
}