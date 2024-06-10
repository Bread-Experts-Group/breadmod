package breadmod.datagen.tool_gun

import breadmod.ClientModEventBus
import breadmod.ClientModEventBus.toolGunBindList
import breadmod.ModMain
import breadmod.datagen.tool_gun.BreadModToolGunModeProvider.Companion.CLASS_KEY
import breadmod.datagen.tool_gun.BreadModToolGunModeProvider.Companion.CONTROLS_CATEGORY_TRANSLATION_KEY
import breadmod.datagen.tool_gun.BreadModToolGunModeProvider.Companion.CONTROLS_ID_KEY
import breadmod.datagen.tool_gun.BreadModToolGunModeProvider.Companion.CONTROLS_NAME_TRANSLATION_KEY
import breadmod.datagen.tool_gun.BreadModToolGunModeProvider.Companion.DISPLAY_NAME_KEY
import breadmod.datagen.tool_gun.BreadModToolGunModeProvider.Companion.KEYBINDS_KEY
import breadmod.datagen.tool_gun.BreadModToolGunModeProvider.Companion.KEY_ENTRY_KEY
import breadmod.datagen.tool_gun.BreadModToolGunModeProvider.Companion.MODIFIER_ENTRY_KEY
import breadmod.datagen.tool_gun.BreadModToolGunModeProvider.Companion.TOOLGUN_INFO_DISPLAY_KEY
import breadmod.datagen.tool_gun.BreadModToolGunModeProvider.Companion.TOOLTIP_KEY
import breadmod.datagen.tool_gun.BreadModToolGunModeProvider.Companion.TOOL_GUN_DEF
import breadmod.util.jsonToComponent
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.mojang.blaze3d.platform.InputConstants
import net.minecraft.client.Minecraft
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.packs.resources.ResourceManager
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener
import net.minecraft.util.profiling.ProfilerFiller
import net.minecraftforge.client.settings.KeyModifier
import org.apache.commons.lang3.ArrayUtils
import org.jetbrains.annotations.ApiStatus.Internal
import kotlin.reflect.full.allSuperclasses
import kotlin.reflect.full.isSubclassOf
import kotlin.reflect.full.primaryConstructor
import kotlin.reflect.jvm.isAccessible

@Internal
internal object ModToolGunModeDataLoader : SimpleJsonResourceReloadListener(Gson(), TOOL_GUN_DEF) {
    data class ToolgunMode internal constructor(
        val displayName: Component,
        val tooltip: Component,
        val keyBinds: List<BreadModToolGunModeProvider.Control>,
        val mode: IToolGunMode
    )

    private val loadedModes: MutableMap<String, MutableMap<String, ToolgunMode>> = mutableMapOf()
    val modes: Map<String, Map<String, ToolgunMode>>
        get() = loadedModes

    override fun apply(
        pObject: MutableMap<ResourceLocation, JsonElement>,
        pResourceManager: ResourceManager,
        pProfiler: ProfilerFiller
    ) {
        pProfiler.push("Load tool gun data")
        pObject.forEach { (location, data) ->
            if(location.path.startsWith("mode/")) {
                val dataObj = data.asJsonObject
                val classSet = loadedModes.getOrPut(location.namespace) { mutableMapOf() }
                val loadedClass = Class.forName(dataObj.getAsJsonPrimitive(CLASS_KEY).asString).kotlin
                loadedClass.allSuperclasses
                if(loadedClass.isSubclassOf(IToolGunMode::class)) {
                    val classConstructor = loadedClass.primaryConstructor!!
                    classConstructor.isAccessible = true
                    classSet[location.path.substringAfter("mode/")] = ToolgunMode(
                        displayName = jsonToComponent(dataObj.getAsJsonObject(DISPLAY_NAME_KEY)),
                        tooltip = jsonToComponent(dataObj.getAsJsonObject(TOOLTIP_KEY)),
                        keyBinds = buildList {
                            dataObj.getAsJsonArray(KEYBINDS_KEY).forEach {
                                val keybind = it.asJsonObject
                                val control = BreadModToolGunModeProvider.Control(
                                    keybind.getAsJsonPrimitive(CONTROLS_ID_KEY).asString,
                                    keybind.getAsJsonPrimitive(CONTROLS_NAME_TRANSLATION_KEY).asString,
                                    keybind.getAsJsonPrimitive(CONTROLS_CATEGORY_TRANSLATION_KEY).asString,
                                    jsonToComponent(keybind.getAsJsonObject(TOOLGUN_INFO_DISPLAY_KEY)),
                                    { InputConstants.getKey(keybind.getAsJsonPrimitive(KEY_ENTRY_KEY).asString) },
                                    keybind.getAsJsonPrimitive(MODIFIER_ENTRY_KEY)?.asString?.let { mod -> KeyModifier.getModifier(InputConstants.getKey(mod)) }
                                )
                                toolGunBindList[control] = null
                                add(control)
                            }
                        },
                        mode = classConstructor.call() as IToolGunMode
                    )
                    classConstructor.isAccessible = false
                } else throw IllegalArgumentException("Class parameter for tool gun mode $location is invalid. Loaded an instance of ${loadedClass.qualifiedName}, expected a subclass of ${IToolGunMode::class.qualifiedName}")
            }
        }
        pProfiler.pop()
        try {
            pProfiler.push("Load controls from toolgun data")
            val options = Minecraft.getInstance().options
            val keyMaps = ClientModEventBus.createMappingsForControls()
            options.keyMappings = ArrayUtils.addAll(
                options.keyMappings,
                *keyMaps.filter { toolgunMap -> options.keyMappings.firstOrNull { it == toolgunMap } == null }.toTypedArray()
            )
            pProfiler.pop()
        } catch(e: RuntimeException) {
            ModMain.LOGGER.info("We're not on the client, so we're gonna skip over toolgun mode key maps.")
        }
    }
}