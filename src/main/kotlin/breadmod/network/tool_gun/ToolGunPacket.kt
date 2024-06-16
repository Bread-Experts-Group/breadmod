package breadmod.network.tool_gun

import breadmod.ModMain
import breadmod.datagen.tool_gun.BreadModToolGunModeProvider
import breadmod.datagen.tool_gun.ModToolGunModeDataLoader
import breadmod.item.tool_gun.ToolGunItem
import breadmod.item.tool_gun.ToolGunItem.Companion.MODE_ITERATOR_STATE_TAG
import breadmod.item.tool_gun.ToolGunItem.Companion.MODE_NAMESPACE_TAG
import breadmod.item.tool_gun.ToolGunItem.Companion.MODE_NAME_TAG
import breadmod.item.tool_gun.ToolGunItem.Companion.NAMESPACE_ITERATOR_STATE_TAG
import breadmod.util.MapIterator
import breadmod.util.componentToJson
import breadmod.util.jsonToComponent
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.mojang.blaze3d.platform.InputConstants
import net.minecraft.network.FriendlyByteBuf
import net.minecraftforge.client.settings.KeyModifier
import net.minecraftforge.network.NetworkEvent
import java.util.concurrent.CompletableFuture
import java.util.function.Supplier

data class ToolGunPacket(val pModeSwitch: Boolean, val pSlot: Int, val pControl: BreadModToolGunModeProvider.Control? = null) {
    companion object {
        fun encodeBuf(input: ToolGunPacket, buffer: FriendlyByteBuf) {
            buffer.writeBoolean(input.pModeSwitch).writeInt(input.pSlot); buffer.writeNullable(input.pControl) { writer, value ->
                writer.writeUtf(value.id)
                writer.writeUtf(value.nameKey)
                writer.writeUtf(value.categoryKey)
                writer.writeUtf(componentToJson(value.toolGunComponent).toString())
                writer.writeUtf(value.key().name)
                writer.writeNullable(value.modifier) { writer2, value2 -> writer2.writeUtf(value2.name) }
            } }
        fun decodeBuf(input: FriendlyByteBuf): ToolGunPacket =
            ToolGunPacket(input.readBoolean(), input.readInt(), input.readNullable {
                BreadModToolGunModeProvider.Control(
                    input.readUtf(),
                    input.readUtf(),
                    input.readUtf(),
                    jsonToComponent(Gson().fromJson(input.readUtf(), JsonObject::class.java)),
                    input.readUtf().let { { InputConstants.getKey(it) } },
                    input.readNullable { KeyModifier.valueFromString(it.readUtf()) },
                )
            })

        fun handle(input: ToolGunPacket, ctx: Supplier<NetworkEvent.Context>): CompletableFuture<Void> = ctx.get().let {
            it.enqueueWork {
                val player = it.sender ?: return@enqueueWork
                val stack = player.inventory.items[input.pSlot]
                val item = stack.item
                if(!player.cooldowns.isOnCooldown(item) && item is ToolGunItem) {
                    if(input.pModeSwitch) {
                        val currentMode = item.ensureCurrentMode(stack)
                        val namespaceIterator = MapIterator(ModToolGunModeDataLoader.modes)
                        namespaceIterator.restoreState(currentMode.getInt(NAMESPACE_ITERATOR_STATE_TAG))
                        val modeIterator = MapIterator(namespaceIterator.current().value)
                        modeIterator.restoreState(currentMode.getInt(MODE_ITERATOR_STATE_TAG))

                        val last = modeIterator.current().value.first
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

                        val level = player.level()
                        last.mode.close(level, player, stack, modeIterator.current().value.first.mode)
                        modeIterator.current().value.first.mode.open(level, player, stack, last.mode)
                        player.cooldowns.addCooldown(item, 10)
                    } else {
                        (item.getCurrentMode(stack) ?: return@enqueueWork ModMain.LOGGER.error("ToolGun is in an erroneous state! Something stinky is going on!"))
                            .mode.action(player.level(), player, stack, input.pControl ?: return@enqueueWork)
                    }
                    it.packetHandled = true
                }
            }
        }
    }
}