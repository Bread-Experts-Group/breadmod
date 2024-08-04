package breadmod.item.tool_gun.mode

import breadmod.ModMain
import breadmod.datagen.tool_gun.BreadModToolGunModeProvider
import breadmod.datagen.tool_gun.BreadModToolGunModeProvider.Companion.TOOL_GUN_DEF
import breadmod.menu.item.ToolGunCreatorMenu
import breadmod.item.tool_gun.IToolGunMode
import breadmod.item.tool_gun.IToolGunMode.Companion.playToolGunSound
import breadmod.util.RayMarchResult.Companion.rayMarchBlock
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.MenuProvider
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.monster.Zombie
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.level.Level
import net.minecraft.world.phys.Vec3
import net.minecraftforge.network.NetworkHooks

internal class ToolGunCreatorMode : IToolGunMode, MenuProvider {
    override fun action(
        pLevel: Level,
        pPlayer: Player,
        pGunStack: ItemStack,
        pControl: BreadModToolGunModeProvider.Control
    ) {
        if(pControl.id == "screen" && !pLevel.isClientSide) {
            NetworkHooks.openScreen(pPlayer as ServerPlayer, this, pPlayer.blockPosition())
        } else if(pLevel is ServerLevel && pControl.id == "use") {
            println("added entity")
            pLevel.rayMarchBlock(pPlayer.eyePosition, Vec3.directionFromRotation(pPlayer.xRot, pPlayer.yRot), 100.0, false)?.let { ray ->
                playToolGunSound(pLevel, pPlayer.blockPosition())
                val entity = Zombie(EntityType.ZOMBIE, pLevel)
                entity.getSlot(103).set(Items.DIAMOND_HELMET.defaultInstance)
                entity.setPos(Vec3(ray.endPosition.x, ray.endPosition.y + 0.5, ray.endPosition.z))
                pLevel.addFreshEntity(entity)
            }
        }
    }

    override fun createMenu(pContainerId: Int, pPlayerInventory: Inventory, pPlayer: Player): AbstractContainerMenu =
        ToolGunCreatorMenu(pContainerId, pPlayerInventory)

    override fun getDisplayName(): Component = ModMain.modTranslatable(TOOL_GUN_DEF, "mode", "display_name", "creator")
}