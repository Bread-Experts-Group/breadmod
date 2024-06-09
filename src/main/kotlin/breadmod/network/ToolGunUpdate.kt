package breadmod.network

import breadmod.datagen.tool_gun.ModToolGunModeDataLoader
import breadmod.item.ToolGunItem
import breadmod.item.ToolGunItem.Companion.MODE_ITERATOR_STATE_TAG
import breadmod.item.ToolGunItem.Companion.MODE_NAMESPACE_TAG
import breadmod.item.ToolGunItem.Companion.MODE_NAME_TAG
import breadmod.item.ToolGunItem.Companion.NAMESPACE_ITERATOR_STATE_TAG
import breadmod.util.MapIterator
import net.minecraft.network.FriendlyByteBuf
import net.minecraftforge.network.NetworkEvent
import java.util.function.Supplier

data class ToolGunUpdate(val pSlot: Int) {
    companion object {
        fun encodeBuf(input: ToolGunUpdate, buffer: FriendlyByteBuf) {
            buffer.writeInt(input.pSlot) }
        fun decodeBuf(input: FriendlyByteBuf): ToolGunUpdate =
            ToolGunUpdate(input.readInt())

        fun handle(input: ToolGunUpdate, ctx: Supplier<NetworkEvent.Context>) = ctx.get().let {
            it.enqueueWork {
                val player = it.sender ?: return@enqueueWork
                val stack = player.inventory.items[input.pSlot]
                val item = stack.item
                if(!player.cooldowns.isOnCooldown(item) && item is ToolGunItem) {
                    val currentMode = item.ensureCurrentMode(stack)
                    val namespaceIterator = MapIterator(ModToolGunModeDataLoader.modes)
                    namespaceIterator.restoreState(currentMode.getInt(NAMESPACE_ITERATOR_STATE_TAG))
                    val modeIterator = MapIterator(namespaceIterator.current().value)
                    modeIterator.restoreState(currentMode.getInt(MODE_ITERATOR_STATE_TAG))

                    when {
                        modeIterator.hasNext() -> {
                            currentMode.putString(MODE_NAME_TAG, modeIterator.next().key)
                            currentMode.putInt(MODE_ITERATOR_STATE_TAG, modeIterator.saveState())
                        }
                        namespaceIterator.hasNext() -> {
                            currentMode.putString(MODE_NAMESPACE_TAG, namespaceIterator.next().key)
                            currentMode.putInt(NAMESPACE_ITERATOR_STATE_TAG, namespaceIterator.saveState())
                            currentMode.putInt(MODE_ITERATOR_STATE_TAG, 0)
                            currentMode.putString(MODE_NAME_TAG, modeIterator.current().key)
                        }
                        else -> {
                            currentMode.putInt(NAMESPACE_ITERATOR_STATE_TAG, 0)
                            currentMode.putString(MODE_NAMESPACE_TAG, namespaceIterator.current().key)
                            currentMode.putInt(MODE_ITERATOR_STATE_TAG, 0)
                            currentMode.putString(MODE_NAME_TAG, modeIterator.current().key)
                        }
                    }
                    player.cooldowns.addCooldown(item, 10)
                }
            }
            it.packetHandled = true
        }
    }
}