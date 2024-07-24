package breadmod.block.machine.entity.renderer

import breadmod.ModMain.modLocation
import breadmod.block.machine.entity.AbstractMachineBlockEntity
import breadmod.util.render.drawQuad
import breadmod.util.render.translateOnBlockSide
import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.vertex.VertexConsumer
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer
import net.minecraft.core.Direction
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.capabilities.ForgeCapabilities
import java.awt.Color


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
                    val list = get(it)
                    if(list == null) set(it, mutableListOf(capability))
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
                hasEnergy && hasFluid            -> FLUID_ENERGY_SLOT
                hasEnergy && hasItem             -> ITEM_ENERGY_SLOT
                hasFluid && hasItem              -> FLUID_ITEM_SLOT
                hasItem                          -> ITEM_SLOT
                hasFluid                         -> FLUID_SLOT
                hasEnergy                        -> ENERGY_SLOT
                else -> BLANK_SLOT
            }
            val vertexConsumer: VertexConsumer = pBuffer.getBuffer(RenderType.entityCutout(texture))

            translateOnBlockSide(pBlockEntity.blockState, direction, pPoseStack)

            drawQuad(
                vertexConsumer, pPoseStack, Color.WHITE.rgb,
                0F, 0F, 0F,
                1F, 1F, 0F,
                0F, 0F,
                1F, 1F,
                pPackedLight,
                pPackedOverlay
            )
            pPoseStack.popPose()
        }
    }

    companion object {
        val BLANK_SLOT = modLocation("textures", "block", "machine_slot", "blank.png")

        val ITEM_FLUID_ENERGY_SLOT = modLocation("textures", "block", "machine_slot", "item_fluid_energy.png")

        val ENERGY_SLOT       = modLocation("textures", "block", "machine_slot", "energy.png")
        val FLUID_ENERGY_SLOT = modLocation("textures", "block", "machine_slot", "fluid_energy.png")
        val ITEM_ENERGY_SLOT  = modLocation("textures", "block", "machine_slot", "item_energy.png")

        val ITEM_SLOT       = modLocation("textures", "block", "machine_slot", "item.png")
        val FLUID_ITEM_SLOT = modLocation("textures", "block", "machine_slot", "item_fluid.png")

        val FLUID_SLOT = modLocation("textures", "block", "machine_slot", "fluid.png")
    }
}