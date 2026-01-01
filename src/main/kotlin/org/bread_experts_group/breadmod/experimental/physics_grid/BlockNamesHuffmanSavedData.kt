package org.bread_experts_group.breadmod.experimental.physics_grid

import net.minecraft.core.HolderLookup
import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.nbt.CompoundTag
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.saveddata.SavedData
import org.bread_experts_group.protocol.huffman.HuffmanBranch
import org.bread_experts_group.protocol.huffman.HuffmanCut
import org.bread_experts_group.protocol.huffman.HuffmanEdge
import org.bread_experts_group.protocol.huffman.HuffmanNode
import java.io.ByteArrayOutputStream

class BlockNamesHuffmanSavedData private constructor(
	val nameSpaceHuffman: HuffmanNode<String>,
	val nameSpaceReverseHuffman: Map<String, BooleanArray>,
	val nameHuffman: HuffmanNode<Char>,
	val nameReverseHuffman: Map<Char, BooleanArray>,
) : SavedData() {
	companion object {
		fun create(blockRegistry: Registry<Block>): BlockNamesHuffmanSavedData {
			val namespaceFrequencies = mutableMapOf<String, Int>()
			val blockNameFrequencies = mutableMapOf(
				'_' to 0,
				'-' to 0,
				'/' to 0,
				'.' to 0
			)
			('a' .. 'z').forEach { blockNameFrequencies[it] = 0 }
			('0' .. '9').forEach { blockNameFrequencies[it] = 0 }
			blockRegistry.forEach { block ->
				blockRegistry.getKey(block)?.let { key ->
					namespaceFrequencies[key.namespace] = (namespaceFrequencies[key.namespace] ?: 0) + 1
					key.path.forEach { pathChar ->
						blockNameFrequencies[pathChar] =
							(blockNameFrequencies[pathChar] ?: throw IllegalStateException()) + 1
					}
				}
			}
			val sortedNameSpaces = namespaceFrequencies.toList()
				.sortedBy { (_, count) -> count }.toMutableList()
			var nameSpaceHuffman: HuffmanNode<String> = HuffmanCut()
			val nameSpaceReverseHuffman = mutableMapOf<String, BooleanArray>()
			while (sortedNameSpaces.isNotEmpty()) {
				val thisEdge = HuffmanEdge(sortedNameSpaces.removeFirst().first)
				nameSpaceHuffman = if (nameSpaceHuffman is HuffmanCut) {
					nameSpaceReverseHuffman[thisEdge.value] = booleanArrayOf(false)
					thisEdge
				} else {
					nameSpaceReverseHuffman.forEach { (key, bits) ->
						nameSpaceReverseHuffman[key] = booleanArrayOf(true) + bits
					}
					nameSpaceReverseHuffman[thisEdge.value] = booleanArrayOf(false)
					HuffmanBranch(thisEdge, nameSpaceHuffman)
				}
			}
			val sortedNames = blockNameFrequencies.filterValues { it > 0 }.toList()
				.sortedBy { (_, count) -> count }
				.toMutableList()
			var nameHuffman: HuffmanNode<Char> = HuffmanCut()
			val nameReverseHuffman = mutableMapOf<Char, BooleanArray>()
			while (sortedNames.isNotEmpty()) {
				val thisEdge = HuffmanEdge(sortedNames.removeFirst().first)
				nameHuffman = if (nameHuffman is HuffmanCut) {
					nameReverseHuffman[thisEdge.value] = booleanArrayOf(false)
					thisEdge
				} else {
					nameReverseHuffman.forEach { (key, bits) ->
						nameReverseHuffman[key] = booleanArrayOf(true) + bits
					}
					nameReverseHuffman[thisEdge.value] = booleanArrayOf(false)
					HuffmanBranch(thisEdge, nameHuffman)
				}
			} // TODO: Fairer dispositions for close frequencies
			return BlockNamesHuffmanSavedData(
				nameSpaceHuffman,
				nameSpaceReverseHuffman,
				nameHuffman,
				nameReverseHuffman
			).also {
				it.setDirty()
			}
		}

		fun <T> decodeHuffman(
			bits: ByteArray,
			decoder: (ByteArray) -> T
		): Pair<HuffmanNode<T>, Map<T, BooleanArray>> {
			var workingByte = bits[0]
			var workingByteOffset = 0
			var offset = 1
			fun nextByte() {
				workingByte = bits[offset++]
				workingByteOffset = 0
			}

			fun nextBit(): Boolean {
				if (workingByteOffset == 8) nextByte()
				return ((workingByte.toInt() and 0xFF) ushr (workingByteOffset++)) and 1 == 1
			}

			val reverseEntry = mutableListOf<Boolean>()
			val reverseHuffman = mutableMapOf<T, BooleanArray>()
			fun readEdge(): HuffmanEdge<T> {
				nextByte()
				workingByteOffset = 8
				val length = workingByte.toInt() and 0xFF
				val data = bits.sliceArray(offset ..< (offset + length))
				offset += length
				val value = decoder(data)
				reverseHuffman[value] = reverseEntry.toBooleanArray()
				return HuffmanEdge(value)
			}

			var readBranch: () -> HuffmanBranch<T> = { throw IllegalStateException() }
			fun readNode(one: Boolean?): HuffmanNode<T> {
				val nextBit = nextBit()
				if (one != null) reverseEntry.addLast(one)
				val value = if (nextBit) readBranch() else readEdge()
				if (one != null) reverseEntry.removeLast()
				return value
			}

			readBranch = {
				HuffmanBranch(
					readNode(false),
					readNode(true)
				)
			}
			return readNode(null) to reverseHuffman
		}

		fun load(compoundTag: CompoundTag): BlockNamesHuffmanSavedData {
			val (nsH, rNsH) = this.decodeHuffman(compoundTag.getByteArray("namespaces")) { it.toString(Charsets.UTF_8) }
			val (nH, rNH) = this.decodeHuffman(compoundTag.getByteArray("names")) { Char(it[0].toInt() and 0xFF) }
			return BlockNamesHuffmanSavedData(
				nsH, rNsH,
				nH, rNH
			)
		}

		val FACTORY: Factory<BlockNamesHuffmanSavedData> = Factory(
			{ this.create(BuiltInRegistries.BLOCK) },
			{ tag, _ -> this.load(tag) }
		)
	}

	fun <T> encodeHuffman(
		huffman: HuffmanNode<T>,
		encoder: (T) -> ByteArray
	): ByteArray {
		val chain = mutableListOf(huffman)
		val bits = ByteArrayOutputStream()
		var workingByte = 0
		var workingByteOffset = 0
		fun flushBits() {
			if (workingByteOffset == 0) return
			bits.write(workingByte)
			workingByte = 0
			workingByteOffset = 0
		}

		fun writeBit(b: Boolean) {
			if (workingByteOffset == 8) flushBits()
			workingByte = workingByte or ((if (b) 1 else 0) shl workingByteOffset++)
		}

		while (chain.isNotEmpty()) {
			val last = chain.removeLast()
			if (last is HuffmanBranch) {
				writeBit(true)
				chain.addFirst(last.zero)
				chain.addFirst(last.one)
			} else {
				writeBit(false)
				flushBits()
				val data = encoder((last as HuffmanEdge).value)
				if (data.size > 255) TODO("Ext")
				bits.write(data.size)
				bits.writeBytes(data)
			}
		}
		return bits.toByteArray()
	}

	override fun save(tag: CompoundTag, provider: HolderLookup.Provider): CompoundTag {
		tag.putByteArray("namespaces", this.encodeHuffman(this.nameSpaceHuffman) { it.toByteArray(Charsets.UTF_8) })
		tag.putByteArray("names", this.encodeHuffman(this.nameHuffman) {
			if (it.code > 255) TODO("lrg code") else byteArrayOf(it.code.toByte())
		})
		return tag
	}
}