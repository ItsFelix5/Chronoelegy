package chronoelegy;

import chronoelegy.block.ModBlocks;
import chronoelegy.item.ModItems;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;

public class Main implements ModInitializer {
    public static boolean transparentItems = false;
    public static boolean customCamera = false;
    public static Vec3d cameraPos = null;
    public static float cameraYaw = 0F;
    public static float lerpTime = 0F;
    public static boolean rollLeft;
    public static boolean wallRunning = false;
    public static Vec3d grapplePoint;
    public static int jumpAttempted;
    public static int minHeight = -64;
    public static boolean speedrunMode;
    public static int ticks;

    public static Identifier id(String path) {
        return Identifier.of("chronoelegy", path);
    }

    @Override
    public void onInitialize() {
        ModBlocks.init();
        ModItems.init();

        ServerTickEvents.END_WORLD_TICK.register(world -> ticks++);
    }
}