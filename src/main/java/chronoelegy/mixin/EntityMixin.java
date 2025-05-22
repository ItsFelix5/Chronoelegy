package chronoelegy.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import static chronoelegy.Main.client;

@Mixin(Entity.class)
public class EntityMixin {
    @ModifyReturnValue(method = "shouldSpawnSprintingParticles", at = @At("TAIL"))
    private boolean shouldSpawnSprintingParticles(boolean original) {
        return original && !client.world.getTickManager().isFrozen();
    }
}
