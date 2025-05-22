package chronoelegy.block.entity.renderer;

import chronoelegy.Rendering;
import chronoelegy.block.entity.CuckooClockBlockEntity;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;

public class CuckooClockRenderer implements BlockEntityRenderer<CuckooClockBlockEntity> {
    public CuckooClockRenderer(BlockEntityRendererFactory.Context context) {
    }

    @Override
    public void render(CuckooClockBlockEntity entity, float tickProgress, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, Vec3d cameraPos) {
        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(Rendering.SOLID_COLOR);
        Matrix4f matrix4f = matrices.peek().getPositionMatrix();

        matrices.translate(0.5, 0, 0.5);
        matrices.multiply(RotationAxis.NEGATIVE_Y.rotation((entity.getCachedState().get(Properties.HORIZONTAL_FACING).getHorizontalQuarterTurns()-1) * MathHelper.HALF_PI));

        int time = (int) entity.getWorld().getTimeOfDay();
        float hour = time / 12000F - 0.5F;
        float minute = time / 1000F;

        matrices.translate(-0.46, 0.375, 0);

        matrices.multiply(RotationAxis.POSITIVE_X.rotation(minute * MathHelper.TAU));
        vertexConsumer.vertex(matrix4f, 0F, -0.005F, 0.025F).light(light).color(0xFF291b0b);
        vertexConsumer.vertex(matrix4f, 0F, 0.12F, 0.025F).light(light).color(0xFF291b0b);
        vertexConsumer.vertex(matrix4f, 0F, 0.12F, -0.025F).light(light).color(0xFF291b0b);
        vertexConsumer.vertex(matrix4f, 0F, -0.005F, -0.025F).light(light).color(0xFF291b0b);
        matrices.multiply(RotationAxis.POSITIVE_X.rotation(-minute * MathHelper.TAU));

        matrices.translate(-0.005, 0, -0.05F);

        matrices.translate(0, 0.005F, 0.05F);
        matrices.multiply(RotationAxis.POSITIVE_X.rotation(hour * MathHelper.TAU));
        matrices.translate(0, 0, -0.05F);

        vertexConsumer.vertex(matrix4f, 0F, -0.005F, 0.075F).light(light).color(0xFF291b0b);
        vertexConsumer.vertex(matrix4f, 0F, 0.08F, 0.075F).light(light).color(0xFF291b0b);
        vertexConsumer.vertex(matrix4f, 0F, 0.08F, 0.025F).light(light).color(0xFF291b0b);
        vertexConsumer.vertex(matrix4f, 0F, -0.005F, 0.025F).light(light).color(0xFF291b0b);
    }
}
