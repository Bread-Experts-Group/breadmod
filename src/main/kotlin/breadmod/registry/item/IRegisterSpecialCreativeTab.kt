package breadmod.registry.item

import net.minecraft.world.item.CreativeModeTab
import net.minecraftforge.registries.RegistryObject

/**
 * Special creative tab registration. Meant to implemented by items.
 * @author Miko Elbrecht
 * @since 1.0.0
 */
interface IRegisterSpecialCreativeTab {
    /**
     * Creative tabs to register this item for.
     * @author Miko Elbrecht
     * @since 1.0.0
     */
    val creativeModeTabs: List<RegistryObject<CreativeModeTab>>

    /**
     * Defines a special creative tab registration method.
     * @author Miko Elbrecht
     * @since 1.0.0
     * @return true if the default item stack should also be registered.
     */
    fun displayInCreativeTab(pParameters: CreativeModeTab.ItemDisplayParameters, pOutput: CreativeModeTab.Output) = true
}