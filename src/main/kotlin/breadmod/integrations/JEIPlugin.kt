package breadmod.integrations

import breadmod.BreadMod
import breadmod.item.ModItems.BREAD_BLOCK_ITEM
import mezz.jei.api.IModPlugin
import mezz.jei.api.JeiPlugin
import mezz.jei.api.registration.IRecipeRegistration
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation

@JeiPlugin
@Suppress("UNUSED")
class JEIPlugin : IModPlugin {
    override fun getPluginUid(): ResourceLocation = BreadMod.resource("jei_plugin") // debug
    override fun registerRecipes(registration: IRecipeRegistration) {
        registration.addItemStackInfo(BREAD_BLOCK_ITEM.defaultInstance, Component.literal("THE BREAD BLOCK IS REAL"))
    }
}
