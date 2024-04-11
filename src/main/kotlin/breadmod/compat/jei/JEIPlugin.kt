package breadmod.compat.jei

import breadmod.BreadMod.modLocation
import breadmod.BreadMod.modTranslatable
import breadmod.registry.block.ModBlocks
import mezz.jei.api.IModPlugin
import mezz.jei.api.JeiPlugin
import mezz.jei.api.registration.IRecipeRegistration
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.ItemStack

//@JeiPlugin
//class JEIPlugin : IModPlugin {
//    override fun getPluginUid(): ResourceLocation = modLocation("jei_plugin") // debug
//    override fun registerRecipes(registration: IRecipeRegistration) {
//        registration.addItemStackInfo(
//            ItemStack(ModBlocks.BREAD_BLOCK.get()),
//            modTranslatable("jei", "bread_block", "description")
//        )
//    }
//}