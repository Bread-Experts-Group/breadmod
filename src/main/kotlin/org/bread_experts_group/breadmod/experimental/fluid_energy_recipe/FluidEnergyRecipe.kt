package org.bread_experts_group.breadmod.experimental.fluid_energy_recipe

import com.mojang.datafixers.Products
import com.mojang.datafixers.util.Function6
import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.core.HolderLookup
import net.minecraft.core.NonNullList
import net.minecraft.data.recipes.RecipeOutput
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.crafting.RecipeSerializer
import net.minecraft.world.item.crafting.RecipeType
import net.minecraft.world.level.Level
import net.minecraft.world.level.material.Fluid
import net.neoforged.neoforge.common.crafting.SizedIngredient
import net.neoforged.neoforge.fluids.FluidStack
import net.neoforged.neoforge.fluids.capability.IFluidHandler
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient
import org.bread_experts_group.breadmod.experimental.fluid_tank.SidedFluidTank
import org.bread_experts_group.breadmod.experimental.recipe_related.recipe.BMRecipeBuilder
import org.bread_experts_group.breadmod.experimental.recipe_related.recipe.BMRecipeInputs
import org.bread_experts_group.breadmod.experimental.recipe_related.recipe.BMRecipeSerializer
import org.bread_experts_group.breadmod.experimental.recipe_related.recipe.BreadModRecipes
import org.bread_experts_group.breadmod.registry.recipe.ModRecipeSerializers

abstract class FluidEnergyRecipe(
    val rItemInputs: NonNullList<SizedIngredient>,
    val rItemOutputs: List<ItemStack>,
    val rFluidInputs: NonNullList<SizedFluidIngredient>,
    val rFluidOutputs: List<FluidStack>,
    rTime: Int?,
    rEnergy: Int?
) : BreadModRecipes<FluidEnergyRecipe.FluidEnergyInput>(rTime, rEnergy) {
    override fun matches(input: FluidEnergyInput, level: Level): Boolean =
        rItemInputs.all { rItem ->
            input.iItems.any { iItem ->
                rItem.test(iItem)
            }
        } && rFluidInputs.all { rFluid ->
            input.iFluids.any { iFluid ->
                rFluid.test(iFluid)
            }
        } && super.matches(input, level)

    override fun assemble(input: FluidEnergyInput, registries: HolderLookup.Provider): ItemStack = rItemOutputs[0]

    fun assembleOutputs(input: FluidEnergyInput): Pair<List<ItemStack>, List<FluidStack>> =
        buildList { repeat(rItemOutputs.size) { index -> add(rItemOutputs[index].copyWithCount(input.iCount[index])) } } to
                buildList { repeat(rFluidOutputs.size) { index -> add(rFluidOutputs[index].copyWithAmount(input.iAmount[index])) } }

    override fun canCraftInDimensions(width: Int, height: Int): Boolean = width * height >= 1

    override fun getResultItem(registries: HolderLookup.Provider): ItemStack = rItemOutputs[0].copy()

    fun consumeInputsExp(items: List<ItemStack>, tank: SidedFluidTank) {
        val itemList: MutableList<ItemStack> = mutableListOf()
        rItemInputs.forEach { itemList.add(items.find { item -> it.test(item) } ?: return@forEach) }
        itemList.forEach { item -> rItemInputs.forEach { if (it.test(item)) item.shrink(it.count()) } }
        repeat(tank.tanks.size) { index ->
            rFluidInputs.forEach {
                if (it.test(tank.tanks[index].fluid)) tank.tanks[index].drain(
                    it.amount(),
                    IFluidHandler.FluidAction.EXECUTE
                )
            }
        }
    }

    fun consumeInputs(items: List<ItemStack>, fluids: List<FluidStack>) {
        val itemList: MutableList<ItemStack> = mutableListOf()
        val fluidList: MutableList<FluidStack> = mutableListOf()
        rItemInputs.forEach { itemList.add(items.find { item -> it.test(item) } ?: return@forEach) }
        rFluidInputs.forEach { fluidList.add(fluids.find { fluid -> it.test(fluid) } ?: return@forEach) }
        itemList.forEach { item -> rItemInputs.forEach { if (it.test(item)) item.shrink(it.count()) } }
        fluidList.forEach { fluid -> rFluidInputs.forEach { if (it.test(fluid)) fluid.shrink(it.amount()) } }
    }

    fun inputStillValid(items: List<ItemStack>, fluids: List<FluidStack>): Boolean =
        rItemInputs.all { rItem -> items.any { rItem.test(it) } } &&
                rFluidInputs.all { rFluid -> fluids.any { rFluid.test(it) } }

    // todo always returning false, needs rewriting (maybe just provide a list of FluidStacks instead of the entire tank itself)
    //  the && in the function is what's causing it to always return false, expecting BOTH items and fluids to be true
    //  when the recipe could only use items or fluids
    fun canFitResults(items: List<ItemStack>, tank: SidedFluidTank, fluidCapacity: Int): Boolean =
        items.all { iItem ->
            rItemOutputs.any { rItem ->
                iItem.count < rItem.maxStackSize || iItem.count + rItem.count < rItem.maxStackSize
            }
        } || tank.tanks.all { iFluidHandler ->
            rFluidOutputs.any { rFluid ->
                iFluidHandler.fluid.amount < fluidCapacity || iFluidHandler.fluid.amount + rFluid.amount < fluidCapacity
            }
        }


    override fun getSerializer(): RecipeSerializer<*> = ModRecipeSerializers.FLUID_ENERGY_TEST.get()
    abstract override fun getType(): RecipeType<*>

    class FluidEnergyInput(
        val iItems: List<ItemStack>,
        val iCount: List<Int>,
        val iFluids: List<FluidStack>,
        val iAmount: List<Int>,
        iSize: Int
    ) : BMRecipeInputs(iSize) {
        override fun getItem(index: Int): ItemStack = iItems[index]
        fun getFluid(index: Int): FluidStack = iFluids[index]

        override fun isEmpty(): Boolean = (super.isEmpty() || iItems.isEmpty()) && iFluids.isEmpty()
    }

    // todo figure out how to implement the codec methods as constructor parameters so we can just instantiate this class
    //  instead of extending it for each recipe
    abstract class FluidEnergySerializer(
    ) : BMRecipeSerializer<FluidEnergyRecipe>() {
        abstract override fun codec(): MapCodec<FluidEnergyRecipe>
        abstract override fun streamCodec(): StreamCodec<RegistryFriendlyByteBuf, FluidEnergyRecipe>

        fun fluidEnergyCodec(
            inst: RecordCodecBuilder.Instance<FluidEnergyRecipe>
        ): Products.P6<RecordCodecBuilder.Mu<FluidEnergyRecipe>, NonNullList<SizedIngredient>, MutableList<ItemStack>, NonNullList<SizedFluidIngredient>, MutableList<FluidStack>, Int?, Int?> =
            inst.group(
                optionalSizedIngredientCodecModule("item_ingredients", FluidEnergyRecipe::rItemInputs),
                optionalItemStackListCodecModule("item_results", FluidEnergyRecipe::rItemOutputs),
                optionalSizedFluidIngredientCodecModule("fluid_ingredients", FluidEnergyRecipe::rFluidInputs),
                optionalFluidStackListCodecModule("fluid_results", FluidEnergyRecipe::rFluidOutputs),
                optionalIntCodecModule("time", FluidEnergyRecipe::rTime),
                optionalIntCodecModule("energy", FluidEnergyRecipe::rEnergy)
            )

        fun fluidEnergyStreamCodec(recipe: Function6<NonNullList<SizedIngredient>, List<ItemStack>, NonNullList<SizedFluidIngredient>, List<FluidStack>, Int?, Int?, FluidEnergyRecipe>): StreamCodec<RegistryFriendlyByteBuf, FluidEnergyRecipe> =
            StreamCodec.composite(
                nonNullListStreamCodec(SizedIngredient.STREAM_CODEC), FluidEnergyRecipe::rItemInputs,
                ItemStack.LIST_STREAM_CODEC, FluidEnergyRecipe::rItemOutputs,
                nonNullListStreamCodec(SizedFluidIngredient.STREAM_CODEC), FluidEnergyRecipe::rFluidInputs,
                listStreamCodec(FluidStack.STREAM_CODEC), FluidEnergyRecipe::rFluidOutputs,
                ByteBufCodecs.INT, FluidEnergyRecipe::rTime,
                ByteBufCodecs.INT, FluidEnergyRecipe::rEnergy,
                recipe
            )
    }

    abstract class FluidEnergyBuilder(
        val itemResults: List<Pair<Item, Int>>,
        val fluidResults: List<Pair<Fluid, Int>>
    ) : BMRecipeBuilder.Multi() {
        override fun getResult(): Item = itemResults[0].first

        abstract override fun save(recipeOutput: RecipeOutput, id: ResourceLocation) /*{
            val recipe = FluidEnergyRecipe(
                items, buildList { itemResults.forEach { add(ItemStack(it.first, it.second)) } },
                fluids, buildList { fluidResults.forEach { add(FluidStack(it.first, it.second)) } },
                time, energy
            )
            recipeOutput.accept(id, recipe, buildAdvancement(recipeOutput, id))
        }*/
    }
}