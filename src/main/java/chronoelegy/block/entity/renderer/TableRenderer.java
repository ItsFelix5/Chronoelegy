package chronoelegy.block.entity.renderer;

import chronoelegy.Main;
import chronoelegy.block.entity.TableBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.property.Properties;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;

public class TableRenderer implements BlockEntityRenderer<TableBlockEntity> {
    private final BlockEntityRendererFactory.Context context;

    public TableRenderer(BlockEntityRendererFactory.Context context) {
        this.context = context;
    }

    @Override
    public void render(TableBlockEntity entity, float tickProgress, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, Vec3d cameraPos) {
        MinecraftClient client = MinecraftClient.getInstance();
        ItemStack stack = entity.getItem();
        if(stack.isEmpty()) {
            if(client.crosshairTarget instanceof BlockHitResult target && entity.getPos().equals(target.getBlockPos()) && !client.player.getMainHandStack().isEmpty()) {
                stack = client.player.getMainHandStack();
                Main.transparentItems = true;
            } else return;
        }
        matrices.translate(0.5, 0.9, 0.5);
        matrices.scale(0.5F, 0.5F, 0.5F);
        matrices.multiply(RotationAxis.NEGATIVE_Y.rotationDegrees(entity.getCachedState().get(Properties.HORIZONTAL_FACING).getHorizontalQuarterTurns() * 90));
        matrices.multiply(RotationAxis.NEGATIVE_X.rotationDegrees(90));
        context.getItemRenderer().renderItem(stack, ItemDisplayContext.FIXED, light, overlay, matrices, vertexConsumers, entity.getWorld(), 0);
        Main.transparentItems = false;
    }
}
