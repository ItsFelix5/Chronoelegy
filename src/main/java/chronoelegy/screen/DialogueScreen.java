package chronoelegy.screen;

import chronoelegy.Main;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.Window;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;
import net.minecraft.util.Language;
import net.minecraft.util.Pair;
import org.joml.Matrix4f;
import org.lwjgl.glfw.GLFW;

import java.util.List;

public class DialogueScreen extends Screen {
    private final boolean skippable;
    private Dialogue dialogue;
    private final Runnable onClose;
    private int optionsWidth = 0;
    private int selected = 0;

    public DialogueScreen(Dialogue dialogue, Runnable onClose) {
        super(Text.empty());
        assert dialogue instanceof Dialogue.Root;
        skippable = ((Dialogue.Root) dialogue).skippable;
        this.dialogue = dialogue;
        this.onClose = onClose;
    }

    @Override
    protected void init() {
        optionsWidth = 0;
        for (Pair<Text, Dialogue> option : dialogue.options) optionsWidth = Math.max(optionsWidth, textRenderer.getWidth(option.getLeft()));
        optionsWidth += width / 10;
        GLFW.glfwSetCursor(client.getWindow().getHandle(), Cursors.get("arrow"));
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        int padding = width / 10;
        int halfPad = width / 20;
        int height = this.height - this.height / 4;

        context.fill(0, height, this.width, this.height, 0xBB000000);
        context.fillGradient(0, height - padding - padding, this.width, height, 0, 0xBB000000);

        List<StringVisitable> lines = client.textRenderer.getTextHandler().wrapLines(dialogue.text, width - padding * 2 - optionsWidth, Style.EMPTY);
        for (int i = 0; i < lines.size(); i++) {
            context.drawText(textRenderer, Language.getInstance().reorder(lines.get(i)), padding, height + i * 9, Colors.WHITE, true);
        }

        context.getMatrices().push();
        MatrixStack.Entry matrices = context.getMatrices().peek();
        matrices.translate(width - optionsWidth - halfPad, height + selected * 15 - 3F, 0);
        context.draw(vertexConsumers -> {
            Sprite sprite = client.getGuiAtlasManager().getSprite(Main.id("arrow"));
            VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getGuiTextured(sprite.getAtlasId()));
            Matrix4f matrix4f = matrices.getPositionMatrix();

            vertexConsumer.vertex(matrix4f, 7.5F, 0F, 0F).texture(sprite.getMinU(), sprite.getMinV()).color(Colors.YELLOW);
            vertexConsumer.vertex(matrix4f, 0F, 7.5F, 0F).texture(sprite.getMinU(), sprite.getMaxV()).color(Colors.YELLOW);
            vertexConsumer.vertex(matrix4f, 7.5F, 15F, 0F).texture(sprite.getMaxU(), sprite.getMaxV()).color(Colors.YELLOW);
            vertexConsumer.vertex(matrix4f, 15F, 7.5F, 0F).texture(sprite.getMaxU(), sprite.getMinV()).color(Colors.YELLOW);
        });
        context.getMatrices().pop();

        for (int i = 0; i < dialogue.options.size(); i++) {
            Text option = dialogue.options.get(i).getLeft();
            context.drawText(textRenderer, option, width - optionsWidth, height + i * 15, i == selected? Colors.YELLOW:Colors.WHITE, true);
        }
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        switch (keyCode) {
            case GLFW.GLFW_KEY_ESCAPE:
                if (skippable) {
                    close();
                    return true;
                }
                break;
            case GLFW.GLFW_KEY_UP:
            case GLFW.GLFW_KEY_RIGHT:
                selected = (selected + dialogue.options.size() - 1) % dialogue.options.size();
                return true;
            case GLFW.GLFW_KEY_DOWN:
            case GLFW.GLFW_KEY_LEFT:
                selected = (selected + 1) % dialogue.options.size();
                return true;
            case GLFW.GLFW_KEY_ENTER:
            case GLFW.GLFW_KEY_SPACE:
                chooseOption();
                return true;
        }
        return false;
    }

    @Override
    public void mouseMoved(double mouseX, double mouseY) {
        int optionsEnd = width - width / 20;
        int height = this.height - this.height / 4;
        if (mouseX < optionsEnd - optionsWidth || mouseX > optionsEnd || mouseY < height) return;

        for (int i = 0; i < dialogue.options.size(); i++) {
            if (mouseY < height + i * 15 + 15) {
                selected = i;
                return;
            }
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int optionsEnd = width - width / 20;
        int height = this.height - this.height / 4;
        if (mouseX < optionsEnd - optionsWidth || mouseX > optionsEnd || mouseY < height) return false;

        for (int i = 0; i < dialogue.options.size(); i++) {
            if (mouseY < height + i * 15 + 15) {
                selected = i;
                chooseOption();
                return true;
            }
        }
        return false;
    }

    private void chooseOption() {
        dialogue = dialogue.options.get(selected).getRight();
        if(dialogue == null) {
            close();
            return;
        }
        dialogue.cb.run();
        optionsWidth = 0;
        for (Pair<Text, Dialogue> option : dialogue.options) optionsWidth = Math.max(optionsWidth, textRenderer.getWidth(option.getLeft()));
        optionsWidth += width / 10;
        selected = 0;
        Window window = client.getWindow();
        mouseMoved(client.mouse.getScaledX(window), client.mouse.getScaledY(window));
    }

    @Override
    public void close() {
        onClose.run();
        GLFW.glfwSetCursor(client.getWindow().getHandle(), 0L);
        super.close();
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}
