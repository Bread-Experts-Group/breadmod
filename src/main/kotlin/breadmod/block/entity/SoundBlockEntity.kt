package breadmod.block.entity

import breadmod.ModMain.modTranslatable
import breadmod.menu.block.SoundBlockMenu
import breadmod.network.PacketHandler.NETWORK
import breadmod.network.clientbound.CapabilitySideDataPacket
import breadmod.network.clientbound.CapabilityTagDataPacket
import breadmod.registry.block.ModBlockEntityTypes
import breadmod.util.capability.CapabilityHolder
import breadmod.util.capability.CapabilityHolder.Companion.ACCEPT_ALL
import breadmod.util.capability.ICapabilitySavable
import breadmod.util.capability.IndexableItemHandler
import breadmod.util.capability.StorageDirection
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.chat.Component
import net.minecraft.network.protocol.Packet
import net.minecraft.network.protocol.game.ClientGamePacketListener
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket
import net.minecraft.server.level.ServerLevel
import net.minecraft.sounds.SoundEvents
import net.minecraft.world.MenuProvider
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.capabilities.ForgeCapabilities
import net.minecraftforge.common.util.LazyOptional
import net.minecraftforge.network.PacketDistributor

// todo actual logic and packet for handling entered sounds
class SoundBlockEntity(
    pPos: BlockPos,
    pBlockState: BlockState
) : BlockEntity(
    ModBlockEntityTypes.SOUND_BLOCK.get(),
    pPos,
    pBlockState
), MenuProvider {
    val capabilityHolder = CapabilityHolder(mapOf(
        ForgeCapabilities.ITEM_HANDLER to (
                IndexableItemHandler(
                    listOf(
                        1 to StorageDirection.BIDIRECTIONAL
                    )
                ) to ACCEPT_ALL
        )
    ))

    init {
        capabilityHolder.capabilities.forEach { _, u ->
            u.first.ifPresent { it.changed = { setChanged() } }
        }
        capabilityHolder.changed = { cap, _, _ ->
            NETWORK.send(
                PacketDistributor.TRACKING_CHUNK.with { (level as ServerLevel).getChunkAt(blockPos) },
                CapabilitySideDataPacket(blockPos, cap.name, capabilityHolder.capabilities[cap]?.second ?: emptyList())
            )
        }
        capabilityHolder.capabilities.forEach {
            (capabilityHolder.capability(it.key) as ICapabilitySavable<*>).changed = ::setChanged
        }
    }

    override fun <T : Any?> getCapability(cap: Capability<T>): LazyOptional<T> =
        getCapability(cap, null)

    override fun <T : Any?> getCapability(cap: Capability<T>, side: Direction?): LazyOptional<T> =
        capabilityHolder.capabilitySided(cap, blockState.getValue(BlockStateProperties.HORIZONTAL_FACING), side)
            ?: super.getCapability(cap, side)

    override fun invalidateCaps() {
        capabilityHolder.invalidate()
        super.invalidateCaps()
    }

    var currentSound: String? = SoundEvents.BELL_BLOCK.location.path

    override fun saveAdditional(pTag: CompoundTag) {
        capabilityHolder.capabilities.forEach { (cap, list) ->
            list.first.ifPresent { pTag.put(cap.name, it.serializeNBT()) }
        }
        currentSound?.let { pTag.putString("current_sound", it) }
        super.saveAdditional(pTag)
    }

    override fun load(pTag: CompoundTag) {
        capabilityHolder.capabilities.forEach { (cap, list) ->
            list.first.ifPresent { it.deserializeNBT(pTag.get(cap.name)) }
        }
        if (currentSound != null) pTag.getCompound("current_sound")
        super.load(pTag)
    }

    override fun setChanged() {
        if (level is ServerLevel) NETWORK.send(
            PacketDistributor.TRACKING_CHUNK.with { (level as ServerLevel).getChunkAt(blockPos) },
            CapabilityTagDataPacket(blockPos, updateTag)
        )
        super.setChanged()
    }

    override fun saveToItem(pStack: ItemStack) =
        BlockItem.setBlockEntityData(pStack, type, saveWithFullMetadata())

    override fun getUpdateTag(): CompoundTag =
        CompoundTag().also { saveAdditional(it) }

    override fun getUpdatePacket(): Packet<ClientGamePacketListener> =
        ClientboundBlockEntityDataPacket.create(this)

    override fun createMenu(pContainerId: Int, pPlayerInventory: Inventory, pPlayer: Player): AbstractContainerMenu =
        SoundBlockMenu(pContainerId, pPlayerInventory, this)

    override fun getDisplayName(): Component = modTranslatable("block", "sound_block")
}