package org.bread_experts_group.breadmod.datagen.tag

import net.minecraft.core.HolderLookup
import net.minecraft.data.PackOutput
import net.minecraft.data.tags.PaintingVariantTagsProvider
import net.minecraft.tags.PaintingVariantTags
import net.neoforged.neoforge.common.data.ExistingFileHelper
import org.bread_experts_group.breadmod.BreadMod
import org.bread_experts_group.breadmod.registry.entity.ModPainting
import java.util.concurrent.CompletableFuture

class ModPaintingTags(
	output : PackOutput,
	lookupProvider : CompletableFuture<HolderLookup.Provider>,
	existingFileHelper : ExistingFileHelper
) : PaintingVariantTagsProvider(output, lookupProvider, BreadMod.ID, existingFileHelper) {
	override fun addTags(provider : HolderLookup.Provider) {
		this.tag(PaintingVariantTags.PLACEABLE)
			.add(
				ModPainting.PAINTING_TEST,
				ModPainting.FISH,
				ModPainting.DEVIL_PUPP,
				ModPainting.CLASSIFIED,
				ModPainting.ELEPHANT
			)
	}
}