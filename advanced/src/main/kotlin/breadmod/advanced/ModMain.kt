package breadmod.advanced

import com.mojang.logging.LogUtils
import net.minecraft.client.Minecraft
import net.minecraft.core.registries.Registries
import net.minecraft.world.food.FoodProperties
import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.item.CreativeModeTabs
import net.minecraft.world.item.Item
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.state.BlockBehaviour
import net.minecraft.world.level.material.MapColor
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent
import net.minecraftforge.event.server.ServerStartingEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.ModLoadingContext
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.config.ModConfig
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext
import net.minecraftforge.registries.DeferredRegister
import net.minecraftforge.registries.ForgeRegistries
import net.minecraftforge.registries.RegistryObject
import org.slf4j.Logger

// The value here should match an entry in the META-INF/mods.toml file
@Mod(ModMain.MODID)
class ModMain {
    init {
        val modEventBus = FMLJavaModLoadingContext.get().modEventBus

        // Register the commonSetup method for modloading
        modEventBus.addListener { event: FMLCommonSetupEvent -> this.commonSetup(event) }

        // Register the Deferred Register to the mod event bus so blocks get registered
        BLOCKS.register(modEventBus)
        // Register the Deferred Register to the mod event bus so items get registered
        ITEMS.register(modEventBus)
        // Register the Deferred Register to the mod event bus so tabs get registered
        CREATIVE_MODE_TABS.register(modEventBus)

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this)

        // Register the item to a creative tab
        modEventBus.addListener { event: BuildCreativeModeTabContentsEvent -> this.addCreative(event) }

        // Register our mod's ForgeConfigSpec so that Forge can create and load the config file for us
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.SPEC)
    }

    private fun commonSetup(event: FMLCommonSetupEvent) {
        // Some common setup code
        LOGGER.info("HELLO FROM COMMON SETUP")
        LOGGER.info("DIRT BLOCK >> {}", ForgeRegistries.BLOCKS.getKey(Blocks.DIRT))

        if (Config.logDirtBlock) LOGGER.info("DIRT BLOCK >> {}", ForgeRegistries.BLOCKS.getKey(Blocks.DIRT))

        LOGGER.info(Config.magicNumberIntroduction + Config.magicNumber)

        //Config.items!!.forEach(Consumer { item: Item? -> LOGGER.info("ITEM >> {}", item.toString()) })
    }

    // Add the example block item to the building blocks tab
    private fun addCreative(event: BuildCreativeModeTabContentsEvent) {
        if (event.tabKey === CreativeModeTabs.BUILDING_BLOCKS) event.accept(EXAMPLE_BLOCK_ITEM)
    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    fun onServerStarting(event: ServerStartingEvent?) {
        // Do something when the server starts
        LOGGER.info("HELLO from server starting")
    }

    // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = [Dist.CLIENT])
    object ClientModEvents {
        @SubscribeEvent
        fun onClientSetup(event: FMLClientSetupEvent?) {
            // Some client setup code
            LOGGER.info("HELLO FROM CLIENT SETUP")
            LOGGER.info("MINECRAFT NAME >> {}", Minecraft.getInstance().user.name)
        }
    }

    companion object {
        // Define mod id in a common place for everything to reference
        const val MODID: String = "breadmodadv"

        // Directly reference a slf4j logger
        private val LOGGER: Logger = LogUtils.getLogger()

        // Create a Deferred Register to hold Blocks which will all be registered under the "breadmodadv" namespace
        val BLOCKS: DeferredRegister<Block> = DeferredRegister.create(ForgeRegistries.BLOCKS, MODID)

        // Create a Deferred Register to hold Items which will all be registered under the "breadmodadv" namespace
        val ITEMS: DeferredRegister<Item> = DeferredRegister.create(ForgeRegistries.ITEMS, MODID)

        // Create a Deferred Register to hold CreativeModeTabs which will all be registered under the "examplemod" namespace
        val CREATIVE_MODE_TABS: DeferredRegister<CreativeModeTab> =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID)

        // Creates a new Block with the id "breadmodadv:example_block", combining the namespace and path
        val EXAMPLE_BLOCK: RegistryObject<Block> = BLOCKS.register("example_block") {
            Block(
                BlockBehaviour.Properties.of().mapColor(
                    MapColor.STONE
                )
            )
        }

        // Creates a new BlockItem with the id "breadmodadv:example_block", combining the namespace and path
        val EXAMPLE_BLOCK_ITEM: RegistryObject<Item> =
            ITEMS.register("example_block") { BlockItem(EXAMPLE_BLOCK.get(), Item.Properties()) }

        // Creates a new food item with the id "examplemod:example_id", nutrition 1 and saturation 2
        val EXAMPLE_ITEM: RegistryObject<Item> = ITEMS.register("example_item") {
            Item(
                Item.Properties().food(
                    FoodProperties.Builder()
                        .alwaysEat().nutrition(1).saturationMod(2f).build()
                )
            )
        }

        // Creates a creative tab with the id "examplemod:example_tab" for the example item, that is placed after the combat tab
        val EXAMPLE_TAB: RegistryObject<CreativeModeTab> = CREATIVE_MODE_TABS.register("example_tab") {
            CreativeModeTab.builder()
                .withTabsBefore(CreativeModeTabs.COMBAT)
                .icon { EXAMPLE_ITEM.get().defaultInstance }
                .displayItems { parameters: CreativeModeTab.ItemDisplayParameters?, output: CreativeModeTab.Output ->
                    output.accept(
                        EXAMPLE_ITEM.get()
                    ) // Add the example item to the tab. For your own tabs, this method is preferred over the event
                }.build()
        }
    }
}
