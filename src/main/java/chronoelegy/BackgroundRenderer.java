package chronoelegy;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ColorHelper;
import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.Random;

public class BackgroundRenderer {
    private static final Identifier MOON_TEXTURE = Main.id("textures/gui/title/background/moon.png");
    private static final Identifier STAR_TEXTURE = Main.id("textures/gui/title/background/stars.png");
    private static final Identifier MOUNTAIN_TEXTURE = Main.id("textures/gui/title/background/mountains.png");
    private static final Random random = new Random();
    private static final ArrayList<Star> stars = new ArrayList<>();
    private static final ArrayList<Mountain> mountains = new ArrayList<>();

    public static void render(DrawContext context, int width, int height, float delta) {
        if(stars.isEmpty()) {
            for (int i = 0; i < 100; i++) {
                stars.add(new Star(random.nextFloat(-0.1F, 1F), linearRandom() / 1000F, random.nextInt(16),
                        random.nextInt(1, 7), linearRandom() / 10000000F, random.nextFloat(-1, 1)));
            }
            for (int i = 0; i < 10; i++) {
                mountains.add(new Mountain(random.nextFloat(-0.5F, 1F), random.nextInt(5,16), random.nextInt(4)));
            }
        }
        context.fillGradient(0, 0, width, height / 2, 0xFF132233, 0xFF232f44);
        context.fillGradient(0, height / 2, width, height - height / 3, 0xFF232f44, 0xFF413c60);
        context.fillGradient(0, height - height / 3, width, height, 0xFF413c60, 0xFF976b8f);
        context.drawTexture(RenderLayer::getGuiTextured, MOON_TEXTURE, width - width / 7, 15, 0.0F, 0.0F, 38, 38, 38, 38, 38, 38);

        stars.forEach(s->{
            int color = ColorHelper.getWhite(Math.abs(s.twinkle));

            RenderLayer renderLayer = RenderLayer.getGuiTextured(STAR_TEXTURE);
            Matrix4f matrix4f = context.getMatrices().peek().getPositionMatrix();
            context.draw(vertexConsumers->{
                VertexConsumer vertexConsumer = vertexConsumers.getBuffer(renderLayer);
                float u = Math.floorDiv(s.i, 4) / 4F;
                float v = (s.i % 4F) / 4F;
                vertexConsumer.vertex(matrix4f, s.x * width, s.y * height, 0.0F).texture(u, v).color(color);
                vertexConsumer.vertex(matrix4f, s.x * width, s.y * height + s.size, 0.0F).texture(u, v + 0.25F).color(color);
                vertexConsumer.vertex(matrix4f, s.x * width + s.size, s.y * height + s.size, 0.0F).texture(u + 0.25F, v + 0.25F).color(color);
                vertexConsumer.vertex(matrix4f, s.x * width + s.size, s.y * height, 0.0F).texture(u + 0.25F, v).color(color);
            });

            s.x += delta * s.speed;
            if(s.x > 1F) s.x = -0.1F;
            s.twinkle += delta / 100;
            if(s.twinkle > 1) s.twinkle -= 2;
        });

        for (int i = 0; i < mountains.size(); i++) {
            Mountain m = mountains.get(i);
            RenderLayer renderLayer = RenderLayer.getGuiTextured(MOUNTAIN_TEXTURE);
            Matrix4f matrix4f = context.getMatrices().peek().getPositionMatrix();
            context.draw(vertexConsumers->{
                VertexConsumer vertexConsumer = vertexConsumers.getBuffer(renderLayer);
                float v = m.i / 4F;
                float x = m.x * width;
                float size = m.z * 5;
                float y = height - size;
                vertexConsumer.vertex(matrix4f, x, y, 0).texture(0, v).color(-1);
                vertexConsumer.vertex(matrix4f, x, y + size, 0).texture(0, v + 0.25F).color(-1);
                vertexConsumer.vertex(matrix4f, x + size * 3, y + size, 0).texture(1, v + 0.25F).color(-1);
                vertexConsumer.vertex(matrix4f, x + size * 3, y, 0).texture(1, v).color(-1);
            });

            m.x += delta / 500 / m.z;
            if(m.x > 1) {
                mountains.remove(i);
                i--;
                mountains.add(new Mountain(-0.5F, random.nextInt(5,16), random.nextInt(4)));
            }
        }
    }

    private static int linearRandom() {
        int randomInt = random.nextInt(500500);
        int result = 0;
        for(int j = 1000; randomInt >= 0; randomInt -= --j) result++;

        return result;
    }

    private static class Star {
        private float x;
        private final float y;
        private final int i;
        private final int size;
        private final float speed;
        private float twinkle;

        public Star(float x, float y, int i, int size, float speed, float twinkle) {
            this.x = x;
            this.y = y;
            this.i = i;
            this.size = size;
            this.speed = speed;
            this.twinkle = twinkle;
        }
    }

    private static class Mountain {
        private float x;
        private final int z;
        private final int i;

        public Mountain(float x, int z, int i) {
            this.x = x;
            this.z = z;
            this.i = i;
        }
    }
}
