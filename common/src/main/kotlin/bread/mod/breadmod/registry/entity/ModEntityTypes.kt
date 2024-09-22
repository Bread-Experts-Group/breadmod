package bread.mod.breadmod.registry.entity

import bread.mod.breadmod.ModMainCommon.MOD_ID
import bread.mod.breadmod.ModMainCommon.modLocation
import bread.mod.breadmod.datagen.language.DataGenerateLanguage
import bread.mod.breadmod.entity.PrimedHappyBlock
import dev.architectury.registry.registries.DeferredRegister
import dev.architectury.registry.registries.RegistrySupplier
import net.minecraft.core.registries.Registries
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.MobCategory

object ModEntityTypes {
    internal val ENTITY_REGISTRY: DeferredRegister<EntityType<*>> =
        DeferredRegister.create(MOD_ID, Registries.ENTITY_TYPE)

    @DataGenerateLanguage("en_us", "Happy Block (but primed)")
    val HAPPY_BLOCK_ENTITY: RegistrySupplier<EntityType<PrimedHappyBlock>> = ENTITY_REGISTRY.register("happy_block") {
        EntityType.Builder.of({ _, pLevel -> PrimedHappyBlock(pLevel, shouldSpread = true) }, MobCategory.MISC)
            .sized(0.98f, 0.98f)
            .clientTrackingRange(10)
            .updateInterval(10)
            .build(modLocation("happy_block").toString())
    }

// --Commented out by Inspection START (9/10/2024 03:53):
//    val BREAD_BULLET_ENTITY: RegistrySupplier<EntityType<BreadBulletEntity>> = ENTITY_REGISTRY.register("bread_bullet") {
//        EntityType.Builder.of({ pEntityType, pLevel -> BreadBulletEntity(pEntityType, pLevel) }, MobCategory.MISC)
//            .sized(0.5f, 0.5f)
//            .clientTrackingRange(4)
//            .updateInterval(20)
//            .build(modLocation("bread_bullet").toString())
//    }
// --Commented out by Inspection STOP (9/10/2024 03:53)
}