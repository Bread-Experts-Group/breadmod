package org.bread_experts_group.breadmod.experimental.particle

import java.net.URI

object Elements {
	class Element(
		val name: String,
		val symbol: String,
		val protons: Int,
		val sources: List<URI>
	)

	val MERCURY: Element = Element(
		"Mercury", "Hg", 80,
		listOf(URI("https://physics.nist.gov/cgi-bin/Elements/elInfo.pl?element=80&context=noframes"))
	)
	val THALLIUM: Element = Element(
		"Thallium", "Tl", 81,
		listOf(URI("https://physics.nist.gov/cgi-bin/Elements/elInfo.pl?element=81&context=noframes"))
	)
	val LEAD: Element = Element(
		"Lead", "Pb", 82,
		listOf(URI("https://physics.nist.gov/cgi-bin/Elements/elInfo.pl?element=82&context=noframes"))
	)
	val BISMUTH: Element = Element(
		"Bismuth", "Bi", 83,
		listOf(URI("https://physics.nist.gov/cgi-bin/Elements/elInfo.pl?element=83&context=noframes"))
	)
	val POLONIUM: Element = Element(
		"Polonium", "Po", 84,
		listOf(URI("https://physics.nist.gov/cgi-bin/Elements/elInfo.pl?element=84&context=noframes"))
	)
	val ASTATINE: Element = Element(
		"Astatine", "At", 85,
		listOf(URI("https://physics.nist.gov/cgi-bin/Elements/elInfo.pl?element=85&context=noframes"))
	)
	val RADON: Element = Element(
		"Radon", "Rn", 86,
		listOf(URI("https://physics.nist.gov/cgi-bin/Elements/elInfo.pl?element=86&context=noframes"))
	)
	val RADIUM: Element = Element(
		"Radium", "Ra", 88,
		listOf(URI("https://physics.nist.gov/cgi-bin/Elements/elInfo.pl?element=88&context=noframes"))
	)
	val THORIUM: Element = Element(
		"Thorium", "Th", 90,
		listOf(URI("https://physics.nist.gov/cgi-bin/Elements/elInfo.pl?element=90&context=noframes"))
	)
	val PROTACTINIUM: Element = Element(
		"Protactinium", "Pa", 91,
		listOf(URI("https://physics.nist.gov/cgi-bin/Elements/elInfo.pl?element=91&context=noframes"))
	)
	val URANIUM: Element = Element(
		"Uranium", "U", 92,
		listOf(URI("https://physics.nist.gov/cgi-bin/Elements/elInfo.pl?element=92&context=noframes"))
	)
}