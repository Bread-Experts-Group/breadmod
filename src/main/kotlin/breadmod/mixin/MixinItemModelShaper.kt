package breadmod.mixin

import breadmod.block.specialItem.UseBlockStateNBT
import breadmod.mixin.accessors.IAccessorItemModelShaper
import net.minecraft.client.renderer.ItemModelShaper
import net.minecraft.client.resources.model.BakedModel
import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.ItemStack
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
import kotlin.reflect.full.hasAnnotation

@Mixin(ItemModelShaper::class)
@Suppress("NonJavaMixin")
abstract class MixinItemModelShaper: IAccessorItemModelShaper {
    @Inject(method = ["getItemModel"], at = [At("HEAD")], cancellable = true)
    private fun getItemModel(pStack: ItemStack, info: CallbackInfoReturnable<BakedModel>) = pStack.item.let {
        if(it is BlockItem && it.block::class.hasAnnotation<UseBlockStateNBT>()) {
            info.returnValue = this.modelManager.blockModelShaper.getBlockModel(UseBlockStateNBT.loadState(pStack.orCreateTag, it.block))
        }
    }
}