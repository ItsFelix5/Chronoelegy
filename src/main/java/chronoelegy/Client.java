package chronoelegy;

import chronoelegy.block.ModBlocks;
import chronoelegy.block.entity.renderer.ClockRenderer;
import chronoelegy.block.entity.renderer.CuckooClockRenderer;
import chronoelegy.block.entity.renderer.TableRenderer;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;

public class Client implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        BlockEntityRendererFactories.register(ModBlocks.CLOCK_BLOCK_ENTITY, ClockRenderer::new);
        BlockEntityRendererFactories.register(ModBlocks.CUCKOO_CLOCK_BLOCK_ENTITY, CuckooClockRenderer::new);
        BlockEntityRendererFactories.register(ModBlocks.TABLE_BLOCK_ENTITY, TableRenderer::new);
    }
}
