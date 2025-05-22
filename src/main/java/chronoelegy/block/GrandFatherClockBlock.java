package chronoelegy.block;

import chronoelegy.block.entity.ClockBlockEntity;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import org.jetbrains.annotations.Nullable;

public class GrandFatherClockBlock extends Block implements BlockEntityProvider {
    private static final VoxelShape SHAPE = VoxelShapes.union(
            VoxelShapes.cuboid(0, -1, 0, 0.125, -0.9375, 0.125),
            VoxelShapes.cuboid(0, -1, 0.875, 0.125, -0.9375, 1),
            VoxelShapes.cuboid(0.875, -1, 0.875, 1, -0.9375, 1),
            VoxelShapes.cuboid(0.875, -1, 0, 1, -0.9375, 0.125),
            VoxelShapes.cuboid(0, -0.9375, 0, 1, -0.6875, 1),
            VoxelShapes.cuboid(0, -0.6875, 0, 0.25, -0.5625, 0.25),
            VoxelShapes.cuboid(0.75, -0.6875, 0, 1, -0.5625, 0.25),
            VoxelShapes.cuboid(0, -0.6875, 0.75, 0.25, -0.5625, 1),
            VoxelShapes.cuboid(0.75, -0.6875, 0.75, 1, -0.5625, 1),
            VoxelShapes.cuboid(0.0625, -0.5625, 0.0625, 0.1875, 0.5, 0.1875),
            VoxelShapes.cuboid(0.8125, -0.5625, 0.0625, 0.9375, 0.5, 0.1875),
            VoxelShapes.cuboid(0.0625, -0.5625, 0.8125, 0.1875, 0.5, 0.9375),
            VoxelShapes.cuboid(0.8125, -0.5625, 0.8125, 0.9375, 0.5, 0.9375),
            VoxelShapes.cuboid(0, 0.5, 0, 0.25, 0.625, 0.25),
            VoxelShapes.cuboid(0.75, 0.5, 0, 1, 0.625, 0.25),
            VoxelShapes.cuboid(0, 0.5, 0.75, 0.25, 0.625, 1),
            VoxelShapes.cuboid(0.75, 0.5, 0.75, 1, 0.625, 1),
            VoxelShapes.cuboid(0.125, -0.6875, 0.125, 0.875, 0.625, 0.875),
            VoxelShapes.cuboid(-0.0625, 0.625, -0.0625, 1.0625, 0.75, 1.0625),
            VoxelShapes.cuboid(0, 0.75, 0, 0.25, 0.875, 0.25),
            VoxelShapes.cuboid(0.75, 0.75, 0, 1, 0.875, 0.25),
            VoxelShapes.cuboid(0, 0.75, 0.75, 0.25, 0.875, 1),
            VoxelShapes.cuboid(0.75, 0.75, 0.75, 1, 0.875, 1),
            VoxelShapes.cuboid(0.0625, 0.875, 0.0625, 0.1875, 1.3125, 0.1875),
            VoxelShapes.cuboid(0.8125, 0.875, 0.0625, 0.9375, 1.3125, 0.1875),
            VoxelShapes.cuboid(0.0625, 0.875, 0.8125, 0.1875, 1.3125, 0.9375),
            VoxelShapes.cuboid(0.8125, 0.875, 0.8125, 0.9375, 1.3125, 0.9375),
            VoxelShapes.cuboid(0, 1.3125, 0, 0.25, 1.4375, 0.25),
            VoxelShapes.cuboid(0.75, 1.3125, 0, 1, 1.4375, 0.25),
            VoxelShapes.cuboid(0, 1.3125, 0.75, 0.25, 1.4375, 1),
            VoxelShapes.cuboid(0.75, 1.3125, 0.75, 1, 1.4375, 1),
            VoxelShapes.cuboid(0.125, 0.75, 0.125, 0.875, 1.4375, 0.875),
            VoxelShapes.cuboid(-0.0625, 1.4375, -0.0625, 1.0625, 1.5625, 1.0625),
            VoxelShapes.cuboid(0, 1.5625, 0, 1, 1.6875, 1)
    );
    public static final IntProperty PART = IntProperty.of("part", 0, 2);

    public GrandFatherClockBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(PART, 0).with(Properties.ENABLED, true).with(Properties.HORIZONTAL_FACING, Direction.NORTH));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(PART).add(Properties.ENABLED).add(Properties.HORIZONTAL_FACING);
    }

    @Override
    public @Nullable BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(Properties.HORIZONTAL_FACING, ctx.getHorizontalPlayerFacing().getOpposite());
    }

    @Override
    protected VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return VoxelShapes.cuboid(0, -1, 0, 1, 1.6875, 1).offset(0, -state.get(PART) + 1, 0);
    }

    @Override
    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE.offset(0, -state.get(PART) + 1, 0);
    }

    @Override
    protected boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        return world.getBlockState(pos.up()).isAir() && world.getBlockState(pos.up().up()).isAir();
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        world.setBlockState(pos.up(), state.with(PART, 1));
        world.setBlockState(pos.up().up(), state.with(PART, 2));
    }

    @Override
    protected BlockRenderType getRenderType(BlockState state) {
        return state.get(PART) == 1? BlockRenderType.MODEL:BlockRenderType.INVISIBLE;
    }

    @Override
    public BlockState onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        int part = state.get(PART);
        if(part != 2) world.setBlockState(pos.up(), Blocks.AIR.getDefaultState());
        else world.setBlockState(pos.down().down(), Blocks.AIR.getDefaultState());
        if(part != 0) world.setBlockState(pos.down(), Blocks.AIR.getDefaultState());
        else world.setBlockState(pos.up().up(), Blocks.AIR.getDefaultState());

        return super.onBreak(world, pos, state, player);
    }

    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if(!state.get(Properties.ENABLED)) return ActionResult.PASS;
        world.getTickManager().setFrozen(!world.getTickManager().isFrozen());
        return ActionResult.SUCCESS;
    }

    @Override
    public @Nullable BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        if(state.get(PART) == 2) return new ClockBlockEntity(pos, state);
        return null;
    }
}
