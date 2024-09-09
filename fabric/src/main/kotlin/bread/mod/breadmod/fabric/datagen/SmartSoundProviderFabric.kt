package bread.mod.breadmod.fabric.datagen

import bread.mod.breadmod.datagen.sound.DataGenerateSound
import bread.mod.breadmod.datagen.sound.SmartSoundProvider
import com.google.common.hash.HashCode
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.minecraft.data.CachedOutput
import net.minecraft.data.DataProvider
import net.minecraft.sounds.SoundEvent
import java.util.concurrent.CompletableFuture

class SmartSoundProviderFabric(
    modID: String, forClassLoader: ClassLoader, forPackage: Package
) : SmartSoundProvider<FabricDataGenerator.Pack>(modID, forClassLoader, forPackage) {
    inner class FabricSoundProvider(
        private val dataOutput: FabricDataOutput,
        private val soundStore: Map<SoundEvent, Array<DataGenerateSound>>
    ) : DataProvider {
        override fun run(output: CachedOutput): CompletableFuture<*> = CompletableFuture.runAsync {
            val obj = JsonObject()
            soundStore.forEach { (event, data) ->
                val subObj = JsonObject()
                subObj.addProperty("subtitle", event.location.toLanguageKey("sound"))

                val sounds = JsonArray()
                data.forEach {
                    val soundObj = JsonObject()
                    soundObj.addProperty("name", modID + ":" + it.sound)
                    if (it.type != DataGenerateSound.Type.FILE) soundObj.addProperty("type", it.type.name.lowercase())

                    if (it.volume != 1.0f) soundObj.addProperty("volume", it.volume)
                    if (it.pitch != 1.0f) soundObj.addProperty("pitch", it.pitch)
                    if (it.weight != 1) soundObj.addProperty("weight", it.weight)
                    if (it.stream) soundObj.addProperty("stream", true)
                    if (it.attenuationDistance != 16) soundObj.addProperty("attenuation_distance", it.attenuationDistance)
                    if (it.preload) soundObj.addProperty("preload", true)

                    if (soundObj.size() == 1) sounds.add(soundObj.getAsJsonPrimitive("name").asString)
                    else sounds.add(soundObj)
                }
                subObj.add("sounds", sounds)

                obj.add(event.location.path, subObj)
            }

            output.writeIfNeeded(
                dataOutput.outputFolder.resolve("sounds.json"),
                GsonBuilder().setPrettyPrinting().create().toJson(obj).encodeToByteArray(),
                HashCode.fromInt(Gson().toJson(obj).encodeToByteArray().contentHashCode())
            )
        }

        override fun getName(): String = "Sound Provider"
    }

    override fun generate(forEvent: FabricDataGenerator.Pack) {
        forEvent.addProvider { dataOutput, _ -> FabricSoundProvider(dataOutput, getSoundMap()) }
    }
}