package org.bread_experts_group.breadmod.experimental.particle

import org.bread_experts_group.breadmod.experimental.particle.Elements.Element
import java.math.BigDecimal
import java.math.BigInteger
import java.math.MathContext
import java.net.URI

object Isotopes {
	enum class DecayMode {
		ALPHA,
		BETA,
		BETA_NEUTRON,
		INTERNAL_TRANSITION
	}

	val isotopes: MutableMap<String, Isotope> = mutableMapOf()

	sealed class DecayStatistic(val mode: DecayMode, probability: BigDecimal) {
		val ratio: BigDecimal = probability.divide(BigDecimal(100))
	}

	class DecayStatisticDaughterAndEnergy(
		mode: DecayMode, probability: BigDecimal,
		energyKeV: BigDecimal, val daughter: Isotope
	) : DecayStatistic(mode, probability) {
		val energyEV: BigDecimal = energyKeV.multiply(BigDecimal(1000))
	}

	val mathContext: MathContext = MathContext.DECIMAL128

	class Isotope(
		val element: Element,
		val atomicNumber: Int,
		val relativeMass: BigDecimal,
		val sources: List<URI>,
		val decayStatistics: List<DecayStatistic> = emptyList(),
		val halfLifeSeconds: BigDecimal? = BigDecimal.ZERO,
		val metastable: Boolean = false,
		val protons: Int = atomicNumber - element.protons
	) {
		fun getTag(): String = "${this.element.symbol}${this.atomicNumber}${if (this.metastable) 'm' else ""}"
		fun gramsToAtoms(grams: Int): BigInteger = this.gramsToAtoms(BigDecimal(grams))
		fun gramsToAtoms(grams: BigDecimal): BigInteger = grams
			.divide(this.relativeMass, this@Isotopes.mathContext)
			.multiply(BigDecimal("6.02214076e+23"), this@Isotopes.mathContext)
			.toBigInteger()

		init {
			fun dataRequired(what: String): Nothing =
				throw NotImplementedError(
					"Data required for isotope ${this.element.symbol}-${this.atomicNumber}" +
							"($what)"
				)

			if (this.decayStatistics.isNotEmpty()) {
				if (this.halfLifeSeconds == null) dataRequired("half life")
				val probabilities = this.decayStatistics.sumOf { it.ratio }
				if (probabilities.toInt() != 1)
					dataRequired("probabilities do not match up to 100% (was $probabilities)")
			} else {
				if (this.halfLifeSeconds != null) dataRequired("decay statistics")
			}
			if (this.sources.isEmpty()) dataRequired("review data and add sources")
			this.getTag().also {
				if (this@Isotopes.isotopes.contains(it)) throw IllegalStateException("Duplicate isotope $it")
				this@Isotopes.isotopes[it] = this
			}
		}
	}

	fun years(string: String): BigDecimal = BigDecimal(string).multiply(BigDecimal("3.156e+7"))
	fun days(string: String): BigDecimal = BigDecimal(string).multiply(BigDecimal("86400"))
	fun hours(string: String): BigDecimal = BigDecimal(string).multiply(BigDecimal("3600"))
	fun minutes(string: String): BigDecimal = BigDecimal(string).multiply(BigDecimal("60"))
	fun milliseconds(string: String): BigDecimal = BigDecimal(string).divide(BigDecimal("1000"))
	fun microseconds(string: String): BigDecimal = BigDecimal(string).divide(BigDecimal("1000000"))

	val THALLIUM_205: Isotope = Isotope(
		// relativeMass with an uncertainty of 0.00000(32)
		Elements.THALLIUM, 205, BigDecimal("204.9744123"),
		listOf(
			URI("https://atom.kaeri.re.kr/cgi-bin/nuclide?nuc=Tl-205"),
			URI("https://www-nds.iaea.org/relnsd/nubase/nubase_min.html")
		),
		halfLifeSeconds = null
	)
	val LEAD_206: Isotope = Isotope(
		// relativeMass with an uncertainty of 0.00000(31)
		Elements.LEAD, 206, BigDecimal("205.9744490"),
		listOf(
			URI("https://atom.kaeri.re.kr/cgi-bin/nuclide?nuc=Pb-206"),
			URI("https://www-nds.iaea.org/relnsd/nubase/nubase_min.html")
		),
		halfLifeSeconds = null
	)
	val THALLIUM_206: Isotope = Isotope(
		// relativeMass with an uncertainty of 0.00000(32)
		Elements.THALLIUM, 206, BigDecimal("205.9760953"),
		listOf(
			URI("https://atom.kaeri.re.kr/cgi-bin/nuclide?nuc=Tl-206"),
			URI("https://www-nds.iaea.org/relnsd/nubase/nubase_min.html")
		),
		listOf(
			DecayStatisticDaughterAndEnergy(
				DecayMode.BETA,
				BigDecimal("100"),
				BigDecimal("1533.536"), // Give or take 0.657 KeV
				this.LEAD_206
			)
		),
		this.minutes("4.199")
	)
	val MERCURY_206: Isotope = Isotope(
		// relativeMass with an uncertainty of 0.0000(221)
		Elements.MERCURY, 206, BigDecimal("205.9774987"),
		listOf(
			URI("https://atom.kaeri.re.kr/cgi-bin/nuclide?nuc=Hg-206"),
			URI("https://www-nds.iaea.org/relnsd/nubase/nubase_min.html")
		),
		listOf(
			DecayStatisticDaughterAndEnergy(
				DecayMode.BETA,
				BigDecimal("100"),
				BigDecimal("1307.213"), // Give or take 20.411 KeV
				this.THALLIUM_206
			)
		),
		this.minutes("8.15")
	)
	val BISMUTH_209: Isotope = Isotope(
		// relativeMass with an uncertainty of 0.00000(32)
		Elements.BISMUTH, 209, BigDecimal("208.9803986"),
		listOf(
			URI("https://atom.kaeri.re.kr/cgi-bin/nuclide?nuc=Bi-209"),
			URI("https://www-nds.iaea.org/relnsd/nubase/nubase_min.html"),
			URI("https://en.wikipedia.org/wiki/Isotopes_of_bismuth")
		),
		listOf(
			DecayStatisticDaughterAndEnergy(
				DecayMode.ALPHA,
				BigDecimal("100"),
				BigDecimal("3137.3"), // Give or take 0.8 KeV
				this.THALLIUM_205
			)
		),
		this.years("20.1").multiply(BigDecimal("1000000000000000000"))
	)
	val LEAD_209: Isotope = Isotope(
		// relativeMass with an uncertainty of 0.00000(34)
		Elements.LEAD, 209, BigDecimal("208.9810748"),
		listOf(
			URI("https://atom.kaeri.re.kr/cgi-bin/nuclide?nuc=Pb-209"),
			URI("https://www-nds.iaea.org/relnsd/nubase/nubase_min.html")
		),
		listOf(
			DecayStatisticDaughterAndEnergy(
				DecayMode.BETA,
				BigDecimal("100"),
				BigDecimal("644.184"), // Give or take 1.141 KeV
				this.BISMUTH_209
			)
		),
		this.hours("3.253")
	)
	val POLONIUM_210: Isotope = Isotope(
		// relativeMass with an uncertainty of 0.00000(31)
		Elements.POLONIUM, 210, BigDecimal("209.9828574"),
		listOf(
			URI("https://atom.kaeri.re.kr/cgi-bin/nuclide?nuc=Po-210"),
			URI("https://www-nds.iaea.org/relnsd/nubase/nubase_min.html")
		),
		listOf(
			DecayStatisticDaughterAndEnergy(
				DecayMode.ALPHA,
				BigDecimal("100"),
				BigDecimal("5407.53"), // Give or take 0.07 KeV
				this.LEAD_206
			)
		),
		this.days("138.376")
	)
	val BISMUTH_210: Isotope = Isotope(
		// relativeMass with an uncertainty of 0.00000(32)
		Elements.BISMUTH, 210, BigDecimal("209.9841049"),
		listOf(
			URI("https://atom.kaeri.re.kr/cgi-bin/nuclide?nuc=Bi-210"),
			URI("https://www-nds.iaea.org/relnsd/nubase/nubase_min.html")
		),
		listOf(
			DecayStatisticDaughterAndEnergy(
				DecayMode.BETA,
				BigDecimal("99.99987"),
				BigDecimal("1162.083"), // Give or take 0.785 KeV
				this.POLONIUM_210
			),
			DecayStatisticDaughterAndEnergy(
				DecayMode.ALPHA,
				BigDecimal("0.00013"),
				BigDecimal("5036.5"), // Give or take 0.8 KeV
				this.THALLIUM_206
			)
		),
		this.days("5.013")
	)
	val LEAD_210: Isotope = Isotope(
		// relativeMass with an uncertainty of 0.00000(32)
		Elements.LEAD, 210, BigDecimal("209.9841731"),
		listOf(
			URI("https://atom.kaeri.re.kr/cgi-bin/nuclide?nuc=Pb-210"),
			URI("https://www-nds.iaea.org/relnsd/nubase/nubase_min.html")
		),
		listOf(
			DecayStatisticDaughterAndEnergy(
				DecayMode.BETA,
				BigDecimal("99.9999981"),
				BigDecimal("63.5"), // Give or take 0.5 KeV
				this.BISMUTH_210
			),
			DecayStatisticDaughterAndEnergy(
				DecayMode.ALPHA,
				BigDecimal("0.0000019"),
				BigDecimal("3792"), // Give or take 20 KeV
				this.MERCURY_206
			)
		),
		this.years("22.3")
	)
	val THALLIUM_210: Isotope = Isotope(
		// relativeMass with an uncertainty of 0.0000(123)
		Elements.THALLIUM, 210, BigDecimal("209.9900656"),
		listOf(
			URI("https://atom.kaeri.re.kr/cgi-bin/nuclide?nuc=Tl-210"),
			URI("https://www-nds.iaea.org/relnsd/nubase/nubase_min.html")
		),
		listOf(
			DecayStatisticDaughterAndEnergy(
				DecayMode.BETA,
				BigDecimal("99.993"),
				BigDecimal("5488.776"), // Give or take 11.220 KeV
				this.LEAD_210
			),
			DecayStatisticDaughterAndEnergy(
				DecayMode.BETA_NEUTRON,
				BigDecimal("0.0070"),
				BigDecimal("299"),
				this.LEAD_209
			)
		),
		this.minutes("1.30")
	)
	val POLONIUM_214: Isotope = Isotope(
		// relativeMass with an uncertainty of 0.00000(32)
		Elements.POLONIUM, 214, BigDecimal("213.9951859"),
		listOf(
			URI("https://atom.kaeri.re.kr/cgi-bin/nuclide?nuc=Po-214"),
			URI("https://www-nds.iaea.org/relnsd/nubase/nubase_min.html")
		),
		listOf(
			DecayStatisticDaughterAndEnergy(
				DecayMode.ALPHA,
				BigDecimal("100"),
				BigDecimal("7833.54"), // Give or take 0.06 KeV
				this.LEAD_210
			)
		),
		this.microseconds("164.3")
	)
	val BISMUTH_214: Isotope = Isotope(
		// relativeMass with an uncertainty of 0.0000(123)
		Elements.BISMUTH, 214, BigDecimal("213.9986987"),
		listOf(
			URI("https://atom.kaeri.re.kr/cgi-bin/nuclide?nuc=Bi-214"),
			URI("https://www-nds.iaea.org/relnsd/nubase/nubase_min.html")
		),
		listOf(
			DecayStatisticDaughterAndEnergy(
				DecayMode.BETA,
				BigDecimal("99.98"),
				BigDecimal("3269"), // Give or take 11 KeV
				this.POLONIUM_214
			),
			DecayStatisticDaughterAndEnergy(
				DecayMode.ALPHA,
				BigDecimal("0.02"),
				BigDecimal("5621"), // Give or take 3 KeV
				this.THALLIUM_210
			)
		),
		this.minutes("19.9")
	)
	val LEAD_214: Isotope = Isotope(
		// relativeMass with an uncertainty of 0.00000(27)
		Elements.LEAD, 214, BigDecimal("213.9997981"),
		listOf(
			URI("https://atom.kaeri.re.kr/cgi-bin/nuclide?nuc=Pb-214"),
			URI("https://www-nds.iaea.org/relnsd/nubase/nubase_min.html")
		),
		listOf(
			DecayStatisticDaughterAndEnergy(
				DecayMode.BETA,
				BigDecimal("100"),
				BigDecimal("1018"), // Give or take 11 KeV
				this.BISMUTH_214
			)
		),
		this.minutes("26.8")
	)
	val RADON_218: Isotope = Isotope(
		// relativeMass with an uncertainty of 0.00000(37)
		Elements.RADON, 218, BigDecimal("218.0055863"),
		listOf(
			URI("https://atom.kaeri.re.kr/cgi-bin/nuclide?nuc=Rn-218"),
			URI("https://www-nds.iaea.org/relnsd/nubase/nubase_min.html")
		),
		listOf(
			DecayStatisticDaughterAndEnergy(
				DecayMode.ALPHA,
				BigDecimal("100"),
				BigDecimal("7262.5"), // Give or take 1.9 KeV
				this.POLONIUM_214
			)
		),
		this.milliseconds("35")
	)
	val ASTATINE_218: Isotope = Isotope(
		// relativeMass with an uncertainty of 0.0000(127)
		Elements.ASTATINE, 218, BigDecimal("218.0086815"),
		listOf(
			URI("https://atom.kaeri.re.kr/cgi-bin/nuclide?nuc=At-218"),
			URI("https://www-nds.iaea.org/relnsd/nubase/nubase_min.html")
		),
		listOf(
			DecayStatisticDaughterAndEnergy(
				DecayMode.ALPHA,
				BigDecimal("99.90"),
				BigDecimal("6876.1"), // Give or take 2.6 KeV
				this.BISMUTH_214
			),
			DecayStatisticDaughterAndEnergy(
				DecayMode.BETA,
				BigDecimal("0.10"),
				BigDecimal("2883"), // Give or take 12 KeV
				this.RADON_218
			)
		),
		BigDecimal("1.5")
	)
	val POLONIUM_218: Isotope = Isotope(
		// relativeMass with an uncertainty of 0.00000(27)
		Elements.POLONIUM, 218, BigDecimal("218.0089658"),
		listOf(
			URI("https://atom.kaeri.re.kr/cgi-bin/nuclide?nuc=Po-218"),
			URI("https://www-nds.iaea.org/relnsd/nubase/nubase_min.html")
		),
		listOf(
			DecayStatisticDaughterAndEnergy(
				DecayMode.ALPHA,
				BigDecimal("99.98"),
				BigDecimal("6114.75"), // Give or take 0.09 KeV
				this.LEAD_214
			),
			DecayStatisticDaughterAndEnergy(
				DecayMode.BETA,
				BigDecimal("0.02"),
				BigDecimal("256"), // Give or take 12 KeV
				this.ASTATINE_218
			)
		),
		this.minutes("3.097")
	)
	val RADON_222: Isotope = Isotope(
		// relativeMass with an uncertainty of 0.00000(27)
		Elements.RADON, 222, BigDecimal("222.0175705"),
		listOf(
			URI("https://atom.kaeri.re.kr/cgi-bin/nuclide?nuc=Rn-222"),
			URI("https://www-nds.iaea.org/relnsd/nubase/nubase_min.html")
		),
		listOf(
			DecayStatisticDaughterAndEnergy(
				DecayMode.ALPHA,
				BigDecimal("100"),
				BigDecimal("5590.4"), // Give or take 0.3 KeV
				this.POLONIUM_218
			)
		),
		this.days("3.8215")
	)
	val RADIUM_226: Isotope = Isotope(
		// relativeMass with an uncertainty of 0.00000(27)
		Elements.RADIUM, 226, BigDecimal("226.0254026"),
		listOf(
			URI("https://atom.kaeri.re.kr/cgi-bin/nuclide?nuc=Ra-226"),
			URI("https://www-nds.iaea.org/relnsd/nubase/nubase_min.html")
		),
		listOf(
			DecayStatisticDaughterAndEnergy(
				DecayMode.ALPHA,
				BigDecimal("100"),
				BigDecimal("4870.70"), // Give or take 0.25 KeV
				this.RADON_222
			)
			// TODO: ACCOUNT FOR 14C DECAY MODE
		),
		this.years("1600")
	)
	val THORIUM_230: Isotope = Isotope(
		// relativeMass with an uncertainty of 0.00000(22)
		Elements.THORIUM, 230, BigDecimal("230.0331266"),
		listOf(
			URI("https://atom.kaeri.re.kr/cgi-bin/nuclide?nuc=Th-230"),
			URI("https://www-nds.iaea.org/relnsd/nubase/nubase_min.html")
		),
		listOf(
			DecayStatisticDaughterAndEnergy(
				DecayMode.ALPHA,
				BigDecimal("100"),
				BigDecimal("4770"), // Give or take 1.5 KeV
				this.RADIUM_226
			)
			// TODO: ACCOUNT FOR SF DECAY MODE
		),
		this.years("75380")
	)
	val URANIUM_234: Isotope = Isotope(
		// relativeMass with an uncertainty of 0.00000(21)
		Elements.URANIUM, 234, BigDecimal("234.0409456"),
		listOf(
			URI("https://atom.kaeri.re.kr/cgi-bin/nuclide?nuc=U-234"),
			URI("https://www-nds.iaea.org/relnsd/nubase/nubase_min.html")
		),
		listOf(
			DecayStatisticDaughterAndEnergy(
				DecayMode.ALPHA,
				BigDecimal("100"),
				BigDecimal("4857.5"), // Give or take 0.7 KeV
				this.THORIUM_230
			),
			// TODO: ACCOUNT FOR SF, Mg, Ne DECAY MODES
		),
		this.years("245500")
	)
	val PROTACTINIUM_234: Isotope = Isotope(
		// relativeMass with an uncertainty of 0.00000(51)
		Elements.PROTACTINIUM, 234, BigDecimal("234.0433023"),
		listOf(
			URI("https://atom.kaeri.re.kr/cgi-bin/nuclide?nuc=Pa-234"),
			URI("https://www-nds.iaea.org/relnsd/nubase/nubase_min.html")
		),
		listOf(
			DecayStatisticDaughterAndEnergy(
				DecayMode.BETA,
				BigDecimal("100"),
				BigDecimal("2195.269"), // Give or take 4.498 KeV
				this.URANIUM_234
			)
		),
		this.hours("6.70")
	)
	val PROTACTINIUM_234_METASTABLE: Isotope = Isotope(
		// relativeMass with an uncertainty of 0.00000(51)
		Elements.PROTACTINIUM, 234, BigDecimal("234.0433023"),
		listOf(
			URI("https://atom.kaeri.re.kr/cgi-bin/nuclide?nuc=Pa-234"),
			URI("https://www-nds.iaea.org/relnsd/nubase/nubase_min.html")
		),
		listOf(
			DecayStatisticDaughterAndEnergy(
				DecayMode.BETA,
				BigDecimal("99.84"),
				BigDecimal("2271"), // Find uncertainty
				this.URANIUM_234
			),
			DecayStatisticDaughterAndEnergy(
				DecayMode.INTERNAL_TRANSITION,
				BigDecimal("0.16"),
				BigDecimal("74"),
				this.PROTACTINIUM_234
			)
		),
		this.minutes("1.17"),
		true
	)
	val THORIUM_234: Isotope = Isotope(
		// relativeMass with an uncertainty of 0.00000(38)
		Elements.THORIUM, 234, BigDecimal("234.0435955"),
		listOf(
			URI("https://atom.kaeri.re.kr/cgi-bin/nuclide?nuc=Th-234"),
			URI("https://www-nds.iaea.org/relnsd/nubase/nubase_min.html")
		),
		listOf(
			DecayStatisticDaughterAndEnergy(
				DecayMode.BETA,
				BigDecimal("100"),
				BigDecimal("273.088"), // Give or take 3.172 KeV
				this.PROTACTINIUM_234_METASTABLE
			)
		),
		this.days("24.107")
	)
	val URANIUM_238: Isotope = Isotope(
		// relativeMass with an uncertainty of 0.000000000(15)
		Elements.URANIUM, 238, BigDecimal("238.050787618"),
		listOf(
			URI("https://physics.nist.gov/cgi-bin/Compositions/stand_alone.pl?ele=U"),
			URI("https://journals.aps.org/prc/abstract/10.1103/PhysRevC.109.L021301"),
			URI("https://www-nds.iaea.org/relnsd/nubase/nubase_min.html")
		),
		listOf(
			DecayStatisticDaughterAndEnergy(
				DecayMode.ALPHA,
				BigDecimal("100"),
				BigDecimal("4269.9"), // Give or take 2.1 KeV
				this.THORIUM_234
			),
			// TODO: ACCOUNT FOR SF, DOUBLE BETA DECAY MODES
		),
		this.years("4.463").multiply(BigDecimal("1000000000")) // 4.463 Gy
	)
}