package breadmod.block.entity.menu

import breadmod.block.entity.AbstractPowerGeneratorBlockEntity
import breadmod.recipe.fluidEnergy.generators.CoalGeneratorRecipe
import breadmod.registry.block.ModBlockEntities
import breadmod.registry.block.ModBlocks
import breadmod.registry.screen.ModMenuTypes
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.world.entity.player.Inventory


class CoalGeneratorMenu(
    pContainerId: Int,
    inventory: Inventory,
    parent: AbstractPowerGeneratorBlockEntity<CoalGeneratorRecipe>
) : AbstractPowerGeneratorMenu<CoalGeneratorRecipe>(
    ModMenuTypes.COAL_GENERATOR.get(),
    pContainerId,
    inventory,
    parent,
    ModBlocks.COAL_GENERATOR.get().block
) {
    constructor(pContainerId: Int, inventory: Inventory, byteBuf: FriendlyByteBuf) : this(
        pContainerId, inventory,
        inventory.player.level().getBlockEntity(byteBuf.readBlockPos(), ModBlockEntities.COAL_GENERATOR.get()).get()
    )
}