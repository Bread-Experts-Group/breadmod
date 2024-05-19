package breadmod.registry.entity

import breadmod.ModMain
import breadmod.ModMain.modLocation
import breadmod.entity.PrimedHappyBlock
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.MobCategory
import net.minecraft.world.level.Level
import net.minecraftforge.registries.DeferredRegister
import net.minecraftforge.registries.ForgeRegistries
import net.minecraftforge.registries.RegistryObject

object ModEntityTypes {
    val deferredRegister: DeferredRegister<EntityType<*>> = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, ModMain.ID)

    val HAPPY_BLOCK_ENTITY: RegistryObject<EntityType<PrimedHappyBlock>> = deferredRegister.register("happy_block") {
        EntityType.Builder.of({ _, pLevel -> PrimedHappyBlock(pLevel, shouldSpread = true) }, MobCategory.AMBIENT)
            .sized(0.98f, 0.98f)
            .build(modLocation("happy_block").toString())
    }
}
