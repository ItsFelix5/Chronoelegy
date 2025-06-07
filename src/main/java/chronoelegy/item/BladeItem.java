package chronoelegy.item;

import chronoelegy.Main;
import chronoelegy.Rendering;
import chronoelegy.block.entity.GrapplePointBlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import net.minecraft.world.chunk.WorldChunk;

import java.util.ArrayList;

public class BladeItem extends Item {
    public BladeItem(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult use(World world, PlayerEntity user, Hand hand) {
        if(!world.isClient) return ActionResult.FAIL;
        ChunkPos chunkPos = user.getChunkPos();

        ArrayList<Vec3d> points = new ArrayList<>();

        for (int i = 0; i < 9; i++) {
            WorldChunk chunk = world.getChunk(chunkPos.x + i % 3 - 1, chunkPos.z + i / 3 - 1);
            if (chunk == null) continue;

            chunk.getBlockEntities().forEach((pos, entity) -> {
                if (entity instanceof GrapplePointBlockEntity && Rendering.getFrustum().isVisible(Box.from(Vec3d.of(pos))) && world.raycast(new RaycastContext(user.getEyePos(), Vec3d.of(pos),
                        RaycastContext.ShapeType.VISUAL, RaycastContext.FluidHandling.NONE, user)).getType() != HitResult.Type.BLOCK) points.add(pos.toCenterPos().add(0, 0.3, 0));
            });
        }

        if (points.isEmpty()) return ActionResult.FAIL;

        Vec3d rotationVector = user.getRotationVector();
        Vec3d eyePos = user.getEyePos();
        Vec3d closest = null;
        double smallestAngle = Double.MAX_VALUE;
        for (Vec3d point : points) {
            double angle = Math.acos(rotationVector.dotProduct(point.subtract(0, 1, 0).subtract(eyePos).normalize()));
            if (angle < smallestAngle) {
                smallestAngle = angle;
                closest = point;
            }
        }
        Main.grapplePoint = closest;
        user.setCurrentHand(hand);

        return ActionResult.FAIL;
    }

    @Override
    public boolean onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        Main.grapplePoint = null;
        return false;
    }

    @Override
    public float getBonusAttackDamage(Entity target, float baseAttackDamage, DamageSource damageSource) {
        return super.getBonusAttackDamage(target, baseAttackDamage, damageSource);
    }
}
