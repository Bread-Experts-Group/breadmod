package bread.mod.breadmod.util.render

import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.Minecraft
import org.jetbrains.annotations.ApiStatus.Internal

@Internal
val rgMinecraft: Minecraft = Minecraft.getInstance()

var skyColorMixinActive = false
var redness = 1f

fun PoseStack.scaleFlat(scale: Float) = this.scale(scale, scale, scale)