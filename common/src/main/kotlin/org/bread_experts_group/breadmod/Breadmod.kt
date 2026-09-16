package org.bread_experts_group.breadmod

import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.bread_experts_group.breadlib.registry.RegistryProvider
import org.bread_experts_group.breadlib.task.TaskManager
import org.bread_experts_group.breadlib.task.client.ClientLogInEvent
import org.bread_experts_group.breadmod.registry.ModBlocks
import org.bread_experts_group.breadmod.registry.ModCreativeTabs
import org.bread_experts_group.breadmod.registry.ModItems

class Breadmod {
    companion object {
        const val MOD_ID: String = "breadmod"
        val LOGGER: Logger = LogManager.getLogger("Breadmod")

        fun init() {
            RegistryProvider.initializeProviders(ModItems.REGISTRY, ModBlocks.REGISTRY, ModCreativeTabs.REGISTRY)

            TaskManager.newTask { task: ClientLogInEvent ->
                this.LOGGER.info("Hello World!")
            }
        }
    }
}