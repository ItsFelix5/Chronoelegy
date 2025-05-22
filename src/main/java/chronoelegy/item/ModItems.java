package chronoelegy.item;

import chronoelegy.Main;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;

import java.util.function.Consumer;
import java.util.function.Function;

public class ModItems {
    public static final Item POCKET_WATCH = register("pocket_watch", Item::new, s->{});
    public static final Item BROKEN_POCKET_WATCH = register("broken_pocket_watch", Item::new, s->{});

    private static Item register(String name, Function<Item.Settings, Item> itemFactory, Consumer<Item.Settings> settingConsumer) {
        RegistryKey<Item> itemKey = RegistryKey.of(RegistryKeys.ITEM, Main.id(name));
        Item item = itemFactory.apply(new Item.Settings().registryKey(itemKey));
        return Registry.register(Registries.ITEM, itemKey, item);
    }

    public static void init() {
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.TOOLS).register((itemGroup) -> itemGroup.add(POCKET_WATCH));
    }
}
