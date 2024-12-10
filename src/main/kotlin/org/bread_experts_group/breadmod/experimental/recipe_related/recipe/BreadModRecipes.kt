package org.bread_experts_group.breadmod.experimental.recipe_related.recipe

import net.minecraft.core.HolderLookup
import net.minecraft.core.NonNullList
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.crafting.Recipe
import net.minecraft.world.item.crafting.RecipeInput
import net.minecraft.world.level.Level
import net.neoforged.neoforge.common.crafting.SizedIngredient
import net.neoforged.neoforge.fluids.FluidStack
import net.neoforged.neoforge.fluids.capability.IFluidHandler
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient
import org.bread_experts_group.breadmod.experimental.fluid_tank.CustomFluidTank

@Suppress("unused")
abstract class BreadModRecipes<T : RecipeInput>(val rTime: Int?, val rEnergy: Int?) : Recipe<T> {
    override fun matches(input: T, level: Level): Boolean = rTime!! >= 0 && rEnergy!! >= 0

    /**
     * @return true if [rTime] is 0
     */
    fun isInstant(): Boolean = rTime == 0

    /**
     * @return true if [rEnergy] is 0
     */
    fun usesNoEnergy(): Boolean = rEnergy == 0

    abstract class SingleFluid(
        val rFluidInput: SizedFluidIngredient,
        val rFluidOutput: FluidStack,
        rTime: Int?,
        rEnergy: Int?
    ) : BreadModRecipes<BMRecipeInputs.SingleFluid>(rTime, rEnergy) {
        override fun matches(input: BMRecipeInputs.SingleFluid, level: Level): Boolean =
            rFluidInput.test(input.iFluid) && super.matches(input, level)

        override fun assemble(input: BMRecipeInputs.SingleFluid, registries: HolderLookup.Provider): ItemStack =
            ItemStack.EMPTY

        fun assembleFluid(input: BMRecipeInputs.SingleFluid): FluidStack =
            rFluidOutput.copyWithAmount(input.iAmount)

        override fun getResultItem(registries: HolderLookup.Provider): ItemStack = ItemStack.EMPTY
        fun getResultFluid(): FluidStack = rFluidOutput.copy()
        override fun canCraftInDimensions(width: Int, height: Int): Boolean = true

        fun inputStillValid(fluid: FluidStack) = rFluidInput.test(fluid)

        fun canFitResults(tank: CustomFluidTank, index: Int) =
            tank.getFluidInTank(index).amount < tank.getTankCapacity(index) ||
                    tank.getFluidInTank(index).amount + rFluidOutput.amount < tank.getTankCapacity(index)

        fun consumeInput(tank: CustomFluidTank, index: Int) =
            tank.drainTank(rFluidInput.amount(), IFluidHandler.FluidAction.EXECUTE, index)
    }

    abstract class SingleItem(
        val rItemInput: SizedIngredient,
        val rItemOutput: ItemStack,
        rTime: Int?,
        rEnergy: Int?
    ) : BreadModRecipes<BMRecipeInputs.SingleItem>(rTime, rEnergy) {
        override fun matches(input: BMRecipeInputs.SingleItem, level: Level): Boolean =
            rItemInput.test(input.iItem) && super.matches(input, level)

        override fun assemble(input: BMRecipeInputs.SingleItem, provider: HolderLookup.Provider): ItemStack =
            rItemOutput.copyWithCount(input.iCount)

        override fun getResultItem(registries: HolderLookup.Provider): ItemStack = rItemOutput.copy()
        override fun canCraftInDimensions(width: Int, height: Int): Boolean = width * height >= 1

        /**
         * Consumes the input item in slot 0.
         * ### Expected slot should always be index 0.
         */
        fun consumeInput(items: List<ItemStack>) = items[0].shrink(rItemInput.count())

        fun inputStillValid(items: List<ItemStack>) = rItemInput.test(items[0])

        fun canFitResults(items: List<ItemStack>, itemIndex: Int) =
            (items[itemIndex].count < items[itemIndex].maxStackSize ||
                    items[itemIndex].count + rItemOutput.count < items[itemIndex].maxStackSize)
    }

    abstract class MultiFluid(
        val rFluidInputs: NonNullList<SizedFluidIngredient>,
        val rFluidOutputs: List<FluidStack>,
        rTime: Int?,
        rEnergy: Int?
    ) : BreadModRecipes<BMRecipeInputs.MultiFluid>(rTime, rEnergy) {

        override fun matches(input: BMRecipeInputs.MultiFluid, level: Level): Boolean =
            rFluidInputs.all { rFluid ->
                input.iFluids.any { iFluid ->
                    rFluid.test(iFluid)
                }
            } && super.matches(input, level)

        override fun assemble(input: BMRecipeInputs.MultiFluid, registries: HolderLookup.Provider): ItemStack =
            ItemStack.EMPTY

        fun assembleFluids(input: BMRecipeInputs.MultiFluid): List<FluidStack> = buildList {
            repeat(rFluidOutputs.size) { index -> add(rFluidOutputs[index].copyWithAmount(input.iAmount[index])) }
        }

        override fun getResultItem(registries: HolderLookup.Provider): ItemStack = ItemStack.EMPTY
        fun getResultFluids(): List<FluidStack> = buildList { rFluidOutputs.forEach { add(it.copy()) } }

        override fun canCraftInDimensions(width: Int, height: Int): Boolean = true

        fun consumeInputs(fluids: List<FluidStack>) {
            val list: MutableList<FluidStack> = mutableListOf()
            rFluidInputs.forEach { list.add(fluids.find { fluid -> it.test(fluid) } ?: return@forEach) }
            list.forEach { fluid -> rFluidInputs.forEach { if (it.test(fluid)) fluid.shrink(it.amount()) } }
        }

        fun inputsStillValid(fluids: List<FluidStack>): Boolean =
            rFluidInputs.all { rFluid -> fluids.any { rFluid.test(it) } }

        fun canFitResults(list: List<FluidStack>, capacity: Int): Boolean =
            list.all { iFluid ->
                rFluidOutputs.any { rFluid ->
                    iFluid.amount < capacity || iFluid.amount + rFluid.amount < capacity
                }
            }
    }

    abstract class MultiItem(
        val rItemInputs: NonNullList<SizedIngredient>,
        val rItemOutputs: List<ItemStack>,
        rTime: Int?,
        rEnergy: Int?
    ) : BreadModRecipes<BMRecipeInputs.MultiItem>(rTime, rEnergy) {

        // todo account for split stacks of matching items
        override fun matches(input: BMRecipeInputs.MultiItem, level: Level): Boolean =
            rItemInputs.all { rItem ->
                input.iItems.any { iItem ->
                    rItem.test(iItem)
                }
            } && super.matches(input, level)

        /**
         * @return The first item in [rItemOutputs]
         */
        override fun assemble(input: BMRecipeInputs.MultiItem, registries: HolderLookup.Provider): ItemStack =
            rItemOutputs[0].copyWithCount(input.iCount[0])

        /**
         * @return a copy of the first item in [rItemOutputs]
         */
        override fun getResultItem(registries: HolderLookup.Provider): ItemStack = rItemOutputs[0].copy()

        fun getResultItems(): List<ItemStack> = buildList { rItemOutputs.forEach { add(it.copy()) } }
        fun assembleItems(input: BMRecipeInputs.MultiItem): List<ItemStack> = buildList {
            repeat(rItemOutputs.size) { index -> add(rItemOutputs[index].copyWithCount(input.iCount[index])) }
        }

        fun consumeInputs(items: List<ItemStack>) {
            val list: MutableList<ItemStack> = mutableListOf()
            rItemInputs.forEach { list.add(items.find { item -> it.test(item) } ?: return@forEach) }
            list.forEach { item -> rItemInputs.forEach { if (it.test(item)) item.shrink(it.count()) } }
        }

        fun inputStillValid(items: List<ItemStack>): Boolean =
            rItemInputs.all { rItem -> items.any { rItem.test(it) } }

        fun canFitResults(list: List<ItemStack>): Boolean =
            list.all { iItem ->
                rItemOutputs.any { rItem ->
                    iItem.count < rItem.maxStackSize || iItem.count + rItem.count < rItem.maxStackSize
                }
            }
    }

    abstract class SingleFluidItem(
        val rFluidInput: SizedFluidIngredient,
        val rItemInput: SizedIngredient,
        val rItemOutput: ItemStack,
        val rFluidOutput: FluidStack,
        rTime: Int?,
        rEnergy: Int?
    ) : BreadModRecipes<BMRecipeInputs.SingleFluidItem>(rTime, rEnergy) {
        override fun matches(input: BMRecipeInputs.SingleFluidItem, level: Level): Boolean =
            rFluidInput.test(input.iFluid) && rItemInput.test(input.iItem) && super.matches(input, level)

        override fun assemble(input: BMRecipeInputs.SingleFluidItem, registries: HolderLookup.Provider): ItemStack =
            rItemOutput.copyWithCount(input.iCount)

        fun assembleOutputs(input: BMRecipeInputs.SingleFluidItem): Pair<FluidStack, ItemStack> =
            Pair(rFluidOutput.copyWithAmount(input.iAmount), rItemOutput.copyWithCount(input.iCount))

        fun inputStillValid(item: ItemStack, fluids: FluidStack) =
            rItemInput.test(item) && rFluidInput.test(fluids)

        fun canFitResults(tank: CustomFluidTank, tankIndex: Int, items: List<ItemStack>, itemIndex: Int) =
            (tank.getFluidInTank(tankIndex).amount < tank.getTankCapacity(tankIndex) ||
                    tank.getFluidInTank(tankIndex).amount + rFluidOutput.amount < tank.getTankCapacity(tankIndex)) &&
                    (items[itemIndex].count < items[itemIndex].maxStackSize ||
                            items[itemIndex].count + rItemOutput.count < items[itemIndex].maxStackSize)

        fun consumeInputs(fluid: CustomFluidTank, tankIndex: Int, items: List<ItemStack>, itemIndex: Int) {
            fluid.drainTank(rFluidInput.amount(), IFluidHandler.FluidAction.EXECUTE, tankIndex)
            items[itemIndex].shrink(rItemInput.count())
        }

        override fun getResultItem(registries: HolderLookup.Provider): ItemStack = rItemOutput.copy()

        override fun canCraftInDimensions(width: Int, height: Int): Boolean = width * height >= 1


    }
}