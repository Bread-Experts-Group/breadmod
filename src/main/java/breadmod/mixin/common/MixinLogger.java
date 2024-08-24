package breadmod.mixin.common;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.message.Message;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(org.apache.logging.log4j.core.Logger.class)
abstract class MixinLogger {
    /**
     * @author Miko Elbrecht
     * @reason Fabric developers have it too easy
     */
    @Overwrite(remap = false)
    public void logMessage(
            final String fqcn, final Level level, final Marker marker, final Message message,
            final Throwable t
    ) {
        System.out.println("Meow");
    }
}
