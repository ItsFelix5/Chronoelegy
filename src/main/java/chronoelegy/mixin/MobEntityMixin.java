package chronoelegy.mixin;

import chronoelegy.screen.Dialogue;
import chronoelegy.screen.DialogueCallbacks;
import chronoelegy.screen.DialogueScreen;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MobEntity.class)
public abstract class MobEntityMixin extends LivingEntity {
    @Shadow protected abstract float changeAngle(float from, float to, float max);
    @Shadow public abstract void writeCustomDataToNbt(NbtCompound nbt);
    protected MobEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Unique
    private Entity inDialogueWith;
    @Unique
    private Dialogue.Root dialogue;

    @WrapOperation(method = "interact", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;interact(Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/util/Hand;)" +
            "Lnet/minecraft/util/ActionResult;"))
    private ActionResult interact(MobEntity instance, PlayerEntity player, Hand hand, Operation<ActionResult> original) {
        ActionResult result = original.call(instance, player, hand);
        if(result != ActionResult.PASS || dialogue == null) return result;

        inDialogueWith = player;
        MinecraftClient client = MinecraftClient.getInstance();
        client.executeSync(()->client.setScreen(new DialogueScreen(dialogue, ()->inDialogueWith = null)));
        return ActionResult.SUCCESS;
    }

    @Inject(method = "writeCustomDataToNbt", at = @At("TAIL"))
    public void writeCustomDataToNbt(NbtCompound nbt, CallbackInfo ci) {
        if(dialogue == null) return;
        NbtCompound root = new NbtCompound();
        root.putBoolean("skippable", dialogue.skippable);
        root.putString("text", dialogue.text.getLiteralString());
        writeDialogueNbt(dialogue, root);
        nbt.put("dialogue", root);
    }

    @Unique
    private void writeDialogueNbt(Dialogue dialogue, NbtCompound nbt) {
        NbtList options = new NbtList();
        dialogue.options.forEach(option -> {
            NbtCompound optionNbt = new NbtCompound();
            optionNbt.putString("option", option.getLeft().getLiteralString());
            if(option.getRight() != null) {
                optionNbt.putString("text", option.getRight().text.getLiteralString());
                writeDialogueNbt(option.getRight(), optionNbt);
            }
            options.add(optionNbt);
        });
        nbt.put("options", options);
    }

    @Inject(method = "readCustomDataFromNbt", at = @At("TAIL"))
    public void readCustomDataFromNbt(NbtCompound nbt, CallbackInfo ci) {
        if(!nbt.contains("dialogue")) return;
        NbtCompound root = nbt.getCompoundOrEmpty("dialogue");
        dialogue = Dialogue.create(root.getBoolean("skippable", true), Text.literal(root.getString("text", "error")));
        readDialogueNbt(dialogue, root);
    }

    @Unique
    private void readDialogueNbt(Dialogue dialogue, NbtCompound nbt) {
        nbt.getListOrEmpty("options").forEach(e -> {
            NbtCompound option = e.asCompound().orElseThrow();
            Text optionName = Text.literal(option.getString("option", "error"));
            if(option.contains("options")) {
                Dialogue optionDialogue = new Dialogue(Text.literal(option.getString("text", "error")));
                if(option.contains("cb")) optionDialogue.cb = DialogueCallbacks.CALLBACKS.get(option.getString("cb", ""));
                readDialogueNbt(optionDialogue, option);
                dialogue.option(optionName, optionDialogue);
            } else dialogue.ending(optionName);
        });
    }

    @Inject(method = "tickMovement", at = @At("HEAD"), cancellable = true)
    private void tickMovement(CallbackInfo ci) {
        if(inDialogueWith == null || getWorld().isClient) return;
        ci.cancel();

        Vec3d vec = inDialogueWith.getEyePos().subtract(getEyePos());

        if((Math.abs(vec.z) > 1.0E-5F) || (Math.abs(vec.x) > 1.0E-5F)) headYaw = changeAngle(headYaw, (float)(MathHelper.atan2(vec.z, vec.x) * 180.0F / (float)Math.PI) - 90.0F, 10);

        double g = Math.sqrt(vec.x * vec.x + vec.z * vec.z);
        if(Math.abs(vec.y) > 1.0E-5F || Math.abs(g) > 1.0E-5F) setPitch(changeAngle(getPitch(), (float)(-(MathHelper.atan2(vec.y, g) * 180.0F / (float)Math.PI)), 40));
    }
}
