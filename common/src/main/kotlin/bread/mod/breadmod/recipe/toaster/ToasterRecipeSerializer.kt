package bread.mod.breadmod.recipe.toaster

import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.crafting.RecipeSerializer

class ToasterRecipeSerializer : RecipeSerializer<ToasterRecipe> {
    companion object {
        val CODEC: MapCodec<ToasterRecipe> = RecordCodecBuilder.mapCodec { inst ->
            inst.group(
                ItemStack.CODEC.fieldOf("ingredient").forGetter(ToasterRecipe::inputItem),
                ItemStack.CODEC.fieldOf("result").forGetter(ToasterRecipe::result),
                Codec.INT.fieldOf("time").forGetter(ToasterRecipe::time)
            ).apply(inst) { ingredient, result, time -> ToasterRecipe(ingredient, result, time) }
        }

        val STREAM_CODEC: StreamCodec<RegistryFriendlyByteBuf, ToasterRecipe> = StreamCodec.composite(
            ItemStack.STREAM_CODEC, ToasterRecipe::inputItem,
            ItemStack.STREAM_CODEC, ToasterRecipe::result,
            ByteBufCodecs.INT, ToasterRecipe::time
        ) { ingredient, result, time ->
            ToasterRecipe(
                ingredient, result, time
            )
        }
    }

    override fun codec(): MapCodec<ToasterRecipe> = CODEC
    override fun streamCodec(): StreamCodec<RegistryFriendlyByteBuf, ToasterRecipe> = STREAM_CODEC
}