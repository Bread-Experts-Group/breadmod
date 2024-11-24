package org.bread_experts_group.breadmod.network.serverbound

import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.neoforged.neoforge.network.handling.IPayloadContext
import org.bread_experts_group.breadmod.BreadMod
import org.bread_experts_group.breadmod.BreadMod.Companion.modLocation
import org.bread_experts_group.breadmod.datagen.tool_gun.ToolGunModeDataLoader
import org.bread_experts_group.breadmod.datagen.tool_gun.ToolGunModeProvider
import org.bread_experts_group.breadmod.registry.item.actual.tool_gun.ToolGunItem
import org.bread_experts_group.breadmod.registry.component.ModDataComponents

class ToolGunConfigurationPacket(
    val modeSwitch: Boolean,
    val control: ToolGunModeProvider.Control = ToolGunModeProvider.Control.EMPTY,
    val early: Boolean = false
) : CustomPacketPayload {
    companion object {
        val TYPE: CustomPacketPayload.Type<ToolGunConfigurationPacket> =
            CustomPacketPayload.Type(modLocation("tool_gun_configuration"))

        val STREAM_CODEC: StreamCodec<RegistryFriendlyByteBuf, ToolGunConfigurationPacket> = StreamCodec.composite(
            ByteBufCodecs.BOOL, ToolGunConfigurationPacket::modeSwitch,
            ToolGunModeProvider.Control.CODEC, ToolGunConfigurationPacket::control,
            ByteBufCodecs.BOOL, ToolGunConfigurationPacket::early,
            ::ToolGunConfigurationPacket
        )

        fun handleServerboundPacket(data: ToolGunConfigurationPacket, context: IPayloadContext) {
            context.enqueueWork {
                val player = context.player()
                BreadMod.LOGGER.info("ToolGunConfigurationPacket: receiving packet from ${player.name.string}")

                val stack = player.mainHandItem
                val item = stack.item
                if (item is ToolGunItem) {
                    if (!player.cooldowns.isOnCooldown(item)) {
                        if (data.modeSwitch) {
                            val currentMode = item.ensureCurrentMode(stack)
                            val namespaceIterator = MapIterator(ToolGunModeDataLoader.modes)
                            namespaceIterator.restoreState(currentMode.namespaceIteratorState)
                            val modeIterator = MapIterator(namespaceIterator.current().value)

                            val last = modeIterator.current().value.first
                            when {
                                modeIterator.hasNext() -> {
                                    currentMode.name = modeIterator.next().key
                                    currentMode.modeIteratorState = modeIterator.saveState()
                                    stack.set(ModDataComponents.TOOL_GUN_DATA.get(), currentMode)
                                }

                                namespaceIterator.hasNext() -> {
                                    currentMode.namespace = namespaceIterator.next().key
                                    currentMode.namespaceIteratorState = namespaceIterator.saveState()
                                    currentMode.modeIteratorState = 0
                                    currentMode.name = modeIterator.current().key
                                    stack.set(ModDataComponents.TOOL_GUN_DATA.get(), currentMode)
                                }

                                else -> {
                                    currentMode.resetIteratorStates()
                                    currentMode.namespace = namespaceIterator.current().key
                                    currentMode.name = modeIterator.current().key
                                    stack.set(ModDataComponents.TOOL_GUN_DATA.get(), currentMode)
                                }
                            }

                            val level = player.level()
                            last.mode.close(level, player, stack, modeIterator.current().value.first.mode)
                            modeIterator.current().value.first.mode.open(level, player, stack, last.mode)
                            player.cooldowns.addCooldown(item, 10)
                        } else {
                            val mode = item.getCurrentMode(stack).mode
                            (if (data.early) mode::actionEarly else mode::action)(
                                player.level(),
                                player,
                                stack,
                                data.control
                            )
                        }
                    }
                }
            }
        }
    }

    override fun type(): CustomPacketPayload.Type<out CustomPacketPayload> = TYPE
}