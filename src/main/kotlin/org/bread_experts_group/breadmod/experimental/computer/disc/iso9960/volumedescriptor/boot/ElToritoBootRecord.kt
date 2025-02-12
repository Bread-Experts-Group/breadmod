package org.bread_experts_group.breadmod.experimental.computer.disc.iso9960.volumedescriptor.boot

import org.bread_experts_group.breadmod.experimental.computer.disc.iso9960.BufferedFileInputStream
import org.bread_experts_group.breadmod.experimental.computer.disc.iso9960.ISO9660Disc
import org.bread_experts_group.breadmod.experimental.computer.disc.iso9960.volumedescriptor.PrimaryVolume
import org.bread_experts_group.breadmod.experimental.computer.disc.iso9960.volumedescriptor.boot.ElToritoBootRecord.Contents.ElToritoPlatform

/**
 * @see <a href=https://pdos.csail.mit.edu/6.828/2017/readings/boot-cdrom.pdf>El Torito 1.0 Specification (1994)</a>
 * @author Miko Elbrecht
 * @since 1.0.0
 */
class ElToritoBootRecord(
	identifier: String,
	version: ISO9660Disc.VolumeDescriptorVersion,
	rawVersion: Int,
	val bootCatalogPointer: Int,
	data: ByteArray
) : BootRecord(
	identifier,
	version,
	rawVersion,
	"EL TORITO SPECIFICATION",
	"",
	data
) {
	override fun toString(): String {
		return super.toString() + ":(bootCatalogPointer=${this.bootCatalogPointer})"
	}

	class Contents(
		val validationEntry: ValidationEntry,
		val standardEntries: List<StandardEntry>
	) {
		override fun toString(): String {
			return "${this::class.simpleName}(validationEntry=${this.validationEntry}, " +
					"standardEntries=${this.standardEntries::class.simpleName}<${StandardEntry::class.simpleName}>[" +
					"${this.standardEntries.size}])"
		}

		enum class ElToritoPlatform(val id: Int) {
			X86(0),
			POWERPC(1),
			MAC(2),
			OTHER(256);

			companion object {
				val mapping: Map<Int, ElToritoPlatform> = ElToritoPlatform.entries.associateBy { it.id }
			}
		}

		enum class ElToritoEmulationType(val id: Int) {
			NONE(0),
			FLOPPY_1_2M(1),
			FLOPPY_1_44M(2),
			FLOPPY_2_88M(3),
			HARD_DRIVE(4),
			OTHER(256);

			companion object {
				val mapping: Map<Int, ElToritoEmulationType> = ElToritoEmulationType.entries.associateBy { it.id }
			}
		}

		class ValidationEntry(
			val platform: ElToritoPlatform,
			val rawPlatform: Int,
			val identifier: String,
			val checksum: Short
		) {
			override fun toString(): String {
				return "${this::class.simpleName}(platform=${this.platform} (${this.rawPlatform}), " +
						"identifier=\"${this.identifier}\", checksum=${this.checksum})"
			}
		}

		class StandardEntry(
			val bootable: Boolean,
			val emulation: ElToritoEmulationType,
			val rawEmulation: Byte,
			val loadSegment: Short,
			val systemType: Byte,
			val sectorCount: Short,
			val loadRBA: Int,
			val data: ByteArray
		) {
			override fun toString(): String {
				return "${this::class.simpleName}(bootable=${this.bootable}, " +
						"emulation=${this.emulation} (${this.rawEmulation}), loadSegment=${this.loadSegment}, " +
						"systemType=${this.systemType}, sectorCount=${this.sectorCount}, loadRBA=${this.loadRBA}, " +
						"data=${this.data::class.simpleName}[${this.data.size}])"
			}
		}
	}

	fun readContents(parent: PrimaryVolume, stream: BufferedFileInputStream): Contents {
		stream.channel.position(parent.logicalBlockSize.toLong() * this.bootCatalogPointer)
		if (stream.read() != 1) throw IllegalArgumentException("Header ID in validation entry must be 1.")
		val rawPlatform = stream.read()
		stream.skip(2)
		val validationEntry = Contents.ValidationEntry(
			Contents.ElToritoPlatform.mapping.getOrDefault(rawPlatform, ElToritoPlatform.OTHER),
			rawPlatform,
			stream.readNBytes(24).decodeToString(),
			stream.readBinaryS(2).toShort()
		)
		if (stream.read() != 0x55) throw IllegalArgumentException("Key byte for 0x1E is incorrect.")
		if (stream.read() != 0xAA) throw IllegalArgumentException("Key byte for 0x1F is incorrect.")
		val standardEntries = buildList<Contents.StandardEntry> {
			val bootable = stream.read() == 0x88
			val emulationType = stream.read()
			this.add(
				Contents.StandardEntry(
					bootable,
					Contents.ElToritoEmulationType.mapping.getOrDefault(
						emulationType,
						Contents.ElToritoEmulationType.OTHER
					),
					emulationType.toByte(),
					stream.readBinaryS(2).toShort().let { if (it == (0).toShort()) 0x7C0 else it.toShort() },
					stream.read().toByte().also { stream.skip(1) },
					stream.readBinaryS(2).toShort(),
					stream.readBinaryS(4).toInt(),
					stream.readNBytes(20)
				)
			)
		}
		return Contents(validationEntry, standardEntries)
	}
}