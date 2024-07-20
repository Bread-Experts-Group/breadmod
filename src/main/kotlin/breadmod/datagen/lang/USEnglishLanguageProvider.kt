package breadmod.datagen.lang

import breadmod.ModMain
import breadmod.ModMain.modAdd
import breadmod.ModMain.modAddExt
import breadmod.compat.jade.JadePlugin.Companion.TOOLTIP_RENDERER
import breadmod.datagen.tool_gun.BreadModToolGunModeProvider.Companion.TOOL_GUN_DEF
import breadmod.registry.block.ModBlocks
import breadmod.registry.fluid.ModFluids
import breadmod.registry.item.ModItems
import breadmod.registry.menu.ModCreativeTabs
import net.minecraft.data.PackOutput
import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.level.ItemLike
import net.minecraftforge.common.data.LanguageProvider
import net.minecraftforge.fluids.FluidType
import net.minecraftforge.registries.RegistryObject

@Suppress("SpellCheckingInspection")
class USEnglishLanguageProvider(
    packOutput: PackOutput,
    locale: String
) : LanguageProvider(packOutput, ModMain.ID, locale) {
    private fun String.joinUnderscoreWithCaps() =
        this.split("_").joinToString(" ") { it.replaceFirstChar { char -> char.uppercaseChar() } }

    // Transforms type.mod_id.example_object into Example Object
    private fun String.addTransformed(override: String? = null) = add(this, override ?: this.substringAfterLast('.').joinUnderscoreWithCaps())

    private inline fun <reified T> add(obj: RegistryObject<T>, override: String? = null) = when(val entry = obj.get()) {
        is ItemLike -> entry.asItem().descriptionId.addTransformed(override)
        is FluidType -> entry.descriptionId.addTransformed(override)
        is CreativeModeTab -> add("itemGroup.${ModMain.ID}.${obj.id.path}", override ?: obj.id.path.joinUnderscoreWithCaps())
        else -> throw IllegalArgumentException("Object provided, ${T::class.qualifiedName}, cannot be added")
    }

    override fun addTranslations() {
        add(ModBlocks.BREAD_BLOCK)
        add(ModItems.TEST_DISC, "Test Music Disc")
        add(ModItems.TEST_BREAD)
        add(ModItems.BREAD_SHIELD)
        add(ModItems.BREAD_HELMET)
        add(ModItems.BREAD_CHESTPLATE)
        add(ModItems.BREAD_LEGGINGS)
        add(ModItems.BREAD_BOOTS)
        add(ModItems.DOPED_BREAD)
        add(ModItems.BREAD_SHOVEL)
        add(ModItems.BREAD_HOE)
        add(ModItems.BREAD_AXE)
        add(ModItems.BREAD_PICKAXE)
        add(ModItems.BREAD_SWORD)
        add(ModItems.RF_BREAD_SHOVEL)
        add(ModItems.RF_BREAD_HOE)
        add(ModItems.RF_BREAD_AXE)
        add(ModItems.RF_BREAD_PICKAXE)
        add(ModItems.RF_BREAD_SWORD)
        add(ModItems.RF_BREAD_HELMET)
        add(ModItems.RF_BREAD_CHESTPLATE)
        add(ModItems.RF_BREAD_LEGGINGS)
        add(ModItems.RF_BREAD_BOOTS)
        add(ModItems.BREAD_SLICE)
        add(ModBlocks.REINFORCED_BREAD_BLOCK)
        add(ModBlocks.CHARCOAL_BLOCK)
        add(ModBlocks.DOUGH_MACHINE_BLOCK)
        add(ModBlocks.LOW_DENSITY_CHARCOAL_BLOCK, "Low-Density Charcoal Block")
        add(ModBlocks.HAPPY_BLOCK)
        add(ModBlocks.HEATING_ELEMENT_BLOCK)
        add(ModItems.DOUGH)
        add(ModItems.FLOUR)
        add(ModBlocks.FLOUR_BLOCK)
        add(ModBlocks.FLOUR_LAYER_BLOCK, "Flour")
        add(ModBlocks.BAUXITE_ORE)
        add(ModBlocks.MONITOR)
        add(ModBlocks.KEYBOARD)
        add(ModItems.ULTIMATE_BREAD)
        add(ModFluids.BREAD_LIQUID.type)
        add(ModFluids.BREAD_LIQUID.bucket)
        add(ModItems.BASIC_BREAD_AMULET)
        add(ModItems.REINFORCED_BREAD_AMULET)
        add(ModItems.INDESTRUCTIBLE_BREAD_AMULET)
        add(ModItems.KNIFE)
        add(ModBlocks.BREAD_DOOR)
        add(ModBlocks.BREAD_FENCE)
        add(ModBlocks.WHEAT_CRUSHER_BLOCK)
        add(ModItems.BREAD_GUN_ITEM)
        add(ModItems.BREAD_BULLET_ITEM)
        add(ModItems.TOOL_GUN)
        add(ModBlocks.GENERATOR)
        add(ModItems.CREATURE)
        add(ModItems.CAPRISPIN)
        add(ModItems.TOAST_SLICE)
        add(ModItems.TOASTED_BREAD)
        add(ModBlocks.TOASTER)
        add(ModItems.TOASTER_HEATING_ELEMENT)
        add(ModBlocks.CREATIVE_GENERATOR)
        add(ModBlocks.NIKO_BLOCK, "Niko Tenshot")
        add(ModBlocks.OMANEKO_BLOCK, "OMANEKO")
        add(ModItems.CERTIFICATE)

        add(ModCreativeTabs.MAIN_TAB, "Bread Mod")
        add(ModCreativeTabs.SPECIALS_TAB, "Bread Mod: Specials")
        // Farmer
        add(ModBlocks.FARMER_CONTROLLER, "Farmer")
        add(ModBlocks.FARMER_BASE_BLOCK)
        add(ModBlocks.FARMER_INPUT_BLOCK, "Farmer Input")
        add(ModBlocks.FARMER_OUTPUT_BLOCK, "Farmer Output")
        add(ModBlocks.GENERIC_POWER_INTERFACE)
        ////
        modAdd("Hell Naw Button",
            "block", "hell_naw")
        modAdd("Wouldn't be official without some light blue dye, would it?",
            "item", "certificate", "description"
        )
        modAdd(
            "Feeds %s every %s",
            "item", "bread_amulet", "description"
        )
        modAdd("%ss",
            "jei","generic","recipe_time"
        )
        modAdd(
            "(stacking!)",
            "item", "bread_amulet", "stacks"
        )
        modAdd("I wouldn't cook charcoal in it..",
            "toaster", "tooltip")

        modAdd(
            "Effect Range: %s %s",
            "item", "bread_armor", "range"
        )

        modAdd(
            "HAPPY HAPPY HAPPY",
            "entity", "happy_block"
        )

        modAdd(
            "Music Disc Description",
            "item", "music_disc_test", "desc" // TODO... make our own record. :3
        )
        modAdd(
            "Identical to bread on the outside - tumors on the inside.",
            "item", "test_bread", "tooltip"
        )
        modAdd(
            "No it does NOT look like balsa wood >:(",
            "item", "bread_shield", "tooltip"
        )
        modAdd(
            "contains trace amounts of neurotoxin",
            "item", "doped_bread", "tooltip"
        )

        modAdd("His name is jeff",
            "painting", "painting_test", "title")
        modAdd("gchris123",
            "painting", "painting_test", "author")
        modAdd("dubious creature",
            "painting", "devil_pupp", "title")
        modAdd("https://twitter.com/devil_pupp",
            "painting", "devil_pupp", "author")
        modAdd("FISH",
            "painting", "fish", "title")
        modAdd("FISH ARTIST",
            "painting", "fish", "author")

        modAdd("Energy", path = arrayOf("energy"))
        modAdd("Input", path = arrayOf("input"))
        modAdd("Output", path = arrayOf("output"))

        // Tool Gun
        modAdd("Toolgun",
            "controls", "category", TOOL_GUN_DEF)
        modAdd("Switch Mode",
            "controls", TOOL_GUN_DEF, "change_mode")
        modAdd("Mode is in a broken state. Run /data get entity @s and report this as a bug.",
            TOOL_GUN_DEF, "broken_tooltip")

        // Remover Action
        modAdd("Remover",
            TOOL_GUN_DEF, "mode", "display_name", "remover")
        modAdd("Remove entities with right click.",
            TOOL_GUN_DEF, "mode", "tooltip", "remover")
        modAdd("... to remove the entity you're looking at.",
            TOOL_GUN_DEF, "mode", "key_tooltip", "remover", "rmb")

        modAdd("Remove Entity",
            TOOL_GUN_DEF, "mode", "controls", "name", "remover", "rmb")
        modAdd("Toolgun: Remover",
            TOOL_GUN_DEF, "mode", "controls", "category", "remover")

        // Creator action
        modAdd("Creator",
            TOOL_GUN_DEF, "mode", "display_name", "creator")
        modAdd("Add entities/blocks with right click.",
            TOOL_GUN_DEF, "mode", "tooltip", "creator")
        modAdd("... to add an entity.",
            TOOL_GUN_DEF, "mode", "key_tooltip", "creator", "rmb")

        modAdd("Create Entity",
            TOOL_GUN_DEF, "mode", "controls", "name", "creator", "rmb")
        modAdd("Toolgun: Creator",
            TOOL_GUN_DEF, "mode", "controls", "category", "creator")

        // Power action
        modAdd("Power",
            TOOL_GUN_DEF, "mode", "display_name", "power")
        modAdd("Turns off the \"power.\"",
            TOOL_GUN_DEF, "mode", "tooltip", "power")
        modAdd("... to turn off the \"power.\"",
            TOOL_GUN_DEF, "mode", "key_tooltip", "power", "rmb")

        modAdd("Power Off (5 Times)",
            TOOL_GUN_DEF, "mode", "controls", "name", "power", "rmb")
        modAdd("Toolgun: Power",
            TOOL_GUN_DEF, "mode", "controls", "category", "power")

        // Explode action
        modAdd("Exploder",
            TOOL_GUN_DEF, "mode", "display_name", "explode")
        modAdd("Explodes whatever surface you're pointing at.",
            TOOL_GUN_DEF, "mode", "tooltip", "explode")
        modAdd("... to explode the block you're looking at.",
            TOOL_GUN_DEF, "mode", "key_tooltip", "explode", "rmb")
        modAdd("... to target fluids.",
            TOOL_GUN_DEF, "mode", "key_tooltip", "explode", "mmb", "off")
        modAdd("... to not target fluids.",
            TOOL_GUN_DEF, "mode", "key_tooltip", "explode", "mmb", "on") // TODO

        modAdd("Explode",
            TOOL_GUN_DEF, "mode", "controls", "name", "explode", "rmb")
        modAdd("Target Fluids",
            TOOL_GUN_DEF, "mode", "controls", "name", "explode", "mmb")
        modAdd("Toolgun: Exploder",
            TOOL_GUN_DEF, "mode", "controls", "category", "explode")

        modAdd("Targeting Fluids:",
            TOOL_GUN_DEF, "mode", "explode", "hit_fluid")
        modAdd("Enabled",
            TOOL_GUN_DEF, "mode", "explode", "hit_fluid", "enabled")
        modAdd("Disabled",
            TOOL_GUN_DEF, "mode", "explode", "hit_fluid", "disabled")

        // Misc
        modAdd("%s left the game",
            "item", TOOL_GUN_DEF, "entity_left_game")
        modAdd("Disconnect: Client 0 overflowed reliable channel.",
            "item", TOOL_GUN_DEF, "player_left_game")
        modAdd("Current Mode: ",
            "item", TOOL_GUN_DEF, "tooltip", "current_mode")
        modAdd(" to switch modes",
            "item", TOOL_GUN_DEF, "tooltip", "mode_switch")
        // Compat
        // JEI
        modAdd(
            "THE BREAD BLOCK IS REAL",
            "jei", "bread_block", "description"
        )
        // Curios
        modAddExt("Bread Orb", "curios", "identifier", "bread_orb")
        // Project E
        ModItems.PROJECT_E?.also { items ->
            add(items.BREAD_ORB_ITEM, "Bread Orb")
            modAdd("An EMC battery made of... bread?", "item", "bread_orb", "tooltip")
        }
        // // Jade
        modAddExt(TOOLTIP_RENDERER.second, "config", "jade", "plugin_", TOOLTIP_RENDERER.first.toLanguageKey())
    }
}