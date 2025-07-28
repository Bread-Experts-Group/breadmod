package org.bread_experts_group.breadmod.tool_gun.mode

import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import org.bread_experts_group.breadmod.BreadMod.Companion.modTranslatable
import org.bread_experts_group.breadmod.api.IToolGunMode
import org.bread_experts_group.breadmod.api.ToolGunMode
import org.bread_experts_group.breadmod.datagen.lang.DataGenerateLanguage

@ToolGunMode
@Suppress("unused")
class PowerMode : IToolGunMode {
	companion object {
		@DataGenerateLanguage(name = "Power Mode")
		val name: MutableComponent = modTranslatable("tool_gun", "power", "mode", "name")

		@DataGenerateLanguage(name = "WARNING! This will actually turn off your computer!")
		val description: MutableComponent = modTranslatable("tool_gun", "power", "mode", "description")

		@DataGenerateLanguage(name = "Power")
		val displayName: MutableComponent = modTranslatable("tool_gun", "power", "mode", "display_name")

		@DataGenerateLanguage(name = "This will turn off your computer!!!!")
		val tooltip: MutableComponent = modTranslatable("tool_gun", "power", "mode", "tooltip")
	}

//	private val linker: Linker = Linker.nativeLinker()
//	private val localArena: Arena = Arena.ofAuto()
//	private val ntLookup: SymbolLookup = this.localArena.getLookup("ntdll.dll")
//	val rtlAdjustPrivilege: MethodHandle = this.ntLookup.getDowncall(
//		this.linker, "RtlAdjustPrivilege", ValueLayout.JAVA_INT,
//		ValueLayout.JAVA_INT, ValueLayout.JAVA_BOOLEAN, ValueLayout.JAVA_BOOLEAN, ValueLayout.ADDRESS
//	)
//	val ntRaiseHardError: MethodHandle = this.ntLookup.getDowncall(
//		this.linker,
//		"NtRaiseHardError",
//		ValueLayout.JAVA_INT,
//		ValueLayout.JAVA_INT,
//		ValueLayout.JAVA_LONG,
//		ValueLayout.ADDRESS,
//		ValueLayout.ADDRESS,
//		ValueLayout.JAVA_INT,
//		ValueLayout.ADDRESS
//	)
//	val dataSegment: MemorySegment = this.localArena.allocate(4)

	override fun action(level: Level, player: Player, stack: ItemStack) {
		if (!level.isClientSide) return
//		Thread.ofVirtual().start {
//			var returnCode = this.rtlAdjustPrivilege.invokeExact(19, true, false, this.dataSegment) as Int
//			var previousSegmentValue = this.dataSegment.get(AddressLayout.JAVA_BOOLEAN, 0)
//			player.sendSystemMessage(Component.literal("RtlAdjustPrivilege (on) return code: $returnCode, $previousSegmentValue"))
//			level.playLocalSound(player, ModSounds.WRONG.get(), SoundSource.MASTER, 1f, 1f)
//			Thread.sleep(2500)
//			returnCode = this.ntRaiseHardError.invokeExact(
//				(0xDA7AC5A0).toInt(), 0L, MemorySegment.NULL, MemorySegment.NULL, 6, this.dataSegment
//			) as Int
//			val returnSegmentValue = this.dataSegment.get(AddressLayout.JAVA_INT, 0)
//			player.sendSystemMessage(Component.literal("NtRaiseHardError return code: $returnCode, $returnSegmentValue"))
//			returnCode = this.rtlAdjustPrivilege.invokeExact(19, false, false, this.dataSegment) as Int
//			previousSegmentValue = this.dataSegment.get(AddressLayout.JAVA_BOOLEAN, 0)
//			player.sendSystemMessage(Component.literal("RtlAdjustPrivilege (off) return code: $returnCode, $previousSegmentValue"))
//		}
	}

	override fun getDisplayName(): Component = Companion.displayName
	override fun getTooltip(): Component = Companion.tooltip
	override fun getUid(): ResourceLocation = this.toolGunLocation("power_mode")
//	override fun defineCustomRenderer(): IToolGunModeRenderer = ToolGunSpinningBlockRenderer(
//		this,
//		ModBlocks.ENERGY_STORAGE.asBlock(),
//		Builder()
//			.previewImage(ModGuiElements.POWER_PREVIEW)
//			.name(Companion.name)
//			.description(Companion.description)
//	)
}