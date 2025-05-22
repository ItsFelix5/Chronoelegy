package chronoelegy.block.entity;

import chronoelegy.block.ModBlocks;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;

public class ClockBlockEntity extends BlockEntity {
    public ClockBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlocks.CLOCK_BLOCK_ENTITY, pos, state);
    }
}
