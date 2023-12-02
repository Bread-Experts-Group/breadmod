package breadmod.datagen.provider

import breadmod.BreadMod
import net.minecraft.data.DataGenerator
import net.minecraftforge.client.model.generators.BlockStateProvider
import net.minecraftforge.common.data.ExistingFileHelper

class BlockStates (
    generator: DataGenerator,
    fileHelper: ExistingFileHelper,
) : BlockStateProvider(generator, BreadMod.ID, fileHelper) {
    override fun registerStatesAndModels() {
        TODO("Not yet implemented")
    }

}