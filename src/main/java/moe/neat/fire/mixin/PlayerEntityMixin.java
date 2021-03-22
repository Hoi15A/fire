package moe.neat.fire.mixin;

import com.mojang.authlib.GameProfile;
import me.shedaniel.autoconfig.AutoConfig;
import moe.neat.fire.config.FireConfig;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Date;


@Mixin(ClientPlayerEntity.class)
public abstract class PlayerEntityMixin extends AbstractClientPlayerEntity {

    private final FireConfig config = AutoConfig.getConfigHolder(FireConfig.class).getConfig();
    private Date playerBurnStart = null;
    private Date cooldownUntil = null;

    public PlayerEntityMixin(ClientWorld world, GameProfile profile) {
        super(world, profile);
    }

    @Inject(at = @At("TAIL"), method = "tick")
    private void tick(CallbackInfo ci) {
        if (this.isOnFire() && !this.isCreative()) {
            // TODO: fire resistance
            // Fire.log(Level.INFO, "Player is burning");
            if (Blocks.LAVA.equals(this.world.getBlockState(this.getBlockPos()).getBlock())) {

                if (playerBurnStart == null) {
                    playerBurnStart = new Date();
                }

                // Fire.log(Level.INFO, "Player is in lava");
                if ((new Date().getTime() - playerBurnStart.getTime()) > config.getActivateAfterNSeconds() * 1_000L) {
                    playerBurnStart = null;

                    if (!isCooldownActive()) {
                        activateCooldown();
                        triggerNotification();
                    }
                }
            }
        } else if (!this.isOnFire() && !this.isCreative()) {
            playerBurnStart = null;
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
        if (cooldownUntil == null) return false;

        if (System.currentTimeMillis() - cooldownUntil.getTime() > 0) {
            cooldownUntil = null; // deactivate cooldown
        }

        return cooldownUntil != null;
    }

    private void activateCooldown() {
        cooldownUntil = new Date(System.currentTimeMillis() + 1000L * config.getCooldownSeconds());
    }
}
