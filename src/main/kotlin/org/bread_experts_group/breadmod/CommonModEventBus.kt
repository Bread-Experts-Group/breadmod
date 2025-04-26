package org.bread_experts_group.breadmod

import net.minecraft.core.Direction
import net.minecraft.core.RegistrySetBuilder
import net.minecraft.core.registries.Registries
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.capabilities.Capabilities
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider
import net.neoforged.neoforge.data.event.GatherDataEvent
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent
import net.neoforged.neoforge.items.wrapper.InvWrapper
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent
import net.neoforged.neoforge.network.registration.PayloadRegistrar
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.bread_experts_group.breadmod.datagen.ModBlockLootProvider
import org.bread_experts_group.breadmod.datagen.ModBlockLootProvider.Companion.constructLootProvider
import org.bread_experts_group.breadmod.datagen.ModBlockStateProvider
import org.bread_experts_group.breadmod.datagen.ModItemModelProvider
import org.bread_experts_group.breadmod.datagen.ModRecipeProvider
import org.bread_experts_group.breadmod.datagen.ModSoundDefinitionsProvider
import org.bread_experts_group.breadmod.datagen.lang.BaseLanguageProvider
import org.bread_experts_group.breadmod.datagen.lang.LanguageDataGenerator
import org.bread_experts_group.breadmod.datagen.tag.ModBlockTags
import org.bread_experts_group.breadmod.datagen.tag.ModFluidTags
import org.bread_experts_group.breadmod.datagen.tag.ModItemTags
import org.bread_experts_group.breadmod.datagen.tag.ModPaintingTags
import org.bread_experts_group.breadmod.network.clientbound.BeamPacket
import org.bread_experts_group.breadmod.network.clientbound.GasGasGasSoundPacket
import org.bread_experts_group.breadmod.network.clientbound.MachTrailPacket
import org.bread_experts_group.breadmod.network.clientbound.PhysicsGridPacket
import org.bread_experts_group.breadmod.network.clientbound.ScreenBleedSetPacket
import org.bread_experts_group.breadmod.network.clientbound.SpreadParticlesPacket
import org.bread_experts_group.breadmod.network.clientbound.physics_grid.ClientPhysicsGridPacket
import org.bread_experts_group.breadmod.network.clientbound.physics_grid.GridPosUpdatePacket
import org.bread_experts_group.breadmod.network.clientbound.war_timer.WarTimerIncrement
import org.bread_experts_group.breadmod.network.clientbound.war_timer.WarTimerSet
import org.bread_experts_group.breadmod.network.clientbound.war_timer.WarTimerSynchronization
import org.bread_experts_group.breadmod.network.clientbound.war_timer.WarTimerToggle
import org.bread_experts_group.breadmod.network.serverbound.GasGasGasNukePacket
import org.bread_experts_group.breadmod.network.serverbound.PhysicsGridRequestPacket
import org.bread_experts_group.breadmod.network.serverbound.PlaceItemInWorldPacket
import org.bread_experts_group.breadmod.network.serverbound.ToolGunDataSyncPacket
import org.bread_experts_group.breadmod.network.serverbound.ToolGunModeChangePacket
import org.bread_experts_group.breadmod.registry.block.ModBlockEntityTypes
import org.bread_experts_group.breadmod.registry.block.ModBlocks
import org.bread_experts_group.breadmod.registry.block.ModBlocks.asBlock
import org.bread_experts_group.breadmod.registry.block.actual.entity.SoundBlockEntity
import org.bread_experts_group.breadmod.registry.entity.ModEntityTypes
import org.bread_experts_group.breadmod.registry.entity.ModPainting
import org.bread_experts_group.breadmod.registry.entity.actual.FakePlayer
import org.bread_experts_group.breadmod.registry.item.ModRecords
import org.bread_experts_group.breadmod.registry.worldgen.dimensions.ModBiomes
import org.bread_experts_group.breadmod.registry.worldgen.dimensions.ModDimensions
import org.bread_experts_group.breadmod.registry.worldgen.dimensions.ModFeatures
import org.bread_experts_group.breadmod.registry.worldgen.dimensions.ModNoiseGenerators
import org.bread_experts_group.breadmod.registry.worldgen.dimensions.structures.ModPools
import org.bread_experts_group.breadmod.registry.worldgen.dimensions.structures.ModStructureSets
import org.bread_experts_group.breadmod.registry.worldgen.dimensions.structures.ModStructures
import org.bread_experts_group.breadmod.util.reflect.LibraryScanner.Companion.getScanner
import kotlin.reflect.full.primaryConstructor

@Suppress("unused")
@EventBusSubscriber(modid = BreadMod.ID, bus = EventBusSubscriber.Bus.MOD)
internal object CommonModEventBus {
	val logger: Logger = LogManager.getLogger()

	@SubscribeEvent
	fun gatherData(event: GatherDataEvent) {
		val generator = event.generator
		val packOutput = generator.packOutput
		val existingFileHelper = event.existingFileHelper
		val scanner = ModRecipeProvider::class.java.`package`.getScanner()
		// add all the bootstrap entries to the registry set builder
		val registrySetBuilder = RegistrySetBuilder()
			.add(Registries.PAINTING_VARIANT, ModPainting::bootstrap)
			.add(Registries.TEMPLATE_POOL, ModPools::bootstrap)
			.add(Registries.STRUCTURE, ModStructures::bootstrap)
			.add(Registries.STRUCTURE_SET, ModStructureSets::bootstrap)
			.add(Registries.NOISE_SETTINGS, ModNoiseGenerators::bootstrapNoiseGenerators)
			.add(Registries.CONFIGURED_FEATURE, ModFeatures::bootstrapConfiguredFeatures)
			.add(Registries.PLACED_FEATURE, ModFeatures::bootstrapPlacedFeatures)
			.add(Registries.BIOME, ModBiomes::bootstrapBiomes)
			.add(Registries.DIMENSION_TYPE, ModDimensions::bootstrapDimensionTypes)
			.add(Registries.LEVEL_STEM, ModDimensions::bootstrapLevelStems)
			.add(Registries.JUKEBOX_SONG, ModRecords::bootstrap)
		// bootstrap all the datapack entries and create the provider
		val datapackEntriesProvider = DatapackBuiltinEntriesProvider(
			packOutput, event.lookupProvider, registrySetBuilder, setOf(BreadMod.ID)
		)
		val lookupProvider = datapackEntriesProvider.registryProvider

		if (event.includeServer()) {
			this.logger.info("Server datagen")
			// actually run the provider for the datapack entries
			generator.addProvider(true, datapackEntriesProvider)

			generator.addProvider(true, ModPaintingTags(packOutput, lookupProvider, existingFileHelper))
			generator.addProvider(true, ModSoundDefinitionsProvider(packOutput, existingFileHelper))
			generator.addProvider(
				true,
				constructLootProvider(ModBlockLootProvider(lookupProvider), packOutput, lookupProvider)
			)
			generator.addProvider(true, ModFluidTags(packOutput, lookupProvider, existingFileHelper))
			val blockTagGenerator =
				generator.addProvider(true, ModBlockTags(packOutput, lookupProvider, existingFileHelper))
			generator.addProvider(
				true,
				ModItemTags(packOutput, lookupProvider, blockTagGenerator.contentsGetter(), existingFileHelper)
			)
			generator.addProvider(true, ModRecipeProvider(packOutput, lookupProvider))
		}
		if (event.includeClient()) {
			this.logger.info("Client datagen")
			generator.addProvider(true, ModBlockStateProvider(packOutput, existingFileHelper))
			generator.addProvider(true, ModItemModelProvider(packOutput, existingFileHelper))
			scanner.getClassesAnnotatedWith(LanguageDataGenerator::class).forEach { clazz ->
				generator.addProvider(
					true,
					(clazz.primaryConstructor ?: return@forEach).call(packOutput) as BaseLanguageProvider
				)
			}
		}
	}

	@SubscribeEvent
	fun registerPayloads(event: RegisterPayloadHandlersEvent) {
		val registrar: PayloadRegistrar = event.registrar("1")
		// Clientbound packets
		WarTimerIncrement.register(registrar)
		WarTimerSet.register(registrar)
		WarTimerSynchronization.register(registrar)
		WarTimerToggle.register(registrar)
		MachTrailPacket.register(registrar)
		BeamPacket.register(registrar)
		SpreadParticlesPacket.register(registrar)
		PhysicsGridPacket.register(registrar)
		ScreenBleedSetPacket.register(registrar)
		ClientPhysicsGridPacket.register(registrar)
		GridPosUpdatePacket.register(registrar)
		GasGasGasSoundPacket.register(registrar)
		// Serverbound packets
		ToolGunModeChangePacket.register(registrar)
		ToolGunDataSyncPacket.register(registrar)
		PlaceItemInWorldPacket.register(registrar)
		PhysicsGridRequestPacket.register(registrar)
		GasGasGasNukePacket.register(registrar)
	}

	@SubscribeEvent
	fun registerEntityAttributes(event: EntityAttributeCreationEvent) {
		event.put(ModEntityTypes.FAKE_PLAYER.get(), FakePlayer.createAttributes().build())
	}

	@SubscribeEvent
	fun registerCapabilities(event: RegisterCapabilitiesEvent) {
		event.registerBlock(
			Capabilities.ItemHandler.BLOCK,
			{ _, _, _, entity, _: Direction? -> InvWrapper(entity as SoundBlockEntity) },
			ModBlocks.SOUND_BLOCK.asBlock()
		)

		event.registerBlockEntity(
			Capabilities.EnergyStorage.BLOCK,
			ModBlockEntityTypes.WHEAT_CRUSHER.get(),
		) { entity, _: Direction? -> entity.energyHandler }
		event.registerBlockEntity(
			Capabilities.ItemHandler.BLOCK,
			ModBlockEntityTypes.WHEAT_CRUSHER.get()
		) { entity, _: Direction? -> entity.itemHandler }

		event.registerBlockEntity(
			Capabilities.EnergyStorage.BLOCK,
			ModBlockEntityTypes.DOUGH_MACHINE.get(),
		) { entity, _: Direction? -> entity.energyHandler }
		event.registerBlockEntity(
			Capabilities.ItemHandler.BLOCK,
			ModBlockEntityTypes.DOUGH_MACHINE.get()
		) { entity, _: Direction? -> entity.itemHandler }
		event.registerBlockEntity(
			Capabilities.FluidHandler.BLOCK,
			ModBlockEntityTypes.DOUGH_MACHINE.get()
		) { entity, direction: Direction? ->
			when (direction) {
				Direction.UP, Direction.DOWN -> entity.fluidHandler
				else                         -> null
			}
		}
		event.registerBlockEntity(
			Capabilities.EnergyStorage.BLOCK,
			ModBlockEntityTypes.ENERGY_STORAGE.get()
		) { entity, _ -> entity.energyHandler }

		event.registerBlockEntity(
			Capabilities.FluidHandler.BLOCK,
			ModBlockEntityTypes.FLUID_TANK_JADE_ENTITY.get()
		) { entity, _: Direction? ->
			entity.fluidHandler
		}

		event.registerBlockEntity(
			Capabilities.FluidHandler.BLOCK,
			ModBlockEntityTypes.FLUID_ENERGY.get()
		) { entity, direction: Direction? ->
			when (direction) {
				Direction.EAST, Direction.WEST, Direction.UP, Direction.DOWN -> entity.fluidHandler
				else                                                         -> null
			}
		}

		event.registerBlockEntity(
			Capabilities.ItemHandler.BLOCK,
			ModBlockEntityTypes.FLUID_ENERGY.get()
		) { entity, _: Direction? -> entity.itemHandler }
	}
}