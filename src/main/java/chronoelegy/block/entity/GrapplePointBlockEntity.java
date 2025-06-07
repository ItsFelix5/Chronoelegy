package chronoelegy.block.entity;

import chronoelegy.block.ModBlocks;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;

public class GrapplePointBlockEntity extends BlockEntity {
    public GrapplePointBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlocks.GRAPPLE_POINT_BLOCK_ENTITY, pos, state);
    }
}
