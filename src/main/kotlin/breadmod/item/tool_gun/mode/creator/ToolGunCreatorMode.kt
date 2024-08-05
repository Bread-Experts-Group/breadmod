package breadmod.item.tool_gun.mode.creator

import breadmod.ModMain
import breadmod.client.render.tool_gun.ToolGunAnimationHandler
import breadmod.datagen.tool_gun.BreadModToolGunModeProvider
import breadmod.datagen.tool_gun.BreadModToolGunModeProvider.Companion.TOOL_GUN_DEF
import breadmod.menu.item.ToolGunCreatorMenu
import breadmod.item.tool_gun.IToolGunMode
import breadmod.item.tool_gun.IToolGunMode.Companion.playToolGunSound
import breadmod.util.RayMarchResult.Companion.rayMarchBlock
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.MenuProvider
import net.minecraft.world.effect.MobEffect
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.ai.attributes.Attributes
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.phys.Vec3
import net.minecraftforge.network.NetworkHooks

@Suppress("MemberVisibilityCanBePrivate")
internal class ToolGunCreatorMode : IToolGunMode, MenuProvider {
    private var customEntityName: String? = null
    private var entityString: String = "zombie"
    private var entityType: EntityType<*>? = getEntityFromString(entityString)

    private var entityHealth: Double? = null
    private var entitySpeed: Double? = null

    // First Int: Duration, Second Int: Amplifier
    private var entityEffect: MutableList<Triple<MobEffect, Int, Int>>? = null

    private var helmetSlot: ItemStack? = null
    private var chestplateSlot: ItemStack? = null
    private var leggingsSlot: ItemStack? = null
    private var bootsSlot: ItemStack? = null

    private var mainHandSlot: ItemStack? = null
    private var offHandSlot: ItemStack? = null

    override fun action(
        pLevel: Level,
        pPlayer: Player,
        pGunStack: ItemStack,
        pControl: BreadModToolGunModeProvider.Control
    ) {
        if(pControl.id == "screen" && !pLevel.isClientSide) {
            NetworkHooks.openScreen(pPlayer as ServerPlayer, this, pPlayer.blockPosition())
        } else if(pLevel is ServerLevel && pControl.id == "use") {
            println("added entity")
            pLevel.rayMarchBlock(pPlayer.eyePosition, Vec3.directionFromRotation(pPlayer.xRot, pPlayer.yRot), 100.0, false)?.let { ray ->
                val finalEntity = entityType?.create(pLevel) as LivingEntity

                // Health
                entityHealth?.let { health ->
                    finalEntity.getAttribute(Attributes.MAX_HEALTH)?.baseValue = health
                    finalEntity.health = health.toFloat()
                }

                // Speed
                entitySpeed?.let {
                    finalEntity.getAttribute(Attributes.MOVEMENT_SPEED)?.baseValue = it
                    finalEntity.speed = it.toFloat()
                }

                // Armor Slots
                helmetSlot?.let { finalEntity.getSlot(HELMET_SLOT).set(it) }
                chestplateSlot?.let { finalEntity.getSlot(CHESTPLATE_SLOT).set(it) }
                leggingsSlot?.let { finalEntity.getSlot(LEGGINGS_SLOT).set(it) }
                bootsSlot?.let { finalEntity.getSlot(BOOTS_SLOT).set(it) }

                // Item Slots
                mainHandSlot?.let { finalEntity.getSlot(MAINHAND_SLOT).set(it) }
                offHandSlot?.let { finalEntity.getSlot(OFFHAND_SLOT).set(it) }

                // Potion Effects
                entityEffect?.let { it.forEach { (effect, duration, amplifier) ->
                    finalEntity.addEffect(MobEffectInstance(effect, duration, amplifier))
                }}

                customEntityName?.let { finalEntity.customName = Component.literal(it) }

                finalEntity.setPos(Vec3(ray.endPosition.x, ray.endPosition.y + 0.5, ray.endPosition.z))
                finalEntity.addTag("spawned_by_${pPlayer.name.string}")
                playToolGunSound(pLevel, pPlayer.blockPosition())
                pLevel.addFreshEntity(finalEntity)
            }
        } else if (pLevel.isClientSide && pControl.id == "use") ToolGunAnimationHandler.trigger()
    }

    override fun createMenu(pContainerId: Int, pPlayerInventory: Inventory, pPlayer: Player): AbstractContainerMenu =
        ToolGunCreatorMenu(pContainerId, pPlayerInventory)

    override fun getDisplayName(): Component = ModMain.modTranslatable(TOOL_GUN_DEF, "mode", "display_name", "creator")
}