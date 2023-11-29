package breadmod.item

import breadmod.gui.ModCreativeTab
import net.minecraft.network.chat.Component
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResultHolder
import net.minecraft.world.entity.player.Player
import net.minecraft.world.food.FoodProperties
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.phys.Vec3


class TestBreadItem : Item(Properties().food(FoodProperties.Builder().nutrition(6).build()).tab(ModCreativeTab)) {
    override fun use(pLevel: Level, pPlayer: Player, pHand: InteractionHand): InteractionResultHolder<ItemStack> {
        if (pHand == InteractionHand.MAIN_HAND) {
//            pLevel.explode(pPlayer, pPlayer.x, pPlayer.y, pPlayer.z, 10f, false, Explosion.BlockInteraction.BREAK)
            val vec3 = pPlayer.getViewVector(1.0f).normalize()
            pPlayer.sendSystemMessage(Component.literal("$vec3"))
            println(vec3)



        }
        return super.use(pLevel, pPlayer, pHand)
    }
}