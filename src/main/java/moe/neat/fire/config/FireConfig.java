package moe.neat.fire.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;

@SuppressWarnings({"FieldMayBeFinal", "FieldCanBeLocal"})
@Config(name = "fire")
public class FireConfig implements ConfigData {
    private boolean broadcastCoords = true;
    private String publicMessage = "[Fire] I'm burning! Coords:";
    private String privateMessage = "[Fire] Coordinates:";
    private int activateAfterNSeconds = 3;
    private int cooldownSeconds = 60;

    public int getCooldownSeconds() {
        return cooldownSeconds;
    }

    public int getActivateAfterNSeconds() {
        return activateAfterNSeconds;
    }

    public String getPublicMessage() {
        return publicMessage;
    }

    public String getPrivateMessage() {
        return privateMessage;
    }

    public boolean isBroadcastCoords() {
        return broadcastCoords;
    }
}
