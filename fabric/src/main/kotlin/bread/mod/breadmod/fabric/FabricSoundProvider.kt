package bread.mod.breadmod.fabric

import bread.mod.breadmod.ModMainCommon
import com.google.gson.JsonObject
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.minecraft.core.HolderLookup
import net.minecraft.data.CachedOutput
import net.minecraft.data.DataProvider
import net.minecraft.data.PackOutput
import net.minecraft.resources.ResourceLocation
import net.minecraft.sounds.SoundEvent
import java.util.concurrent.CompletableFuture

abstract class FabricSoundProvider(
    val dataOutput: FabricDataOutput,
    val registryLookup: CompletableFuture<HolderLookup.Provider>
) : DataProvider {
    val soundMap: MutableMap<Pair<String, SoundEvent>, Triple<Float, Float, Boolean>> = mutableMapOf()

    abstract fun registerSounds()

    override fun run(output: CachedOutput): CompletableFuture<*> {
        soundMap.clear()
        registerSounds()

        return registryLookup.thenCompose { lookup ->
            val soundJson = JsonObject()

            soundMap.forEach { (key, value) ->
                val soundLoc = key.second.location
                soundJson.add(key.first, JsonObject().also { json ->
                    json.add("sounds", JsonObject().also { sounds ->
                        sounds.addProperty("name", "${soundLoc.namespace}:${soundLoc.path}")
                        sounds.addProperty("volume", value.first)
                        sounds.addProperty("pitch", value.second)
                        sounds.addProperty("stream", value.third)
                    })
                    json.addProperty("subtitle", "sound.${ModMainCommon.MOD_ID}.${value.first}")
                })
            }

            DataProvider.saveStable(output, soundJson, getSoundPath())
        }
    }

    private fun getSoundPath() =
        dataOutput
            .createPathProvider(PackOutput.Target.RESOURCE_PACK, "sound")
            .json(ResourceLocation.fromNamespaceAndPath(dataOutput.modId, "sounds.json"))

    fun add(name: String, sound: SoundEvent, volume: Float, pitch: Float, stream: Boolean) =
        soundMap[name to sound] == Triple(volume, pitch, stream)

    override fun getName(): String = "Fabric Sound Generation"
}