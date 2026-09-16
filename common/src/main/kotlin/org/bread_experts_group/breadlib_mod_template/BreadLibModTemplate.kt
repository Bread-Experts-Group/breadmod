package org.bread_experts_group.breadlib_mod_template

import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.bread_experts_group.breadlib.registry.RegistryProvider
import org.bread_experts_group.breadlib.task.TaskManager
import org.bread_experts_group.breadlib.task.client.ClientLogInEvent
import org.bread_experts_group.breadlib_mod_template.registry.ModBlocks
import org.bread_experts_group.breadlib_mod_template.registry.ModCreativeTabs
import org.bread_experts_group.breadlib_mod_template.registry.ModItems

class BreadLibModTemplate {
    companion object {
        const val MOD_ID: String = "breadlib_mod_template"
        val LOGGER: Logger = LogManager.getLogger("BreadLibModTemplate")

        fun init() {
            RegistryProvider.initializeProviders(ModItems.REGISTRY, ModBlocks.REGISTRY, ModCreativeTabs.REGISTRY)

            TaskManager.newTask { task: ClientLogInEvent ->
                this.LOGGER.info("Hello World!")
            }
        }
    }
}