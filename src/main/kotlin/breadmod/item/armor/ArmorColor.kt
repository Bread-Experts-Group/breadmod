package breadmod.item.armor

import breadmod.item.IDyedTintableItem
import breadmod.util.render.getColor
import net.minecraft.client.color.item.ItemColor
import net.minecraft.world.item.ItemStack
import java.awt.Color

object ArmorColor : ItemColor {
    override fun getColor(pStack: ItemStack, pTintIndex: Int): Int =
        (if (pTintIndex == 0) pStack.getColor(pStack.item.let { if (it is IDyedTintableItem) it.defaultTint else Color.BLACK }) else Color.WHITE).rgb
}