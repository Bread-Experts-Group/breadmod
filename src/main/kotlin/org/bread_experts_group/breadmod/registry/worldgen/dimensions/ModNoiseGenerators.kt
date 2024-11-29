package org.bread_experts_group.breadmod.registry.worldgen.dimensions

import net.minecraft.core.HolderGetter
import net.minecraft.core.registries.Registries
import net.minecraft.data.worldgen.BootstrapContext
import net.minecraft.data.worldgen.SurfaceRuleData
import net.minecraft.resources.ResourceKey
import net.minecraft.world.level.levelgen.DensityFunction
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings
import net.minecraft.world.level.levelgen.NoiseRouterData
import net.minecraft.world.level.levelgen.NoiseSettings
import net.minecraft.world.level.levelgen.synth.NormalNoise
import org.bread_experts_group.breadmod.BreadMod
import org.bread_experts_group.breadmod.registry.block.ModBlocks

typealias NoiseGeneratorBuilder = (HolderGetter<DensityFunction>, HolderGetter<NormalNoise.NoiseParameters>) -> NoiseGeneratorSettings

object ModNoiseGenerators : NoiseRouterData() {
    private val entries = mutableListOf<Pair<ResourceKey<NoiseGeneratorSettings>, NoiseGeneratorBuilder>>()
    fun register(name: String, builder: NoiseGeneratorBuilder): ResourceKey<NoiseGeneratorSettings> =
        ResourceKey.create(
            Registries.NOISE_SETTINGS,
            BreadMod.modLocation(name)
        ).also {
            entries.add(it to builder)
        }

    private val BREAD_FLOATING_ISLANDS_NOISE: NoiseSettings = NoiseSettings(0, 512, 2, 1)
    val BREAD_FLOATING_ISLANDS: ResourceKey<NoiseGeneratorSettings> =
        register("bread_floating_islands") { densityFunctionHolderGetter, noiseHolderGetter ->
            NoiseGeneratorSettings(
                BREAD_FLOATING_ISLANDS_NOISE,
                ModBlocks.BREAD_BLOCK.get().block.defaultBlockState(),
                ModBlocks.FLOUR_BLOCK.get().block.defaultBlockState(),
                floatingIslands(
                    densityFunctionHolderGetter,
                    noiseHolderGetter
                ),
                SurfaceRuleData.overworld(),
                listOf(),
                -64,
                false,
                false,
                true,
                true
            )
        }

    fun bootstrapNoiseGenerators(ctx: BootstrapContext<NoiseGeneratorSettings>) {
        val densityFunctionGetter = ctx.lookup(Registries.DENSITY_FUNCTION)
        val noiseGetter = ctx.lookup(Registries.NOISE)
        entries.forEach { ctx.register(it.first, it.second(densityFunctionGetter, noiseGetter)) }
    }
}