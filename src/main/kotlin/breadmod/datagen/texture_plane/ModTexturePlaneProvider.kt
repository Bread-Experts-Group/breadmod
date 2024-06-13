package breadmod.datagen.texture_plane

import breadmod.ModMain
import net.minecraft.data.PackOutput

class ModTexturePlaneProvider(
    packOutput: PackOutput
) : TexturePlaneProvider(packOutput, ModMain.ID) {
    override fun addTextures() {
        addTexture("textureplane_test", ModMain.modLocation("item", "tool_gun"))
    }
}