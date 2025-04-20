package com.drugs.item;

import com.drugs.Drugsmod;
import com.drugs.block.ModBlocks;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.AliasedBlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModItems {
    public static final Item WEED = registerItem("weed", new Item(new Item.Settings()));
    public static final Item JOINT = registerItem("joint", new Item(new Item.Settings().food(ModFoodComponents.joint)));
    public static final Item CANNABIS_SEEDS = registerItem("cannabis_seeds",
            new AliasedBlockItem(ModBlocks.CANNABIS_CROP, new Item.Settings()));

    private static Item registerItem(String name, Item item) {
        return Registry.register(Registries.ITEM, Identifier.of(Drugsmod.MOD_ID, name), item);
    }

    public static void registerModItems() {
        Drugsmod.LOGGER.info("Registering Mod items for " + Drugsmod.MOD_ID);

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.FOOD_AND_DRINK).register(entries -> {
            entries.add(WEED);
            entries.add(JOINT);
            entries.add(CANNABIS_SEEDS);
        });
    }
}
