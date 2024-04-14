package breadmod.compat.jei

import breadmod.registry.block.ModBlocks
import mezz.jei.api.IModPlugin
import mezz.jei.api.JeiPlugin
import mezz.jei.api.registration.IRecipeRegistration
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation

@JeiPlugin
class JEIPlugin : IModPlugin {
    //    override fun getPluginUid(): ResourceLocation = modLocation("jei_plugin") // debug
    override fun getPluginUid(): ResourceLocation {
        return ResourceLocation("breadmod", "jei_plugin")
    }

    override fun registerRecipes(registration: IRecipeRegistration) {
        registration.addItemStackInfo(ModBlocks.BREAD_BLOCK.get().defaultInstance, Component.literal("FUCK"))
    }
}