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
import net.minecraftforge.common.capabilities.ForgeCapabilities
import net.minecraftforge.items.SlotItemHandler

class SoundBlockMenu(
    pContainerId: Int,
    pInventory: Inventory,
    val parent: SoundBlockEntity
) : AbstractModContainerMenu(
    ModMenuTypes.SOUND_BLOCK.get(),
    pContainerId
) {
    val handler = parent.capabilityHolder.capabilityOrNull<IndexableItemHandler>(ForgeCapabilities.ITEM_HANDLER)

    constructor(pContainerId: Int, inventory: Inventory, byteBuf: FriendlyByteBuf): this(
        pContainerId, inventory,
        inventory.player.level().getBlockEntity(byteBuf.readBlockPos(), ModBlockEntityTypes.SOUND_BLOCK.get()).get()
    )

    init {
        addInventorySlots(pInventory, 8, 142, 84)
        addSlot(SlotItemHandler(handler, 0, 0, 0))
    }

    override fun stillValid(pPlayer: Player): Boolean = stillValid(
        ContainerLevelAccess.create(pPlayer.level(), parent.blockPos),
        pPlayer,
        parent.blockState.block
    )
}