package breadmod.menu.block

import breadmod.block.entity.SoundBlockEntity
import breadmod.menu.AbstractModContainerMenu
import breadmod.registry.block.ModBlockEntityTypes
import breadmod.registry.menu.ModMenuTypes
import breadmod.util.capability.IndexableItemHandler
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.ContainerLevelAccess
import net.minecraft.world.inventory.Slot
import net.minecraftforge.common.capabilities.ForgeCapabilities

class SoundBlockMenu(
    pContainerId: Int,
    pInventory: Inventory,
    val parent: SoundBlockEntity
) : AbstractModContainerMenu(
    ModMenuTypes.SOUND_BLOCK.get(),
    pContainerId
) {
    constructor(pContainerId: Int, inventory: Inventory, byteBuf: FriendlyByteBuf): this(
        pContainerId, inventory,
        inventory.player.level().getBlockEntity(byteBuf.readBlockPos(), ModBlockEntityTypes.SOUND_BLOCK.get()).get()
    )

    init {
        val cap = parent.capabilityHolder.capabilityOrNull<IndexableItemHandler>(ForgeCapabilities.ITEM_HANDLER)
        addInventorySlots(pInventory, 8, 142, 84)
        addSlot(Slot(TODO(), 1, 0, 0))
    }

    override fun stillValid(pPlayer: Player): Boolean = stillValid(
        ContainerLevelAccess.create(pPlayer.level(), parent.blockPos),
        pPlayer,
        parent.blockState.block
    )
}