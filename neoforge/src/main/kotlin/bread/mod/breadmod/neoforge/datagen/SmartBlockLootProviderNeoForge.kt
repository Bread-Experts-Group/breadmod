package bread.mod.breadmod.neoforge.datagen

import bread.mod.breadmod.ModMainCommon
import bread.mod.breadmod.datagen.loot.DataGenerateLoot
import bread.mod.breadmod.datagen.loot.SmartBlockLootProvider
import bread.mod.breadmod.datagen.loot.constructLootProvider
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.data.loot.BlockLootSubProvider
import net.minecraft.world.flag.FeatureFlags
import net.minecraft.world.item.Item
import net.minecraft.world.level.block.Block
import net.neoforged.neoforge.data.event.GatherDataEvent

class SmartBlockLootProviderNeoForge(
    modID: String, forClassLoader: ClassLoader, forPackage: Package
) : SmartBlockLootProvider<GatherDataEvent>(modID, forClassLoader, forPackage) {
    override fun generate(forEvent: GatherDataEvent) = getBlockLootMap().forEach { (block, data) ->
        forEvent.generator.addProvider(
            true,
            constructLootProvider(object : BlockLootSubProvider(
                emptySet<Item>(),
                FeatureFlags.DEFAULT_FLAGS,
                forEvent.lookupProvider.get()
            ) {
                override fun generate() {
                    data.forEach {
                        when (it.dropType) {
                            DataGenerateLoot.Type.SELF -> dropSelf(block.block)
                            DataGenerateLoot.Type.OTHER -> {} // todo
                        }
                    }
                }

                override fun getKnownBlocks(): Iterable<Block> = BuiltInRegistries.BLOCK.stream()
                    .filter { it.builtInRegistryHolder().key?.location()?.namespace == ModMainCommon.MOD_ID }
                    .map { it.builtInRegistryHolder().value() }
                    .toList()

            }, forEvent.generator.packOutput, forEvent.lookupProvider)
        )
    }
}