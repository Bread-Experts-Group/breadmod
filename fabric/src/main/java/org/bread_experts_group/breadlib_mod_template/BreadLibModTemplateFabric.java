package org.bread_experts_group.breadlib_mod_template;

import net.fabricmc.api.ModInitializer;

public class BreadLibModTemplateFabric implements ModInitializer {
	@Override
	public void onInitialize() {
		BreadLibModTemplate.LOGGER.info("Hello Mod Template world!");
		BreadLibModTemplate.init();
	}
}
