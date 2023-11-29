package breadmod.block

import breadmod.BreadMod
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.SoundType
import net.minecraft.world.level.block.state.BlockBehaviour
import net.minecraft.world.level.material.Material
import net.minecraftforge.registries.DeferredRegister
import net.minecraftforge.registries.ForgeRegistries
import thedarkcolour.kotlinforforge.forge.registerObject


object ModBlocks {
    val REGISTRY: DeferredRegister<Block> = DeferredRegister.create(ForgeRegistries.BLOCKS, BreadMod.ID)

    // the returned ObjectHolderDelegate can be used as a property delegate
    // this is automatically registered by the deferred registry at the correct times
    //val EXAMPLE_BLOCK by REGISTRY.registerObject("example_block") {
    //    Block(BlockBehaviour.Properties.of(Material.BAMBOO).lightLevel { 15 }.strength(3.0f))
    //}
    val BREAD_BLOCK by REGISTRY.registerObject("bread_block") {
        Block(BlockBehaviour.Properties.of(Material.GRASS).strength(0.5f).sound(SoundType.GRASS))
    }
}