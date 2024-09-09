package bread.mod.breadmod.item.armor

import bread.mod.breadmod.ModMainCommon.MOD_ID
import bread.mod.breadmod.ModMainCommon.modLocation
import dev.architectury.registry.registries.DeferredRegister
import net.minecraft.Util
import net.minecraft.core.Holder
import net.minecraft.core.registries.Registries
import net.minecraft.sounds.SoundEvents
import net.minecraft.world.item.ArmorItem
import net.minecraft.world.item.ArmorMaterial
import net.minecraft.world.item.Items
import net.minecraft.world.item.crafting.Ingredient
import java.util.EnumMap

object ArmorMaterials {
    internal val ARMOR_REGISTRY: DeferredRegister<ArmorMaterial> = DeferredRegister.create(
        MOD_ID, Registries.ARMOR_MATERIAL
    )

    val BREAD: Holder<ArmorMaterial> = ARMOR_REGISTRY.register("bread") {
        ArmorMaterial(
            Util.make(EnumMap(ArmorItem.Type::class.java)) { map ->
                map.put(ArmorItem.Type.BOOTS, 2)
                map.put(ArmorItem.Type.LEGGINGS, 3)
                map.put(ArmorItem.Type.CHESTPLATE, 4)
                map.put(ArmorItem.Type.HELMET, 2)
                map.put(ArmorItem.Type.BODY, 2)
            },
            20, // Enchantability
            SoundEvents.ARMOR_EQUIP_LEATHER,
            { Ingredient.of(Items.BREAD) },
            listOf(
                ArmorMaterial.Layer(modLocation("bread"), "", true),
                ArmorMaterial.Layer(modLocation("bread"), "_overlay", false)
            ),
            0f, // Toughness
            0f // Knockback Resistance
        )
    }

    val RF_BREAD: Holder<ArmorMaterial> = ARMOR_REGISTRY.register("rf_bread") {
        ArmorMaterial(
            Util.make(EnumMap(ArmorItem.Type::class.java)) { map ->
                map.put(ArmorItem.Type.BOOTS, 4)
                map.put(ArmorItem.Type.LEGGINGS, 6)
                map.put(ArmorItem.Type.CHESTPLATE, 8)
                map.put(ArmorItem.Type.HELMET, 4)
                map.put(ArmorItem.Type.BODY, 6)
            },
            20, // Enchantability
            SoundEvents.ARMOR_EQUIP_LEATHER,
            { Ingredient.of(Items.BREAD) },
            listOf(
                ArmorMaterial.Layer(modLocation("rf_bread"), "", false)
            ),
            1f, // Toughness
            0.5f // Knockback Resistance
        )
    }
}