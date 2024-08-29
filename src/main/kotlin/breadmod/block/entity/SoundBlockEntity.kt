package breadmod.block.entity

import breadmod.ModMain.modTranslatable
import breadmod.menu.block.SoundBlockMenu
import breadmod.registry.block.ModBlockEntityTypes
import net.minecraft.core.BlockPos
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.chat.Component
import net.minecraft.network.protocol.Packet
import net.minecraft.network.protocol.game.ClientGamePacketListener
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket
import net.minecraft.sounds.SoundEvents
import net.minecraft.world.MenuProvider
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
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
), MenuProvider {
    var currentSound: String? = SoundEvents.BELL_BLOCK.location.path

    override fun saveAdditional(pTag: CompoundTag) {
        currentSound?.let { pTag.putString("current_sound", it) }
        super.saveAdditional(pTag)
    }

    override fun load(pTag: CompoundTag) {
        if (currentSound != null) pTag.getCompound("current_sound")
        super.load(pTag)
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