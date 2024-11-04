package org.bread_experts_group.breadmod.registry.item

import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.world.item.Item
import net.minecraft.world.item.Rarity
import net.neoforged.neoforge.registries.DeferredRegister
import org.bread_experts_group.breadmod.Breadmod
import org.bread_experts_group.breadmod.item.TestBreadItem
import org.bread_experts_group.breadmod.item.UltimateBreadItem
import org.bread_experts_group.breadmod.item.armor.ChefHatItem
import org.bread_experts_group.breadmod.item.toolGun.ToolGunItem

object ModItems {
    val ITEM_REGISTRY: DeferredRegister.Items = DeferredRegister.createItems(Breadmod.ID)
    fun getLocation(item: Item) = BuiltInRegistries.ITEM.getKey(item)

    val FLOUR = ITEM_REGISTRY.register("flour") { -> Item(Item.Properties()) }
    val TEST_RECORD = ITEM_REGISTRY.register("music_disc_secret_hoppin") { ->
        Item(Item.Properties().jukeboxPlayable(ModRecords.TEST_SOUND).stacksTo(1).rarity(Rarity.RARE))
    }

    val CHEF_HAT = ITEM_REGISTRY.register("chef_hat", ::ChefHatItem)
    val TOOL_GUN = ITEM_REGISTRY.register("tool_gun", ::ToolGunItem)

    val TEST_BREAD = ITEM_REGISTRY.register("test_bread", ::TestBreadItem)
    val ULTIMATE_BREAD = ITEM_REGISTRY.register("ultimate_bread", ::UltimateBreadItem)
}