package org.bread_experts_group.breadlib_mod_template;


import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;

@Mod(BreadLibModTemplate.MOD_ID)
public class BreadlibModTemplateNeoForge {
	public BreadlibModTemplateNeoForge(IEventBus eventBus) {
		BreadLibModTemplate.LOGGER.info("Hello Mod Template world!");
		BreadLibModTemplate.init();
	}
}
