package breadmod.block

import breadmod.BreadMod
import net.minecraft.data.loot.BlockLootSubProvider
import net.minecraft.world.flag.FeatureFlags
import net.minecraft.world.item.Item
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.state.BlockBehaviour
import net.minecraftforge.registries.DeferredRegister
import net.minecraftforge.registries.ForgeRegistries
import net.minecraftforge.registries.RegistryObject
import thedarkcolour.kotlinforforge.forge.registerObject

object ModBlocks {
    val REGISTRY: DeferredRegister<Block> = DeferredRegister.create(ForgeRegistries.BLOCKS, BreadMod.ID)

    val BREAD_BLOCK by REGISTRY.registerObject("bread_block") {
        Block(BlockBehaviour.Properties.copy(Blocks.HAY_BLOCK))
    }
    val REINFORCED_BREAD_BLOCK by REGISTRY.registerObject("reinforced_bread_block") { // Needs at least a wooden pick to break
        Block(BlockBehaviour.Properties.copy(Blocks.COBBLESTONE).strength(1f))
    }

    class ModBlockLoot :
        BlockLootSubProvider(emptySet<Item>(), FeatureFlags.REGISTRY.allFlags()) {
        override fun getKnownBlocks(): Iterable<Block> {
            return Iterable<Block> {
                REGISTRY.entries
                    .stream()
                    .flatMap<Block> { obj: RegistryObject<Block?> -> obj.stream() }
                    .iterator()
            }
        }

        override fun generate() {
            dropSelf(BREAD_BLOCK)
            dropSelf(REINFORCED_BREAD_BLOCK)
        }
    }
}