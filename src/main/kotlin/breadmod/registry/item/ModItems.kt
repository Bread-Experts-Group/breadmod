package breadmod.registry.item

import breadmod.BreadMod
import breadmod.registry.sound.ModSounds
import breadmod.item.DopedBreadItem
import breadmod.item.TestBreadItem
import breadmod.item.armor.BreadArmorItem
import breadmod.item.tools.BreadShieldItem
import breadmod.item.tools.ToolTiers
import breadmod.util.setColor
import net.minecraft.world.food.FoodProperties
import net.minecraft.world.item.*
import net.minecraftforge.registries.DeferredRegister
import net.minecraftforge.registries.ForgeRegistries
import net.minecraftforge.registries.RegistryObject

@Suppress("unused")
object ModItems {
    val deferredRegister: DeferredRegister<Item> = DeferredRegister.create(ForgeRegistries.ITEMS, BreadMod.ID)
    fun getLocation(item: Item) = ForgeRegistries.ITEMS.getKey(item)

    val TEST_BREAD: RegistryObject<TestBreadItem> = deferredRegister.register("test_bread") { TestBreadItem() }

    val BREAD_SHIELD: RegistryObject<BreadShieldItem> = deferredRegister.register("bread_shield") { BreadShieldItem() }
    val DOPED_BREAD: RegistryObject<DopedBreadItem> = deferredRegister.register("doped_bread") { DopedBreadItem() }
    val BREAD_SLICE: RegistryObject<Item> = deferredRegister.register("bread_slice") {
        Item(Item.Properties().food(FoodProperties.Builder().nutrition(1).fast().build())) }
    val FLOUR: RegistryObject<Item> = deferredRegister.register("flour") {Item(Item.Properties())}
    val DOUGH: RegistryObject<Item> = deferredRegister.register("dough") {Item(Item.Properties())}
//    val KNIFE: RegistryObject<Item> = deferredRegister.register("knife") {Item(Item.Properties())}
//      Probably not for this one

    val ALUMINA: RegistryObject<Item> = deferredRegister.register("alumina") {Item(Item.Properties())}

    // Armor
    val BREAD_HELMET: RegistryObject<ArmorItem> = deferredRegister.register("bread_helmet") {
        object : BreadArmorItem(Type.HELMET), RegisterSpecialCreativeTab {
            override fun displayInCreativeTab(
                pParameters: CreativeModeTab.ItemDisplayParameters,
                pOutput: CreativeModeTab.Output
            ) = pOutput.accept(this.defaultInstance.also { it.setColor(BREAD_COLOR) })
        } }
    val BREAD_CHESTPLATE: RegistryObject<ArmorItem> = deferredRegister.register("bread_chestplate") {
        object : BreadArmorItem(Type.CHESTPLATE), RegisterSpecialCreativeTab {
            override fun displayInCreativeTab(
                pParameters: CreativeModeTab.ItemDisplayParameters,
                pOutput: CreativeModeTab.Output
            ) = pOutput.accept(this.defaultInstance.also { it.setColor(BREAD_COLOR) })
        } }
    val BREAD_LEGGINGS: RegistryObject<ArmorItem> = deferredRegister.register("bread_leggings") {
        object : BreadArmorItem(Type.LEGGINGS), RegisterSpecialCreativeTab {
            override fun displayInCreativeTab(
                pParameters: CreativeModeTab.ItemDisplayParameters,
                pOutput: CreativeModeTab.Output
            ) = pOutput.accept(this.defaultInstance.also { it.setColor(BREAD_COLOR) })
        } }
    val BREAD_BOOTS: RegistryObject<ArmorItem> = deferredRegister.register("bread_boots") {
        object : BreadArmorItem(Type.BOOTS), RegisterSpecialCreativeTab {
            override fun displayInCreativeTab(
                pParameters: CreativeModeTab.ItemDisplayParameters,
                pOutput: CreativeModeTab.Output
            ) = pOutput.accept(this.defaultInstance.also { it.setColor(BREAD_COLOR) })
        } }

    // Tools // Speed modifier slows down the tool based on how much of a negative value you give it (Maybe it's a multiplier?)
    val BREAD_PICKAXE: RegistryObject<PickaxeItem> = deferredRegister.register("bread_pickaxe") {
        PickaxeItem(ToolTiers.BREAD,1,-2.5f, Item.Properties()) }
    val BREAD_SHOVEL: RegistryObject<ShovelItem> = deferredRegister.register("bread_shovel") {
        ShovelItem(ToolTiers.BREAD, 1.2f,-2.8f, Item.Properties()) }
    val BREAD_AXE: RegistryObject<AxeItem> = deferredRegister.register("bread_axe") {
        AxeItem(ToolTiers.BREAD, 4.0f,-3f, Item.Properties()) }
    val BREAD_HOE: RegistryObject<HoeItem> = deferredRegister.register("bread_hoe") {
        HoeItem(ToolTiers.BREAD, 1,-2.8f, Item.Properties()) }
    val BREAD_SWORD: RegistryObject<SwordItem> = deferredRegister.register("bread_sword") {
        SwordItem(ToolTiers.BREAD, 2,-2.5f, Item.Properties()) }

    // Reinforced Tools
    val RF_BREAD_PICKAXE: RegistryObject<PickaxeItem> = deferredRegister.register("reinforced_bread_pickaxe") {
        PickaxeItem(ToolTiers.RF_BREAD, 2, -1f, Item.Properties()) }
    val RF_BREAD_SHOVEL: RegistryObject<ShovelItem> = deferredRegister.register("reinforced_bread_shovel") {
        ShovelItem(ToolTiers.RF_BREAD, 1.2f,-2.8f, Item.Properties()) }
    val RF_BREAD_AXE: RegistryObject<AxeItem> = deferredRegister.register("reinforced_bread_axe") {
        AxeItem(ToolTiers.RF_BREAD, 4.0f,-3f, Item.Properties()) }
    val RF_BREAD_HOE: RegistryObject<HoeItem> = deferredRegister.register("reinforced_bread_hoe") {
        HoeItem(ToolTiers.RF_BREAD, 1,-2.8f, Item.Properties()) }
    val RF_BREAD_SWORD: RegistryObject<SwordItem> = deferredRegister.register("reinforced_bread_sword") {
        SwordItem(ToolTiers.RF_BREAD, 2,-2.5f, Item.Properties()) }


    val TEST_DISC: RegistryObject<RecordItem> = deferredRegister.register("music_disc_test") {
        RecordItem(15, ModSounds.TEST_SOUND, Item.Properties()
            .stacksTo(1)
            .rarity(Rarity.RARE),
            7900)
    }
}