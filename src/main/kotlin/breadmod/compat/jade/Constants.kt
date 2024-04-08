package breadmod.compat.jade

import breadmod.BreadMod
import net.minecraft.resources.ResourceLocation
import net.minecraftforge.common.data.LanguageProvider

val TOOLTIP_RENDERER = BreadMod.modLocation("tooltip_renderer") to "Jade Tooltip Renderer"

fun LanguageProvider.addJade(value: Pair<ResourceLocation, String>) =
    add("config.jade.plugin_${value.first.toLanguageKey()}", value.second)