package breadmod.mixin.common;

import breadmod.registry.item.ModItems;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.animal.Parrot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Set;

@Mixin(Parrot.class)
abstract class MixinParrot {
    @Shadow @Final private static Set<Item> TAME_FOOD;

    @Inject(method = "mobInteract", at = @At("HEAD"))
    public void mobInteract(Player pPlayer, InteractionHand pHand, CallbackInfoReturnable<InteractionResult> cir) {
        TAME_FOOD.add(ModItems.INSTANCE.getBREAD_SLICE().get());
    }
}
