package org.bread_experts_group.breadmod.mixin.client;

import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Minecraft.class)
public class MixinMinecraft {
//	@Shadow
//	@Nullable
//	public LocalPlayer player;
//	@Shadow
//	@Nullable
//	public MultiPlayerGameMode gameMode;
//
//	@Shadow
//	@Nullable
//	public ClientLevel level;
//
//	@Inject(method = "pickBlock", at = @At("HEAD"), cancellable = true)
//	private void pickBlock(CallbackInfo ci) {
//		Objects.requireNonNull(this.player);
//		Objects.requireNonNull(this.gameMode);
//		Objects.requireNonNull(this.level);
//		GridHitResult selected = GeneralKt.blockPhysicsGrid(
//				(grid) -> grid instanceof ClientPhysicsGrid,
//				this.player.getEyePosition(),
//				this.player.calculateViewVector(this.player.getXRot(), this.player.getYRot()),
//				false,
//				CollisionContext.of(this.player)
//		);
//		if (selected != null) {
//			ItemStack stack = selected.getState().getCloneItemStack(
//					selected.getHitResult(),
//					this.player.level(),
//					BlockPos.containing(selected.getHitResult().getLocation()),
//					this.player
//			);
//			if (stack.isEmpty()) return;
//			Inventory inventory = this.player.getInventory();
//			inventory.setPickedItem(stack);
//			this.gameMode.handleCreativeModeItemAdd(
//					this.player.getItemInHand(InteractionHand.MAIN_HAND),
//					36 + inventory.selected
//			);
//			ci.cancel();
//		}
//	}
}
