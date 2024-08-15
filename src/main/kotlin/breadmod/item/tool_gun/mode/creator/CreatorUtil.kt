package breadmod.item.tool_gun.mode.creator

import net.minecraft.resources.ResourceLocation
import net.minecraft.world.effect.MobEffect
import net.minecraft.world.entity.EntityType
import net.minecraft.world.item.Item
import net.minecraftforge.registries.ForgeRegistries
import kotlin.jvm.optionals.getOrNull

const val HELMET_SLOT = 103
const val CHESTPLATE_SLOT = 102
const val LEGGINGS_SLOT = 101
const val BOOTS_SLOT = 100
const val MAINHAND_SLOT = 98
const val OFFHAND_SLOT = 99

/** Returns [EntityType.PIG] if provided [id] does not exist */
fun getEntityFromString(id: String): EntityType<*> =
    ForgeRegistries.ENTITY_TYPES.getValue(EntityType.byString(id).getOrNull()?.let { EntityType.getKey(it) })
        ?: EntityType.PIG

/** Returns null if provided [id] or [namespace] does not exist */
fun mobEffectFromString(namespace: String = "minecraft", id: String): MobEffect? =
    ForgeRegistries.MOB_EFFECTS.getValue(ResourceLocation(namespace, id))

fun itemToString(item: Item) = ForgeRegistries.ITEMS.getKey(item).toString()
fun effectToString(effect: MobEffect) = ForgeRegistries.MOB_EFFECTS.getKey(effect).toString()