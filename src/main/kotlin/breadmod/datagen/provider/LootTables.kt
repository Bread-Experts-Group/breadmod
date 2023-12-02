package breadmod.datagen.provider

import com.mojang.datafixers.util.Pair
import net.minecraft.data.DataGenerator
import net.minecraft.data.loot.LootTableProvider
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.level.storage.loot.LootTable
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet
import java.util.function.BiConsumer
import java.util.function.Consumer
import java.util.function.Supplier

class LootTables(generator: DataGenerator) : LootTableProvider(generator) {
    override fun getTables(): List<Pair<Supplier<Consumer<BiConsumer<ResourceLocation, LootTable.Builder>>>, LootContextParamSet>> = buildList {
        return super.getTables()
    }
}

/*
// In some LootTableProvider subclass
@Override
protected
  List< // Get a list
    Pair< // of pairs
      Supplier< // for a factory
        Consumer< // which takes in
          BiConsumer< // a writer of
            ResourceLocation, // the name of the table
            LootTable.Builder // and the table to generate
          >
        >
      >,
      LootContextParamSet // with a given parameter set
    >
  >
getTables() {
  // Return table builders here
}
 */