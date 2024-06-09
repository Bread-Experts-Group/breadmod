package breadmod.datagen.toolgun

import breadmod.util.componentToJson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.mojang.blaze3d.platform.InputConstants
import net.minecraft.data.CachedOutput
import net.minecraft.data.DataProvider
import net.minecraft.data.PackOutput
import net.minecraft.network.chat.Component
import net.minecraftforge.client.settings.KeyModifier
import java.util.concurrent.CompletableFuture

/**
 * Data generator for [breadmod.item.ToolGunItem]. Use this if your mod adds a mode.
 * Mode classes must implement [IToolgunMode].
 *
 * @see IToolgunMode
 * @author Miko Elbrecht
 * @since 1.0.0
 */
abstract class BreadModToolgunModeProvider(private val packOutput: PackOutput, private val modID: String): DataProvider {
    data class Control(
        val nameKey: String,
        val categoryKey: String,
        val toolgunComponent: Component,
        val key: InputConstants.Key,
        val modifier: KeyModifier? = null
    )
    private val addedModes: MutableMap<String, Triple<Pair<Component, Component>, List<Control>, Class<*>>> = mutableMapOf()

    final override fun run(p0: CachedOutput): CompletableFuture<*> {
        addModes()
        val dataLocation = packOutput.getOutputFolder(PackOutput.Target.DATA_PACK).resolve(modID).resolve("toolgun").resolve("mode")
        return CompletableFuture.allOf(
            *buildList {
                addedModes.forEach { (name, data) ->
                    add(DataProvider.saveStable(p0, JsonObject().also {
                        it.add(DISPLAY_NAME_KEY, componentToJson(data.first.first))
                        it.add(TOOLTIP_KEY, componentToJson(data.first.second))
                        it.add(KEYBINDS_KEY, JsonArray().also { array ->
                            data.second.forEach {
                                array.add(JsonObject().also { keyObj ->
                                    keyObj.addProperty(KEY_ENTRY_KEY, it.key.name)
                                    keyObj.addProperty(MODIFIER_ENTRY_KEY, it.modifier?.name)
                                    keyObj.addProperty(CONTROLS_NAME_TRANSLATION_KEY, it.nameKey)
                                    keyObj.addProperty(CONTROLS_CATEGORY_TRANSLATION_KEY, it.categoryKey)
                                    keyObj.add(TOOLGUN_INFO_DISPLAY_KEY, componentToJson(it.toolgunComponent))
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

    fun <T: IToolgunMode> addMode(
        name: String,
        displayName: Component, tooltip: Component,
        keyActions: List<Control>,
        actionClass: Class<T>
    ) {
        if(addedModes.containsKey(name)) throw IllegalStateException("There already exists a toolgun mode for $modID/$name!")
        addedModes[name] = Triple(displayName to tooltip, keyActions, actionClass)
    }

    final override fun getName(): String = "Toolgun Modes: $modID"

    internal companion object {
        const val CONTROLS_NAME_TRANSLATION_KEY = "controls_name_key"
        const val CONTROLS_CATEGORY_TRANSLATION_KEY = "controls_category_key"
        const val TOOLGUN_INFO_DISPLAY_KEY = "toolgun_key"
        const val KEY_ENTRY_KEY = "key"
        const val MODIFIER_ENTRY_KEY = "modifier"

        const val KEYBINDS_KEY = "keybinds"
        const val CLASS_KEY = "class"
        const val DISPLAY_NAME_KEY = "display_name"
        const val TOOLTIP_KEY = "tooltip"
    }
}