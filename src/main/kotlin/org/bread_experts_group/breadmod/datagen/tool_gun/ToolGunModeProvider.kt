package org.bread_experts_group.breadmod.datagen.tool_gun

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import net.minecraft.data.CachedOutput
import net.minecraft.data.DataProvider
import net.minecraft.data.PackOutput
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.ComponentSerialization
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import org.bread_experts_group.breadmod.api.IToolGunMode
import org.bread_experts_group.breadmod.util.componentToJson
import java.util.concurrent.CompletableFuture

abstract class ToolGunModeProvider(private val packOutput: PackOutput, private val modID: String) : DataProvider {
    data class Control(
        val id: String,
        val nameKey: String,
        val categoryKey: String,
        val toolGunComponent: Component,
        val key: String,
        val modifier: String = "none"
    ) {
        companion object {
            val CODEC: StreamCodec<RegistryFriendlyByteBuf, Control> = StreamCodec.composite(
                ByteBufCodecs.STRING_UTF8, Control::id,
                ByteBufCodecs.STRING_UTF8, Control::nameKey,
                ByteBufCodecs.STRING_UTF8, Control::categoryKey,
                ComponentSerialization.STREAM_CODEC, Control::toolGunComponent,
                ByteBufCodecs.STRING_UTF8, Control::key,
                ByteBufCodecs.STRING_UTF8, Control::modifier,
                ToolGunModeProvider::Control
            )

            val EMPTY = Control("breadmod", "empty", "none", Component.empty(), "none")
        }
    }

    private val addedModes: MutableMap<String, Triple<Pair<Component, Component>, List<Control>, Class<*>>> =
        mutableMapOf()

    override fun run(output: CachedOutput): CompletableFuture<*> {
        addModes()
        val dataLocation =
            packOutput.getOutputFolder(PackOutput.Target.DATA_PACK).resolve(modID).resolve(TOOL_GUN_DEF).resolve("mode")
        return CompletableFuture.allOf(
            *buildList {
                addedModes.forEach { (name, data) ->
                    add(DataProvider.saveStable(output, JsonObject().also {
                        it.add(DISPLAY_NAME_KEY, componentToJson(data.first.first))
                        it.add(TOOLTIP_KEY, componentToJson(data.first.second))
                        it.add(KEYBINDS_KEY, JsonArray().also { array ->
                            data.second.forEach {
                                array.add(JsonObject().also { keyObj ->
                                    keyObj.addProperty(CONTROLS_ID_KEY, it.id)
                                    keyObj.addProperty(KEY_ENTRY_KEY, it.key)
                                    keyObj.addProperty(MODIFIER_ENTRY_KEY, it.modifier)
                                    keyObj.addProperty(CONTROLS_NAME_TRANSLATION_KEY, it.nameKey)
                                    keyObj.addProperty(CONTROLS_CATEGORY_TRANSLATION_KEY, it.categoryKey)
                                    keyObj.add(TOOLGUN_INFO_DISPLAY_KEY, componentToJson(it.toolGunComponent))
                                })
                            }
                        })
                        it.addProperty(CLASS_KEY, data.third.kotlin.qualifiedName)
                    }, dataLocation.resolve("$name.json")))
                }
            }.toTypedArray()
        )
    }

    abstract fun addModes()

    fun <T : IToolGunMode> addMode(
        name: String,
        displayName: Component, tooltip: Component,
        keyActions: List<Control>,
        actionClass: Class<T>
    ) {
        if (addedModes.containsKey(name)) throw IllegalStateException("There already exists a tool gun mode for $modID/$name!")
        addedModes[name] = Triple(displayName to tooltip, keyActions, actionClass)
    }

    override fun getName(): String = "Toolgun Modes: $modID"

    companion object {
        const val TOOL_GUN_DEF = "tool_gun"

        const val CONTROLS_ID_KEY = "id"
        const val CONTROLS_NAME_TRANSLATION_KEY = "controls_name_key"
        const val CONTROLS_CATEGORY_TRANSLATION_KEY = "controls_category_key"
        const val TOOLGUN_INFO_DISPLAY_KEY = "${TOOL_GUN_DEF}_key"
        const val KEY_ENTRY_KEY = "key"
        const val MODIFIER_ENTRY_KEY = "modifier"

        const val KEYBINDS_KEY = "keybinds"
        const val CLASS_KEY = "class"
        const val DISPLAY_NAME_KEY = "display_name"
        const val TOOLTIP_KEY = "tooltip"
    }
}