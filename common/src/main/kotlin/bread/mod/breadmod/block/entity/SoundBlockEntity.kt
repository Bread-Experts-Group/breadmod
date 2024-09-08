package bread.mod.breadmod.block.entity

import bread.mod.breadmod.registry.block.ModBlockEntityTypes
import bread.mod.breadmod.registry.sound.ModSounds
import net.minecraft.core.BlockPos
import net.minecraft.core.HolderLookup
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.protocol.Packet
import net.minecraft.network.protocol.game.ClientGamePacketListener
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket
import net.minecraft.sounds.SoundEvent
import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState

// todo actual logic and packet for handling entered sounds
class SoundBlockEntity(
    pPos: BlockPos,
    pBlockState: BlockState
) : BlockEntity(
    ModBlockEntityTypes.SOUND_BLOCK.get(),
    pPos,
    pBlockState
)/*, MenuProvider*/ {
//    val capabilityHolder = CapabilityHolder(mapOf(
//        ForgeCapabilities.ITEM_HANDLER to (
//                IndexableItemHandler(
//                    listOf(
//                        1 to StorageDirection.BIDIRECTIONAL
//                    )
//                ) to ACCEPT_ALL
//                )
//    ))

//    init {
//        capabilityHolder.capabilities.forEach { _, u ->
//            u.first.ifPresent { it.changed = { setChanged() } }
//        }
//        capabilityHolder.changed = { cap, _, _ ->
//            NETWORK.send(
//                PacketDistributor.TRACKING_CHUNK.with { (level as ServerLevel).getChunkAt(blockPos) },
//                CapabilitySideDataPacket(blockPos, cap.name, capabilityHolder.capabilities[cap]?.second ?: emptyList())
//            )
//        }
//        capabilityHolder.capabilities.forEach {
//            (capabilityHolder.capability(it.key) as ICapabilitySavable<*>).changed = ::setChanged
//        }
//    }

//    override fun <T : Any?> getCapability(cap: Capability<T>): LazyOptional<T> =
//        getCapability(cap, null)
//
//    override fun <T : Any?> getCapability(cap: Capability<T>, side: Direction?): LazyOptional<T> =
//        capabilityHolder.capabilitySided(cap, blockState.getValue(BlockStateProperties.HORIZONTAL_FACING), side)
//            ?: super.getCapability(cap, side)
//
//    override fun invalidateCaps() {
//        capabilityHolder.invalidate()
//        super.invalidateCaps()
//    }

    var currentSound: SoundEvent? = ModSounds.WAR_TIMER.get()

    override fun saveAdditional(tag: CompoundTag, registries: HolderLookup.Provider) {
/*        capabilityHolder.capabilities.forEach { (cap, list) ->
            list.first.ifPresent { pTag.put(cap.name, it.serializeNBT()) }
        }*/
        // todo figure out how to turn a sound even into string then back to sound event when loading
        currentSound?.let { tag.putString("current_sound", it.location.toLanguageKey()) }
        super.saveAdditional(tag, registries)
    }

    override fun loadAdditional(tag: CompoundTag, registries: HolderLookup.Provider) {
/*        capabilityHolder.capabilities.forEach { (cap, list) ->
            list.first.ifPresent { it.deserializeNBT(pTag.get(cap.name)) }
        }*/
        if (currentSound != null) tag.getCompound("current_sound")
        super.loadAdditional(tag, registries)
    }

//    override fun setChanged() {
//        if (level is ServerLevel) NETWORK.send(
//            PacketDistributor.TRACKING_CHUNK.with { (level as ServerLevel).getChunkAt(blockPos) },
//            CapabilityTagDataPacket(blockPos, updateTag)
//        )
//        super.setChanged()
//    }

    override fun saveToItem(stack: ItemStack, registries: HolderLookup.Provider) {
        BlockItem.setBlockEntityData(stack, type, saveWithFullMetadata(registries))
    }

    override fun getUpdateTag(registries: HolderLookup.Provider): CompoundTag =
        CompoundTag().also { saveAdditional(it, registries) }

    override fun getUpdatePacket(): Packet<ClientGamePacketListener> =
        ClientboundBlockEntityDataPacket.create(this)

//    override fun createMenu(pContainerId: Int, pPlayerInventory: Inventory, pPlayer: Player): AbstractContainerMenu =
//        SoundBlockMenu(pContainerId, pPlayerInventory, this)

//    override fun getDisplayName(): Component = modTranslatable("block", "sound_block")
}