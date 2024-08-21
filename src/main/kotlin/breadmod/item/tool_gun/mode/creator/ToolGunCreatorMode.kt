package breadmod.item.tool_gun.mode.creator

import breadmod.ModMain.modTranslatable
import breadmod.client.render.tool_gun.ToolGunAnimationHandler
import breadmod.client.screen.tool_gun.ToolGunCreatorScreen.Companion.ENTITY_FILE
import breadmod.client.screen.tool_gun.ToolGunCreatorScreen.Companion.lastSaved
import breadmod.client.screen.tool_gun.ToolGunCreatorScreen.Companion.loadedEntity
import breadmod.datagen.tool_gun.BreadModToolGunModeProvider
import breadmod.datagen.tool_gun.BreadModToolGunModeProvider.Companion.TOOL_GUN_DEF
import breadmod.item.tool_gun.IToolGunMode
import breadmod.item.tool_gun.IToolGunMode.Companion.BASE_TOOL_GUN_DATA_PATH
import breadmod.item.tool_gun.IToolGunMode.Companion.playToolGunSound
import breadmod.menu.item.ToolGunCreatorMenu
import breadmod.network.PacketHandler.NETWORK
import breadmod.network.common.tool_gun.creator.ToolGunCreatorDataRequestPacket
import breadmod.util.EntitySerializer
import breadmod.util.EntityTypeSerializer
import breadmod.util.MobEffectInstanceSerializer
import breadmod.util.RaycastResult.Companion.blockRaycast
import breadmod.util.json
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.MenuProvider
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.ai.attributes.Attributes
import net.minecraft.world.entity.animal.Pig
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.phys.Vec3
import net.minecraftforge.network.NetworkHooks
import net.minecraftforge.network.PacketDistributor
import java.io.FileOutputStream
import java.nio.file.Path

@Suppress("MemberVisibilityCanBePrivate")
internal class ToolGunCreatorMode : IToolGunMode, MenuProvider {
    companion object {
        val DATA_PATH: Path = BASE_TOOL_GUN_DATA_PATH.resolve("creator")

        fun constructEntity(data: CreatorEntity, level: Level): Entity =
            (data.type.create(level) ?: Pig(EntityType.PIG, level)).also {
                data.customName?.let { name -> it.customName = Component.literal(name) }
                if (it is LivingEntity) {
                    it.getAttribute(Attributes.MAX_HEALTH)?.baseValue = data.health
                    it.health = data.health.toFloat()

                    it.speed = data.speed.toFloat()
                    it.getAttribute(Attributes.MOVEMENT_SPEED)?.baseValue = data.speed

                    data.effects.values.forEach { instance -> it.addEffect(instance) }
                    //it.scale = data.scale.toFloat()
                }
            }
    }

    @Serializable
    data class CreatorEntity(
        var customName: String?,
        @Serializable(with = EntityTypeSerializer::class)
        var type: EntityType<out @Serializable(with = EntitySerializer::class) Entity>,

        var health: Double,
        var speed: Double,
        var effects: MutableMap<String, @Serializable(with = MobEffectInstanceSerializer::class) MobEffectInstance>,

        var scale: Double
    )

    override fun action(
        pLevel: Level,
        pPlayer: Player,
        pGunStack: ItemStack,
        pControl: BreadModToolGunModeProvider.Control
    ) {
        if (pLevel is ServerLevel) {
            if (pControl.id == "screen") {
                NetworkHooks.openScreen(pPlayer as ServerPlayer, this, pPlayer.blockPosition())
            } else if (pControl.id == "use") {
                pLevel.blockRaycast(
                    pPlayer.eyePosition,
                    Vec3.directionFromRotation(pPlayer.xRot, pPlayer.yRot),
                    100.0, false
                )?.let { ray ->
                    val player = pPlayer as ServerPlayer

                    ToolGunCreatorDataRequestPacket.requested[pPlayer.uuid] = { data ->
                        playToolGunSound(pLevel, pPlayer.blockPosition())

                        val newEntity = constructEntity(data, pLevel)
                        newEntity.setPos(ray.endPosition)
                        newEntity.addTag("spawned_by_${pPlayer.gameProfile.name}")
                        newEntity.addTag("spawned_by_tool_gun")
                        pLevel.addFreshEntity(newEntity)
                    }

                    val data = ToolGunCreatorDataRequestPacket.data[pPlayer.uuid]
                    NETWORK.send(
                        PacketDistributor.PLAYER.with { player },
                        ToolGunCreatorDataRequestPacket(if (data != null) json.encodeToString(data).hashCode() else 0, null)
                    )
                }
            }
        } else if (pControl.id == "use") {
            ToolGunAnimationHandler.trigger()
            val toSave = json.encodeToString(loadedEntity)
            if (toSave != lastSaved) {
                FileOutputStream(ENTITY_FILE).also {
                    it.write(toSave.encodeToByteArray())
                    it.close()
                }
                lastSaved = toSave
            }
        }
    }

    override fun createMenu(pContainerId: Int, pPlayerInventory: Inventory, pPlayer: Player): AbstractContainerMenu =
        ToolGunCreatorMenu(pContainerId, pPlayerInventory)

    override fun getDisplayName(): Component = modTranslatable(TOOL_GUN_DEF, "mode", "display_name", "creator")
}