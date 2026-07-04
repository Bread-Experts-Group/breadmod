package org.bread_experts_group.breadlib_mod_template.registry;

import net.minecraft.world.item.Item;
import org.bread_experts_group.breadlib.registry.RegistryProvider;
import org.bread_experts_group.breadlib.registry.objects.RegistryItem;
import org.bread_experts_group.breadlib_mod_template.BreadLibModTemplate;

public class ModItems {
	public static final RegistryProvider.Items ITEM_REGISTRY = RegistryProvider.createItems(BreadLibModTemplate.MOD_ID);

	public static final RegistryItem<Item> EXAMPLE_ITEM = ITEM_REGISTRY.simpleItem("example_item", new Item.Properties());
}
