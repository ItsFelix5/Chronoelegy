package chronoelegy.mixin;

import chronoelegy.block.ModBlocks;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.block.Block;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientWorld.class)
public class ClientWorldMixin {
    @Inject(method = "getBlockParticle", at = @At(value = "INVOKE", target = "Ljava/util/Set;contains(Ljava/lang/Object;)Z"), cancellable = true)
    private void register(CallbackInfoReturnable<Block> cir, @Local Item item) {
        if (item == ModBlocks.CHECKPOINT.asItem()) cir.setReturnValue(ModBlocks.CHECKPOINT);
    }
}
