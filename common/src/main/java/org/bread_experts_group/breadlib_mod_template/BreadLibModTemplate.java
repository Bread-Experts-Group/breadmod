package org.bread_experts_group.breadlib_mod_template;

import org.bread_experts_group.breadlib.platform.PlatformServices;
import org.bread_experts_group.breadlib_mod_template.registry.ModItems;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BreadLibModTemplate {
	public static final String MOD_ID = "breadlib_mod_template";
	public static final Logger LOGGER = LoggerFactory.getLogger("Breadlib Mod Template");

	public static void init() {
		ModItems.ITEM_REGISTRY.register();

		if (PlatformServices.PLATFORM.isModLoaded("breadlib_mod_template")) {
			BreadLibModTemplate.LOGGER.info("Hello to breadlib_mod_template");
		}
	}
}
