package org.bread_experts_group.breadmod.registry.item

import net.minecraft.ChatFormatting
import net.minecraft.core.component.DataComponents
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.EquipmentSlotGroup
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.ai.attributes.AttributeModifier
import net.minecraft.world.entity.ai.attributes.Attributes
import net.minecraft.world.food.FoodProperties
import net.minecraft.world.item.*
import net.minecraft.world.item.DiggerItem.BASE_ATTACK_DAMAGE_ID
import net.minecraft.world.item.DiggerItem.BASE_ATTACK_SPEED_ID
import net.minecraft.world.item.alchemy.PotionContents
import net.minecraft.world.item.component.ItemAttributeModifiers
import net.minecraft.world.level.Level
import net.neoforged.neoforge.registries.DeferredItem
import net.neoforged.neoforge.registries.DeferredRegister
import org.bread_experts_group.breadmod.BreadMod
import org.bread_experts_group.breadmod.BreadMod.Companion.modTranslatable
import org.bread_experts_group.breadmod.item.BreadAmuletItem
import org.bread_experts_group.breadmod.item.TestBreadItem
import org.bread_experts_group.breadmod.item.UltimateBreadItem
import org.bread_experts_group.breadmod.item.armor.BreadArmorItem
import org.bread_experts_group.breadmod.item.armor.ChefHatItem
import org.bread_experts_group.breadmod.item.armor.ModArmorMaterials
import org.bread_experts_group.breadmod.item.tool.KnifeItem
import org.bread_experts_group.breadmod.item.tool_gun.ToolGunItem
import kotlin.reflect.KClass

object ModItems {
    val ITEM_REGISTRY: DeferredRegister.Items = DeferredRegister.createItems(BreadMod.ID)
//    fun getLocation(item: Item) = BuiltInRegistries.ITEM.getKey(item)

    val FLOUR: DeferredItem<Item> = ITEM_REGISTRY.registerSimpleItem("flour")
    val TEST_RECORD: DeferredItem<Item> = ITEM_REGISTRY.register("music_disc_secret_hoppin") { ->
        Item(Item.Properties().jukeboxPlayable(ModRecords.TEST_SOUND).stacksTo(1).rarity(Rarity.RARE))
    }

    val CHEF_HAT: DeferredItem<ChefHatItem> = ITEM_REGISTRY.register("chef_hat", ::ChefHatItem)
    val TOOL_GUN: DeferredItem<ToolGunItem> = ITEM_REGISTRY.register("tool_gun", ::ToolGunItem)

    val TEST_BREAD: DeferredItem<TestBreadItem> = ITEM_REGISTRY.register("test_bread", ::TestBreadItem)
    val ULTIMATE_BREAD: DeferredItem<UltimateBreadItem> = ITEM_REGISTRY.register("ultimate_bread", ::UltimateBreadItem)

    val BREAD_SHIELD: DeferredItem<ShieldItem> = ITEM_REGISTRY.register("bread_shield") { ->
        object : ShieldItem(Properties().stacksTo(1).durability(256)) {
            override fun appendHoverText(
                stack: ItemStack,
                context: TooltipContext,
                tooltipComponents: MutableList<Component>,
                tooltipFlag: TooltipFlag
            ) {
                tooltipComponents.add(modTranslatable("item", "bread_shield", "tooltip").withStyle(ChatFormatting.AQUA))
            }
        }
    }

    val DOPED_BREAD: DeferredItem<Item> = ITEM_REGISTRY.register("doped_bread") { ->
        object : Item(
            Properties()
                .food(FoodProperties.Builder().nutrition(6).build())
                .component(DataComponents.POTION_CONTENTS, PotionContents.EMPTY)
        ) {
            override fun finishUsingItem(stack: ItemStack, level: Level, livingEntity: LivingEntity): ItemStack {
                stack.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY)
                    .forEachEffect { livingEntity.addEffect(it) }
                return super.finishUsingItem(stack, level, livingEntity)
            }

            override fun appendHoverText(
                stack: ItemStack,
                context: TooltipContext,
                tooltipComponents: MutableList<Component>,
                tooltipFlag: TooltipFlag
            ) {
                val potion = stack.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY)

                tooltipComponents.add(modTranslatable("item", "doped_bread", "tooltip").withStyle(ChatFormatting.GRAY))
                PotionContents.addPotionTooltip(potion.allEffects, tooltipComponents::add, 1f, context.tickRate())
                super.appendHoverText(stack, context, tooltipComponents, tooltipFlag)
            }
        }
    }

    val TOASTED_BREAD: DeferredItem<Item> = ITEM_REGISTRY.register("toasted_bread") { ->
        Item(
            Item.Properties().food(FoodProperties.Builder().nutrition(7).saturationModifier(0.8f).build())
        )
    }

    val BREAD_SLICE: DeferredItem<Item> = ITEM_REGISTRY.register("bread_slice") { ->
        Item(Item.Properties().food(FoodProperties.Builder().nutrition(2).fast().build()))
    }

    val DOUGH: DeferredItem<Item> = ITEM_REGISTRY.registerSimpleItem("dough")
    val DIE: DeferredItem<Item> = ITEM_REGISTRY.registerSimpleItem("die")

    val KNIFE: DeferredItem<KnifeItem> = ITEM_REGISTRY.register("knife") { -> KnifeItem(Tiers.IRON) }

    val BAGEL: DeferredItem<Item> = ITEM_REGISTRY.register("bagel") { ->
        Item(Item.Properties().food(FoodProperties.Builder().nutrition(4).saturationModifier(0.2f).build()))
    }

    val HALF_BAGEL: DeferredItem<Item> = ITEM_REGISTRY.register("half_bagel") { ->
        object : Item(
            Properties().food(FoodProperties.Builder().nutrition(2).saturationModifier(0.1f).fast().build())
        ) {
            override fun appendHoverText(
                stack: ItemStack,
                context: TooltipContext,
                tooltipComponents: MutableList<Component>,
                tooltipFlag: TooltipFlag
            ) {
                tooltipComponents.add(
                    modTranslatable("item", "half_bagel", "description")
                        .withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC)
                )
            }
        }
    }

    val ALUMINA: DeferredItem<Item> =
        ITEM_REGISTRY.registerSimpleItem("alumina")
    val BREAD_AMULET: DeferredItem<BreadAmuletItem> =
        ITEM_REGISTRY.register("bread_amulet") { -> BreadAmuletItem(500) }

    // Bread Armor
    val BREAD_HELMET: DeferredItem<BreadArmorItem> =
        ITEM_REGISTRY.register("bread_helmet") { -> BreadArmorItem(ArmorItem.Type.HELMET) }
    val BREAD_CHESTPLATE: DeferredItem<BreadArmorItem> =
        ITEM_REGISTRY.register("bread_chestplate") { -> BreadArmorItem(ArmorItem.Type.CHESTPLATE) }
    val BREAD_LEGGINGS: DeferredItem<BreadArmorItem> =
        ITEM_REGISTRY.register("bread_leggings") { -> BreadArmorItem(ArmorItem.Type.LEGGINGS) }
    val BREAD_BOOTS: DeferredItem<BreadArmorItem> =
        ITEM_REGISTRY.register("bread_boots") { -> BreadArmorItem(ArmorItem.Type.BOOTS) }

    // RF Bread Armor
    val RF_BREAD_HELMET: DeferredItem<ArmorItem> = ITEM_REGISTRY.register("reinforced_bread_helmet") { ->
        ArmorItem(ModArmorMaterials.RF_BREAD, ArmorItem.Type.HELMET, Item.Properties())
    }
    val RF_BREAD_CHESTPLATE: DeferredItem<ArmorItem> = ITEM_REGISTRY.register("reinforced_bread_chestplate") { ->
        ArmorItem(ModArmorMaterials.RF_BREAD, ArmorItem.Type.CHESTPLATE, Item.Properties())
    }
    val RF_BREAD_LEGGINGS: DeferredItem<ArmorItem> = ITEM_REGISTRY.register("reinforced_bread_leggings") { ->
        ArmorItem(ModArmorMaterials.RF_BREAD, ArmorItem.Type.LEGGINGS, Item.Properties())
    }
    val RF_BREAD_BOOTS: DeferredItem<ArmorItem> = ITEM_REGISTRY.register("reinforced_bread_boots") { ->
        ArmorItem(ModArmorMaterials.RF_BREAD, ArmorItem.Type.BOOTS, Item.Properties())
    }

    // Tools
    private fun toolProperties(tier: ToolTier, damageModifier: Double, speedModifier: Double) =
        Item.Properties()
            .attributes(
                ItemAttributeModifiers.builder()
                    .add(
                        Attributes.ATTACK_DAMAGE,
                        AttributeModifier(
                            BASE_ATTACK_DAMAGE_ID,
                            damageModifier + tier.attackDamageBonus,
                            AttributeModifier.Operation.ADD_VALUE
                        ), EquipmentSlotGroup.MAINHAND
                    )
                    .add(
                        Attributes.ATTACK_SPEED,
                        AttributeModifier(
                            BASE_ATTACK_SPEED_ID,
                            speedModifier,
                            AttributeModifier.Operation.ADD_VALUE
                        ), EquipmentSlotGroup.MAINHAND
                    )
                    .build()
            )
            .stacksTo(1)

    private fun <T : Item> KClass<T>.registerTool(
        id: String,
        tier: ToolTier,
        damageModifier: Double = 0.0,
        speedModifier: Double = 0.0
    ) = ITEM_REGISTRY.register(id) { ->
        this.java.getConstructor(Tier::class.java, Item.Properties::class.java).newInstance(
            tier,
            toolProperties(tier, damageModifier, speedModifier)
        )
    }

    val BREAD_PICKAXE: DeferredItem<PickaxeItem> =
        PickaxeItem::class.registerTool("bread_pickaxe", ToolTier.BREAD, 1.0, -2.3)
    val BREAD_SHOVEL: DeferredItem<ShovelItem> =
        ShovelItem::class.registerTool("bread_shovel", ToolTier.BREAD, 1.0, -2.6)
    val BREAD_AXE: DeferredItem<AxeItem> =
        AxeItem::class.registerTool("bread_axe", ToolTier.BREAD, 3.5, -2.8)
    val BREAD_HOE: DeferredItem<HoeItem> =
        HoeItem::class.registerTool("bread_hoe", ToolTier.BREAD, 0.8, -2.6)
    val BREAD_SWORD: DeferredItem<SwordItem> =
        SwordItem::class.registerTool("bread_sword", ToolTier.BREAD, 1.8, -2.3)

    val RF_BREAD_PICKAXE: DeferredItem<PickaxeItem> =
        PickaxeItem::class.registerTool("reinforced_bread_pickaxe", ToolTier.RF_BREAD, 2.0, -1.0)
    val RF_BREAD_SHOVEL: DeferredItem<ShovelItem> =
        ShovelItem::class.registerTool("reinforced_bread_shovel", ToolTier.RF_BREAD, 1.2, -2.8)
    val RF_BREAD_AXE: DeferredItem<AxeItem> =
        AxeItem::class.registerTool("reinforced_bread_axe", ToolTier.RF_BREAD, 4.0, -3.0)
    val RF_BREAD_HOE: DeferredItem<HoeItem> =
        HoeItem::class.registerTool("reinforced_bread_hoe", ToolTier.RF_BREAD, 1.0, -2.8)
    val RF_BREAD_SWORD: DeferredItem<SwordItem> =
        SwordItem::class.registerTool("reinforced_bread_sword", ToolTier.RF_BREAD, 2.0, -2.5)
}