package chronoelegy.block;

import chronoelegy.Main;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCollisionHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class CheckpointBlock extends Block {
public static final IntProperty MIN_HEIGHT = IntProperty.of("min_height", 0, 400);

    public CheckpointBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(MIN_HEIGHT, 0));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(MIN_HEIGHT);
    }

    @Override
    protected void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity, EntityCollisionHandler handler) {
        if(entity instanceof ServerPlayerEntity player) {
            if(player.getRespawn() == null || player.getRespawn().pos().getChebyshevDistance(pos) > 15) {
                player.sendMessage(Text.translatable("msg.checkpoint").withColor(Colors.GREEN), true);
                player.playSoundToPlayer(SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.MASTER, 1F, 1.3F);
            }
            Main.minHeight = state.get(MIN_HEIGHT) - 64;
            player.setSpawnPoint(new ServerPlayerEntity.Respawn(world.getRegistryKey(), pos, player.getYaw(), true), false);
        }
    }

    @Override
    protected BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.INVISIBLE;
    }

    @Override
    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        if(context.isHolding(ModBlocks.CHECKPOINT.asItem())) return VoxelShapes.fullCube();
        return VoxelShapes.empty();
    }

    @Override
    protected boolean isTransparent(BlockState state) {
        return true;
    }
}
