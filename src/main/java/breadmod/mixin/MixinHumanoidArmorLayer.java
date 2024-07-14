package breadmod.mixin;

import breadmod.item.armor.BreadArmorItem;
import breadmod.util.StackColorKt;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HumanoidArmorLayer.class)
abstract class MixinHumanoidArmorLayer<T extends LivingEntity, M extends HumanoidModel<T>, A extends HumanoidModel<T>> extends RenderLayer<T, M> {
    public MixinHumanoidArmorLayer(RenderLayerParent<T, M> pRenderer) {
        super(pRenderer);
    }

    @Invoker(value = "setPartVisibility")
    abstract void iSetPartVisibility(A pModel, EquipmentSlot pSlot);
    @Invoker(value = "getArmorModelHook", remap = false)
    abstract Model iGetArmorModelHook(T entity, ItemStack itemStack, EquipmentSlot slot, A model);
    @Invoker(value = "renderGlint", remap = false)
    abstract void iRenderGlint(PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight, Model pModel);
    @Invoker(value = "renderModel", remap = false)
    abstract void iRenderModel(PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight, ArmorItem pArmorItem, Model pModel, boolean pWithGlint, float pRed, float pGreen, float pBlue, ResourceLocation armorResource);
    @Invoker(value = "getArmorResource", remap = false)
    abstract ResourceLocation iGetArmorResource(Entity entity, ItemStack stack, EquipmentSlot slot, String type);
    @Invoker(value = "usesInnerModel")
    abstract boolean iUsesInnerModel(EquipmentSlot pSlot);

    @Inject(method = "renderArmorPiece", at = @At("HEAD"), cancellable = true)
    private void renderArmorPiece(PoseStack pPoseStack, MultiBufferSource pBuffer, T pLivingEntity, EquipmentSlot pSlot, int pPackedLight, A pModel, CallbackInfo callbackInfo) {
        ItemStack itemStack = pLivingEntity.getItemBySlot(pSlot);
        Item item = itemStack.getItem();
        // TODO: Better way to write this
        if(item instanceof BreadArmorItem && ((BreadArmorItem) item).getEquipmentSlot() == pSlot) {
            this.getParentModel().copyPropertiesTo(pModel);
            this.iSetPartVisibility(pModel, pSlot);
            Model model = iGetArmorModelHook(pLivingEntity, itemStack, pSlot, pModel);
            boolean flag = this.iUsesInnerModel(pSlot);

            float[] components = StackColorKt.getColor(itemStack, BreadArmorItem.Companion.getBREAD_COLOR()).getRGBComponents(null);
            this.iRenderModel(
                    pPoseStack, pBuffer, pPackedLight, (BreadArmorItem) item, model, flag, components[0], components[1], components[2],
                    this.iGetArmorResource(pLivingEntity, itemStack, pSlot, null)
            );
            this.iRenderModel(
                    pPoseStack, pBuffer, pPackedLight, (BreadArmorItem) item, model, flag, 1.0F, 1.0F, 1.0F,
                    this.iGetArmorResource(pLivingEntity, itemStack, pSlot, "overlay")
            );

            if(itemStack.hasFoil()) this.iRenderGlint(pPoseStack, pBuffer, pPackedLight, model);

            callbackInfo.cancel();
        }
    }
}
