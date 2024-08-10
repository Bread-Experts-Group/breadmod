package breadmod.item.tool_gun.mode.creator

import breadmod.ModMain
import breadmod.client.render.tool_gun.ToolGunAnimationHandler
import breadmod.client.screen.tool_gun.creator.ToolGunCreatorScreen
import breadmod.datagen.tool_gun.BreadModToolGunModeProvider
import breadmod.datagen.tool_gun.BreadModToolGunModeProvider.Companion.TOOL_GUN_DEF
import breadmod.menu.item.ToolGunCreatorMenu
import breadmod.item.tool_gun.IToolGunMode
import breadmod.item.tool_gun.IToolGunMode.Companion.playToolGunSound
import breadmod.util.RayMarchResult.Companion.rayMarchBlock
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.ChatFormatting
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.util.GsonHelper
import net.minecraft.world.MenuProvider
import net.minecraft.world.effect.MobEffect
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.effect.MobEffects
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.ai.attributes.Attributes
import net.minecraft.world.entity.animal.Pig
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.item.ItemDisplayContext
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.phys.Vec3
import net.minecraftforge.network.NetworkHooks
import net.minecraftforge.registries.ForgeRegistries

@Suppress("MemberVisibilityCanBePrivate")
internal class ToolGunCreatorMode : IToolGunMode, MenuProvider {
    companion object {
        val objects: MutableMap<ServerPlayer, String> = mutableMapOf()
    }

    private fun LivingEntity.setEntitySlot(json: JsonElement, slot: Int, memberName: String) =
        getSlot(slot).set(GsonHelper.convertToItem(json, memberName).defaultInstance)

    private fun getMobEffect(location: String): MobEffect =
        ForgeRegistries.MOB_EFFECTS.getValue(ResourceLocation(location)) ?: MobEffects.REGENERATION

    private fun constructEntity(player: ServerPlayer, pLevel: Level, pos: Vec3): LivingEntity {
        val data = objects[player] ?: return Pig(EntityType.PIG, pLevel).also {
            it.setPos(Vec3(pos.x, pos.y, pos.z))
            it.addTag("spawned_by_${player.name.string}")
            player.sendSystemMessage(
                Component.literal("WARNING: No creator mode data for ${player.name.string} found.")
                    .withStyle(ChatFormatting.RED)
            )
            player.sendSystemMessage(
                Component.literal("[Click here to crash]")
                    .withStyle(ChatFormatting.BLUE, ChatFormatting.ITALIC, ChatFormatting.UNDERLINE)
            )
        }
        val jsonData = Gson().fromJson(data, JsonObject::class.java)
        val finalEntity = getEntityFromString(jsonData.get("entity").asString)?.create(pLevel) as LivingEntity

        finalEntity.addTag("spawned_by_${player.name.string}")

        jsonData.asMap().forEach { (jsonKey, jsonValue) ->
            when (jsonKey) {
                "effects" -> {
                    jsonValue.asJsonObject.asMap().forEach { (effectKey, effectValue) ->
                        var duration = 0
                        var amplifier = 0

                        effectValue.asJsonObject.asMap().forEach { (key, value) ->
                            when (key) {
                                "duration" -> duration = value.asInt
                                "amplifier" -> amplifier = value.asInt
                            }
                        }

                        finalEntity.addEffect(MobEffectInstance(
                            getMobEffect(effectKey.toString()),
                            duration, amplifier
                        ))
                    }
                }
                "entity_health" -> {
                    finalEntity.getAttribute(Attributes.MAX_HEALTH)?.baseValue = jsonValue.asDouble
                    finalEntity.health = jsonValue.asDouble.toFloat()
                }
                "entity_speed" -> {
                    finalEntity.getAttribute(Attributes.MOVEMENT_SPEED)?.baseValue = jsonValue.asDouble
                    finalEntity.speed = jsonValue.asDouble.toFloat()
                }
                "custom_entity_name" -> finalEntity.customName = Component.literal(jsonValue.asString)
                "helmet" -> finalEntity.setEntitySlot(jsonValue, HELMET_SLOT, "helmet")
                "chestplate" -> finalEntity.setEntitySlot(jsonValue, CHESTPLATE_SLOT, "chestplate")
                "leggings" -> finalEntity.setEntitySlot(jsonValue, LEGGINGS_SLOT, "leggings")
                "boots" -> finalEntity.setEntitySlot(jsonValue, BOOTS_SLOT, "boots")
                "main_hand" -> finalEntity.setEntitySlot(jsonValue, MAINHAND_SLOT, "main_hand")
                "off_hand" -> finalEntity.setEntitySlot(jsonValue, OFFHAND_SLOT, "off_hand")
            }
        }

        finalEntity.setPos(Vec3(pos.x, pos.y, pos.z))
        return finalEntity
    }

    override fun action(
        pLevel: Level,
        pPlayer: Player,
        pGunStack: ItemStack,
        pControl: BreadModToolGunModeProvider.Control
    ) {
        if(pControl.id == "screen" && !pLevel.isClientSide) {
            NetworkHooks.openScreen(pPlayer as ServerPlayer, this, pPlayer.blockPosition())
        } else if(pLevel is ServerLevel && pControl.id == "use") {
            objects.forEach { (key, value) ->
                println("key: $key, value: $value")
            }

            println("added entity")
            pLevel.rayMarchBlock(pPlayer.eyePosition, Vec3.directionFromRotation(pPlayer.xRot, pPlayer.yRot), 100.0, false)?.let { ray ->
                playToolGunSound(pLevel, pPlayer.blockPosition())
                pLevel.addFreshEntity(
                    constructEntity(pPlayer as ServerPlayer, pLevel, Vec3(
                        ray.endPosition.x, ray.endPosition.y + 0.5, ray.endPosition.z)
                    )
                )
            }
        } else if (pLevel.isClientSide && pControl.id == "use") ToolGunAnimationHandler.trigger()
    }

    override fun render(
        pGunStack: ItemStack,
        pDisplayContext: ItemDisplayContext,
        pPoseStack: PoseStack,
        pBuffer: MultiBufferSource,
        pPackedLight: Int,
        pPackedOverlay: Int
    ) {
        val jsonData = Gson().fromJson(ToolGunCreatorScreen.finalData, JsonObject::class.java)
//        println(ToolGunCreatorScreen.finalData)
    }

    override fun createMenu(pContainerId: Int, pPlayerInventory: Inventory, pPlayer: Player): AbstractContainerMenu =
        ToolGunCreatorMenu(pContainerId, pPlayerInventory)

    override fun getDisplayName(): Component = ModMain.modTranslatable(TOOL_GUN_DEF, "mode", "display_name", "creator")
}