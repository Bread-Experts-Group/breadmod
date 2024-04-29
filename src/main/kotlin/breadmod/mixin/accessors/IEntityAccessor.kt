package breadmod.mixin.accessors

import net.minecraft.world.entity.Entity
import net.minecraft.world.level.Level
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.gen.Accessor

@Suppress("NonJavaMixin")
@Mixin(Entity::class)
interface IEntityAccessor {
    @get:Accessor("level") val level: Level
}