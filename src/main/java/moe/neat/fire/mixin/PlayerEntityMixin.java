package moe.neat.fire.mixin;

import com.mojang.authlib.GameProfile;
import me.shedaniel.autoconfig.AutoConfig;
import moe.neat.fire.config.FireConfig;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(ClientPlayerEntity.class)
public abstract class PlayerEntityMixin extends AbstractClientPlayerEntity {

    private static final long COOLDOWN_DISABLED = -1;

    private final FireConfig config = AutoConfig.getConfigHolder(FireConfig.class).getConfig();
    private long playerBurnStart = COOLDOWN_DISABLED;
    private long cooldownUntil = COOLDOWN_DISABLED;

    protected PlayerEntityMixin(ClientWorld world, GameProfile profile) {
        super(world, profile);
    }

    @Inject(at = @At("TAIL"), method = "tick")
    private void tick(CallbackInfo ci) {
        boolean isInLava = Blocks.LAVA.equals(this.world.getBlockState(this.getBlockPos()).getBlock());

        if (this.isOnFire() && !this.isCreative() && !hasFireResistance() && isInLava && isBelowHealthThreshhold()) {

            if (playerBurnStart == COOLDOWN_DISABLED) {
                playerBurnStart = System.currentTimeMillis();
            }

            if ((System.currentTimeMillis() - playerBurnStart) > config.getActivateAfterNSeconds() * 1_000L) {
                playerBurnStart = COOLDOWN_DISABLED;

                if (!isCooldownActive()) {
                    activateCooldown();
                    triggerNotification();
                }
            }

        } else if (!this.isOnFire() && !this.isCreative()) {
            playerBurnStart = COOLDOWN_DISABLED;
        }
    }

    private void triggerNotification() {
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        assert player != null;
        String coords = String.format(" %s / %s / %s", Math.round(player.getX()), Math.round(player.getY()), Math.round(player.getZ()));
        if (config.isBroadcastCoords()) {
            player.sendChatMessage(config.getPublicMessage() + coords);
        } else {
            player.sendMessage(Text.of(config.getPrivateMessage() + coords), false);
        }
    }

    private boolean isCooldownActive() {
        if (cooldownUntil == COOLDOWN_DISABLED) return false;

        if (System.currentTimeMillis() - cooldownUntil > 0) {
            cooldownUntil = COOLDOWN_DISABLED;
        }

        return cooldownUntil != COOLDOWN_DISABLED;
    }

    private void activateCooldown() {
        cooldownUntil = System.currentTimeMillis() + 1000L * config.getCooldownSeconds();
    }

    private boolean isBelowHealthThreshhold() {
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        assert player != null;

        return player.getHealth() <= config.getHealthThreshhold();
    }

    private boolean hasFireResistance() {
        for (StatusEffectInstance effect : this.getStatusEffects()) {
            if (StatusEffects.FIRE_RESISTANCE.equals(effect.getEffectType())) return true;
        }
        return false;
    }
}
