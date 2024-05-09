package breadmod.compat.jade.component

import breadmod.ModMain.modTranslatable
import breadmod.compat.jade.JadePlugin.Companion.TOOLTIP_RENDERER
import net.minecraft.resources.ResourceLocation
import snownee.jade.api.BlockAccessor
import snownee.jade.api.IBlockComponentProvider
import snownee.jade.api.ITooltip
import snownee.jade.api.config.IPluginConfig

object Test: IBlockComponentProvider {
    override fun getUid(): ResourceLocation = TOOLTIP_RENDERER.first
    override fun appendTooltip(pTooltip: ITooltip, pAccesor: BlockAccessor, pConfig: IPluginConfig) {
        pTooltip.add(modTranslatable("jade", "component", "test"))
    }
}