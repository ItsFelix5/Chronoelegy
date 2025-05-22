package chronoelegy.block;

import chronoelegy.block.entity.TableBlockEntity;
import chronoelegy.item.ModItems;
import chronoelegy.screen.RepairScreen;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import net.minecraft.world.tick.ScheduledTickView;
import org.jetbrains.annotations.Nullable;

import static chronoelegy.Main.client;

public class TableBlock extends Block implements BlockEntityProvider {
    private static final VoxelShape SW = VoxelShapes.cuboid(0.0625, 0, 0.8125, 0.1875, 0.75, 0.9375);
    private static final VoxelShape NW = VoxelShapes.cuboid(0.0625, 0, 0.0625, 0.1875, 0.75, 0.1875);
    private static final VoxelShape SE = VoxelShapes.cuboid(0.8125, 0, 0.8125, 0.9375, 0.75, 0.9375);
    private static final VoxelShape NE = VoxelShapes.cuboid(0.8125, 0, 0.0625, 0.9375, 0.75, 0.1875);
    private static final VoxelShape PLATE = VoxelShapes.cuboid(0, 0.75, 0, 1, 0.875, 1);

    public TableBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(Properties.HORIZONTAL_FACING, Direction.NORTH).with(Properties.NORTH, false).with(Properties.EAST, false).with(Properties.SOUTH, false).with(Properties.WEST, false));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(Properties.HORIZONTAL_FACING, Properties.NORTH, Properties.EAST, Properties.SOUTH, Properties.WEST);
    }

    @Override
    public @Nullable BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(Properties.HORIZONTAL_FACING, ctx.getHorizontalPlayerFacing().getOpposite());
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        world.setBlockState(pos, state.with(Properties.NORTH, world.getBlockState(pos.north()).isOf(this))
                .with(Properties.EAST, world.getBlockState(pos.east()).isOf(this))
                .with(Properties.SOUTH, world.getBlockState(pos.south()).isOf(this))
                .with(Properties.WEST, world.getBlockState(pos.west()).isOf(this)));
    }

    @Override
    protected BlockState getStateForNeighborUpdate(BlockState state, WorldView world, ScheduledTickView tickView, BlockPos pos, Direction direction, BlockPos neighborPos, BlockState neighborState, Random random) {
        if(direction.getAxis().isVertical()) return state;
        return state.with(switch (direction) {
            case NORTH -> Properties.NORTH;
            case EAST -> Properties.EAST;
            case SOUTH -> Properties.SOUTH;
            case WEST -> Properties.WEST;
            default -> null;
        }, world.getBlockState(pos.offset(direction)).isOf(this));
    }

    @Override
    protected VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return getOutlineShape(state, world, pos, context);
    }

    @Override
    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        boolean N = state.get(Properties.NORTH), S = state.get(Properties.SOUTH), E = state.get(Properties.EAST), W = state.get(Properties.WEST);
        VoxelShape shape = PLATE;

        if(!(N || E)) shape = VoxelShapes.union(shape, NE);
        if(!(N || W)) shape = VoxelShapes.union(shape, NW);
        if(!(S || E)) shape = VoxelShapes.union(shape, SE);
        if(!(S || W)) shape = VoxelShapes.union(shape, SW);
        return shape;
    }

    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (!(world.getBlockEntity(pos) instanceof TableBlockEntity entity)) return ActionResult.PASS;

        ItemStack stack = entity.getItem();
        if (!stack.isEmpty() && player.getMainHandStack().isEmpty()) {
            if(stack.isOf(ModItems.BROKEN_POCKET_WATCH)) {
                if(client.isOnThread()) client.setScreen(new RepairScreen(entity));
            } else {
                entity.setItem(ItemStack.EMPTY);
                player.getInventory().offerOrDrop(stack);
            }
            return ActionResult.SUCCESS;
        }

        return ActionResult.PASS;
    }

    @Override
    protected ActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (!(world.getBlockEntity(pos) instanceof TableBlockEntity entity)) return ActionResult.PASS;

        if (entity.getItem().isEmpty()) {
            entity.setItem(stack);
            player.setStackInHand(hand, ItemStack.EMPTY);
            return ActionResult.SUCCESS;
        }

        return ActionResult.PASS_TO_DEFAULT_BLOCK_ACTION;
    }

    @Override
    public @Nullable BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new TableBlockEntity(pos, state);
    }
}
