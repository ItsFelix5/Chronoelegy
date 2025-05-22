package chronoelegy.screen;

import chronoelegy.Main;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWImage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

import static chronoelegy.Main.client;

public class Cursors {
    private static final Map<String, Long> cursors = new HashMap<>();

    public static long get(String name) {
        return cursors.computeIfAbsent(name, key->{
            try {
                BufferedImage image = ImageIO.read(client.getResourceManager().open(Main.id("textures/cursor/"+key+".png")));
                int w = image.getWidth();
                int h = image.getHeight();
                int[] pixels = new int[w * h];
                image.getRGB(0, 0, w, h, pixels, 0, w);
                ByteBuffer buffer = BufferUtils.createByteBuffer(w * h * 4);
                for (int y = h - 1; y >= 0; y--)
                    for (int x = 0; x < w; x++) {
                        int pixel = pixels[(h - 1 - y) * w + x];
                        buffer.put((byte) ((pixel >> 16) & 0xFF));
                        buffer.put((byte) ((pixel >> 8) & 0xFF));
                        buffer.put((byte) (pixel & 0xFF));
                        buffer.put((byte) ((pixel >> 24) & 0xFF));
                    }
                buffer.flip();
                // These are never cleaned up but eh
                return GLFW.glfwCreateCursor(GLFWImage.create().pixels(buffer).width(w).height(h), 0, 0);
            } catch (IOException | NullPointerException e) {
                e.printStackTrace();
                return 0L;
            }
        });
    }
}
