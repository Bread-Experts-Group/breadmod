package bread.mod.breadmod.registry.config

import bread.mod.breadmod.ModMainCommon.MOD_ID

object ClientConfig : BreadModConfig() {
    lateinit var ALT_TOOLGUN_MODEL: ConfigValue<Boolean>

    override fun fileName(): String = "$MOD_ID-client"

    override fun registerValues() {
        ALT_TOOLGUN_MODEL = getOrDefault(
            "alt_toolgun_model",
            ConfigValue.Builder<Boolean>()
                .define(false, "alt_toolgun_model")
                .comment("Toggle for the alt toolgun model")
        )
    }
}