package chronoelegy.mixin;

import chronoelegy.Main;
import net.minecraft.client.render.Camera;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Camera.class)
public abstract class CameraMixin {
    @Shadow protected abstract void setPos(Vec3d pos);
    @Shadow private Vec3d pos;

    @Shadow protected abstract void setRotation(float yaw, float pitch);
    @Shadow private float yaw;
    @Shadow private float pitch;

    @Inject(method = "update", at = @At("TAIL"))
    private void update(BlockView area, Entity focusedEntity, boolean thirdPerson, boolean inverseView, float tickProgress, CallbackInfo ci) {
        Main.lerpTime = Math.clamp(Main.lerpTime + (Main.customCamera?0.05F:-0.05F), 0F, 1F);
        if(Main.lerpTime == 0F) return;
        setPos(pos.lerp(Main.cameraPos, Main.lerpTime));
        setRotation(MathHelper.lerpAngleDegrees(Main.lerpTime, yaw, Main.cameraYaw), MathHelper.lerpAngleDegrees(Main.lerpTime, pitch, 90F));
    }
}
