package bread.mod.breadmod.registry.config

object ClientConfig : BreadModConfig() {
    lateinit var ALT_TOOLGUN_MODEL: ConfigValue<Boolean>
    override fun fileName(): String = "breadmod-client"

    override fun registerValues() {
        ALT_TOOLGUN_MODEL = getOrDefault(
            "alt_toolgun_model", json,
            ConfigValue.Builder<Boolean>()
                .define(true, "alt_toolgun_model")
                .comment("Toggle for the alt toolgun model")
        )
    }
}