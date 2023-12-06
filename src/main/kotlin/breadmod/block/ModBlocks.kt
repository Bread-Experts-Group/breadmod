package breadmod.block

import breadmod.BreadMod
import breadmod.datagen.provider.loot.LootSupplier
import com.mojang.datafixers.util.Pair
import net.minecraft.data.loot.BlockLoot
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.state.BlockBehaviour
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets
import net.minecraftforge.registries.DeferredRegister
import net.minecraftforge.registries.ForgeRegistries
import thedarkcolour.kotlinforforge.forge.registerObject
import java.util.function.Supplier


object ModBlocks {
    val REGISTRY: DeferredRegister<Block> = DeferredRegister.create(ForgeRegistries.BLOCKS, BreadMod.ID)

    val BREAD_BLOCK by REGISTRY.registerObject("bread_block") { // Can break with hoe
        Block(BlockBehaviour.Properties.copy(Blocks.HAY_BLOCK)) }
    val REINFORCED_BREAD_BLOCK by REGISTRY.registerObject("reinforced_bread_block") { // Needs at least a wooden pick to break
        Block(BlockBehaviour.Properties.copy(Blocks.COBBLESTONE).strength(1f))
    }
    // Make compressed bread block

    val lootSupplier: LootSupplier = Pair.of(Supplier { Loot }, LootContextParamSets.BLOCK)
    object Loot: BlockLoot() {
        override fun addTables() {
            dropSelf(BREAD_BLOCK)
            dropSelf(REINFORCED_BREAD_BLOCK)
        }

        override fun getKnownBlocks(): Iterable<Block> {
            return REGISTRY.entries.stream().map { it.get() }.toList()
        }
    }
}