package bread.mod.breadmod.neoforge.config

import net.neoforged.neoforge.common.ModConfigSpec


object CommonConfigSpecification {
    private val BUILDER = ModConfigSpec.Builder()
    val SPECIFICATION: ModConfigSpec

    // TODO: Abstract this into common
    val TEST_VALUE: ModConfigSpec.ConfigValue<Int>

    init {
        BUILDER
            .push("test")
            .comment("This is a test")
        TEST_VALUE = BUILDER
            .comment("Comment")
            .translation("bb2froggit")
            .define("config_value_name", 5)
        BUILDER.pop()

        SPECIFICATION = BUILDER.build()
    }
}