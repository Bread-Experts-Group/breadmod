package breadmod.item

import breadmod.datagen.provider.ModSounds
import breadmod.gui.BreadModCreativeTab
import net.minecraft.network.chat.Component
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResultHolder
import net.minecraft.world.entity.player.Player
import net.minecraft.world.food.FoodProperties
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.TooltipFlag
import net.minecraft.world.level.Level

class TestBreadItem : Item(Properties().food(FoodProperties.Builder().nutrition(6).build()).tab(BreadModCreativeTab)) {
    override fun use(pLevel: Level, pPlayer: Player, pHand: InteractionHand): InteractionResultHolder<ItemStack> {
        if (pHand == InteractionHand.MAIN_HAND) {
//            pPlayer.playSound(ModSounds.TEST_SOUND, 100.0f, 2.0f)

//            pLevel.explode(pPlayer, pPlayer.x, pPlayer.y, pPlayer.z, 10f, false, Explosion.BlockInteraction.BREAK)
//            val vec3 = pPlayer.getViewVector(1.0f).normalize()
//            pPlayer.sendSystemMessage(Component.literal("$vec3"))
//            println(vec3)

/*
            pLevel.findEntityOnPath(pPlayer, pPlayer.position())

            when(val pickResult = pPlayer.pick(50.0, 1.0f, false)) {
                is EntityHitResult -> {
                    pickResult.entity.kill() // lets try it again
                }
                is BlockHitResult -> {
                    pPlayer.sendSystemMessage(Component.literal("test"))
                    if(!pLevel.getBlockState(pickResult.blockPos).isAir)
                        pLevel.setBlockAndUpdate(pickResult.blockPos, BREAD_BLOCK.defaultBlockState())
                }
                else -> pPlayer.sendSystemMessage(Component.literal("you missed, BOZO!"))
            }


            // running the client ^^ aim for the sky and see what happens

            //TODO("unimplemented")
 */
        }
        return super.use(pLevel, pPlayer, pHand)
    }

    override fun appendHoverText(pStack: ItemStack, pLevel: Level?, pTooltipComponents: MutableList<Component>, pIsAdvanced: TooltipFlag) {
        super.appendHoverText(pStack, pLevel, pTooltipComponents, pIsAdvanced)
    }
}