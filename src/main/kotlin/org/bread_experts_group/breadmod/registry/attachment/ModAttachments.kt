package org.bread_experts_group.breadmod.registry.attachment

import com.mojang.serialization.Codec
import net.neoforged.neoforge.attachment.AttachmentType
import net.neoforged.neoforge.registries.DeferredHolder
import net.neoforged.neoforge.registries.DeferredRegister
import net.neoforged.neoforge.registries.NeoForgeRegistries
import org.bread_experts_group.breadmod.registry.RegistryProvider

object ModAttachments : RegistryProvider(NeoForgeRegistries.Keys.ATTACHMENT_TYPES) {
	private val registry: DeferredRegister<AttachmentType<*>> = this.getRegistry(
		NeoForgeRegistries.Keys.ATTACHMENT_TYPES
	)

	@JvmStatic
	val KEEP_INVENTORY_NEXT_DEATH: DeferredHolder<AttachmentType<*>, AttachmentType<Boolean>> =
		this.registry.register("keep_inventory_next_death", AttachmentType.builder { _ -> false }
			.serialize(Codec.BOOL)
			.copyOnDeath()::build)
}