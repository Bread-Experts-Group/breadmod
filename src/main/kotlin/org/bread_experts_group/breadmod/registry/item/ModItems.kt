package org.bread_experts_group.breadmod.registry.item

import net.minecraft.ChatFormatting
import net.minecraft.core.registries.Registries
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
import org.bread_experts_group.breadmod.BreadMod.Companion.modTranslatable
import org.bread_experts_group.breadmod.datagen.lang.DataGenerateLanguage
import org.bread_experts_group.breadmod.datagen.model.item.DataGenerateModelHandheldItem
import org.bread_experts_group.breadmod.datagen.model.item.DataGenerateModelLayeredItem
import org.bread_experts_group.breadmod.datagen.model.item.DataGenerateModelSingleItem
import org.bread_experts_group.breadmod.datagen.tag.DataGenerateTagItem
import org.bread_experts_group.breadmod.experimental.camera_viewer.item.CameraBinderItem
import org.bread_experts_group.breadmod.experimental.lidar.LidarGunItem
import org.bread_experts_group.breadmod.experimental.lidar.LidarHelmetItem
import org.bread_experts_group.breadmod.experimental.physics_grid.GridCreatorItem
import org.bread_experts_group.breadmod.registry.RegistryProvider
import org.bread_experts_group.breadmod.registry.entity.actual.Forklift
import org.bread_experts_group.breadmod.registry.item.actual.BreadAmuletItem
import org.bread_experts_group.breadmod.registry.item.actual.DopedBreadItem
import org.bread_experts_group.breadmod.registry.item.actual.FineShineItem
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
import org.bread_experts_group.breadmod.util.itemTooltip
import kotlin.reflect.KClass

/**
 * All items as defined by Bread Mod.
 *
 * @author Miko Elbrecht, Logan McLean
 * @since 1.0.0
 */
@Suppress("KDocMissingDocumentation")
object ModItems : RegistryProvider(Registries.ITEM) {
	private val registry: DeferredRegister<Item> = this.getRegistry(Registries.ITEM)
	fun itemIterator(): Iterator<DeferredItem<Item>> = object : Iterator<DeferredItem<Item>> {
		val registryIterator = this@ModItems.registry.entries.iterator()
		override fun hasNext(): Boolean = this.registryIterator.hasNext()
		override fun next(): DeferredItem<Item> {
			val holder = this.registryIterator.next()
			return DeferredItem.createItem(holder.id)
		}
	}

	fun <T : Item> registerItem(id: String, item: () -> T): DeferredItem<T> {
		val holder = this.registry.register(id, item)
		return DeferredItem.createItem(holder.id)
	}

	fun registerItem(id: String, properties: Item.Properties = Item.Properties()): DeferredItem<Item> {
		return this.registerItem(id) { Item(properties) }
	}

	@DataGenerateTagItem("c:flour/wheat")
	@DataGenerateModelSingleItem
	@DataGenerateLanguage
	val FLOUR: DeferredItem<Item> = this.registerItem("flour")

	@DataGenerateTagItem(
		"minecraft:music_discs",
		"minecraft:creeper_drop_music_discs"
	)
	@DataGenerateModelSingleItem
	@DataGenerateLanguage(name = "Music Disc")
	@DataGenerateLanguage(name = "ClascyJitto - Secret Hoppin'", suffix = ".desc")
	val RECORD_SECRET_HOPPIN: DeferredItem<Item> = this.registerItem(
		"music_disc_secret_hoppin",
		Item.Properties()
			.jukeboxPlayable(ModRecords.SECRET_HOPPIN)
			.stacksTo(1)
			.rarity(Rarity.RARE)
	)

	@DataGenerateTagItem("minecraft:dyeable")
	@DataGenerateModelSingleItem
	@DataGenerateLanguage
	@DataGenerateLanguage(name = "IS THAT A PIZZA TOWER REFERENCE???", suffix = ".tooltip")
	val CHEF_HAT: DeferredItem<ChefHatItem> = this.registerItem("chef_hat", ::ChefHatItem)

	@DataGenerateModelSingleItem
	@DataGenerateLanguage
	val OIL_DRUM: DeferredItem<OilDrumItem> = this.registerItem("oil_drum", ::OilDrumItem)

	@DataGenerateLanguage
	val TOOL_GUN: DeferredItem<ToolGunItem> = this.registerItem("tool_gun", ::ToolGunItem)

	@DataGenerateLanguage
	val LIDAR_GUN: DeferredItem<LidarGunItem> = this.registerItem("lidar_gun", ::LidarGunItem)

	@DataGenerateLanguage
	val LIDAR_HELMET: DeferredItem<LidarHelmetItem> = this.registerItem("lidar_helmet", ::LidarHelmetItem)

	@DataGenerateModelSingleItem
	@DataGenerateLanguage
	@DataGenerateLanguage(name = "Identical to bread on the outside - tumors on the inside.", suffix = ".tooltip")
	@DataGenerateLanguage("ja_jp", "実験的なパン")
	@DataGenerateLanguage("ja_jp", "ほとんどパンだ、だけど中に腫瘍ら。", suffix = ".tooltip")
	val TEST_BREAD: DeferredItem<TestBreadItem> = this.registerItem("test_bread", ::TestBreadItem)

	@DataGenerateModelSingleItem
	@DataGenerateLanguage
	@DataGenerateLanguage("ja_jp", "最終的なパン")
	val ULTIMATE_BREAD: DeferredItem<UltimateBreadItem> =
		this.registerItem("ultimate_bread", ::UltimateBreadItem)

	@DataGenerateLanguage
	@DataGenerateLanguage(name = "No it does NOT look like balsa wood >:(", suffix = ".tooltip")
	@DataGenerateLanguage("ja_jp", "パン盾")
	@DataGenerateLanguage("ja_jp", "英語訳は偽っている。バルサ木だ", suffix = ".tooltip")
	val BREAD_SHIELD: DeferredItem<ShieldItem> = this.registerItem("bread_shield") {
		object : ShieldItem(Properties().stacksTo(1).durability(256)) {
			override fun appendHoverText(
				stack: ItemStack,
				context: TooltipContext,
				tooltipComponents: MutableList<Component>,
				tooltipFlag: TooltipFlag
			) {
				tooltipComponents.add(this.itemTooltip().withStyle(ChatFormatting.AQUA))
			}
		}
	}

	@DataGenerateModelLayeredItem("doped_bread", "doped_bread_overlay")
	@DataGenerateLanguage
	@DataGenerateLanguage(name = "Contains trace amounts of neurotoxin", suffix = ".tooltip")
	@DataGenerateLanguage(name = "Doped with:", suffix = ".tooltip_two")
	val DOPED_BREAD: DeferredItem<Item> = this.registerItem("doped_bread", ::DopedBreadItem)

	@DataGenerateTagItem("breadmod:toastable")
	@DataGenerateModelSingleItem
	@DataGenerateLanguage
	val TOASTED_BREAD: DeferredItem<Item> = this.registerItem(
		"toasted_bread",
		Item.Properties()
			.food(FoodProperties.Builder().nutrition(7).saturationModifier(0.8f).build())
	)

	@DataGenerateTagItem("breadmod:toastable")
	@DataGenerateModelSingleItem
	@DataGenerateLanguage
	val BREAD_SLICE: DeferredItem<Item> = this.registerItem(
		"bread_slice",
		Item.Properties()
			.food(FoodProperties.Builder().nutrition(2).fast().build())
	)

	@DataGenerateTagItem("breadmod:toastable")
	@DataGenerateModelSingleItem
	@DataGenerateLanguage
	val TOAST_SLICE: DeferredItem<Item> = this.registerItem(
		"toast_slice",
		Item.Properties()
			.food(FoodProperties.Builder().nutrition(5).fast().build())
	)

	@DataGenerateModelSingleItem
	@DataGenerateLanguage
	val DOUGH: DeferredItem<Item> = this.registerItem("dough")

	@DataGenerateModelSingleItem
	@DataGenerateLanguage
	val DIE: DeferredItem<Item> = this.registerItem("die")

	@DataGenerateTagItem("breadmod:knives")
	@DataGenerateModelHandheldItem
	@DataGenerateLanguage
	val KNIFE: DeferredItem<KnifeItem> = this.registerItem("knife") { KnifeItem(Tiers.IRON) }

	@DataGenerateModelSingleItem
	@DataGenerateLanguage
	val BAGEL: DeferredItem<Item> = this.registerItem(
		"bagel",
		Item.Properties()
			.food(FoodProperties.Builder().nutrition(4).saturationModifier(0.2f).build())
	)

	@DataGenerateModelSingleItem
	@DataGenerateLanguage
	@DataGenerateLanguage(name = "What? you thought it was gonna be sliced like a normal bagel?", suffix = ".tooltip")
	val HALF_BAGEL: DeferredItem<Item> = this.registerItem("half_bagel") {
		object : Item(
			Properties()
				.food(FoodProperties.Builder().nutrition(2).saturationModifier(0.1f).fast().build())
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
	@DataGenerateLanguage
	val ALUMINA: DeferredItem<Item> = this.registerItem("alumina")

	@DataGenerateModelSingleItem
	@DataGenerateLanguage
	@DataGenerateLanguage(name = "Feeds %s every %s", suffix = ".tooltip")
	@DataGenerateLanguage(name = "(stacking!)", suffix = ".stacks")
	val BREAD_AMULET: DeferredItem<BreadAmuletItem> = this.registerItem("bread_amulet") { BreadAmuletItem(500) }

	@DataGenerateModelSingleItem
	@DataGenerateLanguage(name = "Amulet of Keeping")
	val AMULET_OF_KEEPING: DeferredItem<Item> =
		this.registerItem("keep_inventory_amulet") {
			Item(
				Item.Properties()
					.stacksTo(1)
					.rarity(Rarity.UNCOMMON)
			)
		}

	@DataGenerateModelSingleItem
	@DataGenerateLanguage(name = "The Fine Shine")
	@DataGenerateLanguage(name = "Wait... shouldn't this be illegal?", suffix = ".tooltip")
	val FINE_SHINE: DeferredItem<Item> = this.registerItem("fine_shine", ::FineShineItem)

	// Bread Armor
	@DataGenerateTagItem("minecraft:dyeable")
	@DataGenerateModelLayeredItem("bread_helmet", "bread_helmet_overlay")
	@DataGenerateLanguage
	val BREAD_HELMET: DeferredItem<BreadArmorItem> =
		this.registerItem("bread_helmet") { BreadArmorItem(ArmorItem.Type.HELMET) }

	@DataGenerateTagItem("minecraft:dyeable")
	@DataGenerateModelLayeredItem("bread_chestplate", "bread_chestplate_overlay")
	@DataGenerateLanguage
	val BREAD_CHESTPLATE: DeferredItem<BreadArmorItem> =
		this.registerItem("bread_chestplate") { BreadArmorItem(ArmorItem.Type.CHESTPLATE) }

	@DataGenerateTagItem("minecraft:dyeable")
	@DataGenerateModelLayeredItem("bread_leggings", "bread_leggings_overlay")
	@DataGenerateLanguage
	val BREAD_LEGGINGS: DeferredItem<BreadArmorItem> =
		this.registerItem("bread_leggings") { BreadArmorItem(ArmorItem.Type.LEGGINGS) }

	@DataGenerateTagItem("minecraft:dyeable")
	@DataGenerateModelLayeredItem("bread_boots", "bread_boots_overlay")
	@DataGenerateLanguage
	val BREAD_BOOTS: DeferredItem<BreadArmorItem> =
		this.registerItem("bread_boots") { BreadArmorItem(ArmorItem.Type.BOOTS) }

	// RF Bread Armor
	@DataGenerateModelSingleItem
	@DataGenerateLanguage
	val RF_BREAD_HELMET: DeferredItem<ArmorItem> = this.registerItem("reinforced_bread_helmet") {
		ArmorItem(ModArmorMaterials.RF_BREAD, ArmorItem.Type.HELMET, Item.Properties())
	}

	@DataGenerateModelSingleItem
	@DataGenerateLanguage
	val RF_BREAD_CHESTPLATE: DeferredItem<ArmorItem> = this.registerItem("reinforced_bread_chestplate") {
		ArmorItem(ModArmorMaterials.RF_BREAD, ArmorItem.Type.CHESTPLATE, Item.Properties())
	}

	@DataGenerateModelSingleItem
	@DataGenerateLanguage
	val RF_BREAD_LEGGINGS: DeferredItem<ArmorItem> = this.registerItem("reinforced_bread_leggings") {
		ArmorItem(ModArmorMaterials.RF_BREAD, ArmorItem.Type.LEGGINGS, Item.Properties())
	}

	@DataGenerateModelSingleItem
	@DataGenerateLanguage
	val RF_BREAD_BOOTS: DeferredItem<ArmorItem> = this.registerItem("reinforced_bread_boots") {
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
	): DeferredItem<T> = this@ModItems.registerItem(id) {
		this.java.getConstructor(Tier::class.java, Item.Properties::class.java).newInstance(
			tier,
			this@ModItems.toolProperties(tier, damageModifier, speedModifier)
		)
	}

	@DataGenerateModelSingleItem
	@DataGenerateLanguage
	val BREAD_PICKAXE: DeferredItem<PickaxeItem> = PickaxeItem::class.registerTool(
		"bread_pickaxe", ToolTier.BREAD,
		1.0, -2.3
	)

	@DataGenerateModelSingleItem
	@DataGenerateLanguage
	val BREAD_SHOVEL: DeferredItem<ShovelItem> = ShovelItem::class.registerTool(
		"bread_shovel", ToolTier.BREAD,
		1.0, -2.6
	)

	@DataGenerateModelSingleItem
	@DataGenerateLanguage
	val BREAD_AXE: DeferredItem<AxeItem> = AxeItem::class.registerTool(
		"bread_axe", ToolTier.BREAD,
		3.5, -2.8
	)

	@DataGenerateModelSingleItem
	@DataGenerateLanguage
	val BREAD_HOE: DeferredItem<HoeItem> = HoeItem::class.registerTool(
		"bread_hoe", ToolTier.BREAD,
		0.8, -2.6
	)

	@DataGenerateModelSingleItem
	@DataGenerateLanguage
	val BREAD_SWORD: DeferredItem<SwordItem> = SwordItem::class.registerTool(
		"bread_sword", ToolTier.BREAD,
		1.8, -2.3
	)

	@DataGenerateModelSingleItem
	@DataGenerateLanguage
	val RF_BREAD_PICKAXE: DeferredItem<PickaxeItem> = PickaxeItem::class.registerTool(
		"reinforced_bread_pickaxe", ToolTier.RF_BREAD,
		2.0, -1.0
	)

	@DataGenerateModelSingleItem
	@DataGenerateLanguage
	val RF_BREAD_SHOVEL: DeferredItem<ShovelItem> = ShovelItem::class.registerTool(
		"reinforced_bread_shovel", ToolTier.RF_BREAD,
		1.2, -2.8
	)

	@DataGenerateModelSingleItem
	@DataGenerateLanguage
	val RF_BREAD_AXE: DeferredItem<AxeItem> = AxeItem::class.registerTool(
		"reinforced_bread_axe", ToolTier.RF_BREAD,
		4.0, -3.0
	)

	@DataGenerateModelSingleItem
	@DataGenerateLanguage
	val RF_BREAD_HOE: DeferredItem<HoeItem> = HoeItem::class.registerTool(
		"reinforced_bread_hoe", ToolTier.RF_BREAD,
		1.0, -2.8
	)

	@DataGenerateModelSingleItem
	@DataGenerateLanguage
	val RF_BREAD_SWORD: DeferredItem<SwordItem> = SwordItem::class.registerTool(
		"reinforced_bread_sword", ToolTier.RF_BREAD,
		2.0, -2.5
	)

	@DataGenerateModelSingleItem
	@DataGenerateLanguage
	val CAMERA_BINDER: DeferredItem<Item> = this.registerItem("camera_binder", ::CameraBinderItem)

	@DataGenerateModelHandheldItem
	@DataGenerateLanguage
	val WRENCH: DeferredItem<Item> = this.registerItem("wrench", ::WrenchItem)

	@DataGenerateModelHandheldItem
	@DataGenerateLanguage
	val GRID_CREATOR: DeferredItem<Item> = this.registerItem("grid_creator", ::GridCreatorItem)

	@DataGenerateModelHandheldItem
	@DataGenerateLanguage
	val PUSH_GRID_ITEM: DeferredItem<Item> = this.registerItem("push_grid_tool", ::PushGridItem)

	@DataGenerateLanguage
	val SPEED_COIL: DeferredItem<Item> = this.registerItem("speed_coil", ::SpeedCoilItem)

	@DataGenerateLanguage
	val GRAVITY_COIL: DeferredItem<Item> = this.registerItem("gravity_coil", ::GravityCoilItem)

	// End Tools
	@DataGenerateModelSingleItem
	@DataGenerateLanguage
	val CAPRISPIN: DeferredItem<Item> = this.registerItem("caprispin") {
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
	@DataGenerateLanguage
	val TOASTER_HEATING_ELEMENT: DeferredItem<Item> = this.registerItem("toaster_heating_element")

	@DataGenerateModelSingleItem
	@DataGenerateLanguage
	val CREATURE: DeferredItem<Item> = this.registerItem("creature")

	@DataGenerateLanguage
	val GLUON_GUN_BACKPACK: DeferredItem<GluonGunBackpackItem> =
		this.registerItem("gluon_gun_backpack", ::GluonGunBackpackItem)

	@DataGenerateLanguage
	val GLUON_GUN: DeferredItem<Item> = this.registerItem("gluon_gun")

	@DataGenerateLanguage
	val FORKLIFT: DeferredItem<Item> = this.registerItem("forklift") {
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