package org.bread_experts_group.breadmod.datagen.damage_type

/**
 * An annotation to mark a damage source for data generation.
 *
 * @author Miko Elbrecht
 * @since 1.4.0
 * @see ModDamageTypeProvider
 */
@Target(AnnotationTarget.FIELD)
internal annotation class DataGenerateDamageType(
	val exhaustion: Double = 0.0,
	val difficultyScaling: DifficultyScaling = DifficultyScaling.NEVER,
	val damageEffect: IncomingDamageEffect = IncomingDamageEffect.HURT
) {
	enum class DifficultyScaling {
		NEVER,
		ALWAYS,
		WHEN_CAUSED_BY_LIVING_NON_PLAYER
	}

	enum class IncomingDamageEffect {
		HURT,
		THORNS,
		DROWNING,
		BURNING,
		POKING,
		FREEZING
	}
}