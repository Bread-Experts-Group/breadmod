package bread.mod.breadmod.registry.item

import bread.mod.breadmod.ModMainCommon
import dev.architectury.registry.registries.DeferredRegister
import net.minecraft.core.registries.Registries
import net.minecraft.world.item.Item

object ModItems {
    val deferredRegister: DeferredRegister<Item> = DeferredRegister.create(ModMainCommon.MOD_ID, Registries.ITEM)
}