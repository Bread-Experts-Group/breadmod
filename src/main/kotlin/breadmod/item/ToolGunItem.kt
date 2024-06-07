package breadmod.item

import breadmod.ModMain.modTranslatable
import breadmod.compat.geckolib.ToolGunGeoRenderer
import breadmod.network.BeamPacket
import breadmod.network.PacketHandler.NETWORK
import breadmod.registry.item.IRegisterSpecialCreativeTab
import breadmod.registry.screen.ModCreativeTabs
import breadmod.registry.sound.ModSounds
import breadmod.util.RayMarchResult.Companion.rayMarchEntity
import net.minecraft.ChatFormatting
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.sounds.SoundSource
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResultHolder
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.phys.Vec3
import net.minecraftforge.client.extensions.common.IClientItemExtensions
import net.minecraftforge.network.PacketDistributor
import net.minecraftforge.registries.RegistryObject
import software.bernie.geckolib.animatable.GeoItem
import software.bernie.geckolib.animatable.SingletonGeoAnimatable
import software.bernie.geckolib.core.animatable.GeoAnimatable
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache
import software.bernie.geckolib.core.animatable.instance.SingletonAnimatableInstanceCache
import software.bernie.geckolib.core.animation.AnimatableManager
import software.bernie.geckolib.core.animation.AnimationController
import software.bernie.geckolib.core.animation.RawAnimation
import software.bernie.geckolib.core.`object`.PlayState
import software.bernie.geckolib.util.RenderUtils
import java.util.function.Consumer
import kotlin.random.Random


class ToolGunItem: Item(Properties().stacksTo(1)), IRegisterSpecialCreativeTab, GeoItem {
    private val cache: AnimatableInstanceCache = SingletonAnimatableInstanceCache(this)
    private val activateAnim: RawAnimation = RawAnimation.begin().thenPlay("animation.tool_gun")

    init {
        SingletonGeoAnimatable.registerSyncedAnimatable(this)
    }

    override val creativeModeTabs: List<RegistryObject<CreativeModeTab>> = listOf(ModCreativeTabs.SPECIALS_TAB)

    private val random = Random(-34295000)
    override fun use(pLevel: Level, pPlayer: Player, pUsedHand: InteractionHand): InteractionResultHolder<ItemStack> {
        if(pLevel is ServerLevel) {
            pLevel.rayMarchEntity(pPlayer, pPlayer.position(), Vec3.directionFromRotation(pPlayer.xRot, pPlayer.yRot), 1000.0)?.let {
                fun rand() = (random.nextDouble() - 0.5)*1.2
                pLevel.sendParticles(ParticleTypes.END_ROD, it.entity.x, it.entity.y, it.entity.z, 40, rand(), random.nextDouble(), rand(), 1.0)
                pLevel.playSound(null, pPlayer.blockPosition(), ModSounds.TOOL_GUN.get(), SoundSource.PLAYERS, 2.0f, 1f)
                NETWORK.send(
                    PacketDistributor.TRACKING_CHUNK.with { pLevel.getChunkAt(it.entity.blockPosition()) },
                    BeamPacket(it.startPosition.toVector3f(), it.endPosition.toVector3f(), 0.1)
                )
                triggerAnim<GeoAnimatable>(pPlayer, GeoItem.getOrAssignId(pPlayer.getItemInHand(pUsedHand), pLevel), "Activation", "activate")

                if(it.entity is ServerPlayer) it.entity.connection.disconnect(modTranslatable("item", "tool_gun", "player_left_game"))
                else {
                    it.entity.discard()
                    pLevel.server.playerList.players.forEach { player -> player.sendSystemMessage(modTranslatable("item", "tool_gun", "entity_left_game", args = listOf(it.entity.name)).withStyle(ChatFormatting.YELLOW)) }
                }
            }
        }

        return InteractionResultHolder.fail(pPlayer.getItemInHand(pUsedHand))
    }

    override fun registerControllers(controllers: AnimatableManager.ControllerRegistrar) {
        controllers.add(AnimationController(this, "Activation", 0) { PlayState.STOP }.triggerableAnim("activate", activateAnim))
    }

    override fun getAnimatableInstanceCache(): AnimatableInstanceCache = cache
    override fun getTick(itemStack: Any?): Double = RenderUtils.getCurrentTick()
    override fun initializeClient(consumer: Consumer<IClientItemExtensions>) = consumer.accept(object : IClientItemExtensions {
        var renderer: ToolGunGeoRenderer? = null
        override fun getCustomRenderer(): BlockEntityWithoutLevelRenderer {
            if(this.renderer == null) this.renderer = ToolGunGeoRenderer()
            return renderer as ToolGunGeoRenderer
        }
    })
}