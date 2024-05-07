package breadmod.datagen.lang

import breadmod.ModMain.modAdd
import breadmod.compat.jade.TOOLTIP_RENDERER
import breadmod.compat.jade.addJade
import breadmod.registry.block.ModBlocks
import breadmod.registry.fluid.ModFluids
import breadmod.registry.item.ModItems
import net.minecraft.data.PackOutput
import net.minecraft.world.level.ItemLike
import net.minecraftforge.common.data.LanguageProvider
import net.minecraftforge.fluids.FluidType
import net.minecraftforge.registries.RegistryObject

class USEnglishLanguageProvider(output: PackOutput, modID: String, locale: String) : LanguageProvider(output, modID, locale) {
    // Transforms type.mod_id.example_object into Example Object
    private fun String.addTransformed(override: String? = null) =
        if(override != null) add(this, override)
        else add(this, this.substringAfterLast('.').split("_").joinToString(" ") { it.replaceFirstChar { char -> char.uppercaseChar() } })

    private inline fun <reified T> add(obj: RegistryObject<T>, override: String? = null) = when(val entry = obj.get()) {
        is ItemLike -> entry.asItem().descriptionId.addTransformed(override)
        is FluidType -> entry.descriptionId.addTransformed(override)
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

        modAdd(
            "Bread Mod",
            "itemGroup", "main"
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

        modAdd("Energy", path = arrayOf("energy"))
        modAdd("Input", path = arrayOf("input"))
        modAdd("Output", path = arrayOf("output"))

        // Compat
        // // JEI
        modAdd(
            "THE BREAD BLOCK IS REAL",
            "jei", "bread_block", "description"
        )
        // // Jade
        addJade(TOOLTIP_RENDERER)
        // // ProjectE
        ModItems.PROJECT_E?.also {
            add(it.BREAD_EMC_ITEM, "Bread Orb")
        }
    }
}