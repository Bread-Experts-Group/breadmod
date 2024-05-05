package breadmod.util

import net.minecraft.world.inventory.CraftingContainer

data class PoweredFluidCraftingContainer(
    val energy: Int,
    val itemContainer: CraftingContainer,
    val fluidContainer: FluidContainer
)