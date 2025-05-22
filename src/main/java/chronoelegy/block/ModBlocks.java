package chronoelegy.block;

import chronoelegy.Main;
import chronoelegy.block.entity.ClockBlockEntity;
import chronoelegy.block.entity.CuckooClockBlockEntity;
import chronoelegy.block.entity.TableBlockEntity;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.MapColor;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.sound.BlockSoundGroup;

import java.util.function.Consumer;
import java.util.function.Function;

public class ModBlocks {
    public static final Block TABLE = register("table", TableBlock::new, s->s
            .mapColor(MapColor.SPRUCE_BROWN)
            .strength(2.0F)
            .sounds(BlockSoundGroup.WOOD)
            .burnable());

    public static final Block GRANDFATHER_CLOCK = register("grandfather_clock", GrandFatherClockBlock::new, s->s
            .mapColor(MapColor.SPRUCE_BROWN)
            .strength(2.0F)
            .sounds(BlockSoundGroup.WOOD)
            .burnable());

    public static final Block CUCKOO_CLOCK = register("cuckoo_clock", CuckooClockBlock::new, s->s
            .mapColor(MapColor.SPRUCE_BROWN)
            .strength(2.0F)
            .sounds(BlockSoundGroup.WOOD)
            .burnable());

    public static final BlockEntityType<TableBlockEntity> TABLE_BLOCK_ENTITY = register_entity("table", TableBlockEntity::new, TABLE);
    public static final BlockEntityType<ClockBlockEntity> CLOCK_BLOCK_ENTITY = register_entity("clock", ClockBlockEntity::new, GRANDFATHER_CLOCK);
    public static final BlockEntityType<CuckooClockBlockEntity> CUCKOO_CLOCK_BLOCK_ENTITY = register_entity("cuckoo_clock", CuckooClockBlockEntity::new, CUCKOO_CLOCK);

    private static Block register(String name, Function<AbstractBlock.Settings, Block> blockFactory, Consumer<AbstractBlock.Settings> settingConsumer) {
        RegistryKey<Block> blockKey = RegistryKey.of(RegistryKeys.BLOCK, Main.id(name));
        AbstractBlock.Settings settings = AbstractBlock.Settings.create();
        settingConsumer.accept(settings);
        Block block = blockFactory.apply(settings.registryKey(blockKey));

        RegistryKey<Item> itemKey = RegistryKey.of(RegistryKeys.ITEM, Main.id(name));

        BlockItem blockItem = new BlockItem(block, new Item.Settings().registryKey(itemKey));
        Registry.register(Registries.ITEM, itemKey, blockItem);

        return Registry.register(Registries.BLOCK, blockKey, block);
    }

    private static <T extends BlockEntity> BlockEntityType<T> register_entity(
            String name,
            FabricBlockEntityTypeBuilder.Factory<? extends T> entityFactory,
            Block... blocks
    ) {
        return Registry.register(Registries.BLOCK_ENTITY_TYPE, Main.id(name), FabricBlockEntityTypeBuilder.<T>create(entityFactory, blocks).build());
    }

    public static void init() {
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.FUNCTIONAL).register((itemGroup) -> {
            itemGroup.add(GRANDFATHER_CLOCK);
            itemGroup.add(CUCKOO_CLOCK);
            itemGroup.add(TABLE);
        });
    }
}
