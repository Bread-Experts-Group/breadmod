package breadmod.mixin

import breadmod.item.util.BlockStateStack
import breadmod.mixin.accessors.ItemModelShaperAccessor
import net.minecraft.client.renderer.ItemModelShaper
import net.minecraft.client.resources.model.BakedModel
import net.minecraft.world.item.ItemStack
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable

@Mixin(ItemModelShaper::class)
@Suppress("NonJavaMixin")
abstract class MixinItemModelShaper: ItemModelShaperAccessor {
    @Inject(method = ["getItemModel"], at = [At("HEAD")], cancellable = true)
    private fun getItemModel(pStack: ItemStack, info: CallbackInfoReturnable<BakedModel>) {
        //if(pStack is BlockStateStack) info.returnValue = this.modelManager.blockModelShaper.getBlockModel(pStack.blockState)
    }
}