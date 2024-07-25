package breadmod.block.machine.entity.renderer

import breadmod.ModMain.modLocation
import breadmod.block.machine.entity.AbstractMachineBlockEntity
import breadmod.util.render.texturedQuadTest
import breadmod.util.render.translateOnBlockSide
import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.math.Axis
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer
import net.minecraft.core.Direction
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.capabilities.ForgeCapabilities

class GenericMachineBlockEntityRenderer: BlockEntityRenderer<AbstractMachineBlockEntity<*>> {
    override fun render(
        pBlockEntity: AbstractMachineBlockEntity<*>,
        pPartialTick: Float,
        pPoseStack: PoseStack,
        pBuffer: MultiBufferSource,
        pPackedLight: Int,
        pPackedOverlay: Int
    ) {
        buildMap<Direction, MutableList<Capability<*>>> {
            pBlockEntity.capabilityHolder.capabilities.forEach { (capability, additional) ->
                additional.second?.filterNotNull()?.forEach {
                    val actual = when(pBlockEntity.blockState.getValue(BlockStateProperties.HORIZONTAL_FACING)) {
                        Direction.WEST, Direction.EAST -> it.opposite
                        else -> it
                    }

                    val list = get(actual)
                    if(list == null) set(actual, mutableListOf(capability))
                    else list.add(capability)
                }
            }
        }.forEach { (direction, capabilities) ->
            pPoseStack.pushPose()

            val hasEnergy = capabilities.contains(ForgeCapabilities.ENERGY)
            val hasFluid = capabilities.contains(ForgeCapabilities.FLUID_HANDLER)
            val hasItem = capabilities.contains(ForgeCapabilities.ITEM_HANDLER)

            val texture = when {
                hasEnergy && hasFluid && hasItem -> ITEM_FLUID_ENERGY_SLOT
                hasEnergy && hasFluid -> FLUID_ENERGY_SLOT
                hasEnergy && hasItem -> ITEM_ENERGY_SLOT
                hasFluid && hasItem -> FLUID_ITEM_SLOT
                hasItem -> ITEM_SLOT
                hasFluid -> FLUID_SLOT
                hasEnergy -> ENERGY_SLOT
                else -> BLANK_SLOT
            }

            translateOnBlockSide(pBlockEntity.blockState, direction, pPoseStack)
            pPoseStack.mulPose(Axis.XN.rotationDegrees(-90F))

            // Figure out why cutoutMipped is fully transparent
//            drawTexturedQuad(
//                texture, RenderType.cutout(), pPoseStack, pBuffer,
//                0xFFFFFF, 0xFFFFFF
//            )

            // IT FUCKING WORKS BABY WOOOOOO
            texturedQuadTest(texture, RenderType.cutoutMipped(), pPoseStack, pBuffer)
            pPoseStack.popPose()
        }
    }

    companion object {
        val BLANK_SLOT = modLocation("block", "machine_slot", "blank")

        val ITEM_FLUID_ENERGY_SLOT = modLocation("block", "machine_slot", "item_fluid_energy")

        val ENERGY_SLOT       = modLocation("block", "machine_slot", "energy")
        val FLUID_ENERGY_SLOT = modLocation("block", "machine_slot", "fluid_energy")
        val ITEM_ENERGY_SLOT  = modLocation("block", "machine_slot", "item_energy")

        val ITEM_SLOT       = modLocation("block", "machine_slot", "item")
        val FLUID_ITEM_SLOT = modLocation("block", "machine_slot", "item_fluid")

        val FLUID_SLOT = modLocation("block", "machine_slot", "fluid")
    }
}