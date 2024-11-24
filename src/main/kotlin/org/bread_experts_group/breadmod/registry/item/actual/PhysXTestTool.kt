package org.bread_experts_group.breadmod.registry.item.actual

import net.minecraft.server.level.ServerLevel
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResultHolder
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import physx.PxTopLevelFunctions

class PhysXTestTool : Item(Properties().stacksTo(1)) {
    override fun use(level: Level, player: Player, usedHand: InteractionHand): InteractionResultHolder<ItemStack> {
        if (level is ServerLevel) return InteractionResultHolder.pass(player.getItemInHand(usedHand))

        val version: Int = PxTopLevelFunctions.getPHYSICS_VERSION()

        val versionMajor = version shr 24
        val versionMinor = (version shr 16) and 0xff
        val versionMicro = (version shr 8) and 0xff
        System.out.printf("PhysX loaded, version: %d.%d.%d\n", versionMajor, versionMinor, versionMicro)

        return super.use(level, player, usedHand)
    }
}