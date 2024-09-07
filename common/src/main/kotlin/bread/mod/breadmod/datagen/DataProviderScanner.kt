package bread.mod.breadmod.datagen

import bread.mod.breadmod.reflection.LibraryScanner
import net.minecraft.data.DataProvider

/**
 * A [DataProvider] factory which uses a given [Package] to scan for targets which can have data generated for them.
 * @author Miko Elbrecht
 * @since 1.0.0
 */
abstract class DataProviderScanner<T>(val modID: String, forClassLoader: ClassLoader, forPackage: Package) {
    /**
     * The [LibraryScanner] used to facilitate scanning over the provided [ClassLoader] and [Package].
     * @author Miko Elbrecht
     */
    protected val scanner: LibraryScanner = LibraryScanner(forClassLoader, forPackage)

    /**
     * Generates data for an event such as GatherDataEvent, present in NeoForge.
     * @author Miko Elbrecht
     * @since 1.0.0
     */
    abstract fun generate(forEvent: T)
}