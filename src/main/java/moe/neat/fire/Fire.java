package moe.neat.fire;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.Toml4jConfigSerializer;
import moe.neat.fire.config.FireConfig;
import net.fabricmc.api.ModInitializer;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class Fire implements ModInitializer {

    private static final Logger LOGGER = LogManager.getLogger();

    @Override
    public void onInitialize() {
        log(Level.INFO, "Initialising");
        log(Level.INFO, "Registering Config");
        AutoConfig.register(FireConfig.class, Toml4jConfigSerializer::new);
    }

    public static void log(Level level, String message) {
        LOGGER.log(level, "[Fire!] {}", message);
    }
}
