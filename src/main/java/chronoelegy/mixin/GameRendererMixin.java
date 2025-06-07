package chronoelegy.mixin;

import chronoelegy.Main;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.mojang.blaze3d.systems.RenderPass;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.PostEffectProcessor;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.*;
import net.minecraft.client.util.Pool;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;
import java.util.function.Consumer;

@Mixin(GameRenderer.class)
public abstract class GameRendererMixin {
    @Shadow @Final private Pool pool;
    @Shadow @Final private Camera camera;
    @Shadow @Final private MinecraftClient client;
    @Unique
    private float strength = 0.0f;

    @Inject(method = "bobView", at = @At("HEAD"), cancellable = true)
    private void bobView(MatrixStack matrices, float tickProgress, CallbackInfo ci) {
        ci.cancel();
        if (!(client.getCameraEntity() instanceof AbstractClientPlayerEntity abstractClientPlayerEntity)) return;
        float var7 = abstractClientPlayerEntity.distanceMoved - abstractClientPlayerEntity.lastDistanceMoved;
        float g = -(abstractClientPlayerEntity.distanceMoved + var7 * tickProgress) / 2F;
        float h = MathHelper.lerp(tickProgress, abstractClientPlayerEntity.lastStrideDistance, abstractClientPlayerEntity.strideDistance);
        matrices.translate(MathHelper.sin(g * (float) Math.PI) * h * 0.5F, -Math.abs(MathHelper.cos(g * (float) Math.PI) * h), 0.0F);
        matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(MathHelper.sin(g * (float) Math.PI) * h * 3.0F));
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(Math.abs(MathHelper.cos(g * (float) Math.PI - 0.2F) * h) * 5.0F));
    }

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

    @ModifyReturnValue(method = "getFov", at = @At("RETURN"))
    private float getFov(float original) {
        return MathHelper.lerp(Main.lerpTime, original, 30);
    }

    @Unique
    private float roll = 0F;

    @Inject(method = "tiltViewWhenHurt", at = @At("HEAD"))
    private void tiltViewWhenHurt(MatrixStack matrices, float tickProgress, CallbackInfo ci) {
        roll = Math.clamp(roll + tickProgress * (Main.wallRunning? Main.rollLeft?3F:-3F : Main.rollLeft?-3F:3F), -30F, 30F);
        if(!Main.wallRunning) {
            if(Main.rollLeft) roll = Math.max(0F, roll);
            else roll = Math.min(0F, roll);
        }
        matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(roll));
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
