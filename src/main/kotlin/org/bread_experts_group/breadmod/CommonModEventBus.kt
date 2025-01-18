package org.bread_experts_group.breadmod

import net.minecraft.core.Direction
import net.minecraft.core.RegistrySetBuilder
import net.minecraft.core.registries.Registries
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent
import net.neoforged.neoforge.capabilities.Capabilities
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider
import net.neoforged.neoforge.data.event.GatherDataEvent
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent
import net.neoforged.neoforge.items.wrapper.InvWrapper
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent
import net.neoforged.neoforge.network.registration.PayloadRegistrar
import org.apache.logging.log4j.Level
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
import org.bread_experts_group.breadmod.network.clientbound.MachTrailPacket
import org.bread_experts_group.breadmod.network.clientbound.war_timer.WarTimerIncrement
import org.bread_experts_group.breadmod.network.clientbound.war_timer.WarTimerSet
import org.bread_experts_group.breadmod.network.clientbound.war_timer.WarTimerSynchronization
import org.bread_experts_group.breadmod.network.clientbound.war_timer.WarTimerToggle
import org.bread_experts_group.breadmod.network.serverbound.PlaceItemInWorldPacket
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
import org.bread_experts_group.breadmod.util.reflect.LibraryScanner
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
		val scanner = LibraryScanner(BreadMod::class.java.classLoader, ModRecipeProvider::class.java.`package`)
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
			scanner.getClassesAnnotatedWith<LanguageDataGenerator>().forEach { clazz ->
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
		registrar.playToClient(
			WarTimerIncrement.TYPE,
			WarTimerIncrement.STREAM_CODEC,
			WarTimerIncrement::handleClientboundPacket
		)
		registrar.playToClient(
			WarTimerSet.TYPE,
			WarTimerSet.STREAM_CODEC,
			WarTimerSet::handleClientboundPacket
		)
		registrar.playToClient(
			WarTimerSynchronization.TYPE,
			WarTimerSynchronization.STREAM_CODEC,
			WarTimerSynchronization::handleClientboundPacket
		)
		registrar.playToClient(
			WarTimerToggle.TYPE,
			WarTimerToggle.STREAM_CODEC,
			WarTimerToggle::handleClientboundPacket
		)
		registrar.playToClient(
			MachTrailPacket.TYPE,
			MachTrailPacket.STREAM_CODEC,
			MachTrailPacket::handleClientboundPacket
		)
		registrar.playToClient(
			BeamPacket.TYPE,
			BeamPacket.STREAM_CODEC,
			BeamPacket::handleClientboundPacket
		)

		registrar.playToServer(
			ToolGunModeChangePacket.TYPE,
			ToolGunModeChangePacket.STREAM_CODEC,
			ToolGunModeChangePacket::handleServerboundPacket
		)
		registrar.playToServer(
			PlaceItemInWorldPacket.TYPE,
			PlaceItemInWorldPacket.STREAM_CODEC,
			PlaceItemInWorldPacket::handleServerboundPacket
		)
	}

	@SubscribeEvent
	fun onCommonSetup(event: FMLCommonSetupEvent) {
		this.logger.log(Level.INFO, "Hello! This is working!")
	}

	@SubscribeEvent
	fun registerEntityAttributes(event: EntityAttributeCreationEvent) {
		event.put(ModEntityTypes.FAKE_PLAYER.get(), FakePlayer.createAttributes().build())
	}

	@SubscribeEvent
	fun registerCapabilities(event: RegisterCapabilitiesEvent) {
		event.registerBlock(
			Capabilities.ItemHandler.BLOCK,
			{ _, _, _, entity, _: Direction -> InvWrapper(entity as SoundBlockEntity) },
			ModBlocks.SOUND_BLOCK.asBlock()
		)

		event.registerBlockEntity(
			Capabilities.EnergyStorage.BLOCK,
			ModBlockEntityTypes.WHEAT_CRUSHER.get(),
		) { entity, _: Direction -> entity.energy }
		event.registerBlockEntity(
			Capabilities.ItemHandler.BLOCK,
			ModBlockEntityTypes.WHEAT_CRUSHER.get()
		) { entity, _: Direction -> entity.items }

		event.registerBlockEntity(
			Capabilities.EnergyStorage.BLOCK,
			ModBlockEntityTypes.DOUGH_MACHINE.get(),
		) { entity, _: Direction -> entity.energy }
		event.registerBlockEntity(
			Capabilities.ItemHandler.BLOCK,
			ModBlockEntityTypes.DOUGH_MACHINE.get()
		) { entity, _: Direction -> entity.items }
		event.registerBlockEntity(
			Capabilities.FluidHandler.BLOCK,
			ModBlockEntityTypes.DOUGH_MACHINE.get()
		) { entity, direction: Direction ->
			when (direction) {
				Direction.UP   -> entity.tank.tanks[0]
				Direction.DOWN -> entity.tank.tanks[1]
				else           -> null
			}
		}

		event.registerBlockEntity(
			Capabilities.FluidHandler.BLOCK,
			ModBlockEntityTypes.SINGLE_FLUID_TEST.get()
		) { entity, _: Direction -> entity.tank }

		event.registerBlockEntity(
			Capabilities.FluidHandler.BLOCK,
			ModBlockEntityTypes.MULTI_FLUID_TEST.get()
		) { entity, _: Direction -> entity.tank }

		event.registerBlockEntity(
			Capabilities.FluidHandler.BLOCK,
			ModBlockEntityTypes.SINGLE_FLUID_ITEM_TEST.get()
		) { entity, _: Direction? -> entity.tank }

		event.registerBlockEntity(
			Capabilities.FluidHandler.BLOCK,
			ModBlockEntityTypes.FLUID_TANK_JADE_ENTITY.get()
		) { entity, direction: Direction? ->
			entity.sides[direction]
//			when (direction) {
//				Direction.UP    -> tanks[0]
//				Direction.DOWN  -> tanks[1]
//				Direction.NORTH -> tanks[2]
//				Direction.SOUTH -> tanks[3]
//				Direction.EAST  -> tanks[4]
//				Direction.WEST  -> tanks[5]
//				else            -> null
//			}
		}

		event.registerBlockEntity(
			Capabilities.FluidHandler.BLOCK,
			ModBlockEntityTypes.FLUID_ENERGY.get()
		) { entity, direction: Direction ->
			val tanks = entity.tank.tanks
			when (direction) {
				Direction.EAST -> tanks[0]
				Direction.WEST -> tanks[1]
				Direction.UP   -> tanks[2]
				Direction.DOWN -> tanks[3]
				else           -> null
			}
		}

		event.registerBlockEntity(
			Capabilities.ItemHandler.BLOCK,
			ModBlockEntityTypes.FLUID_ENERGY.get()
		) { entity, _: Direction -> entity.items }
	}
}