package breadmod.datagen.toolgun

import net.minecraft.client.KeyMapping
import net.minecraft.data.CachedOutput
import net.minecraft.data.DataProvider
import net.minecraft.data.PackOutput
import net.minecraft.network.chat.Component
import java.util.concurrent.CompletableFuture

abstract class BreadModToolgunModeProvider(private val packOutput: PackOutput, private val modID: String): DataProvider {
    private val addedModes: MutableMap<String, Triple<Pair<Component, Component>, List<KeyMapping>, Class<*>>> = mutableMapOf()
    final override fun run(p0: CachedOutput): CompletableFuture<*> {
        addModes()
        return CompletableFuture.allOf(
            *buildList {
                addedModes.forEach { _ -> add(DataProvider.saveStable(p0, com.google.gson.JsonObject(), packOutput.outputFolder.resolve("test.json"))) }
            }.toTypedArray()
        )
    }

    abstract fun addModes()

    fun <T: IToolgunMode> addMode(
        name: String,
        displayName: Component, tooltip: Component,
        keyActions: List<KeyMapping>,
        actionClass: Class<T>
    ) {
        addedModes[name] = Triple(displayName to tooltip, keyActions, actionClass)
    }

    final override fun getName(): String = "Toolgun Modes: $modID"
}