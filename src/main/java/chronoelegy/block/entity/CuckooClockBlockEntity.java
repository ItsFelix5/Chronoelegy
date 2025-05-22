package chronoelegy.block.entity;

import chronoelegy.block.ModBlocks;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;

public class CuckooClockBlockEntity extends BlockEntity {
    public CuckooClockBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlocks.CUCKOO_CLOCK_BLOCK_ENTITY, pos, state);
    }
}
