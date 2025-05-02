package org.bread_experts_group.breadmod.registry.item.actual.tool_gun.mode

import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import net.minecraft.resources.ResourceLocation
import net.minecraft.sounds.SoundSource
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import org.bread_experts_group.breadmod.BreadMod.Companion.modTranslatable
import org.bread_experts_group.breadmod.api.IToolGunModeRenderer
import org.bread_experts_group.breadmod.api.ToolGunMode
import org.bread_experts_group.breadmod.client.ModTextureLocations
import org.bread_experts_group.breadmod.client.gui.components.ModeWidget
import org.bread_experts_group.breadmod.datagen.lang.DataGenerateLanguage
import org.bread_experts_group.breadmod.registry.block.ModBlocks
import org.bread_experts_group.breadmod.registry.block.ModBlocks.asBlock
import org.bread_experts_group.breadmod.registry.sound.ModSounds
import org.bread_experts_group.getDowncall
import org.bread_experts_group.getLookup
import java.lang.foreign.AddressLayout
import java.lang.foreign.Arena
import java.lang.foreign.Linker
import java.lang.foreign.MemorySegment
import java.lang.foreign.SymbolLookup
import java.lang.foreign.ValueLayout
import java.lang.invoke.MethodHandle

@ToolGunMode
@Suppress("unused")
class PowerMode : AbstractToolGunMode() {
	companion object {
		@DataGenerateLanguage("en_us", "Power Mode")
		val name: MutableComponent = modTranslatable("tool_gun", "power", "mode", "name")

		@DataGenerateLanguage("en_us", "WARNING! This will actually turn off your computer!")
		val description: MutableComponent = modTranslatable("tool_gun", "power", "mode", "description")

		@DataGenerateLanguage("en_us", "Power")
		val displayName: MutableComponent = modTranslatable("tool_gun", "power", "mode", "display_name")

		@DataGenerateLanguage("en_us", "This will turn off your computer!!!!")
		val tooltip: MutableComponent = modTranslatable("tool_gun", "power", "mode", "tooltip")
	}

	override fun action(level: Level, player: Player, stack: ItemStack) {
		if (!level.isClientSide) return
		val linker = Linker.nativeLinker()
		val localArena = Arena.ofAuto()
		val ntLookup: SymbolLookup = localArena.getLookup("ntdll.dll")
		val rtlAdjustPrivilege: MethodHandle = ntLookup.getDowncall(
			linker, "RtlAdjustPrivilege", ValueLayout.JAVA_INT,
			ValueLayout.JAVA_INT, ValueLayout.JAVA_BOOLEAN, ValueLayout.JAVA_BOOLEAN, ValueLayout.ADDRESS
		)
		val previousValueSegment = localArena.allocate(1)
		var returnCode = rtlAdjustPrivilege.invokeExact(
			19, true, false, previousValueSegment
		) as Int
		val previousSegmentValue = previousValueSegment.get(AddressLayout.JAVA_BOOLEAN, 0)
		player.sendSystemMessage(Component.literal("RtlAdjustPrivilege return code: $returnCode, $previousSegmentValue"))
		level.playLocalSound(player, ModSounds.WRONG.get(), SoundSource.MASTER, 1f, 1f)
		Thread.ofVirtual().start {
			Thread.sleep(3000)
			val ntRaiseHardError: MethodHandle = ntLookup.getDowncall(
				linker,
				"NtRaiseHardError",
				ValueLayout.JAVA_INT,
				ValueLayout.JAVA_INT,
				ValueLayout.JAVA_LONG,
				ValueLayout.ADDRESS,
				ValueLayout.ADDRESS,
				ValueLayout.JAVA_INT,
				ValueLayout.ADDRESS
			)
			val returnSegment = localArena.allocate(4)
			returnCode = ntRaiseHardError.invokeExact(
				(0xBA7AC5A0).toInt(), 0L, MemorySegment.NULL, MemorySegment.NULL, 6, returnSegment
			) as Int
			val returnSegmentValue = returnSegment.get(AddressLayout.JAVA_INT, 0)
			player.sendSystemMessage(Component.literal("NtRaiseHardError return code: $returnCode, $returnSegmentValue"))
		}
	}

	override fun getDisplayName(): Component = Companion.displayName
	override fun getTooltip(): Component = Companion.tooltip
	override fun getUid(): ResourceLocation = this.toolGunLocation("power_mode")
	override fun getCustomRenderer(): IToolGunModeRenderer = ToolGunSpinningBlockRenderer(
		this.getUid(),
		ModBlocks.ENERGY_STORAGE.asBlock(),
		ModeWidget.Builder()
			.previewImage(ModTextureLocations.POWER_PREVIEW)
			.name(Companion.name)
			.description(Companion.description)
	)
}