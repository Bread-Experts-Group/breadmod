package breadmod.datagen.tool_gun

import breadmod.ClientForgeEventBus.changeMode
import breadmod.ClientModEventBus
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
import breadmod.item.tool_gun.IToolGunMode
import breadmod.util.jsonToComponent
import breadmod.util.render.minecraft
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.mojang.blaze3d.platform.InputConstants
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.packs.resources.ResourceManager
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener
import net.minecraft.util.profiling.ProfilerFiller
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.client.settings.KeyModifier
import net.minecraftforge.fml.DistExecutor
import org.apache.commons.lang3.ArrayUtils
import org.jetbrains.annotations.ApiStatus.Internal
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

    private val loadedModes: MutableMap<String, MutableMap<String, Triple<ToolgunMode, ResourceLocation, ByteArray>>> = mutableMapOf()
    val modes: Map<String, Map<String, Triple<ToolgunMode, ResourceLocation, ByteArray>>>
        get() = loadedModes

    override fun apply(
        pObject: MutableMap<ResourceLocation, JsonElement>,
        pResourceManager: ResourceManager,
        pProfiler: ProfilerFiller
    ) {
        pProfiler.push("Load tool gun data")
        load(pObject)
        pProfiler.pop()

        DistExecutor.unsafeRunWhenOn(Dist.CLIENT) {
            Runnable {
                pProfiler.push("Load controls from toolgun data")
                loadKeys()
                pProfiler.pop()
            }
        }
    }

    val keybindsToAdd: MutableList<BreadModToolGunModeProvider.Control> = mutableListOf()
    fun load(pObject: Map<ResourceLocation, JsonElement>) {
        pObject.forEach { (location, data) ->
            if(location.path.startsWith("mode/")) {
                try {
                    val dataObj = data.asJsonObject
                    val classSet = loadedModes.getOrPut(location.namespace) { mutableMapOf() }

                    val loadedClass =
                        Thread.currentThread().contextClassLoader.loadClass(dataObj.getAsJsonPrimitive(CLASS_KEY).asString).kotlin
                    if (loadedClass.isSubclassOf(IToolGunMode::class)) {
                        val classConstructor = loadedClass.primaryConstructor ?: return@forEach
                        classConstructor.isAccessible = true
                        classSet[location.path.substringAfter("mode/")] = Triple(
                            ToolgunMode(
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
                                            keybind.getAsJsonPrimitive(MODIFIER_ENTRY_KEY)?.asString?.let { mod ->
                                                KeyModifier.getModifier(
                                                    InputConstants.getKey(mod)
                                                )
                                            }
                                        )
                                        keybindsToAdd.add(control)
                                        add(control)
                                    }
                                },
                                mode = classConstructor.call() as IToolGunMode
                            ), location, dataObj.toString().encodeToByteArray())
                        classConstructor.isAccessible = false
                    } else throw IllegalArgumentException("Class parameter for tool gun mode $location is invalid. Loaded an instance of ${loadedClass.qualifiedName}, expected a subclass of ${IToolGunMode::class.qualifiedName}")
                } catch (e: ClassNotFoundException) {
                    ModMain.LOGGER.error("Failed to load a tool-gun mode: ${e.stackTraceToString()}")
                }
            }
        }
    }

    fun loadKeys() {
        val keyMaps = ClientModEventBus.createMappingsForControls(keybindsToAdd)
        minecraft.options.keyMappings = ArrayUtils.addAll(
            minecraft.options.keyMappings,
            changeMode,
            *keyMaps
                //.filter { toolgunMap -> minecraft.options.keyMappings.firstOrNull { it == toolgunMap } == null }
                .toTypedArray()
        )
    }
}