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
import net.minecraftforge.fml.ModList
import java.util.concurrent.CompletableFuture
import breadmod.util.add
import breadmod.util.createTagKey
import net.minecraft.world.item.Items
import net.minecraftforge.registries.ForgeRegistries

class ModItemTags(
    pOutput: PackOutput,
    pLookupProvider: CompletableFuture<HolderLookup.Provider>,
    pBlockTags: CompletableFuture<TagLookup<Block>>,
    existingFileHelper: ExistingFileHelper
) : ItemTagsProvider(pOutput, pLookupProvider, pBlockTags, ModMain.ID, existingFileHelper) {
    override fun addTags(pProvider: HolderLookup.Provider) {
        tag(ItemTags.MUSIC_DISCS)
            .add(ModItems.TEST_DISC)
        tag(ItemTags.CREEPER_DROP_MUSIC_DISCS)
            .add(ModItems.TEST_DISC)
        tag(ItemTags.SWORDS)
            .add(ModItems.BREAD_SWORD, ModItems.RF_BREAD_SWORD)
        tag(ItemTags.PICKAXES)
            .add(ModItems.BREAD_PICKAXE, ModItems.RF_BREAD_PICKAXE)
        tag(ItemTags.SHOVELS)
            .add(ModItems.BREAD_SHOVEL, ModItems.RF_BREAD_SHOVEL)
        tag(ItemTags.AXES)
            .add(ModItems.BREAD_AXE, ModItems.RF_BREAD_AXE)
        tag(ItemTags.HOES)
            .add(ModItems.BREAD_HOE, ModItems.RF_BREAD_HOE)

        tag(ItemTags.create(ResourceLocation("forge", "flour/wheat")))
            .add(ModItems.FLOUR)
        tag(ItemTags.create(ResourceLocation("forge", "dough")))
            .add(ModItems.DOUGH)
        tag(ItemTags.create(ResourceLocation("forge", "dough/wheat")))
            .add(ModItems.DOUGH)

        tag(TOASTABLE)
            .add(Items.BREAD,
                ModItems.BREAD_SLICE.get(),
                ModItems.TOAST_SLICE.get(),
                ModItems.TOASTED_BREAD.get(),
                Items.CHARCOAL)

        tag(KNIVES)
            .add(ModItems.KNIFE)

        // Curios
        if(ModList.get().isLoaded("curios")) {
            tag(ItemTags.create(ResourceLocation("curios", "necklace")))
                .addOptional(ModItems.BASIC_BREAD_AMULET.id)
                .addOptional(ModItems.REINFORCED_BREAD_AMULET.id)
                .addOptional(ModItems.INDESTRUCTIBLE_BREAD_AMULET.id)
            ModItems.PROJECT_E?.also {
                tag(ItemTags.create(ResourceLocation("curios", "bread_orb")))
                    .addOptional(it.BREAD_ORB_ITEM.id)
            }
        }
    }

    companion object {
        val TOASTABLE = ForgeRegistries.ITEMS.createTagKey("breadmod:toastable")
        val KNIVES = ForgeRegistries.ITEMS.createTagKey("breadmod:tools/knives")
    }
}