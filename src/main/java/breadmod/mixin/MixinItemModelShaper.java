package breadmod.mixin;

import breadmod.block.specialItem.UseBlockStateNBT;
import breadmod.mixin.accessors.IAccessorItemModelShaper;
import net.minecraft.client.renderer.ItemModelShaper;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemModelShaper.class)
abstract class MixinItemModelShaper implements IAccessorItemModelShaper {
    @Shadow @Final private ModelManager modelManager;

    @Inject(method = "getItemModel(Lnet/minecraft/world/item/ItemStack;)Lnet/minecraft/client/resources/model/BakedModel;", at = @At("HEAD"), cancellable = true)
    private void getItemModel(ItemStack pStack, CallbackInfoReturnable<BakedModel> cir) {
        if(pStack.getItem() instanceof BlockItem && pStack.getItem().getClass().isAnnotationPresent(UseBlockStateNBT.class)) {
            cir.setReturnValue(this.modelManager.getBlockModelShaper().getBlockModel(UseBlockStateNBT.Companion.loadState(pStack.getOrCreateTag(), ((BlockItem) pStack.getItem()).getBlock())));
        }
    }
}
