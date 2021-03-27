package moe.neat.fire.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;

@SuppressWarnings({"FieldMayBeFinal", "FieldCanBeLocal"})
@Config(name = "fire")
public class FireConfig implements ConfigData {
    private boolean broadcastCoords = true;
    private String publicMessage = "[Fire] I'm burning! Coords:";
    private String privateMessage = "[Fire] Coordinates:";
    private int activateAfterNSeconds = 0;
    private int cooldownSeconds = 30;
    private int healthThreshhold = 10;

    @Override
    public void validatePostLoad() throws ValidationException {
        if (healthThreshhold > 20 || healthThreshhold < 0) {
            healthThreshhold = 0;
        }
    }

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

    public int getHealthThreshhold() {
        return healthThreshhold;
    }
}
