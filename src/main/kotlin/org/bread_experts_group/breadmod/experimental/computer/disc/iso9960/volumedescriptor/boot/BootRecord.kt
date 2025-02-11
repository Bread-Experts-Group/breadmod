package org.bread_experts_group.breadmod.experimental.computer.disc.iso9960.volumedescriptor.boot

import org.bread_experts_group.breadmod.experimental.computer.disc.iso9960.ISO9660Disc
import org.bread_experts_group.breadmod.experimental.computer.disc.iso9960.volumedescriptor.VolumeDescriptor

open class BootRecord(
	identifier: String,
	version: ISO9660Disc.VolumeDescriptorVersion,
	rawVersion: Int,
	val bootSystemIdentifier: String,
	val bootIdentifier: String,
	data: ByteArray
) : VolumeDescriptor(
	ISO9660Disc.VolumeDescriptorType.BOOT_RECORD_VOLUME_DESCRIPTOR,
	ISO9660Disc.VolumeDescriptorType.BOOT_RECORD_VOLUME_DESCRIPTOR.id,
	identifier,
	version,
	rawVersion,
	data
) {
	override fun toString(): String {
		return super.toString() +
				":(bootSystemIdentifier=\"${this.bootSystemIdentifier}\", " +
				"bootIdentifier=\"${this.bootIdentifier}\")"
	}
}