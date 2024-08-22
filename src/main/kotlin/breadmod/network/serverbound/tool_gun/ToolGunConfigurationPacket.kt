package breadmod.network.serverbound.tool_gun

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

internal data class ToolGunConfigurationPacket(
    val pModeSwitch: Boolean,
    val pControl: BreadModToolGunModeProvider.Control? = null,
    val early: Boolean = false
) {
    companion object {
        fun encodeBuf(input: ToolGunConfigurationPacket, buffer: FriendlyByteBuf) {
            buffer.writeBoolean(input.pModeSwitch)
            buffer.writeNullable(input.pControl) { writer, value ->
                writer.writeUtf(value.id)
                writer.writeUtf(value.nameKey)
                writer.writeUtf(value.categoryKey)
                writer.writeUtf(componentToJson(value.toolGunComponent).toString())
                writer.writeUtf(value.key().name)
                writer.writeNullable(value.modifier) { writer2, value2 -> writer2.writeUtf(value2.name) }
            }
            buffer.writeBoolean(input.early)
        }

        fun decodeBuf(input: FriendlyByteBuf): ToolGunConfigurationPacket =
            ToolGunConfigurationPacket(input.readBoolean(), input.readNullable {
                BreadModToolGunModeProvider.Control(
                    input.readUtf(),
                    input.readUtf(),
                    input.readUtf(),
                    jsonToComponent(Gson().fromJson(input.readUtf(), JsonObject::class.java)),
                    input.readUtf().let { { InputConstants.getKey(it) } },
                    input.readNullable { KeyModifier.valueFromString(it.readUtf()) }
                )
            }, input.readBoolean())

        fun handle(input: ToolGunConfigurationPacket, ctx: Supplier<NetworkEvent.Context>): CompletableFuture<Void> =
            ctx.get().let {
                it.enqueueWork {
                    val player = it.sender ?: return@enqueueWork
                    ModMain.LOGGER.info("ToolGunConfigurationPacket: receiving packet from ${player.name.string}")
//                val stack = player.inventory.items[input.pSlot]
                    val stack = player.mainHandItem
                    val item = stack.item
                    if (item is ToolGunItem) {
                        if (!player.cooldowns.isOnCooldown(item)) {
                            if (input.pModeSwitch) {
                                ModMain.LOGGER.info("ToolGunConfigurationPacket: executing tool gun mode switch")
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
                                ModMain.LOGGER.info("ToolGunConfigurationPacket: executing tool gun action")
                                val mode = item.getCurrentMode(stack).mode
                                (if (input.early) mode::actionEarly else mode::action)(
                                    player.level(),
                                    player,
                                    stack,
                                    input.pControl ?: return@enqueueWork
                                )
                            }
                            it.packetHandled = true
                        }
                    }
                }
            }
    }
}