package chronoelegy.mixin;

import chronoelegy.Main;
import chronoelegy.Rendering;
import chronoelegy.item.ModItems;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Arm;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HeldItemRenderer.class)
public abstract class HeldItemRendererMixin {
    @Shadow public abstract void renderItem(LivingEntity entity, ItemStack stack, ItemDisplayContext renderMode, MatrixStack matrices, VertexConsumerProvider vertexConsumer, int light);

    @Unique
    private float rotX, rotY;

    @Inject(method = "renderFirstPersonItem", at = @At("HEAD"), cancellable = true)
    public void renderFirstPersonItem(AbstractClientPlayerEntity player, float tickProgress, float pitch, Hand hand, float swingProgress, ItemStack item, float equipProgress, MatrixStack matrices,
                                      VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci) {
        if (!item.isOf(ModItems.BLADE)) return;
        ci.cancel();

        matrices.push();
        boolean leftHand = (player.getMainArm() == Arm.LEFT) == (hand == Hand.MAIN_HAND);
        matrices.translate(leftHand ? -0.56F : 0.56F, -0.52F + equipProgress * -0.6F, -0.72F);

        //todo

        if(Main.grapplePoint == null) {
            rotX = MathHelper.lerp(0.3F, rotX, (float) 0);
            rotY = MathHelper.lerp(0.3F, rotY, (float) 0);
        } else {
            Vec3d dir = Main.grapplePoint.add(0.5, 0.8, 0.5).subtract(player.getEyePos()).normalize();

            rotX = MathHelper.lerp(0.7F, rotX, (float) Math.acos(-dir.y));
            rotY = MathHelper.lerp(0.7F, rotY, (float) -Math.atan2(dir.x, -dir.z));
        }

        matrices.multiply(RotationAxis.POSITIVE_X.rotation(rotX));
        matrices.multiply(RotationAxis.POSITIVE_Y.rotation(rotY));

        this.renderItem(player, item, leftHand ? ItemDisplayContext.FIRST_PERSON_LEFT_HAND : ItemDisplayContext.FIRST_PERSON_RIGHT_HAND, matrices, vertexConsumers, light);

        if(Main.grapplePoint != null) {
            Vec3d dir = Main.grapplePoint.add(0.5, 0.8, 0.5).subtract(player.getPos()).normalize();

            VertexConsumer buffer = vertexConsumers.getBuffer(Rendering.GRAPPLE_ROPE);
            MatrixStack.Entry entry = matrices.peek();
            buffer.vertex(entry, 0F, -0.09375F, 0F).color(0xaa003dff).normal(entry, -1F, 0F, 0F);
            buffer.vertex((float)dir.x, (float)dir.y, (float)dir.z).color(0xaa003dff).normal(entry, -1F, 0F, 0F);
        }

        matrices.pop();
    }
}
