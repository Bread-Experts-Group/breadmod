package org.bread_experts_group.breadmod.experimental.physics_grid.micro

import com.mojang.authlib.GameProfile
import net.minecraft.SystemReport
import net.minecraft.server.MinecraftServer
import net.minecraft.util.debugchart.SampleLogger

class MicroMinecraftServer : MinecraftServer(
	null,
	null,
	null,
//	WorldStem(
//		null,
//		null,
//		LayeredRegistryAccess(RegistryLayer.entries).replaceFrom(
//			RegistryLayer.DIMENSIONS,
//			listOf(
//				object : RegistryAccess.Frozen {
//					override fun registries(): Stream<RegistryAccess.RegistryEntry<*>> = Stream.of(
//						RegistryAccess.RegistryEntry(
//							Registries.DIMENSION.also { ReloadableServerRegistries.REGISTRY.forEach { println(it.key()) } },
//							BuiltInRegistries.REGISTRY.get(Registries.DIMENSION.registry()) as Registry<Level>
//						)
//					)
//
//					override fun <E : Any> registry(registryKey: ResourceKey<out Registry<out E>>): Optional<Registry<E>> {
//						TODO("Not yet implemented")
//					}
//				}
//			)
//		),
//		null
//	),
	null,
	null,
	null,
	null,
	null
) {
	override fun initServer(): Boolean {
		TODO("Not yet implemented")
	}

	override fun getOperatorUserPermissionLevel(): Int {
		TODO("Not yet implemented")
	}

	override fun getFunctionCompilationLevel(): Int {
		TODO("Not yet implemented")
	}

	override fun shouldRconBroadcast(): Boolean {
		TODO("Not yet implemented")
	}

	override fun getTickTimeLogger(): SampleLogger {
		TODO("Not yet implemented")
	}

	override fun isTickTimeLoggingEnabled(): Boolean {
		TODO("Not yet implemented")
	}

	override fun fillServerSystemReport(report: SystemReport): SystemReport {
		TODO("Not yet implemented")
	}

	override fun isDedicatedServer(): Boolean {
		TODO("Not yet implemented")
	}

	override fun getRateLimitPacketsPerSecond(): Int {
		TODO("Not yet implemented")
	}

	override fun isEpollEnabled(): Boolean {
		TODO("Not yet implemented")
	}

	override fun isCommandBlockEnabled(): Boolean {
		TODO("Not yet implemented")
	}

	override fun isPublished(): Boolean {
		TODO("Not yet implemented")
	}

	override fun shouldInformAdmins(): Boolean {
		TODO("Not yet implemented")
	}

	override fun isSingleplayerOwner(profile: GameProfile): Boolean {
		TODO("Not yet implemented")
	}
}