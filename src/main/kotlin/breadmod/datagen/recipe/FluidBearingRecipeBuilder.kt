package breadmod.datagen.recipe

import net.minecraft.data.recipes.RecipeBuilder
import net.minecraft.tags.TagKey
import net.minecraft.world.level.material.Fluid
import net.minecraftforge.fluids.FluidStack

@Suppress("unused")
interface FluidBearingRecipeBuilder: RecipeBuilder {
    val fluidsRequired: MutableList<FluidStack>
    val fluidsRequiredTagged: MutableList<Pair<TagKey<Fluid>, Int>>
    fun requiresFluid(fluidStack: FluidStack) = this.also { fluidsRequired.add(fluidStack) }
    fun requiresFluid(fluid: Fluid, count: Int = 1) = requiresFluid(FluidStack(fluid, count))
    fun requiresFluid(fluid: TagKey<Fluid>, count: Int = 1) = this.also { fluidsRequiredTagged.add(fluid to count) }
}