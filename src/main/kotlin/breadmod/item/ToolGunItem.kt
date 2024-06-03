package breadmod.item

import breadmod.entity.ToolGunShotEntity
import breadmod.registry.entity.ModEntityTypes
import breadmod.registry.sound.ModSounds
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResultHolder
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level

class ToolGunItem: Item(Properties().stacksTo(1)) {

    override fun use(pLevel: Level, pPlayer: Player, pUsedHand: InteractionHand): InteractionResultHolder<ItemStack> {
        if(!pLevel.isClientSide) { // todo doesn't work at all, Probably cause of how the entity is declared here. look in BowItem for spawning entity
            val projectile = ToolGunShotEntity(ModEntityTypes.TOOL_GUN_SHOT_ENTITY.get(), pLevel)
            projectile.shootFromRotation(pPlayer, pPlayer.xRot, pPlayer.yRot, 0.0F, 2f, 0f)
            projectile.owner = pPlayer
            pLevel.addFreshEntity(projectile)
        }
        pPlayer.playSound(ModSounds.TOOL_GUN.get(), 2.0f, 1f)

        return InteractionResultHolder.consume(pPlayer.getItemInHand(pUsedHand))
    }
}