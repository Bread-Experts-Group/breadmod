package org.bread_experts_group.breadmod.registry.item

import net.minecraft.ChatFormatting
import net.minecraft.network.chat.Component
import net.minecraft.world.InteractionResult
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.effect.MobEffects
import net.minecraft.world.entity.EquipmentSlotGroup
import net.minecraft.world.entity.ai.attributes.AttributeModifier
import net.minecraft.world.entity.ai.attributes.Attributes
import net.minecraft.world.food.FoodProperties
import net.minecraft.world.item.ArmorItem
import net.minecraft.world.item.AxeItem
import net.minecraft.world.item.DiggerItem.BASE_ATTACK_DAMAGE_ID
import net.minecraft.world.item.DiggerItem.BASE_ATTACK_SPEED_ID
import net.minecraft.world.item.HoeItem
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.PickaxeItem
import net.minecraft.world.item.Rarity
import net.minecraft.world.item.ShieldItem
import net.minecraft.world.item.ShovelItem
import net.minecraft.world.item.SwordItem
import net.minecraft.world.item.Tier
import net.minecraft.world.item.Tiers
import net.minecraft.world.item.TooltipFlag
import net.minecraft.world.item.UseAnim
import net.minecraft.world.item.component.ItemAttributeModifiers
import net.minecraft.world.item.context.UseOnContext
import net.neoforged.neoforge.registries.DeferredItem
import net.neoforged.neoforge.registries.DeferredRegister
import org.bread_experts_group.breadmod.BreadMod
import org.bread_experts_group.breadmod.BreadMod.Companion.modTranslatable
import org.bread_experts_group.breadmod.datagen.lang.DataGenerateLanguage
import org.bread_experts_group.breadmod.datagen.model.item.DataGenerateModelHandheldItem
import org.bread_experts_group.breadmod.datagen.model.item.DataGenerateModelLayeredItem
import org.bread_experts_group.breadmod.datagen.model.item.DataGenerateModelSingleItem
import org.bread_experts_group.breadmod.datagen.tag.DataGenerateTagItem
import org.bread_experts_group.breadmod.experimental.particle.RadioactiveMaterial
import org.bread_experts_group.breadmod.registry.entity.actual.Forklift
import org.bread_experts_group.breadmod.registry.item.ModItems.ITEM_REGISTRY
import org.bread_experts_group.breadmod.registry.item.actual.BreadAmuletItem
import org.bread_experts_group.breadmod.registry.item.actual.BulkBlockItem
import org.bread_experts_group.breadmod.registry.item.actual.DopedBreadItem
import org.bread_experts_group.breadmod.registry.item.actual.GravityCoilItem
import org.bread_experts_group.breadmod.registry.item.actual.OilDrumItem
import org.bread_experts_group.breadmod.registry.item.actual.PushGridItem
import org.bread_experts_group.breadmod.registry.item.actual.SpeedCoilItem
import org.bread_experts_group.breadmod.registry.item.actual.TestBreadItem
import org.bread_experts_group.breadmod.registry.item.actual.UltimateBreadItem
import org.bread_experts_group.breadmod.registry.item.actual.WrenchItem
import org.bread_experts_group.breadmod.registry.item.actual.armor.BreadArmorItem
import org.bread_experts_group.breadmod.registry.item.actual.armor.ChefHatItem
import org.bread_experts_group.breadmod.registry.item.actual.armor.GluonGunBackpackItem
import org.bread_experts_group.breadmod.registry.item.actual.armor.ModArmorMaterials
import org.bread_experts_group.breadmod.registry.item.actual.tool.KnifeItem
import org.bread_experts_group.breadmod.tool_gun.ToolGunItem
import kotlin.reflect.KClass

/**
 * All items as defined by Bread Mod.
 *
 * @author Miko Elbrecht, Logan McLean
 * @since 1.0.0
 * @see ITEM_REGISTRY
 */
object ModItems {
	val ITEM_REGISTRY: DeferredRegister.Items = DeferredRegister.createItems(BreadMod.ID)

	@DataGenerateTagItem("c:flour/wheat")
	@DataGenerateModelSingleItem
	@DataGenerateLanguage("en_us")
	val FLOUR: DeferredItem<Item> = this.ITEM_REGISTRY.registerSimpleItem("flour")

	@DataGenerateTagItem(
		"minecraft:music_discs",
		"minecraft:creeper_drop_music_discs"
	)
	@DataGenerateModelSingleItem
	@DataGenerateLanguage("en_us", "Music Disc")
	@DataGenerateLanguage("en_us", "ClascyJitto - Secret Hoppin'", suffix = ".desc")
	val RECORD_SECRET_HOPPIN: DeferredItem<Item> = this.ITEM_REGISTRY.registerSimpleItem(
		"music_disc_secret_hoppin",
		Item.Properties().jukeboxPlayable(ModRecords.SECRET_HOPPIN).stacksTo(1).rarity(Rarity.RARE)
	)

	@DataGenerateTagItem("minecraft:dyeable")
	@DataGenerateModelSingleItem
	@DataGenerateLanguage("en_us")
	@DataGenerateLanguage("en_us", "IS THAT A PIZZA TOWER REFERENCE???", suffix = ".tooltip")
	val CHEF_HAT: DeferredItem<ChefHatItem> = this.ITEM_REGISTRY.register("chef_hat", ::ChefHatItem)

	@DataGenerateModelSingleItem
	@DataGenerateLanguage("en_us")
	val OIL_DRUM: DeferredItem<OilDrumItem> = this.ITEM_REGISTRY.register("oil_drum", ::OilDrumItem)

	@DataGenerateLanguage("en_us")
	val TOOL_GUN: DeferredItem<ToolGunItem> = this.ITEM_REGISTRY.register("tool_gun", ::ToolGunItem)

	@DataGenerateModelSingleItem
	@DataGenerateLanguage("en_us")
	@DataGenerateLanguage("en_us", "Identical to bread on the outside - tumors on the inside.", suffix = ".tooltip")
	@DataGenerateLanguage("ja_jp", "実験的なパン")
	@DataGenerateLanguage("ja_jp", "ほとんどパンだ、だけど中に腫瘍ら。", suffix = ".tooltip")
	val TEST_BREAD: DeferredItem<TestBreadItem> = this.ITEM_REGISTRY.register("test_bread", ::TestBreadItem)

	@DataGenerateModelSingleItem
	@DataGenerateLanguage("en_us")
	@DataGenerateLanguage("ja_jp", "最終的なパン")
	val ULTIMATE_BREAD: DeferredItem<UltimateBreadItem> =
		this.ITEM_REGISTRY.register("ultimate_bread", ::UltimateBreadItem)

	@DataGenerateLanguage("en_us")
	@DataGenerateLanguage("en_us", "No it does NOT look like balsa wood >:(", suffix = ".tooltip")
	@DataGenerateLanguage("ja_jp", "パン盾")
	@DataGenerateLanguage("ja_jp", "英語訳は偽っている。バルサ木だ", suffix = ".tooltip")
	val BREAD_SHIELD: DeferredItem<ShieldItem> = this.ITEM_REGISTRY.register("bread_shield") { ->
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

	@DataGenerateModelLayeredItem("doped_bread", "doped_bread_overlay")
	@DataGenerateLanguage("en_us")
	@DataGenerateLanguage("en_us", "Contains trace amounts of neurotoxin", suffix = ".tooltip")
	@DataGenerateLanguage("en_us", "Doped with:", suffix = ".tooltip_two")
	val DOPED_BREAD: DeferredItem<Item> = this.ITEM_REGISTRY.register("doped_bread", ::DopedBreadItem)

	@DataGenerateTagItem("breadmod:toastable")
	@DataGenerateModelSingleItem
	@DataGenerateLanguage("en_us")
	val TOASTED_BREAD: DeferredItem<Item> = this.ITEM_REGISTRY.registerSimpleItem(
		"toasted_bread",
		Item.Properties().food(FoodProperties.Builder().nutrition(7).saturationModifier(0.8f).build())
	)

	@DataGenerateTagItem("breadmod:toastable")
	@DataGenerateModelSingleItem
	@DataGenerateLanguage("en_us")
	val BREAD_SLICE: DeferredItem<Item> = this.ITEM_REGISTRY.registerSimpleItem(
		"bread_slice",
		Item.Properties().food(FoodProperties.Builder().nutrition(2).fast().build())
	)

	@DataGenerateTagItem("breadmod:toastable")
	@DataGenerateModelSingleItem
	@DataGenerateLanguage("en_us")
	val TOAST_SLICE: DeferredItem<Item> = this.ITEM_REGISTRY.registerSimpleItem(
		"toast_slice",
		Item.Properties().food(FoodProperties.Builder().nutrition(5).fast().build())
	)

	@DataGenerateModelSingleItem
	@DataGenerateLanguage("en_us")
	val DOUGH: DeferredItem<Item> = this.ITEM_REGISTRY.registerSimpleItem("dough")

	@DataGenerateModelSingleItem
	@DataGenerateLanguage("en_us")
	val DIE: DeferredItem<Item> = this.ITEM_REGISTRY.registerSimpleItem("die")

	@DataGenerateTagItem("breadmod:knives")
	@DataGenerateModelHandheldItem
	@DataGenerateLanguage("en_us")
	val KNIFE: DeferredItem<KnifeItem> = this.ITEM_REGISTRY.register("knife") { -> KnifeItem(Tiers.IRON) }

	@DataGenerateModelSingleItem
	@DataGenerateLanguage("en_us")
	val BAGEL: DeferredItem<Item> = this.ITEM_REGISTRY.registerSimpleItem(
		"bagel",
		Item.Properties().food(FoodProperties.Builder().nutrition(4).saturationModifier(0.2f).build())
	)

	@DataGenerateModelSingleItem
	@DataGenerateLanguage("en_us")
	@DataGenerateLanguage("en_us", "What? you thought it was gonna be sliced like a normal bagel?", suffix = ".tooltip")
	val HALF_BAGEL: DeferredItem<Item> = this.ITEM_REGISTRY.register("half_bagel") { ->
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
					modTranslatable("item", "half_bagel", "tooltip")
						.withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC)
				)
			}
		}
	}

	@DataGenerateModelSingleItem
	@DataGenerateLanguage("en_us")
	val ALUMINA: DeferredItem<Item> = this.ITEM_REGISTRY.registerSimpleItem("alumina")

	@DataGenerateModelSingleItem
	@DataGenerateLanguage("en_us")
	@DataGenerateLanguage("en_us", "Feeds %s every %s", suffix = ".tooltip")
	@DataGenerateLanguage("en_us", "(stacking!)", "stacks.")
	val BREAD_AMULET: DeferredItem<BreadAmuletItem> =
		this.ITEM_REGISTRY.register("bread_amulet") { -> BreadAmuletItem(500) }

	@DataGenerateModelSingleItem
	@DataGenerateLanguage("en_us", "Amulet of Keeping")
	val AMULET_OF_KEEPING: DeferredItem<Item> =
		this.ITEM_REGISTRY.register("keep_inventory_amulet") { ->
			Item(Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON))
		}

	// Bread Armor
	@DataGenerateTagItem("minecraft:dyeable")
	@DataGenerateModelLayeredItem("bread_helmet", "bread_helmet_overlay")
	@DataGenerateLanguage("en_us")
	val BREAD_HELMET: DeferredItem<BreadArmorItem> =
		this.ITEM_REGISTRY.register("bread_helmet") { -> BreadArmorItem(ArmorItem.Type.HELMET) }

	@DataGenerateTagItem("minecraft:dyeable")
	@DataGenerateModelLayeredItem("bread_chestplate", "bread_chestplate_overlay")
	@DataGenerateLanguage("en_us")
	val BREAD_CHESTPLATE: DeferredItem<BreadArmorItem> =
		this.ITEM_REGISTRY.register("bread_chestplate") { -> BreadArmorItem(ArmorItem.Type.CHESTPLATE) }

	@DataGenerateTagItem("minecraft:dyeable")
	@DataGenerateModelLayeredItem("bread_leggings", "bread_leggings_overlay")
	@DataGenerateLanguage("en_us")
	val BREAD_LEGGINGS: DeferredItem<BreadArmorItem> =
		this.ITEM_REGISTRY.register("bread_leggings") { -> BreadArmorItem(ArmorItem.Type.LEGGINGS) }

	@DataGenerateTagItem("minecraft:dyeable")
	@DataGenerateModelLayeredItem("bread_boots", "bread_boots_overlay")
	@DataGenerateLanguage("en_us")
	val BREAD_BOOTS: DeferredItem<BreadArmorItem> =
		this.ITEM_REGISTRY.register("bread_boots") { -> BreadArmorItem(ArmorItem.Type.BOOTS) }

	// RF Bread Armor
	@DataGenerateModelSingleItem
	@DataGenerateLanguage("en_us")
	val RF_BREAD_HELMET: DeferredItem<ArmorItem> = this.ITEM_REGISTRY.register("reinforced_bread_helmet") { ->
		ArmorItem(ModArmorMaterials.RF_BREAD, ArmorItem.Type.HELMET, Item.Properties())
	}

	@DataGenerateModelSingleItem
	@DataGenerateLanguage("en_us")
	val RF_BREAD_CHESTPLATE: DeferredItem<ArmorItem> = this.ITEM_REGISTRY.register("reinforced_bread_chestplate") { ->
		ArmorItem(ModArmorMaterials.RF_BREAD, ArmorItem.Type.CHESTPLATE, Item.Properties())
	}

	@DataGenerateModelSingleItem
	@DataGenerateLanguage("en_us")
	val RF_BREAD_LEGGINGS: DeferredItem<ArmorItem> = this.ITEM_REGISTRY.register("reinforced_bread_leggings") { ->
		ArmorItem(ModArmorMaterials.RF_BREAD, ArmorItem.Type.LEGGINGS, Item.Properties())
	}

	@DataGenerateModelSingleItem
	@DataGenerateLanguage("en_us")
	val RF_BREAD_BOOTS: DeferredItem<ArmorItem> = this.ITEM_REGISTRY.register("reinforced_bread_boots") { ->
		ArmorItem(ModArmorMaterials.RF_BREAD, ArmorItem.Type.BOOTS, Item.Properties())
	}

	// Tools
	private fun toolProperties(tier: ToolTier, damageModifier: Double, speedModifier: Double): Item.Properties =
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
	): DeferredItem<T> = this@ModItems.ITEM_REGISTRY.register(id) { ->
		this.java.getConstructor(Tier::class.java, Item.Properties::class.java).newInstance(
			tier,
			this@ModItems.toolProperties(tier, damageModifier, speedModifier)
		)
	}

	@DataGenerateModelSingleItem
	@DataGenerateLanguage("en_us")
	val BREAD_PICKAXE: DeferredItem<PickaxeItem> =
		PickaxeItem::class.registerTool("bread_pickaxe", ToolTier.BREAD, 1.0, -2.3)

	@DataGenerateModelSingleItem
	@DataGenerateLanguage("en_us")
	val BREAD_SHOVEL: DeferredItem<ShovelItem> =
		ShovelItem::class.registerTool("bread_shovel", ToolTier.BREAD, 1.0, -2.6)

	@DataGenerateModelSingleItem
	@DataGenerateLanguage("en_us")
	val BREAD_AXE: DeferredItem<AxeItem> =
		AxeItem::class.registerTool("bread_axe", ToolTier.BREAD, 3.5, -2.8)

	@DataGenerateModelSingleItem
	@DataGenerateLanguage("en_us")
	val BREAD_HOE: DeferredItem<HoeItem> =
		HoeItem::class.registerTool("bread_hoe", ToolTier.BREAD, 0.8, -2.6)

	@DataGenerateModelSingleItem
	@DataGenerateLanguage("en_us")
	val BREAD_SWORD: DeferredItem<SwordItem> =
		SwordItem::class.registerTool("bread_sword", ToolTier.BREAD, 1.8, -2.3)

	@DataGenerateModelSingleItem
	@DataGenerateLanguage("en_us")
	val RF_BREAD_PICKAXE: DeferredItem<PickaxeItem> =
		PickaxeItem::class.registerTool("reinforced_bread_pickaxe", ToolTier.RF_BREAD, 2.0, -1.0)

	@DataGenerateModelSingleItem
	@DataGenerateLanguage("en_us")
	val RF_BREAD_SHOVEL: DeferredItem<ShovelItem> =
		ShovelItem::class.registerTool("reinforced_bread_shovel", ToolTier.RF_BREAD, 1.2, -2.8)

	@DataGenerateModelSingleItem
	@DataGenerateLanguage("en_us")
	val RF_BREAD_AXE: DeferredItem<AxeItem> =
		AxeItem::class.registerTool("reinforced_bread_axe", ToolTier.RF_BREAD, 4.0, -3.0)

	@DataGenerateModelSingleItem
	@DataGenerateLanguage("en_us")
	val RF_BREAD_HOE: DeferredItem<HoeItem> =
		HoeItem::class.registerTool("reinforced_bread_hoe", ToolTier.RF_BREAD, 1.0, -2.8)

	@DataGenerateModelSingleItem
	@DataGenerateLanguage("en_us")
	val RF_BREAD_SWORD: DeferredItem<SwordItem> =
		SwordItem::class.registerTool("reinforced_bread_sword", ToolTier.RF_BREAD, 2.0, -2.5)

	@DataGenerateModelHandheldItem
	@DataGenerateLanguage("en_us")
	val WRENCH: DeferredItem<Item> =
		this.ITEM_REGISTRY.register("wrench", ::WrenchItem)

	@DataGenerateModelHandheldItem
	@DataGenerateLanguage("en_us")
	val BULK_BLOCK_ITEM: DeferredItem<Item> =
		this.ITEM_REGISTRY.register("bulk_block_creator", ::BulkBlockItem)

	@DataGenerateLanguage("en_us")
	val PUSH_GRID_ITEM: DeferredItem<Item> =
		this.ITEM_REGISTRY.register("push_grid_tool", ::PushGridItem)

	@DataGenerateLanguage("en_us")
	val SPEED_COIL: DeferredItem<Item> =
		this.ITEM_REGISTRY.register("speed_coil", ::SpeedCoilItem)

	@DataGenerateLanguage("en_us")
	val GRAVITY_COIL: DeferredItem<Item> =
		this.ITEM_REGISTRY.register("gravity_coil", ::GravityCoilItem)

	// End Tools
	@DataGenerateModelSingleItem
	@DataGenerateLanguage("en_us")
	val CAPRISPIN: DeferredItem<Item> = this.ITEM_REGISTRY.register("caprispin") { ->
		object : Item(
			Properties().food(
				FoodProperties.Builder().alwaysEdible().nutrition(20)
					.effect({ MobEffectInstance(MobEffects.LEVITATION, 100, 20) }, 1f).build()
			).rarity(Rarity.RARE)
		) {
			override fun getUseAnimation(stack: ItemStack): UseAnim = UseAnim.DRINK
		}
	}

	@DataGenerateModelSingleItem
	@DataGenerateLanguage("en_us")
	val TOASTER_HEATING_ELEMENT: DeferredItem<Item> = this.ITEM_REGISTRY.registerSimpleItem("toaster_heating_element")

	@DataGenerateModelSingleItem
	@DataGenerateLanguage("en_us")
	val CREATURE: DeferredItem<Item> = this.ITEM_REGISTRY.registerSimpleItem("creature")

	@DataGenerateLanguage("en_us")
	val GLUON_GUN_BACKPACK: DeferredItem<GluonGunBackpackItem> =
		this.ITEM_REGISTRY.register("gluon_gun_backpack", ::GluonGunBackpackItem)

	@DataGenerateLanguage("en_us")
	val GLUON_GUN: DeferredItem<Item> = this.ITEM_REGISTRY.registerSimpleItem("gluon_gun")

	@DataGenerateLanguage("en_us")
	@DataGenerateLanguage("en_us", "Free Energy: %s eV", suffix = ".energy")
	@DataGenerateLanguage("en_us", "Total Energy: %s J", suffix = ".total_energy")
	val RADIOACTIVE_MATERIAL: DeferredItem<Item> =
		this.ITEM_REGISTRY.register("radioactive_material", ::RadioactiveMaterial)

	@DataGenerateLanguage("en_us")
	val FORKLIFT: DeferredItem<Item> = this.ITEM_REGISTRY.register("forklift") { ->
		object : Item(Properties()) {
			override fun useOn(context: UseOnContext): InteractionResult {
				val pos = context.clickedPos.above()
				val level = context.level
				val rotation = context.player?.yHeadRot ?: 0f
				val forklift = Forklift(level, pos, rotation)
				level.addFreshEntity(forklift)
				return InteractionResult.sidedSuccess(level.isClientSide)
			}
		}
	}
}