package chronoelegy.mixin;

import chronoelegy.Main;
import net.minecraft.client.render.item.ItemRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(ItemRenderer.class)
public class ItemRendererMixin {
    @ModifyArg(method = "renderBakedItemQuads", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/VertexConsumer;quad(Lnet/minecraft/client/util/math/MatrixStack$Entry;" +
            "Lnet/minecraft/client/render/model/BakedQuad;FFFFII)V"), index = 5)
    private static float renderBakedItemQuads(float alpha) {
        return Main.transparentItems?alpha / 2:alpha;
    }
}
