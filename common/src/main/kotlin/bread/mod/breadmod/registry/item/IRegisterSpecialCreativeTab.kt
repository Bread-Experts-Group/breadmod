package bread.mod.breadmod.registry.item

import dev.architectury.registry.registries.RegistrySupplier
import net.minecraft.world.item.CreativeModeTab

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
    val creativeModeTabs: List<RegistrySupplier<CreativeModeTab>>

    /**
     * Defines a special creative tab registration method.
     * @author Miko Elbrecht
     * @since 1.0.0
     * @return true if the default item stack should also be registered.
     */
    fun displayInCreativeTab(pParameters: CreativeModeTab.ItemDisplayParameters, pOutput: CreativeModeTab.Output) = true
}