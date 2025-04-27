package org.bread_experts_group.breadmod.datagen.damage_type

import com.google.gson.JsonObject
import net.minecraft.core.registries.Registries
import net.minecraft.data.CachedOutput
import net.minecraft.data.DataProvider
import net.minecraft.data.PackOutput
import org.bread_experts_group.breadmod.BreadMod
import org.bread_experts_group.breadmod.datagen.path
import org.bread_experts_group.breadmod.registry.ModDamageType
import org.bread_experts_group.breadmod.util.reflect.LibraryScanner
import org.bread_experts_group.breadmod.util.reflect.LibraryScanner.Companion.getScanner
import java.util.concurrent.CompletableFuture
import kotlin.io.path.name

class ModDamageTypeProvider(
	val packOutput: PackOutput
) : DataProvider {
	private val registryScanner: LibraryScanner =
		org.bread_experts_group.breadmod.registry.Registry::class.java.`package`
			.getScanner()

	override fun getName(): String = "BreadMod Smart Damage Type Provider"
	override fun run(output: CachedOutput): CompletableFuture<*> = buildList<CompletableFuture<*>> {
		this@ModDamageTypeProvider.registryScanner.resolveAnnotationValuePairs<DataGenerateDamageType>()
			.forEach { (annotation, data) ->
				data as ModDamageType
				this.add(
					DataProvider.saveStable(
						output,
						JsonObject().also {
							it.addProperty("message_id", data.translationKey().substringAfter("death.attack."))
							it.addProperty("exhaustion", annotation.exhaustion)
							it.addProperty("scaling", annotation.difficultyScaling.name.lowercase())
							it.addProperty("effects", annotation.damageEffect.name.lowercase())
						},
						Registries.DAMAGE_TYPE.path(
							this@ModDamageTypeProvider.packOutput,
							data.key.location().path
						).let {
							it.parent.parent.parent.parent
								.resolve(BreadMod.ID)
								.resolve(Registries.DAMAGE_TYPE.location().path)
								.resolve(it.name)
						}
					)
				)
			}
	}.toTypedArray().let { CompletableFuture.allOf(*it) }
}