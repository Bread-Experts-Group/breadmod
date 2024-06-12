package breadmod.block.machine.entity.menu

import breadmod.block.machine.entity.DoughMachineBlockEntity
import breadmod.recipe.fluidEnergy.DoughMachineRecipe
import breadmod.registry.block.ModBlockEntities
import breadmod.registry.screen.ModMenuTypes
import breadmod.util.isTag
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.tags.FluidTags
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.inventory.Slot
import net.minecraft.world.item.BucketItem
import net.minecraft.world.item.ItemStack
import net.minecraftforge.fluids.FluidUtil
import net.minecraftforge.fluids.capability.IFluidHandler
import kotlin.jvm.optionals.getOrNull

class DoughMachineMenu(
    pContainerId: Int,
    inventory: Inventory,
    parent: DoughMachineBlockEntity
) : AbstractMachineMenu<DoughMachineBlockEntity, DoughMachineRecipe>(
    ModMenuTypes.DOUGH_MACHINE.get(),
    pContainerId, inventory, parent
) {
    constructor(pContainerId: Int, inventory: Inventory, byteBuf: FriendlyByteBuf) : this(
        pContainerId, inventory,
        inventory.player.level().getBlockEntity(byteBuf.readBlockPos(), ModBlockEntities.DOUGH_MACHINE.get()).get()
    )

    class DoughMachineBucketSlot(parent: DoughMachineBlockEntity) : Slot(parent, 2, 153, 7) {
        override fun mayPlace(stack: ItemStack): Boolean =
            stack.item.let { it is BucketItem && it.fluid.isTag(FluidTags.WATER) } ||
            FluidUtil.getFluidHandler(stack).resolve().getOrNull().let { it?.drain(1, IFluidHandler.FluidAction.SIMULATE)?.let { drained -> drained.amount == 1 && drained.fluid.isTag(FluidTags.WATER) } == true }
    }

    init {
        addSlot(Slot(parent, 0, 26, 34))
        addSlot(ResultSlot(1, 78, 35))
        addSlot(DoughMachineBucketSlot(parent))
    }
}