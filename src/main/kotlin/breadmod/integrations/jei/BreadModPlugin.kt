package breadmod.integrations.jei

import breadmod.BreadMod
import breadmod.item.ModItems.BREAD_BLOCK_ITEM
import mezz.jei.api.IModPlugin
import mezz.jei.api.JeiPlugin
import mezz.jei.api.registration.IRecipeRegistration
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation

@JeiPlugin
class BreadModPlugin : IModPlugin {
    override fun getPluginUid(): ResourceLocation {
        return ResourceLocation(BreadMod.ID, "bread_jei")
        // Is this override even doing anything? I had it at null earlier, and it didn't affect anything
    }

    override fun registerRecipes(registration: IRecipeRegistration) {
        registration.addItemStackInfo(BREAD_BLOCK_ITEM.defaultInstance, Component.literal("THE BREAD BLOCK IS REAL"))
    }
}
