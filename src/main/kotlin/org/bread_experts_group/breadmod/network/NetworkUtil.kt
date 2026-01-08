package org.bread_experts_group.breadmod.network

import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import org.bread_experts_group.breadmod.BreadMod.Companion.modLocation

/**
 * CustomPacketPayload.Type with breadmod's resource location prefixed.
 */
fun <T : CustomPacketPayload> payloadType(id: String): CustomPacketPayload.Type<T> =
	CustomPacketPayload.Type(modLocation("bm_packet_$id"))