package breadmod.natives;

import java.io.IOException;

/**
 * Exception thrown when a native library fails to load.
 */
@SuppressWarnings({"UncheckedExceptionClass", "SerializableHasSerializationMethods", "ClassWithoutNoArgConstructor", "serial"})
public class FailedToLoadNativeException extends RuntimeException {
    /**
     * @param cause The exception that caused the failure to load the native library.
     */
    @SuppressWarnings("PublicConstructor")
    public FailedToLoadNativeException(final IOException cause) {
        super(cause);
    }
}
