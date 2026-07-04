package org.bread_experts_group.breadlib_mod_template;

import net.minecraftforge.fml.common.Mod;

@Mod(BreadLibModTemplate.MOD_ID)
public class BreadlibModTemplate {

	public BreadlibModTemplate() {
		BreadLibModTemplate.LOGGER.info("Hello Mod Template world!");
		BreadLibModTemplate.init();

	}
}
