package breadmod.mixin.accessors

import net.minecraft.client.renderer.ItemModelShaper
import net.minecraft.client.resources.model.ModelManager
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.gen.Accessor

@Suppress("NonJavaMixin")
@Mixin(ItemModelShaper::class)
interface IItemModelShaperAccessor {
    @get:Accessor val modelManager: ModelManager
}