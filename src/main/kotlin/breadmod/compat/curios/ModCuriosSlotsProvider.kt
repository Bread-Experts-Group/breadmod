package breadmod.compat.curios

import breadmod.ModMain
import net.minecraft.core.HolderLookup
import net.minecraft.data.PackOutput
import net.minecraft.world.entity.EntityType
import net.minecraftforge.common.data.ExistingFileHelper
import top.theillusivec4.curios.api.CuriosDataProvider
import java.util.concurrent.CompletableFuture

internal class ModCuriosSlotsProvider(
    pOutput: PackOutput,
    pFileHelper: ExistingFileHelper,
    pRegistries: CompletableFuture<HolderLookup.Provider>
) : CuriosDataProvider(ModMain.ID, pOutput, pFileHelper, pRegistries) {
    override fun generate(pProvider: HolderLookup.Provider, pFileHelper: ExistingFileHelper) {
        createEntities(ModMain.ID).addEntities(EntityType.PLAYER).addSlots("bread_orb")
        createSlot("bread_orb").order(1000).size(1)
    }
}