package org.bread_experts_group.breadmod.registry.item

import net.minecraft.ChatFormatting
import net.minecraft.core.component.DataComponents
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.food.FoodProperties
import net.minecraft.world.item.*
import net.minecraft.world.item.alchemy.PotionContents
import net.minecraft.world.level.Level
import net.neoforged.neoforge.registries.DeferredItem
import net.neoforged.neoforge.registries.DeferredRegister
import org.bread_experts_group.breadmod.Breadmod
import org.bread_experts_group.breadmod.Breadmod.Companion.modTranslatable
import org.bread_experts_group.breadmod.item.TestBreadItem
import org.bread_experts_group.breadmod.item.UltimateBreadItem
import org.bread_experts_group.breadmod.item.armor.ChefHatItem
import org.bread_experts_group.breadmod.item.tool_gun.ToolGunItem

object ModItems {
    val ITEM_REGISTRY: DeferredRegister.Items = DeferredRegister.createItems(Breadmod.ID)
    fun getLocation(item: Item) = BuiltInRegistries.ITEM.getKey(item)

    val FLOUR = ITEM_REGISTRY.register("flour") { -> Item(Item.Properties()) }
    val TEST_RECORD = ITEM_REGISTRY.register("music_disc_secret_hoppin") { ->
        Item(Item.Properties().jukeboxPlayable(ModRecords.TEST_SOUND).stacksTo(1).rarity(Rarity.RARE))
    }

    val CHEF_HAT = ITEM_REGISTRY.register("chef_hat", ::ChefHatItem)
    val TOOL_GUN = ITEM_REGISTRY.register("tool_gun", ::ToolGunItem)

    val TEST_BREAD = ITEM_REGISTRY.register("test_bread", ::TestBreadItem)
    val ULTIMATE_BREAD = ITEM_REGISTRY.register("ultimate_bread", ::UltimateBreadItem)

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

    val TOASTED_BREAD = ITEM_REGISTRY.register("toasted_bread") { ->
        Item(
            Item.Properties().food(FoodProperties.Builder().nutrition(7).saturationModifier(0.8f).build())
        )
    }

    val BREAD_SLICE = ITEM_REGISTRY.register("bread_slice") { ->
        Item(Item.Properties().food(FoodProperties.Builder().nutrition(2).fast().build()))
    }

    val DOUGH = ITEM_REGISTRY.register("dough") { -> Item(Item.Properties()) }
    val DIE = ITEM_REGISTRY.register("die") { -> Item(Item.Properties()) }
}