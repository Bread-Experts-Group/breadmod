package org.bread_experts_group.breadmod.registry.item

import net.minecraft.world.item.CreativeModeTab
import java.util.function.Supplier

/**
 * Special creative tab registration. Meant to be implemented by items.
 * @author Miko Elbrecht
 * @since 1.0.0
 */
interface IRegisterSpecialCreativeTab {
    /**
     * Creative tabs to register this item for.
     * @author Miko Elbrecht
     * @since 1.0.0
     */
    val creativeModeTabs: List<Supplier<CreativeModeTab>>

    /**
     * Defines a special creative tab registration method.
     * @author Miko Elbrecht
     * @since 1.0.0
     * @return true if the default item stack should also be registered.
     */
    fun displayInCreativeTab(
        parameters: CreativeModeTab.ItemDisplayParameters,
        output: CreativeModeTab.Output
    ): Boolean = true
}