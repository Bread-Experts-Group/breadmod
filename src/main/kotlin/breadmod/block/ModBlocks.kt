package breadmod.block

import breadmod.BreadMod
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.state.BlockBehaviour
import net.minecraftforge.registries.DeferredRegister
import net.minecraftforge.registries.ForgeRegistries
import thedarkcolour.kotlinforforge.forge.registerObject


object ModBlocks {
    val REGISTRY: DeferredRegister<Block> = DeferredRegister.create(ForgeRegistries.BLOCKS, BreadMod.ID)

    val BREAD_BLOCK by REGISTRY.registerObject("bread_block") {
        Block(BlockBehaviour.Properties.copy(Blocks.HAY_BLOCK))
    }
}