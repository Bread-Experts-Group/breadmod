package org.bread_experts_group.breadmod.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.mojang.blaze3d.pipeline.RenderTarget;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import org.bread_experts_group.breadmod.experimental.camera_viewer.CameraTexture;
import org.bread_experts_group.breadmod.experimental.physics_grid.PhysicsGrid;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import javax.annotation.Nullable;

@Mixin(Minecraft.class)
public abstract class MixinMinecraft {
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

	@Shadow
	@Nullable
	public LocalPlayer player;

	@Shadow
	@Nullable
	public HitResult hitResult;

	@ModifyReturnValue(method = "getMainRenderTarget", at = @At(value = "RETURN"))
	private RenderTarget overrideRenderTarget(RenderTarget original) {
		if (CameraTexture.Companion.getTargetBeingRendered() != null) {
			return CameraTexture.Companion.getTargetBeingRendered();
		}
		return original;
	}

	@ModifyExpressionValue(
			method = "pickBlock",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/multiplayer/ClientLevel;getBlockState(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/state/BlockState;"
			)
	)
	private BlockState pickGridBlock(BlockState original) {
		LocalPlayer player = this.player;
		if (player == null) return original;
		BlockHitResult hitResult = (BlockHitResult) this.hitResult;
		PhysicsGrid grid = PhysicsGrid.Companion.getClosestGrid(player);
		return (grid != null && hitResult != null) ? grid.getMicroLevel().getBlockState(hitResult.getBlockPos()) : original;
	}
}