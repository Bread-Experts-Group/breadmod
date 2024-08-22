package breadmod.mixin.client;

import breadmod.block.specialItem.UseBlockStateNBT;
import net.minecraft.client.renderer.ItemModelShaper;
import net.minecraft.client.renderer.block.BlockModelShaper;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemModelShaper.class)
abstract class MixinItemModelShaper {
    @Invoker("getModelManager")
    abstract ModelManager iGetModelManager();

    @Inject(
            method = "getItemModel(Lnet/minecraft/world/item/ItemStack;)Lnet/minecraft/client/resources/model/BakedModel;",
            at = @At("HEAD"),
            cancellable = true)
    private void getItemModel(final ItemStack pStack, final CallbackInfoReturnable<? super BakedModel> cir) {
        final Item item = pStack.getItem();
        final Class<? extends Item> itemClass = item.getClass();

        if (item instanceof BlockItem && itemClass.isAnnotationPresent(UseBlockStateNBT.class)) {
            final ModelManager modelManager = this.iGetModelManager();
            final BlockModelShaper blockModelShaper = modelManager.getBlockModelShaper();
            final CompoundTag nbt = pStack.getOrCreateTag();
            final Block block = ((BlockItem) item).getBlock();
            final BlockState pState = UseBlockStateNBT.Companion.loadState(nbt, block);
            final BakedModel blockModel = blockModelShaper.getBlockModel(pState);
            cir.setReturnValue(blockModel);
        }
    }
}