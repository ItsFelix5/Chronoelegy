package chronoelegy.block;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.BlockView;

public class JumpPadBlock extends Block {
    public JumpPadBlock(Settings settings) {
        super(settings);
    }

    @Override
    public void onEntityLand(BlockView world, Entity entity) {
        float yaw = entity.getYaw() * MathHelper.RADIANS_PER_DEGREE;
        entity.addVelocity(-MathHelper.sin(yaw) * 5, 1.0, MathHelper.cos(yaw) * 5);
    }
}
