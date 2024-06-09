package breadmod.datagen.toolgun

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import net.minecraft.client.KeyMapping
import net.minecraft.data.CachedOutput
import net.minecraft.data.DataProvider
import net.minecraft.data.PackOutput
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.contents.LiteralContents
import net.minecraft.network.chat.contents.TranslatableContents
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
    private val addedModes: MutableMap<String, Triple<Pair<Component, Component>, List<KeyMapping>, Class<*>>> = mutableMapOf()
    final override fun run(p0: CachedOutput): CompletableFuture<*> {
        addModes()
        val dataLocation = packOutput.getOutputFolder(PackOutput.Target.DATA_PACK).resolve(modID).resolve("toolgun").resolve("mode")
        return CompletableFuture.allOf(
            *buildList {
                addedModes.forEach { (name, data) ->
                    add(DataProvider.saveStable(p0, JsonObject().also {
                        it.add("display_name", componentToJson(data.first.first))
                        it.add("tooltip", componentToJson(data.first.second))
                        it.add("keybinds", JsonArray().also { array ->
                            data.second.forEach {
                                array.add(JsonObject().also { keyObj ->
                                    keyObj.add("message", componentToJson(it.translatedKeyMessage))
                                    keyObj.addProperty("key", it.key.value)
                                    // TODO! This is certain to break key saving.
                                })
                            }
                        })
                        it.addProperty("class", data.third.kotlin.qualifiedName)
                    }, dataLocation.resolve("$name.json")))
                }
            }.toTypedArray()
        )
    }

    private fun componentToJson(component: Component) = JsonObject().also {
        when(val contents = component.contents) {
            is TranslatableContents -> {
                it.addProperty("type", "translate")
                it.addProperty("key", contents.key)
                it.addProperty("fallback", contents.fallback)
                if(contents.args.isNotEmpty()) throw IllegalArgumentException("Arguments not supposed for jsonifying translatable contents - sorry!")
            }
            is LiteralContents -> {
                it.addProperty("type", "literal")
                it.addProperty("text", contents.text)
            }
            else -> throw IllegalArgumentException("Illegal contents: ${contents::class.qualifiedName} - please dbg.")
        }
    }

    abstract fun addModes()

    fun <T: IToolgunMode> addMode(
        name: String,
        displayName: Component, tooltip: Component,
        keyActions: List<KeyMapping>,
        actionClass: Class<T>
    ) {
        if(addedModes.containsKey(name)) throw IllegalStateException("There already exists a toolgun mode for $modID/$name!")
        addedModes[name] = Triple(displayName to tooltip, keyActions, actionClass)
    }

    final override fun getName(): String = "Toolgun Modes: $modID"
}