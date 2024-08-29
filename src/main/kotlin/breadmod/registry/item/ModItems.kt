package breadmod.registry.item

import breadmod.ModMain
import breadmod.datagen.tool_gun.BreadModToolGunModeProvider.Companion.TOOL_GUN_DEF
import breadmod.item.*
import breadmod.item.TieredBreadAmuletItem.BreadAmuletType
import breadmod.item.armor.ArmorTiers
import breadmod.item.armor.BreadArmorItem
import breadmod.item.compat.projecte.BreadOrbItem
import breadmod.item.tool.BreadShieldItem
import breadmod.item.tool.KnifeItem
import breadmod.item.tool.ToolTiers
import breadmod.item.tool_gun.ToolGunItem
import breadmod.registry.sound.ModSounds
import moze_intel.projecte.gameObjs.items.ItemPE
import net.minecraft.ChatFormatting
import net.minecraft.network.chat.Component
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.effect.MobEffects
import net.minecraft.world.food.FoodProperties
import net.minecraft.world.item.*
import net.minecraft.world.level.Level
import net.minecraftforge.fml.ModList
import net.minecraftforge.registries.DeferredRegister
import net.minecraftforge.registries.ForgeRegistries
import net.minecraftforge.registries.RegistryObject

@Suppress("unused")
object ModItems {
    internal val deferredRegister: DeferredRegister<Item> = DeferredRegister.create(ForgeRegistries.ITEMS, ModMain.ID)
    fun getLocation(item: Item) = ForgeRegistries.ITEMS.getKey(item)

    val TEST_BREAD: RegistryObject<TestBreadItem> = deferredRegister.register("test_bread") { TestBreadItem() }
    val ULTIMATE_BREAD: RegistryObject<UltimateBreadItem> =
        deferredRegister.register("ultimate_bread") { UltimateBreadItem() }

    val BREAD_SHIELD: RegistryObject<BreadShieldItem> = deferredRegister.register("bread_shield") { BreadShieldItem() }
    val DOPED_BREAD: RegistryObject<DopedBreadItem> = deferredRegister.register("doped_bread") { DopedBreadItem() }
    val TOASTED_BREAD: RegistryObject<Item> = deferredRegister.register("toasted_bread") {
        Item(
            Item.Properties()
                .food(FoodProperties.Builder().nutrition(7).saturationMod(0.8f).build())
        )
    }
    val BREAD_SLICE: RegistryObject<Item> = deferredRegister.register("bread_slice") {
        Item(Item.Properties().food(FoodProperties.Builder().nutrition(2).fast().build()))
    }
    val TOAST_SLICE: RegistryObject<Item> = deferredRegister.register("toast_slice") {
        Item(Item.Properties().food(FoodProperties.Builder().nutrition(5).fast().build()))
    }
    val FLOUR: RegistryObject<Item> = deferredRegister.register("flour") { Item(Item.Properties()) }
    val DOUGH: RegistryObject<Item> = deferredRegister.register("dough") { Item(Item.Properties()) }
    val DIE: RegistryObject<Item> = deferredRegister.register("die") { Item(Item.Properties()) }

    // todo better knife texture
    val KNIFE: RegistryObject<KnifeItem> = deferredRegister.register("knife") {
        KnifeItem(Tiers.IRON, 0.5f, -2.0f)
    }

    // todo textures, recipes
    val BAGEL: RegistryObject<Item> = deferredRegister.register("bagel") {
        Item(
            Item.Properties()
                .food(FoodProperties.Builder().nutrition(4).saturationMod(0.2f).build())
        )
    }
    val HALF_BAGEL: RegistryObject<Item> = deferredRegister.register("half_bagel") {
        object : Item(
            Properties()
                .food(FoodProperties.Builder().nutrition(2).saturationMod(0.1f).fast().build())
        ) {

            override fun appendHoverText(
                pStack: ItemStack, pLevel: Level?,
                pTooltipComponents: MutableList<Component>, pIsAdvanced: TooltipFlag
            ) {
                pTooltipComponents.add(
                    ModMain.modTranslatable("item", "half_bagel", "description")
                        .withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC)
                )
            }
        }
    }

    val ALUMINA: RegistryObject<Item> = deferredRegister.register("alumina") { Item(Item.Properties()) }

    // todo fix durability, textures
    val BASIC_BREAD_AMULET: RegistryObject<TieredBreadAmuletItem> = deferredRegister.register("bread_amulet") {
        TieredBreadAmuletItem(BreadAmuletType.NORMAL, 500)
    }
    val REINFORCED_BREAD_AMULET: RegistryObject<TieredBreadAmuletItem> =
        deferredRegister.register("reinforced_bread_amulet") {
            TieredBreadAmuletItem(BreadAmuletType.REINFORCED, 2500)
        }
    val INDESTRUCTIBLE_BREAD_AMULET: RegistryObject<TieredBreadAmuletItem> =
        deferredRegister.register("indestructible_bread_amulet") {
            TieredBreadAmuletItem(BreadAmuletType.INDESTRUCTIBLE, 0)
        }

    // Armor
    val BREAD_HELMET: RegistryObject<ArmorItem> =
        deferredRegister.register("bread_helmet") { BreadArmorItem(ArmorItem.Type.HELMET) }
    val BREAD_CHESTPLATE: RegistryObject<ArmorItem> =
        deferredRegister.register("bread_chestplate") { BreadArmorItem(ArmorItem.Type.CHESTPLATE) }
    val BREAD_LEGGINGS: RegistryObject<ArmorItem> =
        deferredRegister.register("bread_leggings") { BreadArmorItem(ArmorItem.Type.LEGGINGS) }
    val BREAD_BOOTS: RegistryObject<ArmorItem> =
        deferredRegister.register("bread_boots") { BreadArmorItem(ArmorItem.Type.BOOTS) }

    // Reinforced Armor
    val RF_BREAD_HELMET: RegistryObject<ArmorItem> = deferredRegister.register("reinforced_bread_helmet") {
        ArmorItem(ArmorTiers.RF_BREAD, ArmorItem.Type.HELMET, Item.Properties())
    }
    val RF_BREAD_CHESTPLATE: RegistryObject<ArmorItem> = deferredRegister.register("reinforced_bread_chestplate") {
        ArmorItem(ArmorTiers.RF_BREAD, ArmorItem.Type.CHESTPLATE, Item.Properties())
    }
    val RF_BREAD_LEGGINGS: RegistryObject<ArmorItem> = deferredRegister.register("reinforced_bread_leggings") {
        ArmorItem(ArmorTiers.RF_BREAD, ArmorItem.Type.LEGGINGS, Item.Properties())
    }
    val RF_BREAD_BOOTS: RegistryObject<ArmorItem> = deferredRegister.register("reinforced_bread_boots") {
        ArmorItem(ArmorTiers.RF_BREAD, ArmorItem.Type.BOOTS, Item.Properties())
    }

    // Tools // Speed modifier slows down the tool based on how much of a negative value you give it (Maybe it's a multiplier?)
    val BREAD_PICKAXE: RegistryObject<PickaxeItem> = deferredRegister.register("bread_pickaxe") {
        PickaxeItem(ToolTiers.BREAD, 1, -2.5f, Item.Properties())
    }
    val BREAD_SHOVEL: RegistryObject<ShovelItem> = deferredRegister.register("bread_shovel") {
        ShovelItem(ToolTiers.BREAD, 1.2f, -2.8f, Item.Properties())
    }
    val BREAD_AXE: RegistryObject<AxeItem> = deferredRegister.register("bread_axe") {
        AxeItem(ToolTiers.BREAD, 4.0f, -3f, Item.Properties())
    }
    val BREAD_HOE: RegistryObject<HoeItem> = deferredRegister.register("bread_hoe") {
        HoeItem(ToolTiers.BREAD, 1, -2.8f, Item.Properties())
    }
    val BREAD_SWORD: RegistryObject<SwordItem> = deferredRegister.register("bread_sword") {
        SwordItem(ToolTiers.BREAD, 2, -2.5f, Item.Properties())
    }

    // Reinforced Tools
    val RF_BREAD_PICKAXE: RegistryObject<PickaxeItem> = deferredRegister.register("reinforced_bread_pickaxe") {
        PickaxeItem(ToolTiers.RF_BREAD, 2, -1f, Item.Properties())
    }
    val RF_BREAD_SHOVEL: RegistryObject<ShovelItem> = deferredRegister.register("reinforced_bread_shovel") {
        ShovelItem(ToolTiers.RF_BREAD, 1.2f, -2.8f, Item.Properties())
    }
    val RF_BREAD_AXE: RegistryObject<AxeItem> = deferredRegister.register("reinforced_bread_axe") {
        AxeItem(ToolTiers.RF_BREAD, 4.0f, -3f, Item.Properties())
    }
    val RF_BREAD_HOE: RegistryObject<HoeItem> = deferredRegister.register("reinforced_bread_hoe") {
        HoeItem(ToolTiers.RF_BREAD, 1, -2.8f, Item.Properties())
    }
    val RF_BREAD_SWORD: RegistryObject<SwordItem> = deferredRegister.register("reinforced_bread_sword") {
        SwordItem(ToolTiers.RF_BREAD, 2, -2.5f, Item.Properties())
    }

    val WRENCH: RegistryObject<WrenchItem> = deferredRegister.register("wrench") { WrenchItem() }

    val BREAD_GUN_ITEM: RegistryObject<ProjectileWeaponItem> = deferredRegister.register("bread_gun") { BreadGunItem() }
    val BREAD_BULLET_ITEM: RegistryObject<Item> = deferredRegister.register("bread_bullet") { Item(Item.Properties()) }
    val TOOL_GUN: RegistryObject<Item> = deferredRegister.register(TOOL_GUN_DEF) { ToolGunItem() }
    val CAPRISPIN: RegistryObject<Item> = deferredRegister.register("caprispin") {
        object : Item(
            Properties()
                .food(
                    FoodProperties.Builder()
                        .alwaysEat()
                        .nutrition(20)
                        .effect({ MobEffectInstance(MobEffects.LEVITATION, 100, 20) }, 1f)
                        .build()
                )
                .rarity(Rarity.EPIC)
        ) {
            override fun getUseAnimation(pStack: ItemStack): UseAnim = UseAnim.DRINK
        }
    }
    val TOASTER_HEATING_ELEMENT: RegistryObject<Item> =
        deferredRegister.register("toaster_heating_element") { Item(Item.Properties()) }

    val CREATURE: RegistryObject<Item> = deferredRegister.register("creature") { Item(Item.Properties()) }

    val CERTIFICATE: RegistryObject<Item> = deferredRegister.register("certificate") { CertificateItem() }

    val TEST_DISC: RegistryObject<RecordItem> = deferredRegister.register("music_disc_test") {
        RecordItem(
            15, ModSounds.TEST_SOUND, Item.Properties()
                .stacksTo(1)
                .rarity(Rarity.RARE),
            7900
        )
    }

    val PROJECT_E: ProjectEItems? = if (ModList.get().isLoaded("projecte")) ProjectEItems() else null

    @Suppress("PropertyName")
    class ProjectEItems {
        val BREAD_ORB_ITEM: RegistryObject<ItemPE> = deferredRegister.register("bread_emc_item") { BreadOrbItem() }
    }
}