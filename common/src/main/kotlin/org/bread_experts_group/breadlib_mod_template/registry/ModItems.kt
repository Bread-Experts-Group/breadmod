package org.bread_experts_group.breadlib_mod_template.registry

import net.minecraft.world.item.Item
import org.bread_experts_group.breadlib_mod_template.BreadLibModTemplate
import org.bread_experts_group.breadlib.registry.RegistryProvider
import org.bread_experts_group.breadlib.registry.objects.RegistryItem

object ModItems {
    val REGISTRY: RegistryProvider.Items = RegistryProvider.getItems(BreadLibModTemplate.MOD_ID)

    val TEST_ITEM: RegistryItem<Item> = this.REGISTRY.simpleItem("test_item", Item.Properties())
}