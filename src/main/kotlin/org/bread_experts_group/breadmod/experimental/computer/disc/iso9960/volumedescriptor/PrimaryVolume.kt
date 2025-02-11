package org.bread_experts_group.breadmod.experimental.computer.disc.iso9960.volumedescriptor

import org.bread_experts_group.breadmod.experimental.computer.disc.iso9960.DirectoryRecord
import org.bread_experts_group.breadmod.experimental.computer.disc.iso9960.ISO9660Disc
import java.time.ZonedDateTime
import java.util.Optional

class PrimaryVolume(
	identifier: String,
	version: ISO9660Disc.VolumeDescriptorVersion,
	rawVersion: Int,
	val systemIdentifier: String,
	val volumeIdentifier: String,
	// Skip 8
	val volumeSpaceSize: Int, // LSB, MSB
	// Skip 32
	val volumeSetSize: Short, // LSB, MSB
	val volumeSequenceNumber: Short, // LSB, MSB
	val logicalBlockSize: Short, // LSB, MSB
	val pathTableSize: Int, // LSB, MSB
	val typeLPathTablePointer: Optional<Int>, // LSB
	val optionalTypeLPathTablePointer: Optional<Int>, // LSB
	val typeMPathTablePointer: Optional<Int>, // MSB
	val optionalTypeMPathTablePointer: Optional<Int>, // MSB
	val rootDirectoryRecord: DirectoryRecord,
	val volumeSetIdentifier: String,
	val publisherIdentifier: String,
	val dataPreparerIdentifier: String,
	val applicationIdentifier: String,
	val copyrightFileIdentifier: String,
	val abstractFileIdentifier: String,
	val bibliographicFileIdentifier: String,
	val createdDate: Optional<ZonedDateTime>,
	val modifiedDate: Optional<ZonedDateTime>,
	val expirationDate: Optional<ZonedDateTime>,
	val effectiveDate: Optional<ZonedDateTime>,
	val fileStructureVersion: Byte,
	// Skip 1
	data: ByteArray
) : VolumeDescriptor(
	ISO9660Disc.VolumeDescriptorType.PRIMARY_VOLUME_DESCRIPTOR,
	ISO9660Disc.VolumeDescriptorType.PRIMARY_VOLUME_DESCRIPTOR.id,
	identifier,
	version,
	rawVersion,
	data
) {
	override fun toString(): String {
		return super.toString() +
				":(systemIdentifier=\"${this.systemIdentifier}\", " +
				"volumeIdentifier=\"${this.volumeIdentifier}\", " +
				"volumeSpaceSize=${this.volumeSpaceSize}, " +
				"volumeSetSize=${this.volumeSetSize}, " +
				"volumeSequenceNumber=${this.volumeSequenceNumber}, " +
				"logicalBlockSize=${this.logicalBlockSize}, " +
				"pathTableSize=${this.pathTableSize}, " +
				"typeLPathTablePointer=${this.typeLPathTablePointer}, " +
				"optionalTypeLPathTablePointer=${this.optionalTypeLPathTablePointer}, " +
				"typeMPathTablePointer=${this.typeMPathTablePointer}, " +
				"optionalTypeMPathTablePointer=${this.optionalTypeMPathTablePointer}, " +
				"rootDirectoryRecord=${this.rootDirectoryRecord}, " +
				"volumeSetIdentifier=\"${this.volumeSetIdentifier}\", " +
				"publisherIdentifier=\"${this.publisherIdentifier}\", " +
				"dataPreparerIdentifier=\"${this.dataPreparerIdentifier}\", " +
				"applicationIdentifier=\"${this.applicationIdentifier}\", " +
				"copyrightFileIdentifier=\"${this.copyrightFileIdentifier}\", " +
				"abstractFileIdentifier=\"${this.abstractFileIdentifier}\", " +
				"bibliographicFileIdentifier=\"${this.bibliographicFileIdentifier}\", " +
				"createdDate=${this.createdDate}, " +
				"modifiedDate=${this.modifiedDate}, " +
				"expirationDate=${this.expirationDate}, " +
				"effectiveDate=${this.effectiveDate}, " +
				"fileStructureVersion=${this.fileStructureVersion})"
	}
}