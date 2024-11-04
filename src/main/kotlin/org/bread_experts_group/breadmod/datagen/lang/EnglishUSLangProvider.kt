package org.bread_experts_group.breadmod.datagen.lang

import net.minecraft.data.PackOutput
import net.minecraft.sounds.SoundEvent
import net.minecraft.world.entity.EntityType
import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.level.ItemLike
import net.neoforged.neoforge.common.data.LanguageProvider
import net.neoforged.neoforge.fluids.FluidType
import org.bread_experts_group.breadmod.Breadmod
import org.bread_experts_group.breadmod.Breadmod.Companion.modAdd
import org.bread_experts_group.breadmod.registry.block.ModBlocks
import org.bread_experts_group.breadmod.registry.entity.ModEntityTypes
import org.bread_experts_group.breadmod.registry.item.ModItems
import org.bread_experts_group.breadmod.registry.menu.ModCreativeTabs
import org.bread_experts_group.breadmod.registry.sound.ModSounds
import org.bread_experts_group.breadmod.util.ModDamageTypes
import java.util.function.Supplier

class EnglishUSLangProvider(
    output: PackOutput
) : LanguageProvider(output, Breadmod.ID, "en_us") {
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

        add(ModItems.TEST_RECORD, "Music Disc")
        add(ModItems.FLOUR)
        add(ModItems.CHEF_HAT)
        add(ModItems.TOOL_GUN)
        add(ModItems.TEST_BREAD)
        add(ModItems.ULTIMATE_BREAD)

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

        add(ModCreativeTabs.MAIN_TAB, "Bread Mod")
        add(ModCreativeTabs.SPECIALS_TAB, "Bread Mod: Specials")
    }
}