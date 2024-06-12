package breadmod.block.machine.entity.menu

import breadmod.block.machine.entity.GeneratorBlockEntity
import breadmod.recipe.fluidEnergy.generators.GeneratorRecipe
import breadmod.registry.block.ModBlockEntities
import breadmod.registry.screen.ModMenuTypes
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.world.entity.player.Inventory


class GeneratorMenu(
    pContainerId: Int,
    inventory: Inventory,
    parent: GeneratorBlockEntity
) : AbstractMachineMenu<GeneratorBlockEntity, GeneratorRecipe>(
    ModMenuTypes.GENERATOR.get(),
    pContainerId,
    inventory,
    parent
) {
    constructor(pContainerId: Int, inventory: Inventory, byteBuf: FriendlyByteBuf) : this(
        pContainerId, inventory,
        inventory.player.level().getBlockEntity(byteBuf.readBlockPos(), ModBlockEntities.GENERATOR.get()).get()
    )
}