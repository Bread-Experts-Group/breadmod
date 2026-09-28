package org.bread_experts_group.breadmod.registry

import net.minecraft.world.item.Item
import net.minecraft.world.item.Rarity
import org.bread_experts_group.breadmod.Breadmod
import org.bread_experts_group.breadlib.registry.RegistryProvider
import org.bread_experts_group.breadlib.registry.objects.RegistryItem
import org.bread_experts_group.breadmod.registry.bootstrap.ModRecords

object ModItems {
    val REGISTRY: RegistryProvider.Items = RegistryProvider.getItems(Breadmod.MOD_ID)

    val TEST_ITEM: RegistryItem<Item> = this.REGISTRY.simpleItem("test_item", Item.Properties())

    val RECORD_SECRET_HOPPIN: RegistryItem<Item> = this.REGISTRY.simpleItem(
        "music_disc_secret_hoppin",
        Item.Properties()
            .jukeboxPlayable(ModRecords.SECRET_HOPPIN)
            .stacksTo(1)
            .rarity(Rarity.RARE)
    )
}