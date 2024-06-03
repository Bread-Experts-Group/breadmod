package breadmod.item

import breadmod.ModMain.modTranslatable
import breadmod.registry.item.RegisterSpecialCreativeTab
import breadmod.registry.screen.ModCreativeTabs
import breadmod.registry.sound.ModSounds
import breadmod.util.RayMarchResult.Companion.rayMarchEntity
import com.simibubi.create.foundation.item.render.SimpleCustomRenderer
import net.minecraft.ChatFormatting
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
import net.minecraft.world.item.UseAnim
import net.minecraft.world.level.Level
import net.minecraft.world.phys.Vec3
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.client.extensions.common.IClientItemExtensions
import net.minecraftforge.registries.RegistryObject
import java.util.function.Consumer
import kotlin.random.Random

class ToolGunItem: Item(Properties().stacksTo(1)), RegisterSpecialCreativeTab {
    private val random = Random(-34295000)
    override fun use(pLevel: Level, pPlayer: Player, pUsedHand: InteractionHand): InteractionResultHolder<ItemStack> {
        if(pLevel is ServerLevel) {
            pLevel.rayMarchEntity(pPlayer, pPlayer.eyePosition, Vec3.directionFromRotation(pPlayer.xRot, pPlayer.yRot), 1000.0)?.let {
                fun rand() = (random.nextDouble() - 0.5)*1.2
                pLevel.sendParticles(ParticleTypes.FIREWORK, it.entity.x, it.entity.y, it.entity.z, 40, rand(), random.nextDouble(), rand(), 1.0)
                pLevel.playSound(null, pPlayer.blockPosition(), ModSounds.TOOL_GUN.get(), SoundSource.PLAYERS, 2.0f, 1f)

                if(it.entity is ServerPlayer) it.entity.connection.disconnect(modTranslatable("item", "tool_gun", "player_left_game"))
                else {
                    it.entity.discard()
                    pLevel.server.playerList.players.forEach { player -> player.sendSystemMessage(modTranslatable("item", "tool_gun", "entity_left_game", args = listOf(it.entity.name)).withStyle(ChatFormatting.YELLOW)) }
                }
            }
        }

        return InteractionResultHolder.consume(pPlayer.getItemInHand(pUsedHand))
    }

    override fun getUseAnimation(pStack: ItemStack): UseAnim = UseAnim.CUSTOM

    override val creativeModeTabs: List<RegistryObject<CreativeModeTab>> = listOf(ModCreativeTabs.SPECIALS_TAB)

    override fun shouldCauseReequipAnimation(
        oldStack: ItemStack?,
        newStack: ItemStack?,
        slotChanged: Boolean
    ): Boolean = false

    @OnlyIn(Dist.CLIENT)
    override fun initializeClient(consumer: Consumer<IClientItemExtensions>) = consumer.accept(SimpleCustomRenderer.create(this, ToolGunItemRenderer()))
}