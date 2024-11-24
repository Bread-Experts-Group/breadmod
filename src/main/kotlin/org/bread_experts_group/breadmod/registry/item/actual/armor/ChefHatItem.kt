package org.bread_experts_group.breadmod.registry.item.actual.armor

import org.bread_experts_group.breadmod.client.model.ChefHatModel
import org.bread_experts_group.breadmod.client.render.entity.layers.ChefHatArmorLayer
import net.minecraft.ChatFormatting
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.sounds.SoundSource
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.effect.MobEffects
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.item.ArmorItem
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.TooltipFlag
import net.minecraft.world.level.Level
import net.minecraft.world.phys.AABB
import net.minecraft.world.phys.Vec3
import net.neoforged.neoforge.network.PacketDistributor
import org.bread_experts_group.breadmod.BreadMod.Companion.modTranslatable
import org.bread_experts_group.breadmod.network.clientbound.MachTrailPacket
import org.bread_experts_group.breadmod.registry.sound.ModSounds

/**
 * Chef Hat, inspired from the game Pizza Tower by Tour De Pizza.
 *
 * @author Logan Mclean
 * @since 1.0.0
 * @see ChefHatArmorLayer
 * @see ChefHatModel
 */
class ChefHatItem : ArmorItem(ModArmorMaterials.CHEF, Type.HELMET, Properties().stacksTo(1)) {
    private var sprintTimer = 0
    private var machStage = 0
    private var isRunning = false

    override fun appendHoverText(
        stack: ItemStack,
        context: TooltipContext,
        tooltipComponents: MutableList<Component>,
        tooltipFlag: TooltipFlag
    ) {
        tooltipComponents.add(
            modTranslatable("item", "chef_hat", "tooltip")
                .withStyle(ChatFormatting.LIGHT_PURPLE)
        )
    }

    // todo figure out why other players using the hat cancel out the first player also using the hat
    override fun inventoryTick(stack: ItemStack, level: Level, entity: Entity, slotId: Int, isSelected: Boolean) {
//        LogManager.getLogger().info("${entity.name.string} mach stage: $machStage, sprint timer: $sprintTimer")
        if (!level.isClientSide && level is ServerLevel && slotId == 39) {
            if (entity is ServerPlayer && entity.isSprinting) {

                if (machStage >= 3) {
                    val aabb = AABB(entity.x, entity.y, entity.z, entity.x, entity.y, entity.z).inflate(0.5, 0.5, 0.5)
                    level.getEntities(null, aabb).filter { it != entity }.forEach { target ->
                        if (target is LivingEntity && !target.isDeadOrDying) {
                            fun rand() = (target.random.nextDouble() - 0.5) * 1.1
                            level.playSound(
                                null,
                                target.x, target.y, target.z,
                                ModSounds.PUNCH.get(),
                                SoundSource.AMBIENT
                            )
                            level.playSound(
                                null,
                                target.x, target.y, target.z,
                                ModSounds.KILL_ENEMY.get(),
                                SoundSource.AMBIENT
                            )
                            target.addDeltaMovement(Vec3(0.0, 2.0, 0.0))
                            target.knockback(5.0, entity.deltaMovement.x, entity.deltaMovement.z)
                            target.kill()
                            level.sendParticles(
                                ParticleTypes.CLOUD,
                                target.x, target.y, target.z,
                                50,
                                rand(), target.random.nextDouble(), rand(),
                                0.5
                            )
                        }
                    }
                }

                val machSpeed = when (machStage) {
                    1 -> 1
                    2 -> 2
                    3 -> 5
                    4 -> 9
                    else -> 0
                }
                if (sprintTimer < 20) machStage = 1
                if (sprintTimer >= 20) machStage = 2
                if (sprintTimer >= 40) machStage = 3
                if (sprintTimer >= 70) machStage = 4

                if (sprintTimer == 1 && !isRunning) {
                    PacketDistributor.sendToPlayersTrackingChunk(
                        level,
                        entity.chunkPosition(),
                        MachTrailPacket(entity.gameProfile)
                    )
                    isRunning = true
                }

                entity.addEffect(MobEffectInstance(MobEffects.MOVEMENT_SPEED, 10, machSpeed, false, false))
                sprintTimer++
            } else if (!entity.isSprinting && entity is ServerPlayer) {
                machStage = 0
                sprintTimer = 0
                if (isRunning) isRunning = false
            }
        }
    }
}