package breadmod.registry.entity

import breadmod.ModMain
import breadmod.ModMain.modLocation
import breadmod.entity.BreadBulletEntity
import breadmod.entity.PrimedHappyBlock
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.MobCategory
import net.minecraftforge.registries.DeferredRegister
import net.minecraftforge.registries.ForgeRegistries
import net.minecraftforge.registries.RegistryObject

object ModEntityTypes {
    internal val deferredRegister: DeferredRegister<EntityType<*>> = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, ModMain.ID)

    val HAPPY_BLOCK_ENTITY: RegistryObject<EntityType<PrimedHappyBlock>> = deferredRegister.register("happy_block") {
        EntityType.Builder.of({ _, pLevel -> PrimedHappyBlock(pLevel, shouldSpread = true) }, MobCategory.AMBIENT)
            .sized(0.98f, 0.98f)
            .clientTrackingRange(10)
            .updateInterval(10)
            .build(modLocation("happy_block").toString())
    }

    val BREAD_BULLET_ENTITY: RegistryObject<EntityType<BreadBulletEntity>> = deferredRegister.register("bread_bullet") {
        EntityType.Builder.of({ pEntityType, pLevel -> BreadBulletEntity(pEntityType, pLevel)}, MobCategory.MISC)
            .sized(0.5f, 0.5f)
            .clientTrackingRange(4)
            .updateInterval(20)
            .build(modLocation("bread_bullet").toString())
    }
}
