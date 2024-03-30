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
import java.util.function.Function
import java.util.stream.Stream

object ModBlocks {
    val REGISTRY = DeferredRegister.create(ForgeRegistries.BLOCKS, BreadMod.ID)

    // the returned ObjectHolderDelegate can be used as a property delegate
    // this is automatically registered by the deferred registry at the correct times
//    val EXAMPLE_BLOCK by REGISTRY.registerObject("example_block") {
//        Block(BlockBehaviour.Properties.of().lightLevel { 15 }.strength(3.0f))
//    }

    val BREAD_BLOCK by REGISTRY.registerObject("bread_block") {
        Block(BlockBehaviour.Properties.copy(Blocks.HAY_BLOCK))
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
        }
    }
}