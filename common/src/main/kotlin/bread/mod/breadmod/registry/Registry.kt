package bread.mod.breadmod.registry

import bread.mod.breadmod.ModMainCommon
import bread.mod.breadmod.ModMainCommon.modLocation
import bread.mod.breadmod.block.util.FlammableBlock
import bread.mod.breadmod.item.armor.ArmorMaterials
import bread.mod.breadmod.item.util.FuelItem
import bread.mod.breadmod.networking.Networking.registerNetworking
import bread.mod.breadmod.reflection.LibraryScanner
import bread.mod.breadmod.registry.block.ModBlockEntityTypes
import bread.mod.breadmod.registry.block.ModBlocks
import bread.mod.breadmod.registry.entity.ModPainting
import bread.mod.breadmod.registry.item.ModItems
import bread.mod.breadmod.registry.menu.ModCreativeTabs
import bread.mod.breadmod.registry.sound.ModSounds
import dev.architectury.event.events.common.LifecycleEvent
import dev.architectury.registry.fuel.FuelRegistry
import dev.architectury.registry.item.ItemPropertiesRegistry
import dev.architectury.registry.registries.RegistrySupplier
import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.ItemLike
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.FireBlock

internal object Registry {
    private fun runAnnotations() {
        LibraryScanner(ModMainCommon::class.java.classLoader, ModMainCommon::class.java.`package`)
            .getObjectPropertiesAnnotatedWith<FlammableBlock>()
            .forEach { (_, data) ->
                val supplier = data.first
                if (supplier !is RegistrySupplier<*>) throw IllegalArgumentException(
                    String.format(
                        "Values annotated with %s must be of type %s",
                        FlammableBlock::class.qualifiedName, RegistrySupplier::class.qualifiedName
                    )
                )

                val first = data.second.first()
                if (first.flammability < 0 || first.encouragement < 0) throw IllegalArgumentException(
                    String.format(
                        "Values annotated with %s cannot have a negative flammability or encouragement value",
                        FlammableBlock::class.qualifiedName
                    )
                )

                supplier.listen {
                    val actual = when (it) {
                        is Block -> it
                        is BlockItem -> it.block
                        else -> throw IllegalArgumentException(
                            String.format(
                                "Values annotated with %s must be a %s supplying a type of %s, or %s",
                                FlammableBlock::class.qualifiedName, RegistrySupplier::class.qualifiedName,
                                Block::class.qualifiedName, BlockItem::class.qualifiedName
                            )
                        )
                    }
                    (Blocks.FIRE as FireBlock).setFlammable(actual, first.flammability, first.encouragement)
                }
            }

        LibraryScanner(ModMainCommon::class.java.classLoader, ModMainCommon::class.java.`package`)
            .getObjectPropertiesAnnotatedWith<FuelItem>()
            .forEach { (_, data) ->
                val supplier = data.first
                if (supplier !is RegistrySupplier<*>) throw IllegalArgumentException(
                    String.format(
                        "Values annotated with %s must be of type %s",
                        FuelItem::class.qualifiedName, RegistrySupplier::class.qualifiedName
                    )
                )

                val first = data.second.first()
                if (first.burnTime < 0) throw IllegalArgumentException(
                    String.format(
                        "Values annotated with %s cannot have a negative burn time",
                        FlammableBlock::class.qualifiedName
                    )
                )

                supplier.listen {
                    val actual = when (it) {
                        is ItemLike -> it
                        else -> throw IllegalArgumentException(
                            String.format(
                                "Values annotated with %s must be a %s supplying a type of %s",
                                FuelItem::class.qualifiedName, RegistrySupplier::class.qualifiedName,
                                ItemLike::class.qualifiedName
                            )
                        )
                    }
                    FuelRegistry.register(first.burnTime, actual)
                }
            }
    }

    private fun runLifecycleSetupStage() {
        LifecycleEvent.SETUP.register {
            ItemPropertiesRegistry.register(ModItems.BREAD_SHIELD.get(), modLocation("blocking")) {
                    itemStack: ItemStack, _: ClientLevel?, livingEntity: LivingEntity?, _: Int ->
                if (livingEntity != null && livingEntity.isUsingItem && livingEntity.useItem == itemStack) 1f else 0f
            }
        }
    }

    fun registerAll() {
        ModBlocks.BLOCK_REGISTRY.register()
        ModCreativeTabs.CREATIVE_TAB_REGISTRY.register()
        ModSounds.SOUND_REGISTRY.register()
        ModItems.ITEM_REGISTRY.register()
        ModBlockEntityTypes.BLOCK_ENTITY_TYPE_REGISTRY.register()
        ArmorMaterials.ARMOR_REGISTRY.register()
        ModPainting.PAINTING_VARIANT_REGISTRY.register()
        registerNetworking()
        CommonEvents.registerServerTickEvent()
        CommonEvents.registerCommands()
//        registerClientTick()

        runAnnotations()
        runLifecycleSetupStage()
    }
}