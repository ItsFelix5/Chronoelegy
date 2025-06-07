package chronoelegy.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.Mouse;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.client.option.GameOptions;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public abstract class MinecraftClientMixin {
    @Shadow @Final public GameOptions options;
    @Shadow @Nullable public ClientPlayerEntity player;
    @Shadow @Nullable public ClientPlayerInteractionManager interactionManager;
    @Shadow protected abstract boolean doAttack();
    @Shadow protected abstract void doItemUse();
    @Shadow @Final public Mouse mouse;
    @Shadow @Nullable public Screen currentScreen;
    @Shadow protected abstract void handleBlockBreaking(boolean breaking);
    @Shadow private int itemUseCooldown;

    @Inject(method = "handleInputEvents", at = @At("HEAD"), cancellable = true)
    private void handleInputEvents(CallbackInfo ci) {
        if(player.isCreative()) return;
        ci.cancel();

        boolean bl3 = false;
        if (this.player.isUsingItem()) {
            if (!this.options.useKey.isPressed()) interactionManager.stopUsingItem(this.player);
        } else {
            while (this.options.attackKey.wasPressed()) bl3 |= doAttack();
            while (this.options.useKey.wasPressed()) doItemUse();
        }

        if (this.options.useKey.isPressed() && itemUseCooldown == 0 && !this.player.isUsingItem()) this.doItemUse();

        handleBlockBreaking(currentScreen == null && !bl3 && this.options.attackKey.isPressed() && mouse.isCursorLocked());
    }
}
