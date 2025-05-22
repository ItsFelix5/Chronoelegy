package chronoelegy.mixin;

import chronoelegy.BackgroundRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Screen.class)
public class ScreenMixin {
    @Shadow public int width;
    @Shadow public int height;

    @Inject(method = "renderPanoramaBackground", at = @At("HEAD"), cancellable = true)
    protected void renderPanoramaBackground(DrawContext context, float deltaTicks, CallbackInfo ci) {
        ci.cancel();
        BackgroundRenderer.render(context, width, height, deltaTicks);
    }
}
