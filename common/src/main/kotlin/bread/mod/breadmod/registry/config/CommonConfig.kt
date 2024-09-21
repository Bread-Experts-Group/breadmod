package bread.mod.breadmod.registry.config

object CommonConfig : BreadModConfig() {
    lateinit var TEST_INT: ConfigValue<Int>
    lateinit var TEST_STRING: ConfigValue<String>
    override fun fileName(): String = "breadmod-common"

    override fun registerValues() {
        TEST_INT = getOrDefault(
            "test_int", json,
            ConfigValue.Builder<Int>()
                .define(69, "test_int")
                .comment("test int")
        )
        TEST_STRING = getOrDefault(
            "test_string", json,
            ConfigValue.Builder<String>()
                .define("test string value", "test_string")
                .comment("a test string")
        )
    }
}