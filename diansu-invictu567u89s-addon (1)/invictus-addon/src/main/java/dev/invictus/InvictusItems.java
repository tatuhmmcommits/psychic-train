package dev.invictus;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.FoodComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;

public final class InvictusItems {
    public static final Item ENCHANTED_ELYSIUM_FLASK = new Item(new FabricItemSettings().maxCount(16));
    public static final Item NANOBOT_SWARM = new Item(new FabricItemSettings().maxCount(16));
    public static final Item INVICTUS_ENGINE = new InvictusEngineItem(new FabricItemSettings().maxCount(1));
    public static final Item INVICTUS_RAPIER = new RapierItem(new FabricItemSettings().maxCount(1));
    public static final Item AMPE = new AmpeItem(new FabricItemSettings().maxCount(1));
    /** "Very good food source, like a steak": 8 hunger (4 drumsticks), same saturation as steak. */
    public static final Item BATTERY = new Item(new FabricItemSettings().food(
            new FoodComponent.Builder().hunger(8).saturationModifier(0.8f).build()));

    private InvictusItems() {}

    private static void reg(String name, Item item) {
        Registry.register(Registries.ITEM, InvictusMod.id(name), item);
    }

    public static void register() {
        reg("enchanted_elysium_flask", ENCHANTED_ELYSIUM_FLASK);
        reg("nanobot_swarm", NANOBOT_SWARM);
        reg("invictus_engine", INVICTUS_ENGINE);
        reg("invictus_rapier", INVICTUS_RAPIER);
        reg("ampe", AMPE);
        reg("battery", BATTERY);

        Registry.register(Registries.ITEM_GROUP, InvictusMod.id("main"), FabricItemGroup.builder()
                .icon(() -> new ItemStack(INVICTUS_ENGINE))
                .displayName(Text.translatable("itemGroup.invictus.main"))
                .entries((ctx, entries) -> {
                    entries.add(ENCHANTED_ELYSIUM_FLASK);
                    entries.add(NANOBOT_SWARM);
                    entries.add(INVICTUS_ENGINE);
                    entries.add(INVICTUS_RAPIER);
                    entries.add(AMPE);
                    entries.add(BATTERY);
                })
                .build());
    }
}
