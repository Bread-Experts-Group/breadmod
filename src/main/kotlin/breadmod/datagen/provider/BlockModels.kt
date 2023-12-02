package breadmod.datagen.provider

import breadmod.BreadMod
import net.minecraft.data.DataGenerator
import net.minecraftforge.client.model.generators.BlockModelProvider
import net.minecraftforge.common.data.ExistingFileHelper


class BlockModels (
    generator: DataGenerator,
    fileHelper: ExistingFileHelper,
) : BlockModelProvider(generator, BreadMod.ID, fileHelper) {
    override fun registerModels() {
        cubeAll("breadmod:bread_block", modLoc("block/bread_block"))
    }


}