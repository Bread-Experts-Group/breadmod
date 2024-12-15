package org.bread_experts_group.breadmod.compat.lookingat.jade

import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.neoforged.neoforge.fluids.capability.IFluidHandler
import org.bread_experts_group.breadmod.experimental.fluid_tank.SidedFluidTankJadeBlockEntity
import snownee.jade.api.BlockAccessor
import snownee.jade.api.IBlockComponentProvider
import snownee.jade.api.ITooltip
import snownee.jade.api.config.IPluginConfig

class TestProvider : IBlockComponentProvider/*, IServerDataProvider<BlockAccessor>*/ {
    companion object {
        val INSTANCE = TestProvider()
    }

    override fun getUid(): ResourceLocation = JadePlugin.BLOCK_DATA

//    override fun appendServerData(data: CompoundTag, accessor: BlockAccessor) {
//        val entity = accessor.blockEntity as DoughMachineBlockEntity
//        data.putInt("fluid", entity.fluidHandler.fluid.amount)
//    }

    override fun appendTooltip(tooltip: ITooltip, accessor: BlockAccessor, config: IPluginConfig) {
        val entity = accessor.blockEntity as SidedFluidTankJadeBlockEntity
        for (tank: IFluidHandler in entity.tank.tanks) {
            tooltip.add(Component.literal("${tank.getFluidInTank(0).amount} ${tank.getFluidInTank(0).fluidType}"))
        }
    }
}