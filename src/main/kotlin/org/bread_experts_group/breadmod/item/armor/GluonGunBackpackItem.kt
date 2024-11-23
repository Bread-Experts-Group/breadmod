package org.bread_experts_group.breadmod.item.armor

import net.minecraft.client.model.HumanoidModel
import net.minecraft.world.InteractionHand
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.item.ArmorItem
import net.minecraft.world.item.ItemStack
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions

class GluonGunBackpackItem : ArmorItem(
    ModArmorMaterials.GLUON_GUN_BACKPACK,
    Type.CHESTPLATE,
    Properties().stacksTo(1)
) {
    class GluonGunExtensions : IClientItemExtensions {
        override fun getArmPose(
            entityLiving: LivingEntity,
            hand: InteractionHand,
            itemStack: ItemStack
        ): HumanoidModel.ArmPose = HumanoidModel.ArmPose.BOW_AND_ARROW
    }
}