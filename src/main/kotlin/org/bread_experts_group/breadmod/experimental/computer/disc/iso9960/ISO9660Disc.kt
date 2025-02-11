package org.bread_experts_group.breadmod.experimental.computer.disc.iso9960

import cpw.mods.niofs.union.UnionPath
import org.bread_experts_group.breadmod.experimental.computer.disc.iso9960.volumedescriptor.PrimaryVolume
import org.bread_experts_group.breadmod.experimental.computer.disc.iso9960.volumedescriptor.VolumeDescriptor
import org.bread_experts_group.breadmod.experimental.computer.disc.iso9960.volumedescriptor.boot.BootRecord
import org.bread_experts_group.breadmod.experimental.computer.disc.iso9960.volumedescriptor.boot.ElToritoBootRecord
import java.net.URI
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardOpenOption
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.util.Optional

class ISO9660Disc(
	val systemArea: ByteArray,
	val volumeDescriptors: List<VolumeDescriptor>,
	val discStream: BufferedFileInputStream
) {
	override fun toString(): String {
		return "${this::class.simpleName}(systemArea=${this.systemArea::class.simpleName}[${this.systemArea.size}], " +
				"volumeDescriptors=${this.volumeDescriptors::class.simpleName}<${VolumeDescriptor::class.simpleName}>" +
				"[${this.volumeDescriptors.size}], diskStream=${this.discStream})"
	}

	enum class VolumeDescriptorType(val id: Int) {
		BOOT_RECORD_VOLUME_DESCRIPTOR(0),
		PRIMARY_VOLUME_DESCRIPTOR(1),
		SUPPLEMENTARY_VOLUME_DESCRIPTOR(2),
		VOLUME_PARTITION_DESCRIPTOR(3),
		VOLUME_DESCRIPTOR_SET_TERMINATOR(255),
		OTHER(256);

		companion object {
			val mapping: Map<Int, VolumeDescriptorType> = entries.associateBy { it.id }
		}
	}

	enum class VolumeDescriptorVersion(val version: Int) {
		V_1(1),
		OTHER(256);

		companion object {
			val mapping: Map<Int, VolumeDescriptorVersion> = entries.associateBy { it.version }
		}
	}

	companion object {
		// TODO fix reading offset
		fun getZoneIdFrom15M(offset: Int): ZoneId = ZoneId.ofOffset(
			"GMT",
			ZoneOffset.ofHoursMinutes(0, 0)
		)

		fun readDisc(from: URI): ISO9660Disc {
			val stream = BufferedFileInputStream(
				Files.newByteChannel(
					Paths.get(from) as UnionPath,
					StandardOpenOption.READ
				)
			)
			val systemArea = stream.readNBytes(32768)
			val volumeDescriptors = mutableListOf<VolumeDescriptor>()

			while (true) {
				val rawType = stream.read()
				val identifier = stream.readNBytes(5).decodeToString()
				val rawVersion = stream.read()
				val version = VolumeDescriptorVersion.mapping.getOrDefault(rawVersion, VolumeDescriptorVersion.OTHER)
				volumeDescriptors.add(
					when (val type = VolumeDescriptorType.mapping.getOrDefault(rawType, VolumeDescriptorType.OTHER)) {
						VolumeDescriptorType.BOOT_RECORD_VOLUME_DESCRIPTOR -> {
							val specCheck = stream.readNBytes(32).decodeToString()
							val bootID = stream.readNBytes(32)
							if (specCheck.substring(0, 23) == "EL TORITO SPECIFICATION")
								ElToritoBootRecord(
									identifier,
									version,
									rawVersion,
									stream.readBinaryS(4),
									stream.readNBytes(1973)
								)
							else
								BootRecord(
									identifier,
									version,
									rawVersion,
									specCheck,
									bootID.decodeToString(),
									stream.readNBytes(1977)
								)
						}
						VolumeDescriptorType.PRIMARY_VOLUME_DESCRIPTOR     -> {
							fun asciiToInt(length: Int) = stream.readNBytes(length).decodeToString().trim().toInt()

							fun readDecDate(): Optional<ZonedDateTime> {
								val year = asciiToInt(4)
								val month = asciiToInt(2)
								val day = asciiToInt(2)
								val hour = asciiToInt(2)
								val minute = asciiToInt(2)
								val second = asciiToInt(2)
								val centisecond = asciiToInt(2)
								val offset = stream.read()
								if (year + month + day + hour + minute + second + centisecond + offset == 0)
									return Optional.empty()
								return Optional.of(
									ZonedDateTime.of(
										year, month, day,
										hour, minute, second,
										centisecond * 10000000,
										this.getZoneIdFrom15M(offset)
									)
								)
							}

							stream.skip(1)
							PrimaryVolume(
								identifier,
								version,
								rawVersion,
								stream.readNBytes(32).decodeToString(),
								stream.readNBytes(32).decodeToString().also {
									stream.skip(8)
								},
								stream.readLSBMSB(4).also {
									stream.skip(32)
								},
								stream.readLSBMSB(2).toShort(),
								stream.readLSBMSB(2).toShort(),
								stream.readLSBMSB(2).toShort(),
								stream.readLSBMSB(4),
								stream.readLSB(4),
								stream.readLSB(4).let { if (it.isPresent && it.get() == 0) Optional.empty() else it },
								stream.readMSB(4),
								stream.readMSB(4).let { if (it.isPresent && it.get() == 0) Optional.empty() else it },
								DirectoryRecord.readRecord(stream),
								stream.readNBytes(128).decodeToString(),
								stream.readNBytes(128).decodeToString(),
								stream.readNBytes(128).decodeToString(),
								stream.readNBytes(128).decodeToString(),
								stream.readNBytes(37).decodeToString(),
								stream.readNBytes(37).decodeToString(),
								stream.readNBytes(37).decodeToString(),
								readDecDate(),
								readDecDate(),
								readDecDate(),
								readDecDate(),
								stream.read().also { stream.skip(1) }.toByte(),
								stream.readNBytes(512).also { stream.skip(653) }
							)
						}
						else                                               -> VolumeDescriptor(
							type,
							rawType,
							identifier,
							version,
							rawVersion,
							stream.readNBytes(2041)
						)
					}
				)
				if (rawType == VolumeDescriptorType.VOLUME_DESCRIPTOR_SET_TERMINATOR.id) break
			}
			stream.close()
			return ISO9660Disc(systemArea, volumeDescriptors, stream)
		}
	}
}