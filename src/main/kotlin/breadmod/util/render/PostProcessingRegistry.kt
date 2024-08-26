package breadmod.util.render

import breadmod.util.render.PostProcessingRegistry.addProcessor
import net.minecraft.client.renderer.PostChain

/**
 * Holds a set of shaders, indexed by both the calling module's name and a provided shader name.
 * @see addProcessor
 * @author Miko Elbrecht
 * @since 1.0.0
 */
object PostProcessingRegistry {
    private val shaders = mutableMapOf<String, MutableMap<String, PostChain>>()

    /**
     * The complete set of shaders to run for post-processing.
     * @author Miko Elbrecht
     * @since 1.0.0
     */
    val visibleShaders: Map<String, Map<String, PostChain>> = shaders

    private fun getCallerID() = StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE).walk { frames ->
        frames
            .map { it.declaringClass }
            .skip(2)
            .findFirst()
            .get()
            .module.name
    }

    /**
     * Adds a post-processing chain to your module's registry. Post-processing shaders in this registry are run through
     * iteratively, before Minecraft's own postChain runs.
     * @see visibleShaders
     * @param name The name of the post-processing chain to add to your registry.
     * @param chain The post-processing chain to add.
     * @author Miko Elbrecht
     * @since 1.0.0
     */
    fun addProcessor(name: String, chain: PostChain) {
        shaders.computeIfAbsent(getCallerID()) { mutableMapOf() }[name] = chain
    }

    /**
     * Removes a post-processing chain from your module's registry.
     * @see addProcessor
     * @param name The name of the post-processing chain in your registry.
     * @return The previous post-processing chain that existed at [name] (if any)
     * @author Miko Elbrecht
     * @since 1.0.0
     */
    fun removeProcessor(name: String) = shaders.remove(name)
}