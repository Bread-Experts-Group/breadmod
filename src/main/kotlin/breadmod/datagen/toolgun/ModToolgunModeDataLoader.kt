package breadmod.datagen.toolgun

import breadmod.ClientModEventBus.toolgunBindList
import breadmod.datagen.toolgun.BreadModToolgunModeProvider.Companion.CLASS_KEY
import breadmod.datagen.toolgun.BreadModToolgunModeProvider.Companion.CONTROLS_CATEGORY_TRANSLATION_KEY
import breadmod.datagen.toolgun.BreadModToolgunModeProvider.Companion.CONTROLS_NAME_TRANSLATION_KEY
import breadmod.datagen.toolgun.BreadModToolgunModeProvider.Companion.DISPLAY_NAME_KEY
import breadmod.datagen.toolgun.BreadModToolgunModeProvider.Companion.KEYBINDS_KEY
import breadmod.datagen.toolgun.BreadModToolgunModeProvider.Companion.KEY_ENTRY_KEY
import breadmod.datagen.toolgun.BreadModToolgunModeProvider.Companion.TOOLGUN_INFO_DISPLAY_KEY
import breadmod.datagen.toolgun.BreadModToolgunModeProvider.Companion.TOOLTIP_KEY
import breadmod.util.jsonToComponent
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.mojang.blaze3d.platform.InputConstants
import net.minecraft.client.KeyMapping
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.packs.resources.ResourceManager
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener
import net.minecraft.util.profiling.ProfilerFiller
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraftforge.client.settings.KeyModifier
import org.jetbrains.annotations.ApiStatus.Internal
import kotlin.reflect.full.allSuperclasses
import kotlin.reflect.full.isSubclassOf
import kotlin.reflect.full.primaryConstructor
import kotlin.reflect.jvm.isAccessible

@Internal
internal object ModToolgunModeDataLoader : SimpleJsonResourceReloadListener(Gson(), "toolgun") {
    data class ToolgunMode internal constructor(
        val displayName: Component,
        val tooltip: Component,
        val keyBinds: List<KeyMapping>,
        val action: (Player, ItemStack) -> Unit
    )

    private val loadedModes: MutableMap<String, MutableMap<String, ToolgunMode>> = mutableMapOf()
    val modes: Map<String, Map<String, ToolgunMode>> = loadedModes

    override fun apply(
        pObject: MutableMap<ResourceLocation, JsonElement>,
        pResourceManager: ResourceManager,
        pProfiler: ProfilerFiller
    ) {
        pProfiler.push("Load toolgun data")
        pObject.forEach { (location, data) ->
            if(location.path.startsWith("mode/")) {
                val dataObj = data.asJsonObject
                val classSet = loadedModes.getOrPut(location.namespace) { mutableMapOf() }
                val loadedClass = Class.forName(dataObj.getAsJsonPrimitive(CLASS_KEY).asString).kotlin
                loadedClass.allSuperclasses
                if(loadedClass.isSubclassOf(IToolgunMode::class)) {
                    val classConstructor = loadedClass.primaryConstructor!!
                    classConstructor.isAccessible = true
                    classSet[location.path.substringAfter("mode/")] = ToolgunMode(
                        displayName = jsonToComponent(dataObj.getAsJsonObject(DISPLAY_NAME_KEY)),
                        tooltip = jsonToComponent(dataObj.getAsJsonObject(TOOLTIP_KEY)),
                        keyBinds = buildList {
                            dataObj.getAsJsonArray(KEYBINDS_KEY).forEach {
                                val keybind = it.asJsonObject
                                toolgunBindList[BreadModToolgunModeProvider.Control(
                                    keybind.getAsJsonPrimitive(CONTROLS_NAME_TRANSLATION_KEY).asString,
                                    keybind.getAsJsonPrimitive(CONTROLS_CATEGORY_TRANSLATION_KEY).asString,
                                    jsonToComponent(keybind.getAsJsonObject(TOOLGUN_INFO_DISPLAY_KEY)),
                                    InputConstants.getKey(keybind.getAsJsonPrimitive(KEY_ENTRY_KEY).asString),
                                    keybind.getAsJsonPrimitive("modifier")?.asString?.let { mod -> KeyModifier.getModifier(InputConstants.getKey(mod)) }
                                )] = null
                            }
                        },
                        action = (classConstructor.call() as IToolgunMode)::action
                    )
                    classConstructor.isAccessible = false
                } else throw IllegalArgumentException("Class parameter for toolgun mode $location is invalid. Loaded an instance of ${loadedClass.qualifiedName}, expected a subclass of ${IToolgunMode::class.qualifiedName}")
            }
        }
        println("Here's the classes I loaded: $loadedModes")
        pProfiler.pop()
    }
}