package chronoelegy.mixin;

import chronoelegy.Main;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class EntityMixin {
    @Shadow public abstract float getYaw();
    @Shadow private BlockPos blockPos;
    @Shadow public boolean verticalCollision;
    @Shadow private World world;
    @Shadow private boolean onGround;
    @Shadow public boolean horizontalCollision;
    @Shadow protected abstract void updateSupportingBlockPos(boolean onGround, @Nullable Vec3d movement);

    @ModifyReturnValue(method = "shouldSpawnSprintingParticles", at = @At("TAIL"))
    private boolean shouldSpawnSprintingParticles(boolean original) {
        return original && !world.getTickManager().isFrozen();
    }

    @Inject(method = "setMovement(ZZLnet/minecraft/util/math/Vec3d;)V", at = @At("HEAD"), cancellable = true)
    private void setMovement(boolean onGround, boolean horizontalCollision, Vec3d movement, CallbackInfo ci) {
        if(this.onGround && !onGround) {
            ci.cancel();
            this.horizontalCollision = horizontalCollision;
            this.updateSupportingBlockPos(false, movement);
        }
    }

    @Inject(method = "getSteppingPos", at = @At("HEAD"), cancellable = true)
    private void getSteppingPos(CallbackInfoReturnable<BlockPos> cir) {//TODO
        if((Object) this instanceof PlayerEntity && Main.wallRunning) {
            float yaw = ((getYaw() + (Main.rollLeft?-90:90))) * MathHelper.RADIANS_PER_DEGREE;
            cir.setReturnValue(blockPos.add((int) -MathHelper.sin(yaw), 0, (int) MathHelper.cos(yaw)));
        }
    }

    @Inject(method = "move", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;setMovement(ZZLnet/minecraft/util/math/Vec3d;)V"))
    private void move(MovementType type, Vec3d movement, CallbackInfo ci, @Local(ordinal = 1) Vec3d vec3d) {
        verticalCollision = !MathHelper.approximatelyEquals(movement.y, vec3d.y);
    }

    @Redirect(method = "adjustMovementForCollisions(Lnet/minecraft/util/math/Vec3d;)Lnet/minecraft/util/math/Vec3d;", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;isOnGround()Z"))
    private boolean isOnGround(Entity instance) {
        return true;
    }
}
