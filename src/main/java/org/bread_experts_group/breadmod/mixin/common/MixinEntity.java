package org.bread_experts_group.breadmod.mixin.common;

import com.google.common.collect.AbstractIterator;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Cursor3D;
import net.minecraft.world.level.BlockCollisions;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.CollisionGetter;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;
import java.util.function.BiFunction;

@Mixin(BlockCollisions.class)
abstract class MixinBlockCollisions<T> extends AbstractIterator<T> {
	@Shadow
	@Final
	private Cursor3D cursor;
	@Shadow
	@Final
	private BlockPos.MutableBlockPos pos;
	@Shadow
	@Final
	private boolean onlySuffocatingBlocks;
	@Shadow
	@Final
	private AABB box;
	@Shadow
	@Final
	private BiFunction<BlockPos.MutableBlockPos, VoxelShape, T> resultProvider;
	@Shadow
	@Final
	private CollisionGetter collisionGetter;
	@Shadow
	@Final
	private CollisionContext context;
	@Shadow
	@Final
	private VoxelShape entityShape;

	@Shadow
	@Nullable
	protected abstract BlockGetter getChunk(int x, int z);

	@Inject(method = "computeNext", at = @At("HEAD"), cancellable = true)
	private void computeNext(CallbackInfoReturnable<? super T> cir) {
		while (this.cursor.advance()) {
			int x = this.cursor.nextX();
			int y = this.cursor.nextY();
			int z = this.cursor.nextZ();
			int nextType = this.cursor.getNextType();
			if (nextType != 3) {
				BlockGetter blockgetter = this.getChunk(x, z);
				if (blockgetter != null) {
					this.pos.set(x, y, z);
					BlockState blockstate = blockgetter.getBlockState(this.pos);
					if (
							(!this.onlySuffocatingBlocks || blockstate.isSuffocating(blockgetter, this.pos))
									&& (nextType != 1 || blockstate.hasLargeCollisionShape())
									&& (nextType != 2 || blockstate.is(Blocks.MOVING_PISTON))
					) {
						VoxelShape voxelshape = blockstate.getCollisionShape(this.collisionGetter, this.pos, this.context);
						if (voxelshape == Shapes.block()) {
							if (this.box.intersects(
									(double) x, (double) y, (double) z,
									(double) x + 1.0, (double) y + 1.0, (double) z + 1.0)
							) {
								cir.setReturnValue(
										this.resultProvider.apply(
												this.pos,
												voxelshape.move((double) x, (double) y, (double) z)
										)
								);
								return;
							}
						} else {
							VoxelShape shape = voxelshape.move((double) x, (double) y, (double) z);
							if (!shape.isEmpty() && Shapes.joinIsNotEmpty(shape, this.entityShape, BooleanOp.AND)) {
								cir.setReturnValue(this.resultProvider.apply(this.pos, shape));
								return;
							}
						}
					}
				}
			}
		}

		cir.setReturnValue(this.endOfData());
	}
}