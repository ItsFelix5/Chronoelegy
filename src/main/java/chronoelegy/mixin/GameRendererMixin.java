package chronoelegy.mixin;

import chronoelegy.Main;
import com.mojang.blaze3d.systems.RenderPass;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.PostEffectProcessor;
import net.minecraft.client.render.*;
import net.minecraft.client.util.Pool;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Objects;
import java.util.function.Consumer;

import static chronoelegy.Main.client;

@Mixin(GameRenderer.class)
public abstract class GameRendererMixin {
    @Shadow @Final private Pool pool;
    @Shadow @Final private Camera camera;

    @Unique
    private float strength = 0.0f;

    @Inject(method = "renderWorld", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/profiler/Profiler;swap(Ljava/lang/String;)V", ordinal = 1))
    private void depth(RenderTickCounter renderTickCounter, CallbackInfo ci) {
        post("post", pass -> pass.setUniform("MotionDir", calculateBlurDirection()));
    }

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/profiler/Profiler;pop()V"))
    private void time_stop(RenderTickCounter tickCounter, boolean tick, CallbackInfo ci) {
        if (client.world.getTickManager().isFrozen()) strength = Math.min(1F, strength + tickCounter.getDynamicDeltaTicks() / 10F);
        else if(strength > 0) strength = Math.max(0F, strength - tickCounter.getDynamicDeltaTicks() / 10F);
        if(strength > 0) post("time_stop", pass -> pass.setUniform("Strength", strength));
    }

    @Inject(method = "getFov", at = @At("RETURN"), cancellable = true)
    private void getFov(Camera camera, float tickProgress, boolean changingFov, CallbackInfoReturnable<Float> cir) {
        cir.setReturnValue(MathHelper.lerp(Main.lerpTime, cir.getReturnValueF(), 30));
    }

    @Unique
    private Vec3d prevPos = Vec3d.ZERO, deltaPos = Vec3d.ZERO;
    @Unique
    private float prevPitch = 0F, prevYaw = 0F, deltaPitch = 0F, deltaYaw = 0F;

    @Unique
    private float[] calculateBlurDirection() {
        deltaPos = deltaPos.multiply(0.5F).add(camera.getPos().subtract(prevPos).multiply(3));
        deltaPitch = deltaPitch / 2F + (camera.getPitch() - prevPitch);
        deltaYaw = deltaYaw / 2F + (camera.getYaw() - prevYaw);

        prevPos = MathHelper.lerp(0.5F, prevPos, camera.getPos());
        prevPitch = MathHelper.lerp(0.5F, prevPitch, camera.getPitch());
        prevYaw = MathHelper.lerp(0.5F, prevYaw, camera.getYaw());

        float cameraVelocityX = deltaYaw + (float) deltaPos.x + (float) deltaPos.z;
        float cameraVelocityY = deltaPitch + (float) deltaPos.y;

        return new float[]{-cameraVelocityX / 7F, -cameraVelocityY / 7F};
    }

    @Unique
    private void post(String stack, Consumer<RenderPass> additionalUniformsSetter) {
        Framebuffer main = client.getFramebuffer();
        FrameGraphBuilder frameGraphBuilder = new FrameGraphBuilder();
        Objects.requireNonNull(client.getShaderLoader().loadPostEffect(Main.id(stack), DefaultFramebufferSet.MAIN_ONLY))
                .render(frameGraphBuilder, main.textureWidth, main.textureHeight,
                        PostEffectProcessor.FramebufferSet.singleton(PostEffectProcessor.MAIN, frameGraphBuilder.createObjectNode("main", main)), additionalUniformsSetter);
        frameGraphBuilder.run(pool);
    }
}
