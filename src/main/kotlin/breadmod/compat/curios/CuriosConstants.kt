package breadmod.compat.curios

import net.minecraftforge.common.data.LanguageProvider

val BREAD_ORB_SLOT = "bread_orb" to "Bread Orb"

fun LanguageProvider.addCurios(value: Pair<String, String>) =
    add("curios.identifier.${value.first}", value.second)