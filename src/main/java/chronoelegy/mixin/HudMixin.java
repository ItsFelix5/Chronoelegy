package chronoelegy.mixin;

import chronoelegy.Main;
import chronoelegy.block.ModBlocks;
import chronoelegy.block.entity.TableBlockEntity;
import chronoelegy.item.ModItems;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.item.ItemStack;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public abstract class HudMixin {
    @Shadow @Final private static Identifier CROSSHAIR_TEXTURE;
    @Shadow @Final private MinecraftClient client;
    @Shadow public abstract TextRenderer getTextRenderer();
    @Unique
    private static final Identifier TIME_CROSSHAIR_TEXTURE = Main.id("hud/time_crosshair");
    @Unique
    private static final Identifier PICKUP_CROSSHAIR_TEXTURE = Main.id("hud/pickup_crosshair");
    @Unique
    private static final Identifier PLACE_CROSSHAIR_TEXTURE = Main.id("hud/place_crosshair");
    @Unique
    private static final Identifier WRENCH_CROSSHAIR_TEXTURE = Main.id("hud/wrench_crosshair");

    @Inject(method = "renderCrosshair", at = @At("HEAD"), cancellable = true)
    private void renderCrosshair(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
        if(client.crosshairTarget == null || client.currentScreen != null) ci.cancel();
    }

    @Redirect(method = "renderCrosshair", at = @At(value = "FIELD", target = "Lnet/minecraft/client/gui/hud/InGameHud;CROSSHAIR_TEXTURE:Lnet/minecraft/util/Identifier;"))
    private Identifier renderCrosshair() {
        if(client.crosshairTarget instanceof BlockHitResult target) {
            BlockState state = client.world.getBlockState(target.getBlockPos());
            if(state.isOf(ModBlocks.GRANDFATHER_CLOCK) && state.get(Properties.ENABLED)) return TIME_CROSSHAIR_TEXTURE;
            if(state.isOf(ModBlocks.TABLE) && client.world.getBlockEntity(target.getBlockPos()) instanceof TableBlockEntity entity) {
                boolean handEmpty = client.player.getMainHandStack().isEmpty();
                ItemStack stack = entity.getItem();
                if(stack.isEmpty()) {
                    if(!handEmpty) return PLACE_CROSSHAIR_TEXTURE;
                } else {
                    if(stack.isOf(ModItems.BROKEN_POCKET_WATCH)) return WRENCH_CROSSHAIR_TEXTURE;
                    if(handEmpty) return PICKUP_CROSSHAIR_TEXTURE;
                }
            }
        }
        return CROSSHAIR_TEXTURE;
    }

    @Inject(method = "renderMainHud", at = @At("HEAD"), cancellable = true)
    private void renderMainHud(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
        if(!client.player.isCreative()) ci.cancel();
        if(Main.speedrunMode) {
            int seconds = Main.ticks / 20;
            int minutes = seconds / 60;
            seconds = seconds % 60;
            context.drawText(getTextRenderer(), minutes + ":" + seconds, 10, 10, -1, true);
            context.drawText(getTextRenderer(), Math.round(client.player.getVelocity().length() * 2000) / 100 + "m/s", 10, 20, -1, true);
        }
    }
}
