package breadmod.mixin

import breadmod.item.armor.BreadArmorItem
import breadmod.util.getColor
import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.model.HumanoidModel
import net.minecraft.client.model.Model
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.entity.RenderLayerParent
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer
import net.minecraft.client.renderer.entity.layers.RenderLayer
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EquipmentSlot
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.item.ArmorItem
import net.minecraft.world.item.ItemStack
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.gen.Invoker
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo

@Mixin(HumanoidArmorLayer::class)
abstract class MixinHumanoidArmorLayer<T: LivingEntity, M: HumanoidModel<T>, A: HumanoidModel<T>>(pRenderer: RenderLayerParent<T, M>) : RenderLayer<T, M>(pRenderer) {
    @Invoker("setPartVisibility") abstract fun iSetPartVisibility(pModel: A, pSlot: EquipmentSlot)
    @Invoker("getArmorModelHook") abstract fun iGetArmorModelHook(entity: T, itemStack: ItemStack, slot: EquipmentSlot, model: A): Model
    @Invoker("renderGlint"      ) abstract fun iRenderGlint      (pPoseStack: PoseStack, pBuffer: MultiBufferSource, pPackedLight: Int, pModel: Model)
    @Invoker("renderModel"      ) abstract fun iRenderModel      (pPoseStack: PoseStack, pBuffer: MultiBufferSource, pPackedLight: Int, pArmorItem: ArmorItem, pModel: Model, pWithGlint: Boolean, pRed: Float, pGreen: Float, pBlue: Float, armorResource: ResourceLocation)
    @Invoker("getArmorResource" ) abstract fun iGetArmorResource (entity: Entity, stack: ItemStack, slot: EquipmentSlot, type: String?): ResourceLocation
    @Invoker("usesInnerModel"   ) abstract fun iUsesInnerModel   (pSlot: EquipmentSlot): Boolean

    @Inject(method = ["renderArmorPiece"], at = [At("HEAD")], cancellable = true)
    private fun renderArmorPiece(pPoseStack: PoseStack, pBuffer: MultiBufferSource, pLivingEntity: T, pSlot: EquipmentSlot, pPackedLight: Int, pModel: A, callbackInfo: CallbackInfo) {
        val itemStack: ItemStack = pLivingEntity.getItemBySlot(pSlot)
        val item = itemStack.item
        if(item is BreadArmorItem && item.equipmentSlot == pSlot) {
            this.parentModel.copyPropertiesTo(pModel)
            this.iSetPartVisibility(pModel, pSlot)
            val model: Model = iGetArmorModelHook(pLivingEntity, itemStack, pSlot, pModel)
            val flag: Boolean = this.iUsesInnerModel(pSlot)

            val components = itemStack.getColor(BreadArmorItem.BREAD_COLOR).getRGBColorComponents(null)
            this.iRenderModel(
                pPoseStack, pBuffer, pPackedLight, item, model, flag, components[0], components[1], components[2],
                this.iGetArmorResource(pLivingEntity, itemStack, pSlot, null)
            )
            this.iRenderModel(
                pPoseStack, pBuffer, pPackedLight, item, model, flag, 1.0f , 1.0f, 1.0f,
                this.iGetArmorResource(pLivingEntity, itemStack, pSlot, "overlay")
            )

            if (itemStack.hasFoil()) this.iRenderGlint(pPoseStack, pBuffer, pPackedLight, model)

            callbackInfo.cancel()
        }
    }
}