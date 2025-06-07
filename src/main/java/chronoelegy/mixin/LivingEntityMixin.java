package chronoelegy.mixin;

import chronoelegy.Main;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {
    @Shadow protected abstract float getJumpVelocity();
    @Shadow protected abstract void playBlockFallSound();

    public LivingEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @WrapOperation(method = "tickMovement", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;isOnGround()Z"))
    private boolean onGround(LivingEntity instance, Operation<Boolean> original) {
        return original.call(instance) || (instance instanceof PlayerEntity && Main.wallRunning);
    }

    @Inject(method = "jump", at = @At("HEAD"), cancellable = true)
    private void jump(CallbackInfo ci) {
        if (!((Object) this instanceof PlayerEntity player)) return;
        ci.cancel();

        Main.jumpAttempted = 0;
        setOnGround(false);

        if (Main.wallRunning) {
            float yaw = ((this.getYaw() + (Main.rollLeft? -90 : 90) + 180) % 360) * MathHelper.RADIANS_PER_DEGREE;
            addVelocity(-MathHelper.sin(yaw), 0.3, MathHelper.cos(yaw));
            return;
        }

        if (player.isSneaking()) {
            Main.jumpAttempted = 10;
            return;
        }

        this.addVelocity(0, this.getJumpVelocity(), 0);
        if (this.isSprinting()) {
            float yaw = this.getYaw() * MathHelper.RADIANS_PER_DEGREE;
            this.addVelocity(new Vec3d(-MathHelper.sin(yaw) * 0.2, 0.0, MathHelper.cos(yaw) * 0.2));
        }
    }

    @Inject(method = "handleFallDamage", at = @At("HEAD"), cancellable = true)
    private void handleFallDamage(double fallDistance, float damagePerDistance, DamageSource damageSource, CallbackInfoReturnable<Boolean> cir) {
        if((Object) this instanceof PlayerEntity) {
            this.playBlockFallSound();
            cir.setReturnValue(false);
        }
    }
}
