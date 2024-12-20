package org.bread_experts_group.breadmod.mixin.client;

import net.neoforged.neoforge.client.extensions.IVertexConsumerExtension;
import org.joml.Matrix3f;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.nio.ByteBuffer;

@Mixin(IVertexConsumerExtension.class)
public interface InvokerIVertexConsumerExtension {
	@Invoker("applyBakedLighting")
	int invokeApplyBakedLighting(int packedLight, ByteBuffer data);

	@Invoker("applyBakedNormals")
	void invokeApplyBakedNormals(Vector3f generated, ByteBuffer data, Matrix3f normalTransform);
}
