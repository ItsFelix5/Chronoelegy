package chronoelegy.block;

import chronoelegy.block.entity.CuckooClockBlockEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.DirectionTransformation;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import org.jetbrains.annotations.Nullable;

public class CuckooClockBlock extends Block implements BlockEntityProvider{
    private static final VoxelShape SHAPE_Z = VoxelShapes.union(
            VoxelShapes.cuboid(0, 0, 0, 1, 0.125, 1),
            VoxelShapes.cuboid(0.1875, 0.125, 0.0625, 0.8125, 0.4375, 0.9375),
            VoxelShapes.cuboid(0.125, 0.4375, 0.0625, 0.875, 0.8125, 0.9375),
            VoxelShapes.cuboid(0.1875, 0.8125, 0.0625, 0.8125, 0.9375, 0.9375),
            VoxelShapes.cuboid(0.3125, 0.9375, 0.0625, 0.6875, 1.0625, 0.9375),
            VoxelShapes.cuboid(0.4375, 1.0625, 0.0625, 0.5625, 1.125, 0.9375)
    );
    private static final VoxelShape SHAPE_X = VoxelShapes.transform(SHAPE_Z, DirectionTransformation.ROT_90_Y_POS);

    public CuckooClockBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(Properties.HORIZONTAL_FACING, Direction.NORTH));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(Properties.HORIZONTAL_FACING);
    }

    @Override
    public @Nullable BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(Properties.HORIZONTAL_FACING, ctx.getHorizontalPlayerFacing().getOpposite());
    }

    @Override
    protected VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return VoxelShapes.cuboid(0, 0, 0, 1, 1, 1);
    }

    @Override
    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return state.get(Properties.HORIZONTAL_FACING).getAxis() == Direction.Axis.X? SHAPE_X : SHAPE_Z;
    }

    @Override
    public @Nullable BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new CuckooClockBlockEntity(pos, state);
    }
}
