package bread.mod.breadmod.util.render

import net.minecraft.client.Minecraft
import org.jetbrains.annotations.ApiStatus.Internal

@Internal
val rgMinecraft: Minecraft = Minecraft.getInstance()

var skyColorMixinActive = false
var redness = 1f