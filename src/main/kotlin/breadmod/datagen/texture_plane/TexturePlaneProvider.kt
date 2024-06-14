package breadmod.datagen.texture_plane

import breadmod.util.isSquareOf
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import net.minecraft.data.CachedOutput
import net.minecraft.data.DataProvider
import net.minecraft.data.PackOutput
import net.minecraft.resources.ResourceLocation
import java.awt.image.BufferedImage
import java.util.concurrent.CompletableFuture
import javax.imageio.ImageIO

abstract class TexturePlaneProvider(private val packOutput: PackOutput, private val modID: String): DataProvider {
    private val addedModels: MutableMap<String, ResourceLocation> = mutableMapOf()
    private val directions = listOf("north", "east", "south", "west", "down")

    override fun run(pOutput: CachedOutput): CompletableFuture<*> {
        addTextures()
        val dataLocation = packOutput.getOutputFolder(PackOutput.Target.RESOURCE_PACK).resolve(modID).resolve(MODELS_LOC).resolve(RESULT_LOC)
        return CompletableFuture.allOf(
            *buildList{
                addedModels.forEach { (name, location) ->
                    add(DataProvider.saveStable(pOutput, JsonObject().also {
                        it.add(TEXTURE_SIZE, JsonArray().also { array ->
                            array.add(getTextureRes(location).first); array.add(getTextureRes(location).second) })
                        it.add(TEXTURES, JsonObject().also { key ->
                            key.addProperty(TEXTURE_IDENTIFIER, location.toString())
                            key.addProperty("particle", location.toString())
                        })
                        it.add(ELEMENTS, JsonArray().also { element ->
                            element.add(JsonObject().also { elementObject ->
                                elementObject.add("from", JsonArray().also { array -> array.add(0.0); array.add(0); array.add(0) })
                                elementObject.add("to", JsonArray().also { array -> array.add(16); array.add(0.025); array.add(16) })
                                elementObject.add("faces", JsonObject().also { faceObject ->
                                    directions.forEach { direction ->
                                        faceObject.add(direction, JsonObject().also { directions ->
                                            directions.add("uv", JsonArray().also { array -> repeat(4) {array.add(0)} })
                                            directions.addProperty("texture", TEXTURE_IDENTIFIER)
                                        })
                                    }
                                    faceObject.add("up", JsonObject().also { upDirection ->
                                        upDirection.add("uv", JsonArray().also { up -> up.add(16); up.add(16); up.add(0); up.add(0) })
                                        upDirection.addProperty("texture", TEXTURE_IDENTIFIER)
                                    })
                                })
                            })
                        })
                    }, dataLocation.resolve("$name.json")))
                }
            }.toTypedArray()
        )
    }

    override fun getName(): String = "Texture Planes: $modID"

    abstract fun addTextures()

    fun addTexture(
        name: String,
        textureLocation: ResourceLocation
    ) {
        if(addedModels.containsKey(name)) throw IllegalStateException("$name is already defined.")
        println("texture width: ${getTextureRes(textureLocation).first}")
        println("${isSquareOf(getTextureRes(textureLocation).first, 2)}")
        println("texture height: ${getTextureRes(textureLocation).second}")
        println(println("${isSquareOf(getTextureRes(textureLocation).second, 2)}"))
//        if(!isSquareOf(getTextureRes(textureLocation).first, 2) && !isSquareOf(getTextureRes(textureLocation).second, 2)) throw IllegalStateException("Texture resolution is not power of 2.")
        addedModels[name] = textureLocation
    }

    private fun getTextureRes(location: ResourceLocation): Pair<Int, Int> {
//        println("INPUT FILE: /assets/${location.namespace}/${location.path}.png")
        val image: BufferedImage = ImageIO.read(TexturePlaneProvider::class.java.getResourceAsStream("/assets/${location.namespace}/textures/${location.path}.png"))
        val width = image.width
        val height = image.height
        val resolution = Pair(width, height)
        return resolution
    }

    internal companion object {
        const val MODELS_LOC = "models/item"
        const val RESULT_LOC = "textureplane"

        const val TEXTURE_SIZE = "texture_size"
        const val TEXTURES = "textures"
        const val TEXTURE_IDENTIFIER = "0"

        const val ELEMENTS = "elements"
    }
}