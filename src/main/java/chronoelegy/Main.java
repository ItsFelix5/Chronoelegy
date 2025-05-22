package chronoelegy;

import chronoelegy.block.ModBlocks;
import chronoelegy.item.ModItems;
import net.fabricmc.api.ModInitializer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;

public class Main implements ModInitializer {
    public static final MinecraftClient client = MinecraftClient.getInstance();
    public static boolean transparentItems = false;
    public static boolean customCamera = false;
    public static Vec3d cameraPos = null;
    public static float cameraYaw = 0F;
    public static float lerpTime = 0F;

    public static Identifier id(String path) {
        return Identifier.of("chronoelegy", path);
    }

    @Override
    public void onInitialize() {
        ModBlocks.init();
        ModItems.init();
    }
}