package org.bread_experts_group.breadmod.registry.item.actual.tool_gun.mode

import com.mojang.blaze3d.platform.InputConstants
import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.client.KeyMapping
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.util.StringRepresentable
import net.neoforged.neoforge.client.settings.KeyConflictContext
import net.neoforged.neoforge.client.settings.KeyModifier
import net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs

// todo there's probably a much better way to send keybinds over server
//  but this is the easiest way for me right now
data class KeyMappingData(
	val description: String,
	val conflictContext: KeyConflictContextServer,
	val modifier: KeyModifierServer,
	val key: Int,
	val category: String
) {
	companion object {
		val CODEC: Codec<KeyMappingData> = RecordCodecBuilder.create { inst ->
			inst.group(
				Codec.STRING.fieldOf("description").forGetter(KeyMappingData::description),
				KeyConflictContextServer.CODEC.fieldOf("conflict").forGetter(KeyMappingData::conflictContext),
				KeyModifierServer.CODEC.fieldOf("modifier").forGetter(KeyMappingData::modifier),
				Codec.INT.fieldOf("key").forGetter(KeyMappingData::key),
				Codec.STRING.fieldOf("category").forGetter(KeyMappingData::category)
			).apply(inst, ::KeyMappingData)
		}
		val STREAM_CODEC: StreamCodec<FriendlyByteBuf, KeyMappingData> = StreamCodec.composite(
			ByteBufCodecs.STRING_UTF8, KeyMappingData::description,
			KeyConflictContextServer.STREAM_CODEC, KeyMappingData::conflictContext,
			KeyModifierServer.STREAM_CODEC, KeyMappingData::modifier,
			ByteBufCodecs.INT, KeyMappingData::key,
			ByteBufCodecs.STRING_UTF8, KeyMappingData::category,
			::KeyMappingData
		)
		val EMPTY: KeyMappingData = KeyMappingData(
			"empty",
			KeyConflictContextServer.UNIVERSAL,
			KeyModifierServer.NONE,
			265,
			"empty"
		)
	}

	// todo needs to support more than just keyboard for input
	fun toKeyMapping(): KeyMapping = KeyMapping(
		this.description,
		this.toKeyConflictContext(this.conflictContext),
		this.toKeyModifier(this.modifier),
		InputConstants.Type.KEYSYM.getOrCreate(this.key),
		this.category
	)

	private fun toKeyConflictContext(conflict: KeyConflictContextServer): KeyConflictContext =
		when (conflict) {
			KeyConflictContextServer.UNIVERSAL -> KeyConflictContext.UNIVERSAL
			KeyConflictContextServer.GUI       -> KeyConflictContext.GUI
			KeyConflictContextServer.IN_GAME   -> KeyConflictContext.IN_GAME
		}

	private fun toKeyModifier(modifier: KeyModifierServer): KeyModifier =
		when (modifier) {
			KeyModifierServer.CONTROL -> KeyModifier.CONTROL
			KeyModifierServer.SHIFT   -> KeyModifier.SHIFT
			KeyModifierServer.ALT     -> KeyModifier.ALT
			KeyModifierServer.NONE    -> KeyModifier.NONE
		}

	/**
	 * Send over [KeyConflictContext] without causing a NoClassDefFound on server.
	 */
	enum class KeyConflictContextServer(private val cName: String) : StringRepresentable {
		UNIVERSAL("universal"), GUI("gui"), IN_GAME("in_game");

		override fun getSerializedName(): String = this.cName

		companion object {
			val CODEC: Codec<KeyConflictContextServer> =
				StringRepresentable.fromEnum(KeyConflictContextServer::values)
			val STREAM_CODEC: StreamCodec<FriendlyByteBuf, KeyConflictContextServer> =
				NeoForgeStreamCodecs.enumCodec(KeyConflictContextServer::class.java)
		}
	}

	/**
	 * Send over [KeyModifier] without causing a NoClassDefFound on server.
	 */
	enum class KeyModifierServer(private val cName: String) : StringRepresentable {
		CONTROL("control"), SHIFT("shift"), ALT("alt"), NONE("none");

		override fun getSerializedName(): String = this.cName

		companion object {
			val CODEC: Codec<KeyModifierServer> =
				StringRepresentable.fromEnum(KeyModifierServer::values)
			val STREAM_CODEC: StreamCodec<FriendlyByteBuf, KeyModifierServer> =
				NeoForgeStreamCodecs.enumCodec(KeyModifierServer::class.java)
		}
	}
}