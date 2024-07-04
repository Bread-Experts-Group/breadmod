package breadmod.block.machine.entity.menu

import breadmod.block.machine.entity.WheatCrusherBlockEntity
import breadmod.recipe.fluidEnergy.WheatCrushingRecipe
import breadmod.registry.block.ModBlockEntities
import breadmod.registry.screen.ModMenuTypes
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.inventory.Slot
import kotlin.jvm.optionals.getOrNull

class WheatCrusherMenu(
    pContainerId: Int,
    inventory: Inventory,
    parent: WheatCrusherBlockEntity
) : AbstractMachineMenu<WheatCrusherBlockEntity, WheatCrushingRecipe>(
    ModMenuTypes.WHEAT_CRUSHER.get(),
    pContainerId, inventory, parent, 174, 116
) {
    override fun getScaledProgress(): Int = parent.currentRecipe.getOrNull()?.let { ((parent.progress.toFloat() / it.time) * 48).toInt() } ?: 0

    constructor(pContainerId: Int, inventory: Inventory, byteBuf: FriendlyByteBuf) : this(
        pContainerId, inventory,
        inventory.player.level().getBlockEntity(byteBuf.readBlockPos(), ModBlockEntities.WHEAT_CRUSHER.get()).get()
    )

    init {
        addSlot(Slot(parent, 0, 80, 15))
        addSlot(ResultSlot(1, 80, 87))
    }
}