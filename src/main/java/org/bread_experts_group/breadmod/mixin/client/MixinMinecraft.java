package org.bread_experts_group.breadmod.mixin.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import org.bread_experts_group.breadmod.util.GeneralKt;
import org.bread_experts_group.breadmod.util.HitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;
import java.util.Objects;

@Mixin(Minecraft.class)
public class MixinMinecraft {
	@Shadow
	@Nullable
	public LocalPlayer player;
	@Shadow
	@Nullable
	public MultiPlayerGameMode gameMode;

	@Inject(method = "pickBlock", at = @At("HEAD"), cancellable = true)
	private void pickBlock(CallbackInfo ci) {
		Objects.requireNonNull(this.player);
		Objects.requireNonNull(this.gameMode);
		HitResult<BlockState> selected = GeneralKt.rayCast(
				this.player, this.player.blockInteractionRange(),
				GeneralKt.blocksPhysicsGrids()
		);
		if (selected != null) {
			ItemStack stack = selected.getHit().getCloneItemStack(
					selected.getAsMinecraftHitResult(selected),
					this.player.level(),
					selected.getBlockPosition(),
					this.player
			);
			if (stack.isEmpty()) return;
			Inventory inventory = this.player.getInventory();
			inventory.setPickedItem(stack);
			this.gameMode.handleCreativeModeItemAdd(
					this.player.getItemInHand(InteractionHand.MAIN_HAND),
					36 + inventory.selected
			);
			ci.cancel();
		}
	}
}
