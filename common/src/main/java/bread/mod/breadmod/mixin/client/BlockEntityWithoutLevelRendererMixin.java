package bread.mod.breadmod.mixin.client;

import bread.mod.breadmod.client.render.ToolGunItemRenderer;
import bread.mod.breadmod.registry.item.ModItems;
import com.mojang.blaze3d.vertex.PoseStack;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BlockEntityWithoutLevelRenderer.class)
class BlockEntityWithoutLevelRendererMixin {
    @Inject(method = "renderByItem", at = @At("HEAD"))
    private void renderByItem(
            final ItemStack stack, final ItemDisplayContext displayContext, final PoseStack poseStack,
            final MultiBufferSource buffer, final int packedLight, final int packedOverlay,
            final CallbackInfo ci
    ) {
        final RegistrySupplier<Item> toolGunItem = ModItems.INSTANCE.getTOOL_GUN();
        if (stack.is(toolGunItem)) {
            final ToolGunItemRenderer toolGunItemRenderer = new ToolGunItemRenderer();
            toolGunItemRenderer.renderByItem(stack, displayContext, poseStack, buffer, packedLight, packedOverlay);
        }
    }
}
