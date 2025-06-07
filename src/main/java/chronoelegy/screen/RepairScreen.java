package chronoelegy.screen;

import chronoelegy.Main;
import chronoelegy.block.entity.TableBlockEntity;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.texture.Sprite;
import net.minecraft.item.ItemStack;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec2f;
import org.joml.Matrix4f;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;

public class RepairScreen extends Screen {
    private final TableBlockEntity entity;
    private final ArrayList<Part> parts = new ArrayList<>();
    private int dragging = -1;
    private Vec2f dragOffset;

    public RepairScreen(TableBlockEntity entity) {
        super(Text.empty());
        this.entity = entity;
    }

    @Override
    protected void init() {
        GLFW.glfwSetCursor(client.getWindow().getHandle(), Cursors.get("hand"));

        Main.cameraPos = entity.getPos().toCenterPos().add(0, 2, 0);
        Main.cameraYaw = (entity.getCachedState().get(Properties.HORIZONTAL_FACING).getHorizontalQuarterTurns() * 90 + 180) % 360;
        Main.customCamera = true;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        for (int i = parts.size() - 1; i >= 0; i--) {
            Part part = parts.get(i);
            if(! part.draggable) continue;
            if(mouseX > part.x && mouseX <= part.x + part.size && mouseY > part.y && mouseY <= part.y + part.size) {
                dragging = i;
                dragOffset = new Vec2f((float) mouseX - part.x, (float) mouseY - part.y);
                GLFW.glfwSetCursor(client.getWindow().getHandle(), Cursors.get("drag"));
                return true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if(dragging != -1) GLFW.glfwSetCursor(client.getWindow().getHandle(), Cursors.get("hand"));
        dragging = -1;
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if(dragging == -1) return false;
        Part part = parts.get(dragging);
        part.x = Math.clamp((int) (mouseX - dragOffset.x), 0, width);
        part.y = Math.clamp((int) (mouseY - dragOffset.y), 0, height);
        return true;
    }

    @Override
    protected void refreshWidgetPositions() {
        for(Part part : parts) {
            part.x = Math.clamp(part.x, 0, width);
            part.y = Math.clamp(part.y, 0, height);
        }
    }

    @Override
    public void close() {
        super.close();
        GLFW.glfwSetCursor(client.getWindow().getHandle(), 0L);
        Main.customCamera = false;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        if(Main.lerpTime == 1F && parts.isEmpty()) {
            entity.setItem(ItemStack.EMPTY);

            parts.add(new Part(0, 0, 140, "front"));
            parts.add(new Part(0, 5, 40, "broken_gear"));
            parts.add(new Part(10, 0, 30, "gear2"));
            parts.add(new Part(11, 20, 40, "gear3"));
            parts.add(new Part(22, 12, 30, "gear1"));
            parts.add(new Part(10, 0, 30, "gear2"));
            parts.add(new Part(16, 20, 40, "gear3"));
            parts.add(new Part(0, 21, 30, "gear1"));
            parts.add(new Part(0, 0, 140, "back"));
            parts.getFirst().draggable = false;
        } else parts.forEach(p->p.render(context));
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    private class Part {
        public float x, y;
        public final float size;
        private final Sprite sprite;
        public boolean draggable = true;

        public Part(float x, float y, int size, String name) {
            this.x = width / 2F - size / 2F + x;
            this.y = height / 2F - size / 2F + y;
            this.size = size;
            sprite = client.getGuiAtlasManager().getSprite(Main.id("parts/" + name));
        }

        public void render(DrawContext context) {
            context.draw(vertexConsumerProvider -> {
                VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(RenderLayer.getGuiTextured(sprite.getAtlasId()));
                Matrix4f matrix4f = context.getMatrices().peek().getPositionMatrix();
                vertexConsumer.vertex(matrix4f, x, y, 0F).texture(sprite.getMinU(), sprite.getMinV()).color(-1);
                vertexConsumer.vertex(matrix4f, x, y + size, 0F).texture(sprite.getMinU(), sprite.getMaxV()).color(-1);
                vertexConsumer.vertex(matrix4f, x + size, y + size, 0F).texture(sprite.getMaxU(), sprite.getMaxV()).color(-1);
                vertexConsumer.vertex(matrix4f, x + size, y, 0F).texture(sprite.getMaxU(), sprite.getMinV()).color(-1);
            });
        }
    }
}
