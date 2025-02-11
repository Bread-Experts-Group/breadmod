package org.bread_experts_group.breadmod.experimental.computer.disc.iso9960.volumedescriptor

import org.bread_experts_group.breadmod.experimental.computer.disc.iso9960.ISO9660Disc

open class VolumeDescriptor(
	val type: ISO9660Disc.VolumeDescriptorType,
	val rawType: Int,
	val identifier: String,
	val version: ISO9660Disc.VolumeDescriptorVersion,
	val rawVersion: Int,
	val data: ByteArray
) {
	override fun toString(): String {
		return "${this::class.simpleName}(type=${this.type} (${this.rawType}), identifier=\"${this.identifier}\", " +
				"version=${this.version} (${this.rawVersion}), data=${this.data::class.simpleName}[${this.data.size}])"
	}
}