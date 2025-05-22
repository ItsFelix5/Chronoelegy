package chronoelegy.block.entity;

import chronoelegy.block.ModBlocks;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.math.BlockPos;

public class TableBlockEntity extends BlockEntity {
    private ItemStack itemStack = ItemStack.EMPTY;

    public TableBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlocks.TABLE_BLOCK_ENTITY, pos, state);
    }

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {
        if(!itemStack.isEmpty()) nbt.put("item", itemStack.toNbt(registries));
    }

    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {
        if(nbt.contains("item")) itemStack = ItemStack.fromNbt(registries, nbt.get("item")).orElse(ItemStack.EMPTY);
        else itemStack = ItemStack.EMPTY;
    }

    @Override
    public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registryLookup) {
        return createNbt(registryLookup);
    }

    public ItemStack getItem() {
        return itemStack;
    }

    public void setItem(ItemStack itemStack) {
        this.itemStack = itemStack;
        markDirty();
    }
}
