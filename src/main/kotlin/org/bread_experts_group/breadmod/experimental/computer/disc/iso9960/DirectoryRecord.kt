package org.bread_experts_group.breadmod.experimental.computer.disc.iso9960

import org.bread_experts_group.breadmod.experimental.computer.disc.iso9960.ISO9660Disc.Companion.getZoneIdFrom15M
import org.bread_experts_group.breadmod.experimental.computer.disc.iso9960.volumedescriptor.PrimaryVolume
import java.time.ZonedDateTime
import java.util.Optional
import kotlin.math.max

class DirectoryRecord(
	val extentPointer: Int, // LSB, MSB
	val extentSize: Int, // LSB, MSB
	val date: ZonedDateTime,
	val fileFlags: Byte, // TODO Write flags.
	val interleavedFileUnitSize: Optional<Byte>,
	val interleavedGapSize: Optional<Byte>,
	val volumeSequenceNumber: Short, // LSB, MSB
	val identifier: String
) {
	override fun toString(): String {
		return "${this::class.simpleName}(extentPointer=${this.extentPointer}, extentSize=${this.extentSize}, " +
				"date=${this.date}, fileFlags=${this.fileFlags}, " +
				"interleavedFileUnitSize=${this.interleavedFileUnitSize}, " +
				"interleavedGapSize=${this.interleavedGapSize}, " +
				"volumeSequenceNumber=${this.volumeSequenceNumber}, identifier=\"${this.identifier}\")"
	}

	fun getContainedRecords(
		parent: PrimaryVolume,
		stream: BufferedFileInputStream
	): List<DirectoryRecord> {
		stream.channel.position(this.extentPointer.toLong() * parent.logicalBlockSize)
		return buildList {
			try {
				while (true) this.add(Companion.readRecord(stream))
			} catch (_: Exception) {
			}
		}
	}

	companion object {
		fun readRecord(stream: BufferedFileInputStream): DirectoryRecord {
			val length = stream.read()
			/*val extendedAttributeRecordLength = */stream.read()
			val record = DirectoryRecord(
				stream.readLSBMSB(4).toInt(),
				stream.readLSBMSB(4).toInt(),
				ZonedDateTime.of(
					stream.read() + 1900,
					max(1, stream.read()),
					max(1, stream.read()),
					stream.read(),
					stream.read(),
					stream.read(),
					0,
					getZoneIdFrom15M(stream.read())
				),
				stream.read().toByte(),
				stream.read().let { if (it == 0) Optional.empty() else Optional.of(it.toByte()) },
				stream.read().let { if (it == 0) Optional.empty() else Optional.of(it.toByte()) },
				stream.readLSBMSB(2).toShort(),
				stream.readNBytes(stream.read()).decodeToString()
			)
			if (length % 2 == 1) {
				stream.read()
				stream.readNBytes((length - 34) - record.identifier.length)
			} else {
				stream.readNBytes((length - 33) - record.identifier.length)
			}
			return record
		}
	}
}