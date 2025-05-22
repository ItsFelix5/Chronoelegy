package chronoelegy.mixin;

import chronoelegy.screen.Dialogue;
import chronoelegy.screen.DialogueScreen;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static chronoelegy.Main.client;

@Mixin(MobEntity.class)
public abstract class MobEntityMixin extends LivingEntity {
    @Shadow protected abstract float changeAngle(float from, float to, float max);

    protected MobEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Unique
    private Entity inDialogueWith;

    @WrapOperation(method = "interact", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;interact(Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/util/Hand;)" +
            "Lnet/minecraft/util/ActionResult;"))
    private ActionResult interact(MobEntity instance, PlayerEntity playerEntity, Hand hand, Operation<ActionResult> original) {
        ActionResult result = original.call(instance, playerEntity, hand);
        if(result != ActionResult.PASS) return result;

        inDialogueWith = playerEntity;
        client.executeSync(()->client.setScreen(new DialogueScreen(Dialogue.create(true, Text.literal("huhh"))
                .option(Text.literal("continue"), new Dialogue(Text.literal("hi"))
                .option(Text.literal("Hi?"), new Dialogue(Text.literal("Hellooo!")).ending(Text.literal("Okay")))
                .ending(Text.literal("Run"))), ()->inDialogueWith = null)));
        return ActionResult.SUCCESS;
    }

    @Inject(method = "tickMovement", at = @At("HEAD"), cancellable = true)
    private void tickMovement(CallbackInfo ci) {
        if(inDialogueWith == null || client.isOnThread()) return;
        ci.cancel();

        Vec3d vec = inDialogueWith.getEyePos().subtract(getEyePos());

        if((Math.abs(vec.z) > 1.0E-5F) || (Math.abs(vec.x) > 1.0E-5F)) headYaw = changeAngle(headYaw, (float)(MathHelper.atan2(vec.z, vec.x) * 180.0F / (float)Math.PI) - 90.0F, 10);

        double g = Math.sqrt(vec.x * vec.x + vec.z * vec.z);
        if(Math.abs(vec.y) > 1.0E-5F || Math.abs(g) > 1.0E-5F) setPitch(changeAngle(getPitch(), (float)(-(MathHelper.atan2(vec.y, g) * 180.0F / (float)Math.PI)), 40));
    }
}
