package breadmod.item.colors

import breadmod.util.getColor
import net.minecraft.client.color.item.ItemColor
import net.minecraft.world.item.ItemStack
import java.awt.Color

object ArmorColor: ItemColor {
    override fun getColor(pStack: ItemStack, pTintIndex: Int): Int = if(pTintIndex == 0) pStack.getColor().rgb else Color.WHITE.rgb
}