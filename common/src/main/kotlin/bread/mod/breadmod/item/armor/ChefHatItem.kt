package bread.mod.breadmod.item.armor

import bread.mod.breadmod.client.sound.MachSoundInstance
import bread.mod.breadmod.registry.sound.ModSounds
import bread.mod.breadmod.util.render.playerRenderTest
import bread.mod.breadmod.util.render.rgMinecraft
import dev.architectury.platform.Platform
import net.minecraft.client.player.LocalPlayer
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.effect.MobEffects
import net.minecraft.world.entity.Entity
import net.minecraft.world.item.ArmorItem
import net.minecraft.world.item.ArmorMaterials
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level

// todo model and texture, colorable armor type
class ChefHatItem : ArmorItem(ArmorMaterials.IRON, Type.HELMET, Properties()) {
    var sprintTimer = 0

    override fun inventoryTick(stack: ItemStack, level: Level, entity: Entity, slotId: Int, isSelected: Boolean) {
        val platformSlot = if (Platform.isForgeLike()) 39 else 3
        if (!level.isClientSide && slotId == platformSlot) {
            if (entity is ServerPlayer && entity.isSprinting) {
                entity.addEffect(MobEffectInstance(MobEffects.MOVEMENT_SPEED, 10, 1, false, false))
                if (sprintTimer >= 20) {
                    entity.addEffect(MobEffectInstance(MobEffects.MOVEMENT_SPEED, 10, 2, false, false))
                }
                if (sprintTimer >= 40) {
                    entity.addEffect(MobEffectInstance(MobEffects.MOVEMENT_SPEED, 10, 5, false, false))
                }
                if (sprintTimer >= 70) {
                    entity.addEffect(MobEffectInstance(MobEffects.MOVEMENT_SPEED, 10, 9, false, false))
                }
                sprintTimer++
            } else if (!entity.isSprinting) {
                sprintTimer = 0
            }
        } else {
            if (entity.isSprinting && entity is LocalPlayer && slotId == platformSlot) {
                val machOneSound = MachSoundInstance(ModSounds.MACH_ONE.get(), entity)
                val machTwoSound = MachSoundInstance(ModSounds.MACH_TWO.get(), entity)
                val machThreeSound = MachSoundInstance(ModSounds.MACH_THREE.get(), entity)
                val machFourSound = MachSoundInstance(ModSounds.MACH_FOUR.get(), entity)
                val soundManager = rgMinecraft.soundManager

                if (sprintTimer == 1) {
                    println("sound trigger")
                    soundManager.play(machOneSound)
                }
                if (sprintTimer == 21) {
                    soundManager.play(machTwoSound)
                } else if (sprintTimer > 21) machOneSound.allowedToLoop = false
                if (sprintTimer == 41) {
                    soundManager.play(machThreeSound)
                } else if (sprintTimer > 41) machTwoSound.allowedToLoop = false
                if (sprintTimer == 71) {
                    soundManager.play(machFourSound)
                } else if (sprintTimer > 71) machThreeSound.allowedToLoop = false
                if (sprintTimer >= 20) {
                    val partialTick = rgMinecraft.timer.realtimeDeltaTicks
//                    val random = RandomSource.create()
                    playerRenderTest(
                        entity,
                        entity.x,
                        entity.y,
                        entity.z,
                        -entity.rotationVector.y,
                        entity.walkAnimation.position(),
//                        entity.getViewYRot(partialTick),
//                        entity.getViewXRot(partialTick),
//                        random.nextBoolean()
                    )
                }
            }
        }
        super.inventoryTick(stack, level, entity, slotId, isSelected)
    }
}