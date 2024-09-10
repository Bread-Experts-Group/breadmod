package bread.mod.breadmod.fabric

import com.terraformersmc.modmenu.api.ConfigScreenFactory
import com.terraformersmc.modmenu.api.ModMenuApi
import org.apache.logging.log4j.LogManager

class ModMenuIntegration : ModMenuApi {
    private val logger = LogManager.getLogger()

    override fun getModConfigScreenFactory(): ConfigScreenFactory<*> = ConfigScreenFactory {
        logger.info("Getting mod config screen factory")
        it
    }
}