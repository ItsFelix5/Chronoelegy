package chronoelegy.mixin;

import chronoelegy.Main;
import net.minecraft.block.PowderSnowBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerAbilities;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity {
    @Shadow public abstract float getMovementSpeed();
    @Shadow public abstract PlayerAbilities getAbilities();
    protected PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Redirect(method = "<clinit>", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/EntityDimensions;withEyeHeight(F)Lnet/minecraft/entity/EntityDimensions;", ordinal = 4))
    private static EntityDimensions withEyeHeight(EntityDimensions instance, float height) {
        return EntityDimensions.changing(0.6F, 0.6F).withEyeHeight(0.4F);
    }

    @Inject(method = "createPlayerAttributes", at = @At("TAIL"))
    private static void createPlayerAttributes(CallbackInfoReturnable<DefaultAttributeContainer.Builder> cir) {
        cir.getReturnValue().add(EntityAttributes.JUMP_STRENGTH, 0.5D).add(EntityAttributes.STEP_HEIGHT, 0.7D);
    }

    @Unique
    private int wallRunTime = 0;

    @Inject(method = "travel", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;getAbilities()Lnet/minecraft/entity/player/PlayerAbilities;"), cancellable = true)
    private void travel(Vec3d movementInput, CallbackInfo ci) {
        if(getPos().y <= Main.minHeight && (Object) this instanceof ServerPlayerEntity spe && (spe.getRespawn() == null || spe.getRespawn().pos().getY() > Main.minHeight)) kill(spe.getServerWorld());
        FluidState fluidState = this.getWorld().getFluidState(this.getBlockPos());
        if (((this.isTouchingWater() || this.isInLava()) && this.shouldSwimInFluids() && !this.canWalkOnFluid(fluidState)) || this.isGliding()) return;
        ci.cancel();

        Vec3d velocity = getVelocity();
        Vec3d horizontalVelocity = velocity.getHorizontal();
        float yawRad = getYaw() * MathHelper.RADIANS_PER_DEGREE;
        float f = MathHelper.sin(yawRad);
        float g = MathHelper.cos(yawRad);
        if (isSneaking()) {
            Main.jumpAttempted--;
            Vec3d forward = new Vec3d(-f, 0, g);

            velocity = velocity.multiply(0.9).add(forward.multiply(horizontalVelocity.length() * 0.05)).subtract(0, getEffectiveGravity() * 3, 0);
            setVelocity(velocity);
            this.move(MovementType.SELF, velocity);
            Main.wallRunning = false;

            if(verticalCollision) this.addVelocity(forward.multiply(Math.max(0, velocity.y - getVelocity().y)));
            return;
        } else if(Main.jumpAttempted > 0) jump();

        boolean moving = movementInput.lengthSquared() > 0;
        if(moving) {
            float speed = this.isOnGround() ? getMovementSpeed() * (0.21600002F / (0.45F * 0.45F * 0.45F)) : 0.075F;
            Vec3d vec3d = (movementInput.lengthSquared() > 1.0 ? movementInput.normalize() : movementInput).multiply(speed * (isSprinting() ? 0.75F : 0.3F));
            Vec3d movement = new Vec3d(vec3d.x * g - vec3d.z * f, 0, vec3d.z * g + vec3d.x * f);


            if (isOnGround() && !movement.equals(Vec3d.ZERO) && !horizontalVelocity.equals(Vec3d.ZERO)) {
                double dot = movement.normalize().dotProduct(horizontalVelocity.normalize());
                if (dot < 0) {
                    Vec3d counterForce = movement.multiply(-dot * 2);
                    movement = movement.add(counterForce);
                }
            }

            if (Main.wallRunning) movement = movement.withAxis(Direction.Axis.Y, -0.03);

            double scaleFactor = Math.min(1 / horizontalVelocity.lengthSquared(), 1);
            velocity = velocity.add(movement).multiply(scaleFactor, 1, scaleFactor);
        } else velocity = velocity.multiply(0.85, 1, 0.85);
        if (!getAbilities().flying && !Main.wallRunning) velocity = velocity.subtract(0, getEffectiveGravity(), 0);

        if(Main.grapplePoint != null) {
            Vec3d diff = Main.grapplePoint.subtract(getEyePos()).normalize();
            velocity = velocity.add(diff.multiply(0.2));
            velocity = velocity.normalize().multiply(Math.min(velocity.length(), 1.5));
        }

        if(getAbilities().flying) velocity = velocity.multiply(1, 0.6, 1);

        setVelocity(velocity);
        this.move(MovementType.SELF, velocity);

        Vec3d collision = velocity.subtract(getVelocity());
        MinecraftClient client = MinecraftClient.getInstance();
        if(!(client.options.leftKey.isPressed() || client.options.rightKey.isPressed()) || moving) if(!verticalCollision) {
            double sidewaysCollision = collision.x * g + collision.z * f;
            if (sidewaysCollision > 0 && client.options.leftKey.isPressed()) {
                Main.rollLeft = true;
                Main.wallRunning = true;
                wallRunTime = 3;
            } else if (sidewaysCollision < 0 && client.options.rightKey.isPressed()) {
                Main.rollLeft = false;
                Main.wallRunning = true;
                wallRunTime = 3;
            } else if(--wallRunTime < 0) Main.wallRunning = false;
        } else if(--wallRunTime < 0) Main.wallRunning = false;

        if ((this.horizontalCollision || jumping) && (isClimbing() || this.wasInPowderSnow && PowderSnowBlock.canWalkOnPowderSnow(this)))
            setVelocity(getVelocity().withAxis(Direction.Axis.Y, 0.2));
    }

    @Inject(method = "adjustMovementForSneaking", at = @At("HEAD"), cancellable = true)
    private void adjustMovementForSneaking(Vec3d movement, MovementType type, CallbackInfoReturnable<Vec3d> cir) {
        cir.setReturnValue(movement);
    }
}
