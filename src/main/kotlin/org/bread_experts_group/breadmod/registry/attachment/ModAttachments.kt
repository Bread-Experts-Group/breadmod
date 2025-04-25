package org.bread_experts_group.breadmod.registry.attachment

import com.mojang.serialization.Codec
import net.neoforged.neoforge.attachment.AttachmentType
import net.neoforged.neoforge.registries.DeferredHolder
import net.neoforged.neoforge.registries.DeferredRegister
import net.neoforged.neoforge.registries.NeoForgeRegistries
import org.bread_experts_group.breadmod.BreadMod

object ModAttachments {
	val ATTACHMENT_REGISTRY: DeferredRegister<AttachmentType<*>> =
		DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, BreadMod.ID)

	@JvmStatic
	val KEEP_INVENTORY_NEXT_DEATH: DeferredHolder<AttachmentType<*>, AttachmentType<Boolean>> =
		this.ATTACHMENT_REGISTRY.register("keep_inventory_next_death", AttachmentType.builder { _ -> false }
			.serialize(Codec.BOOL)
			.copyOnDeath()::build)
}