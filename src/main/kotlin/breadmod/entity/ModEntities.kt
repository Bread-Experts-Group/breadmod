package breadmod.entity

import breadmod.BreadMod
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.MobCategory
import net.minecraft.world.level.Level
import net.minecraftforge.registries.DeferredRegister
import net.minecraftforge.registries.ForgeRegistries
import net.minecraftforge.registries.RegistryObject

@Suppress("unused")
object ModEntities {
    val REGISTRY: DeferredRegister<EntityType<*>> = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, BreadMod.ID)

    val HAPPY_BLOCK_ENTITY: RegistryObject<EntityType<PrimedHappyBlock>> = REGISTRY.register("happy_block") {
        EntityType.Builder.of({ pEntityType: EntityType<PrimedHappyBlock>, pLevel: Level ->
            PrimedHappyBlock(pEntityType, pLevel) }, MobCategory.MISC)
            .sized(0.98f, 0.98f)
            .build(ResourceLocation(BreadMod.ID, "happy_block").toString())
    }
}
