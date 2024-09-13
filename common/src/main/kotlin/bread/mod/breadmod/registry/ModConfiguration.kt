package bread.mod.breadmod.registry

import eu.midnightdust.lib.config.MidnightConfig

class ModConfiguration : MidnightConfig() {
    /* https://www.midnightdust.eu/wiki/midnightlib/ */
    companion object {
        const val TEST: String = "test"

        @Comment(category = TEST)
        lateinit var text1: Comment

        @Entry(category = TEST)
        val showInfo = true

        @Entry(category = TEST)
        val testInt = 0
    }
}