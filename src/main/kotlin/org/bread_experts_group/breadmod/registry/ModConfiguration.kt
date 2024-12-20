package org.bread_experts_group.breadmod.registry

import net.neoforged.neoforge.common.ModConfigSpec
import net.neoforged.neoforge.common.ModConfigSpec.Builder
import net.neoforged.neoforge.common.ModConfigSpec.ConfigValue
import org.apache.commons.lang3.tuple.Pair

object ModConfiguration {
	val CLIENT_SPEC : Pair<Client, ModConfigSpec> = Builder().configure(::Client)
	val COMMON_SPEC : Pair<Common, ModConfigSpec> = Builder().configure(::Common)
	val COMMON : Common = this.COMMON_SPEC.left
	val CLIENT : Client = this.CLIENT_SPEC.left

	class Client(builder : Builder) {
		val useAlternateToolGunModel : ConfigValue<Boolean>

		init {
			builder.push("client")

			this.useAlternateToolGunModel = builder
				.comment("Toggle for the alternative tool gun model")
				.define("useAltToolgunModel", false)

			builder.pop()
		}
	}

	class Common(builder : Builder) {
		val breadArmorDecayChancePerTick : ConfigValue<Int>
		val dopedArmorEffectDistanceMultiplier : ConfigValue<Double>
		val ultimateBreadMaxCreativeTimeTicks : ConfigValue<Long>
		val breadAmuletFeedTimeTicks : ConfigValue<Int>
		val breadAmuletFeedAmount : ConfigValue<Int>
		val breadAmuletEffectCanStack : ConfigValue<Boolean>
		val happyBlockExplosionDivisions : ConfigValue<Int>
		val happyBlockExplosionSpreadRadius : ConfigValue<Double>
		val generatorMaxBurnTimeTicks : ConfigValue<Int>
		val generatorRFPerTick : ConfigValue<Int>

		init {
			builder.push("common")

			this.breadArmorDecayChancePerTick = builder
				.comment("Chance (1 in ...) for every tick to cause damage to decayable armor (e.g. bread)")
				.define("decayChance", 120)
			this.dopedArmorEffectDistanceMultiplier = builder
				.comment("How many blocks does one amplification point on a potion effect extend in dopable armor?")
				.define("dopedArmorDistanceMultiplier", 1.5)
			this.ultimateBreadMaxCreativeTimeTicks = builder
				.comment("How long the ultimate bread will give someone creative, in ticks")
				.define("ultimateBreadMaxTicks", 20 * 20)
			this.breadAmuletFeedTimeTicks = builder
				.comment("Time, in ticks, before the bread amulet will feed someone")
				.define("breadAmuletFeedTime", 20 * 10)
			this.breadAmuletFeedAmount = builder
				.comment("The amount the bread amulet will feed someone - one hunger icon is 2")
				.define("breadAmuletFeedAmount", 2)
			this.breadAmuletEffectCanStack = builder
				.comment("Allows the bread amulet to stack effects with other bread amulets")
				.define("breadAmuletStacks", false)
			this.happyBlockExplosionDivisions = builder
				.comment("How many happy blocks will be created after the first explosion")
				.define("happyBlockDiv", 8)
			this.happyBlockExplosionSpreadRadius = builder
				.comment("How far the happy block will spread it's divided blocks")
				.define("happyBlockRng", 0.5)
			this.generatorMaxBurnTimeTicks = builder
				.comment("How long the generator will burn for, in ticks")
				.define("generatorMaxBurnTime", 20000)
			this.generatorRFPerTick = builder
				.comment("How much RF the generator will produce per tick")
				.define("generatorRfPerTick", 64)

			builder.pop()
		}
	}
}