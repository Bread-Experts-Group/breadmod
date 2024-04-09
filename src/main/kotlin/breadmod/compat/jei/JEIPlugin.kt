package breadmod.compat.jei

import breadmod.BreadMod
import breadmod.block.registry.ModBlocks
import mezz.jei.api.IModPlugin
import mezz.jei.api.JeiPlugin
import mezz.jei.api.registration.IRecipeRegistration
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation


//@JeiPlugin
//@Suppress("UNUSED")
//class JEIPlugin : IModPlugin {
//    override fun getPluginUid(): ResourceLocation = BreadMod.asResource("jei_plugin") // debug
//    /* TODO: redo this to actually make it work
//    override fun registerRecipes(registration: IRecipeRegistration) {
//        registration.addItemStackInfo(ModBlocks.BREAD_BLOCK.get().asItem().defaultInstance, Component.literal("THE BREAD BLOCK IS REAL"))
//    }
//
//     */
//}