package bread.mod.breadmod.mixin.client;

import bread.mod.breadmod.event.BlockEntityWithoutLevelRendererEvent;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BlockEntityWithoutLevelRenderer.class)
class BlockEntityWithoutLevelRendererMixin {
    @Inject(method = "renderByItem", at = @At("TAIL"))
    private void renderByItem(
            final ItemStack stack, final ItemDisplayContext displayContext, final PoseStack poseStack,
            final MultiBufferSource buffer, final int packedLight, final int packedOverlay,
            final CallbackInfo ci
    ) {
        final BlockEntityWithoutLevelRendererEvent eventInvoker = BlockEntityWithoutLevelRendererEvent.RENDER_ITEMS_EVENT.invoker();
        eventInvoker.renderByItem(stack, displayContext, poseStack, buffer, packedLight, packedOverlay);
    }
}