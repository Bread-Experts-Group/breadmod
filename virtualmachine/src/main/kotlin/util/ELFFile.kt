package breadmod.rnd.util

data class ELFFile(
    val ident: ELFIdentification,
    val abi: ELFABI,
    val type: ELFType,
    val machine: Short,
    val entryPoint: Long,
    val programHeaders: List<ProgramHeader>,
    val sectionHeaders: Map<String, SectionHeader>,
    val programHeaderStart: Long,
    val sectionHeaderStart: Long,
    val flags: Int,
    val fileHeaderSize: Short,
    val programHeaderEntrySize: Short,
    val programHeaderEntryCount: Short,
    val sectionHeaderEntrySize: Short,
    val sectionHeaderEntryCount: Short,
    val sectionNameEntryIndex: Short
) {
    data class ELFIdentification(
        val clazz: Bitness,
        val data: Endianness
    ) {
        enum class Bitness {
            BIT32,
            BIT64
        }
        enum class Endianness {
            LITTLE,
            BIG
        }
    }

    data class ELFABI(
        val type: Byte,
        val version: Byte
    )

    enum class ELFType(val cliff: Int) {
        ET_NONE(0x00),
        ET_REL(0x01),
        ET_EXEC(0x02),
        ET_DYN(0x03),
        ET_CORE(0x04),
        ET_OS(0xFE00),
        ET_PROC(0xFF00);

        companion object {
            private val reversed = entries.reversed()
            fun getType(forValue: Short): ELFType {
                reversed.forEachIndexed { index, elfType ->
                    val next = reversed.getOrNull(index + 1).let { if(it == null) true else (forValue > it.cliff) }
                    if(forValue <= elfType.cliff && next) return elfType
                }
                return ET_NONE
            }
        }
    }

    data class ProgramHeader(
        val type: Type,
        val offset: Long,
        val loadAtVirtualAddress: Long,
        val loadAtPhysicalAddress: Long,
        val fileSize: Long,
        val memorySize: Long,
        val alignment: Long?,
        val flags: Flags
    ) {
        enum class Type(val cliff: Int) {
            PT_NULL(0x00000000),
            PT_LOAD(0x00000001),
            PT_DYNAMIC(0x00000002),
            PT_INTERP(0x00000003),
            PT_NOTE(0x00000004),
            PT_SHLIB(0x00000005),
            PT_PHDR(0x00000006),
            PT_TLS(0x00000007),
            PT_OS(0x60000000),
            PT_PROC(0x70000000);

            companion object {
                private val reversed = entries.reversed()
                fun getType(forValue: Int): Type {
                    reversed.forEachIndexed { index, programType ->
                        val next = reversed.getOrNull(index + 1).let { if(it == null) true else (forValue > it.cliff) }
                        if(forValue <= programType.cliff && next) return programType
                    }
                    return PT_NULL
                }
            }
        }

        @Suppress("PropertyName")
        data class Flags(
            val PF_X: Boolean,
            val PF_W: Boolean,
            val PF_R: Boolean
        )
    }

    data class SectionHeader(
        val type: Type,
        val flags: Flags,
        val virtualAddress: Long,
        val offset: Long,
        val size: Long,
        val index: Int,
        val info: Int,
        val alignment: Long,
        val entrySize: Long?
    ) {
        enum class Type(val cliff: Int) {
            SHT_NULL(0x0),
            SHT_PROGBITS(0x1),
            SHT_SYMTAB(0x2),
            SHT_STRTAB(0x3),
            SHT_RELA(0x4),
            SHT_HASH(0x5),
            SHT_DYNAMIC(0x6),
            SHT_NOTE(0x7),
            SHT_NOBITS(0x8),
            SHT_REL(0x9),
            SHT_SHLIB(0x0A),
            SHT_DYNSYM(0x0B),
            SHT_INIT_ARRAY(0x0E),
            SHT_FINI_ARRAY(0x0F),
            SHT_PREINIT_ARRAY(0x10),
            SHT_GROUP(0x11),
            SHT_SYMTAB_SHNDX(0x12),
            SHT_NUM(0x13),
            SHT_OS(0x60000000);

            companion object {
                private val reversed = entries.reversed()
                fun getType(forValue: Int): Type {
                    reversed.forEachIndexed { index, programType ->
                        val next = reversed.getOrNull(index + 1).let { if(it == null) true else (forValue > it.cliff) }
                        if(forValue <= programType.cliff && next) return programType
                    }
                    return SHT_NULL
                }
            }
        }

        @Suppress("PropertyName")
        data class Flags(
            val SHF_WRITE: Boolean,
            val SHF_ALLOC: Boolean,
            val SHF_EXECINSTR: Boolean,
            val SHF_MERGE: Boolean,
            val SHF_STRINGS: Boolean,
            val SHF_INFO_LINK: Boolean,
            val SHF_LINK_ORDER: Boolean,
            val SHF_OS_NONCONFORMING: Boolean,
            val SHF_GROUP: Boolean,
            val SHF_TLS: Boolean,
            val SHF_MASKOS: Boolean,
            val SHF_MASKPROC: Boolean,
            val SHF_ORDERED: Boolean,
            val SHF_EXCLUDE: Boolean
        )
    }

    class MalformedELFException(reason: String): Exception(reason)

    companion object {
        fun bitCheck(n: Long, b: Long): Boolean = (n and b) == b
        fun bitCheck(n: Int, b: Int) = bitCheck(n.toLong(), b.toLong())

        private fun BinaryStringCursor.readBitwise(read64: Boolean) =
            if(read64) this.readLong() else this.readInt().toLong()
        
        fun decodeElf(data: BinaryStringCursor): ELFFile {
            if(data.readInt() != 0x7F454C46) throw MalformedELFException("Magic numbers incorrect")
            val ident = ELFIdentification(
                if(data.readByte().toInt() == 1) ELFIdentification.Bitness.BIT32 else ELFIdentification.Bitness.BIT64,
                if(data.readByte().toInt() == 1) ELFIdentification.Endianness.LITTLE else ELFIdentification.Endianness.BIG
            )
            data.skip(1)
            val programHeaders = mutableListOf<ProgramHeader>()
            val sectionHeaders = mutableMapOf<String, SectionHeader>()

            val use64 = ident.clazz == ELFIdentification.Bitness.BIT64
            val baseElf = ELFFile(
                ident,
                ELFABI(
                    data.readByte(),
                    data.readByte()
                ),
                data.skip(7).let { ELFType.getType(data.readShort()) },
                data.readShort(),
                data.skip(4).let { data.readBitwise(use64) },
                programHeaders,
                sectionHeaders, 
                data.readBitwise(use64),
                data.readBitwise(use64),
                data.readInt(),
                data.readShort(),
                data.readShort(),
                data.readShort(),
                data.readShort(),
                data.readShort(),
                data.readShort()
            )

            data.position = baseElf.programHeaderStart.toInt() // use an extended array for this?
            repeat(baseElf.programHeaderEntryCount.toInt()) {
                val type = ProgramHeader.Type.getType(data.readInt())
                val flags = if(use64) data.readInt() else null
                val offset = data.readBitwise(use64)
                val virtualAddress = data.readBitwise(use64)
                val physicalAddress = data.readBitwise(use64)
                val fileSize = data.readBitwise(use64)
                val memorySize = data.readBitwise(use64)
                val alignment = data.readBitwise(use64)
                val reifiedFlags = flags ?: data.readInt()
                programHeaders.add(ProgramHeader(
                    type,
                    offset,
                    virtualAddress,
                    physicalAddress,
                    fileSize,
                    memorySize,
                    if(alignment > 1) alignment else null,
                    ProgramHeader.Flags(
                        bitCheck(reifiedFlags, 0x1),
                        bitCheck(reifiedFlags, 0x2),
                        bitCheck(reifiedFlags, 0x4),
                    )
                ))
            }

            data.position = baseElf.sectionHeaderStart.toInt()
            var nameSectionHeader: SectionHeader
            val unnamedSections = buildMap {
                repeat(baseElf.sectionHeaderEntryCount.toInt()) {
                    set(
                        data.readInt(),
                        SectionHeader(
                            SectionHeader.Type.getType(data.readInt()),
                            (data.readBitwise(use64)).let {
                                 SectionHeader.Flags(
                                     bitCheck(it, 0x1),
                                     bitCheck(it, 0x2),
                                     bitCheck(it, 0x4),
                                     bitCheck(it, 0x10),
                                     bitCheck(it, 0x20),
                                     bitCheck(it, 0x40),
                                     bitCheck(it, 0x80),
                                     bitCheck(it, 0x100),
                                     bitCheck(it, 0x200),
                                     bitCheck(it, 0x400),
                                     bitCheck(it, 0x0FF00000),
                                     bitCheck(it, 0xF0000000),
                                     bitCheck(it, 0x4000000),
                                     bitCheck(it, 0x8000000)
                                 )
                            },
                            data.readBitwise(use64),
                            data.readBitwise(use64),
                            data.readBitwise(use64),
                            data.readInt(),
                            data.readInt(),
                            data.readBitwise(use64),
                            data.readBitwise(use64)
                        ).also {
                            if(it.type == SectionHeader.Type.SHT_STRTAB) nameSectionHeader = it
                        }
                    )
                }
            }

            return baseElf
        }
    }
}