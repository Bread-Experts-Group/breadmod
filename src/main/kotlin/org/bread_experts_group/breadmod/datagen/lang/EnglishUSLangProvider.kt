package org.bread_experts_group.breadmod.datagen.lang

import net.minecraft.data.PackOutput
import net.minecraft.sounds.SoundEvent
import net.minecraft.world.entity.EntityType
import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.level.ItemLike
import net.neoforged.neoforge.common.data.LanguageProvider
import net.neoforged.neoforge.fluids.FluidType
import org.bread_experts_group.breadmod.BreadMod
import org.bread_experts_group.breadmod.BreadMod.Companion.modAdd
import org.bread_experts_group.breadmod.datagen.tool_gun.ToolGunModeProvider.Companion.TOOL_GUN_DEF
import org.bread_experts_group.breadmod.registry.block.ModBlocks
import org.bread_experts_group.breadmod.registry.entity.ModEntityTypes
import org.bread_experts_group.breadmod.registry.item.ModItems
import org.bread_experts_group.breadmod.registry.menu.ModCreativeTabs
import org.bread_experts_group.breadmod.registry.sound.ModSounds
import org.bread_experts_group.breadmod.util.ModDamageTypes
import java.util.function.Supplier

class EnglishUSLangProvider(
    output: PackOutput
) : LanguageProvider(output, BreadMod.ID, "en_us") {
    private fun String.joinUnderscoreWithCaps() =
        this.split("_").joinToString(" ") { it.replaceFirstChar { char -> char.uppercaseChar() } }

    // Transforms type.mod_id.example_object into Example Object
    private fun String.addTransformed(override: String? = null) =
        add(this, override ?: this.substringAfterLast('.').joinUnderscoreWithCaps())

    private inline fun <reified T> add(obj: Supplier<T>, override: String? = null) =
        when (val entry = obj.get()) {
            is ItemLike -> entry.asItem().descriptionId.addTransformed(override)
            is FluidType -> entry.descriptionId.addTransformed(override)
            is CreativeModeTab -> add(
                entry.displayName.string,
                override ?: entry.displayName.string.joinUnderscoreWithCaps()
            )

            is SoundEvent -> entry.location.toLanguageKey("sound").addTransformed(override)
            is EntityType<*> -> entry.descriptionId.addTransformed(override)

            else -> throw IllegalArgumentException("Object provided, ${T::class.qualifiedName}, cannot be added")
        }

    override fun addTranslations() {
        add(ModBlocks.BREAD_BLOCK)
        add(ModBlocks.FLOUR_BLOCK)
        add(ModBlocks.FLOUR_LAYER_BLOCK, "Flour")
        add(ModBlocks.HAPPY_BLOCK)
        add(ModBlocks.MONITOR)
        add(ModBlocks.REINFORCED_BREAD_BLOCK)
        add(ModBlocks.LOW_DENSITY_CHARCOAL_BLOCK, "Low-Density Charcoal Block")
        add(ModBlocks.CHARCOAL_BLOCK)
        add(ModBlocks.KEYBOARD)
        add(ModBlocks.HELL_NAW_BUTTON)
        add(ModBlocks.WAR_TERMINAL)
        add(ModBlocks.RANDOM_SOUND_BLOCK)
        add(ModBlocks.SOUND_BLOCK)
        add(ModBlocks.WHEAT_CRUSHER)
        add(ModBlocks.DOUGH_MACHINE)

        add(ModSounds.POW, "Pow!")
        add(ModSounds.TEST_SOUND, "Test sound plays")
        add(ModSounds.HAPPY_BLOCK_FUSE, "HAPPY HAPPY HAPPY")
        add(ModSounds.ULTRAMARINE, "Ultramarine plays")
        add(ModSounds.SCREAM, "AAAAAAAAAAAAAAA-")
        add(ModSounds.MINIGUN, "Minigun fires")
        add(ModSounds.TOOL_GUN, "Tool gun fires")
        add(ModSounds.HELL_NAW, "HELL NAW!")
        add(ModSounds.WAR_TIMER, "War Timer counts down")
        add(ModSounds.WAR_TIMER_UP, "War Timer increases")

        add(ModItems.RECORD_SECRET_HOPPIN, "Music Disc")
        add(ModItems.FLOUR)
        add(ModItems.CHEF_HAT)
        add(ModItems.TOOL_GUN)
        add(ModItems.TEST_BREAD)
        add(ModItems.ULTIMATE_BREAD)
        add(ModItems.BREAD_SHIELD)
        add(ModItems.DOPED_BREAD)
        add(ModItems.TOASTED_BREAD)
        add(ModItems.BREAD_SLICE)
        add(ModItems.DOUGH)
        add(ModItems.DIE)
        add(ModItems.BAGEL)
        add(ModItems.HALF_BAGEL)
        add(ModItems.ALUMINA)
        add(ModItems.BREAD_AMULET)
        add(ModItems.BREAD_PICKAXE)
        add(ModItems.BREAD_SHOVEL)
        add(ModItems.BREAD_AXE)
        add(ModItems.BREAD_HOE)
        add(ModItems.BREAD_SWORD)
        add(ModItems.RF_BREAD_PICKAXE)
        add(ModItems.RF_BREAD_SHOVEL)
        add(ModItems.RF_BREAD_AXE)
        add(ModItems.RF_BREAD_HOE)
        add(ModItems.RF_BREAD_SWORD)
        add(ModItems.BREAD_BULLET)
        add(ModItems.CAPRISPIN)
        add(ModItems.TOASTER_HEATING_ELEMENT)
        add(ModItems.CREATURE)
        add(ModItems.WRENCH)
        add(ModItems.BREAD_GUN)
        add(ModItems.CERTIFICATE)

        add(ModDamageTypes.TIMER_RAN_OUT.translationKey(), "%1\$s ran out of time!")

        add(ModEntityTypes.FAKE_PLAYER)

        modAdd(
            "ClascyJitto - Secret Hoppin'",
            "item", "music_disc_secret_hoppin", "desc"
        )
        modAdd(
            "IS THAT A PIZZA TOWER REFERENCE???",
            "item", "chef_hat", "tooltip"
        )
        modAdd(
            "Identical to bread on the outside - tumors on the inside.",
            "item", "test_bread", "tooltip"
        )
        modAdd(
            "Prolongs the inevitable.",
            "block", "war_terminal", "tooltip"
        )
        modAdd(
            "Uses the power of a die to make random noises",
            "block", "random_sound_block", "tooltip"
        )
        modAdd(
            "No it does NOT look like balsa wood >:(",
            "item", "bread_shield", "tooltip"
        )
        modAdd(
            "contains trace amounts of neurotoxin",
            "item", "doped_bread", "tooltip"
        )
        modAdd(
            "What? you thought it was gonna be sliced like a normal bagel?",
            "item", "half_bagel", "tooltip"
        )
        modAdd(
            "Feeds %s every %s",
            "item", "bread_amulet", "tooltip"
        )
        modAdd(
            "(stacking!)",
            "item", "bread_amulet", "stacks"
        )
        modAdd(
            "Wouldn't be official without some light blue dye, would it?",
            "item", "certificate", "tooltip"
        )

        modAdd("Energy", path = arrayOf("energy"))
        modAdd("Input", path = arrayOf("input"))
        modAdd("Output", path = arrayOf("output"))

        // Tool Gun //
        modAdd(
            "Toolgun",
            "controls", "category", TOOL_GUN_DEF
        )
        modAdd(
            "Bread Mod",
            "controls", "category"
        )
        modAdd(
            "Switch Mode",
            "controls", TOOL_GUN_DEF, "change_mode"
        )
        modAdd(
            "Open BM-GUI Editor",
            "controls", "gui_editor"
        )
        modAdd(
            "Mode is in a broken state. Run /data get entity @s and report this as a bug.",
            TOOL_GUN_DEF, "broken_tooltip"
        )

        // Remover Action
        modAdd(
            "Remover",
            TOOL_GUN_DEF, "mode", "display_name", "remover"
        )
        modAdd(
            "Remove entities with right click.",
            TOOL_GUN_DEF, "mode", "tooltip", "remover"
        )
        modAdd(
            "... to remove the entity you're looking at.",
            TOOL_GUN_DEF, "mode", "key_tooltip", "remover", "rmb"
        )

        modAdd(
            "Remove Entity",
            TOOL_GUN_DEF, "mode", "controls", "name", "remover", "rmb"
        )
        modAdd(
            "Toolgun: Remover",
            TOOL_GUN_DEF, "mode", "controls", "category", "remover"
        )
        modAdd(
            "%s left the game",
            "item", TOOL_GUN_DEF, "remover", "entity_left_game"
        )
        modAdd(
            "BreadMod: Disconnect: Client 0 overflowed reliable channel.",
            "item", TOOL_GUN_DEF, "remover", "player_left_game"
        )

        // Creator action
        modAdd(
            "Creator",
            TOOL_GUN_DEF, "mode", "display_name", "creator"
        )
        modAdd(
            "Add entities/blocks with right click.",
            TOOL_GUN_DEF, "mode", "tooltip", "creator"
        )
        modAdd(
            "Create Entity",
            TOOL_GUN_DEF, "mode", "controls", "name", "creator", "rmb"
        )
        modAdd(
            "... to add an entity.",
            TOOL_GUN_DEF, "mode", "key_tooltip", "creator", "rmb"
        )
        modAdd(
            "Open Entity Menu",
            TOOL_GUN_DEF, "mode", "controls", "name", "creator", "r"
        )
        modAdd(
            "... to open the entity/block editor.",
            TOOL_GUN_DEF, "mode", "key_tooltip", "creator", "r"
        )
        modAdd(
            "Save / Load",
            TOOL_GUN_DEF, "creator", "save_load"
        )
        modAdd(
            "expected %s, got %s",
            TOOL_GUN_DEF, "creator", "invalid_entity"
        )
        modAdd(
            "Toolgun: Creator",
            TOOL_GUN_DEF, "mode", "controls", "category", "creator"
        )
        modAdd(
            "BreadMod: Bad creator data packet timing",
            "item", TOOL_GUN_DEF, "creator", "bad_timing"
        )
        modAdd(
            "BreadMod: No creator data packet",
            "item", TOOL_GUN_DEF, "creator", "no_data"
        )

        // Power action
        modAdd(
            "Power",
            TOOL_GUN_DEF, "mode", "display_name", "power"
        )
        modAdd(
            "Turns off the \"power.\"",
            TOOL_GUN_DEF, "mode", "tooltip", "power"
        )
        modAdd(
            "... to turn off the \"power.\"",
            TOOL_GUN_DEF, "mode", "key_tooltip", "power", "rmb"
        )

        modAdd(
            "Power Off (5 Times)",
            TOOL_GUN_DEF, "mode", "controls", "name", "power", "rmb"
        )
        modAdd(
            "Toolgun: Power",
            TOOL_GUN_DEF, "mode", "controls", "category", "power"
        )

        // Explode action
        modAdd(
            "Exploder",
            TOOL_GUN_DEF, "mode", "display_name", "explode"
        )
        modAdd(
            "Explodes whatever surface you're pointing at.",
            TOOL_GUN_DEF, "mode", "tooltip", "explode"
        )
        modAdd(
            "... to explode the block you're looking at.",
            TOOL_GUN_DEF, "mode", "key_tooltip", "explode", "rmb"
        )
        modAdd(
            "... to target fluids.",
            TOOL_GUN_DEF, "mode", "key_tooltip", "explode", "mmb", "off"
        )
        modAdd(
            "... to not target fluids.",
            TOOL_GUN_DEF, "mode", "key_tooltip", "explode", "mmb", "on"
        )

        modAdd(
            "Explode",
            TOOL_GUN_DEF, "mode", "controls", "name", "explode", "rmb"
        )
        modAdd(
            "Target Fluids",
            TOOL_GUN_DEF, "mode", "controls", "name", "explode", "mmb"
        )
        modAdd(
            "Toolgun: Exploder",
            TOOL_GUN_DEF, "mode", "controls", "category", "explode"
        )

        modAdd(
            "Targeting Fluids:",
            TOOL_GUN_DEF, "mode", "explode", "hit_fluid"
        )
        modAdd(
            "Enabled",
            TOOL_GUN_DEF, "mode", "explode", "hit_fluid", "enabled"
        )
        modAdd(
            "Disabled",
            TOOL_GUN_DEF, "mode", "explode", "hit_fluid", "disabled"
        )

        // Misc
        modAdd(
            "Current Mode: ",
            "item", TOOL_GUN_DEF, "tooltip", "current_mode"
        )
        modAdd(
            " to switch modes",
            "item", TOOL_GUN_DEF, "tooltip", "mode_switch"
        )

        // End Tool Gun //

        add(ModCreativeTabs.MAIN_TAB, "Bread Mod")
        add(ModCreativeTabs.SPECIALS_TAB, "Bread Mod: Specials")
    }
}