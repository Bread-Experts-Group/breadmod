package bread.mod.breadmod.event;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.architectury.event.Event;
import dev.architectury.event.EventFactory;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.item.*;

public interface BlockEntityWithoutLevelRendererEvent {
    /**
     * Use this event for registering BEWLRs.
     */
    Event<BlockEntityWithoutLevelRendererEvent> RENDER_ITEMS_EVENT = EventFactory.createLoop();

    /**
     * Passed through to the BlockEntityWithoutLevelRenderer Mixin to render all items in this function
     *
     * @param stack Will throw an exception if the item does not match your BEWLR.
     */
    void renderByItem(
            ItemStack stack,
            ItemDisplayContext displayContext,
            PoseStack poseStack,
            MultiBufferSource bufferSource,
            int packedLight,
            int PackedOverlay
    );
}