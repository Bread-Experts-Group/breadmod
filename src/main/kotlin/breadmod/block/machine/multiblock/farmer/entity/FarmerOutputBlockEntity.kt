package breadmod.block.machine.multiblock.farmer.entity

import breadmod.ModMain
import breadmod.registry.block.ModBlockEntityTypes
import breadmod.util.deserialize
import breadmod.util.serialize
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.nbt.CompoundTag
import net.minecraft.world.WorldlyContainer
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.block.DirectionalBlock
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.capabilities.ForgeCapabilities
import net.minecraftforge.common.util.LazyOptional
import net.minecraftforge.items.IItemHandlerModifiable
import net.minecraftforge.items.wrapper.SidedInvWrapper

class FarmerOutputBlockEntity( // todo add menu and screen
    pPos: BlockPos,
    pBlockState: BlockState
) : BlockEntity(ModBlockEntityTypes.FARMER_OUTPUT.get(), pPos, pBlockState), WorldlyContainer {
    val storedItems = MutableList(4) { ItemStack.EMPTY }

    private val facing = this.blockState.getValue(DirectionalBlock.FACING)
    private var handlers: Array<out LazyOptional<IItemHandlerModifiable>> = SidedInvWrapper.create(
        this, facing)

    override fun <T : Any?> getCapability(cap: Capability<T>, side: Direction?): LazyOptional<T> {
        return when {
            (cap == ForgeCapabilities.ITEM_HANDLER) && (side != null && side == facing && !this.remove) -> handlers[0].cast()
            else -> super.getCapability(cap, side)
        }
    }

    override fun invalidateCaps() {
        handlers.forEach { it.invalidate() }
    }

    override fun saveAdditional(pTag: CompoundTag) {
        super.saveAdditional(pTag)
        pTag.put(ModMain.ID, CompoundTag().also { dataTag ->
            dataTag.put("items", storedItems.serialize())
        })
    }

    override fun load(pTag: CompoundTag) {
        super.load(pTag)
        val dataTag = pTag.getCompound(ModMain.ID)
        storedItems.deserialize(dataTag.getCompound("items"))
    }

    override fun getUpdateTag(): CompoundTag = super.getUpdateTag().also { saveAdditional(it) }

    override fun clearContent() = storedItems.forEach { it.count = 0 }
    override fun getContainerSize(): Int = storedItems.size
    override fun isEmpty(): Boolean = storedItems.any { !it.isEmpty }
    override fun getItem(pSlot: Int): ItemStack = storedItems[pSlot]
    override fun removeItem(pSlot: Int, pAmount: Int): ItemStack = storedItems[pSlot].split(pAmount)
    override fun removeItemNoUpdate(pSlot: Int): ItemStack = storedItems[pSlot].copyAndClear()
    override fun setItem(pSlot: Int, pStack: ItemStack) { storedItems[pSlot] = pStack}
    override fun stillValid(pPlayer: Player): Boolean = true
    override fun getSlotsForFace(pSide: Direction): IntArray = when(pSide) {
        facing -> intArrayOf(0,1,2,3)
        else -> intArrayOf()
    }
    override fun canPlaceItemThroughFace(pIndex: Int, pItemStack: ItemStack, pDirection: Direction?): Boolean = false
    override fun canTakeItemThroughFace(pIndex: Int, pStack: ItemStack, pDirection: Direction): Boolean =
        getSlotsForFace(pDirection).contains(pIndex)
}