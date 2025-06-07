package chronoelegy.item;

import chronoelegy.Main;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.component.type.AttributeModifierSlot;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.function.Consumer;
import java.util.function.Function;

public class ModItems {
    public static final ArrayList<Item> ITEMS = new ArrayList<>();

    public static final Item POCKET_WATCH = register("pocket_watch", Item::new, s->{});
    public static final Item BROKEN_POCKET_WATCH = register("broken_pocket_watch", Item::new, s->{});
    public static final Item BLADE = register("blade", BladeItem::new, s->s.attributeModifiers(AttributeModifiersComponent.builder()
                    .add(
                            EntityAttributes.ATTACK_DAMAGE,
                            new EntityAttributeModifier(Item.BASE_ATTACK_DAMAGE_MODIFIER_ID, 10, EntityAttributeModifier.Operation.ADD_VALUE),//TODO
                            AttributeModifierSlot.MAINHAND
                    ).build()).maxCount(1));

    public static Item register(String name, Function<Item.Settings, Item> itemFactory, Consumer<Item.Settings> settingConsumer) {
        RegistryKey<Item> itemKey = RegistryKey.of(RegistryKeys.ITEM, Main.id(name));
        Item.Settings settings = new Item.Settings();
        settingConsumer.accept(settings);
        Item item = itemFactory.apply(settings.registryKey(itemKey));
        ITEMS.add(item);
        return Registry.register(Registries.ITEM, itemKey, item);
    }

    public static void init() {
        Registry.register(Registries.ITEM_GROUP, RegistryKey.of(Registries.ITEM_GROUP.getKey(), Main.id("group")), FabricItemGroup.builder()
                .icon(() -> new ItemStack(ModItems.POCKET_WATCH))
                .displayName(Text.translatable("itemGroup.chronoelegy"))
                .entries((ctx, itemGroup) -> ITEMS.forEach(itemGroup::add))
                .build());
    }
}
