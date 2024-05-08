package breadmod.datagen.tag

import breadmod.ModMain
import breadmod.registry.item.ModItems
import net.minecraft.core.HolderLookup
import net.minecraft.data.PackOutput
import net.minecraft.data.tags.ItemTagsProvider
import net.minecraft.resources.ResourceLocation
import net.minecraft.tags.ItemTags
import net.minecraft.world.level.block.Block
import net.minecraftforge.common.data.ExistingFileHelper
import java.util.concurrent.CompletableFuture

class ModItemTags(
    pOutput: PackOutput,
    pLookupProvider: CompletableFuture<HolderLookup.Provider>,
    pBlockTags: CompletableFuture<TagLookup<Block>>,
    existingFileHelper: ExistingFileHelper
) : ItemTagsProvider(pOutput, pLookupProvider, pBlockTags, ModMain.ID, existingFileHelper) {
    override fun addTags(pProvider: HolderLookup.Provider) {
        tag(ItemTags.MUSIC_DISCS)
            .add(ModItems.TEST_DISC.get())
        tag(ItemTags.CREEPER_DROP_MUSIC_DISCS)
            .add(ModItems.TEST_DISC.get())
        tag(ItemTags.SWORDS)
            .add(ModItems.BREAD_SWORD.get())
            .add(ModItems.RF_BREAD_SWORD.get())
        tag(ItemTags.PICKAXES)
            .add(ModItems.BREAD_PICKAXE.get())
            .add(ModItems.RF_BREAD_PICKAXE.get())
        tag(ItemTags.SHOVELS)
            .add(ModItems.BREAD_SHOVEL.get())
            .add(ModItems.RF_BREAD_SHOVEL.get())
        tag(ItemTags.AXES)
            .add(ModItems.BREAD_AXE.get())
            .add(ModItems.RF_BREAD_AXE.get())
        tag(ItemTags.HOES)
            .add(ModItems.BREAD_HOE.get())
            .add(ModItems.RF_BREAD_HOE.get())

        tag(ItemTags.create(ResourceLocation("forge", "flour/wheat")))
            .add(ModItems.FLOUR.get())
        tag(ItemTags.create(ResourceLocation("forge", "dough")))
            .add(ModItems.DOUGH.get())
        tag(ItemTags.create(ResourceLocation("forge", "dough/wheat")))
            .add(ModItems.DOUGH.get())

        tag(ItemTags.create(ResourceLocation("curios", "necklace")))
            .add(ModItems.BREAD_AMULET.get())
        ModItems.PROJECT_E?.also {
            tag(ItemTags.create(ResourceLocation("curios", "klein_star")))
                .add(it.BREAD_EMC_ITEM.get())
        }
    }
}