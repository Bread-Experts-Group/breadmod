package breadmod.item

import breadmod.BreadMod
import breadmod.block.ModBlocks
import breadmod.datagen.ModSounds
import breadmod.item.tools.BreadShieldItem
import net.minecraft.world.item.*
import net.minecraftforge.registries.DeferredRegister
import net.minecraftforge.registries.ForgeRegistries
import net.minecraftforge.registries.RegistryObject
import thedarkcolour.kotlinforforge.forge.registerObject
import java.util.function.Supplier

object ModItems {
    val REGISTRY: DeferredRegister<Item> = DeferredRegister.create(ForgeRegistries.ITEMS, BreadMod.ID)

    // Create a Deferred Register to hold Items which will all be registered under the "bread_mod_rewritten" namespace
    //    public static final RegistryObject<Item> EXAMPLE_BLOCK_ITEM = ITEMS.register("example_block", () -> new BlockItem(ModBlocks.EXAMPLE_BLOCK.get(), new Item.Properties()));
    // Creates a new BlockItem with the id "bread_mod_rewritten:example_block", combining the namespace and path

    val BREAD_BLOCK_ITEM by REGISTRY.registerObject("bread_block") {
        BlockItem(ModBlocks.BREAD_BLOCK, Item.Properties()) }
    val BREAD_SHIELD by REGISTRY.registerObject("bread_shield") { BreadShieldItem() }
    val TEST_BREAD by REGISTRY.registerObject("test_bread") { TestBreadItem() }

//    val TEST_DISC by REGISTRY.registerObject<Item>("music_disc_test",
//        Supplier<Item> {
//            RecordItem(
//                6, ModSounds.TEST_SOUND, Item.Properties()
//                    .stacksTo(1)
//                    .rarity(Rarity.RARE),
//                7900
//            )
//        })

    val TEST_DISC by REGISTRY.registerObject("music_disc_test") {
        RecordItem(15, ModSounds.TEST_SOUND, Item.Properties()
            .stacksTo(1)
            .rarity(Rarity.RARE),
            7900)
    }

//    public static final RegistryObject<Item> EXAMPLE_ITEM = ITEMS.register("example_item", () -> new Item(new Item.Properties().food(new FoodProperties.Builder()
    //            .alwaysEat().nutrition(1).saturationMod(2f).build())));
    // Creates a new food item with the id "examplemod:example_id", nutrition 1 and saturation 2
}