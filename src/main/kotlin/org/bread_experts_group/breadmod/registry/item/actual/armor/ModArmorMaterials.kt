package org.bread_experts_group.breadmod.registry.item.actual.armor

import net.minecraft.Util
import net.minecraft.core.Holder
import net.minecraft.core.registries.Registries
import net.minecraft.sounds.SoundEvents
import net.minecraft.world.item.ArmorItem
import net.minecraft.world.item.ArmorMaterial
import net.minecraft.world.item.Items
import net.minecraft.world.item.crafting.Ingredient
import net.neoforged.neoforge.registries.DeferredRegister
import org.bread_experts_group.breadmod.BreadMod
import org.bread_experts_group.breadmod.BreadMod.Companion.modLocation
import java.util.*

object ModArmorMaterials {
	internal val ARMOR_REGISTRY: DeferredRegister<ArmorMaterial> = DeferredRegister.create(
		Registries.ARMOR_MATERIAL, BreadMod.ID
	)
	val BREAD: Holder<ArmorMaterial> = this.ARMOR_REGISTRY.register("bread") { ->
		ArmorMaterial(
			Util.make(EnumMap(ArmorItem.Type::class.java)) { map ->
				map[ArmorItem.Type.BOOTS] = 2
				map[ArmorItem.Type.LEGGINGS] = 3
				map[ArmorItem.Type.CHESTPLATE] = 4
				map[ArmorItem.Type.HELMET] = 2
				map[ArmorItem.Type.BODY] = 2
			},
			30, // Enchantability
			SoundEvents.ARMOR_EQUIP_LEATHER,
			{ Ingredient.of(Items.BREAD) },
			listOf(
				ArmorMaterial.Layer(modLocation("bread"), "", true),
				ArmorMaterial.Layer(modLocation("bread"), "_overlay", false)
			),
			0f,
			0f
		)
	}
	val RF_BREAD: Holder<ArmorMaterial> = this.ARMOR_REGISTRY.register("rf_bread") { ->
		ArmorMaterial(
			Util.make(EnumMap(ArmorItem.Type::class.java)) { map ->
				map[ArmorItem.Type.BOOTS] = 4
				map[ArmorItem.Type.LEGGINGS] = 6
				map[ArmorItem.Type.CHESTPLATE] = 8
				map[ArmorItem.Type.HELMET] = 4
				map[ArmorItem.Type.BODY] = 6
			},
			20,
			SoundEvents.ARMOR_EQUIP_LEATHER,
			{ Ingredient.of(Items.BREAD) },
			listOf(
				ArmorMaterial.Layer(modLocation("rf_bread"), "", false)
			),
			1f,
			0.5f
		)
	}
	val CHEF: Holder<ArmorMaterial> = this.ARMOR_REGISTRY.register("chef") { ->
		ArmorMaterial(
			Util.make(EnumMap(ArmorItem.Type::class.java)) { map ->
				map[ArmorItem.Type.HELMET] = 2
			},
			10,
			SoundEvents.ARMOR_EQUIP_LEATHER,
			{ Ingredient.of(Items.LEATHER) },
			listOf(),
			1f,
			1f
		)
	}
	val GLUON_GUN_BACKPACK: Holder<ArmorMaterial> = this.ARMOR_REGISTRY.register("gluon_backpack") { ->
		ArmorMaterial(
			Util.make(EnumMap(ArmorItem.Type::class.java)) { map ->
				map[ArmorItem.Type.BODY] = 3
			},
			4,
			SoundEvents.ARMOR_EQUIP_IRON,
			{ Ingredient.of(Items.NETHERITE_INGOT) },
			listOf(),
			2f,
			0f
		)
	}
}