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
    public static final Item WILD_WEED = registerItem("wild_weed", new Item(new Item.Settings()));
    public static final Item CALLY_WEED = registerItem("cally_weed", new Item(new Item.Settings()));
    public static final Item WILD_JOINT = registerItem("wild_joint", new Item(new Item.Settings().food(ModFoodComponents.joint)));
    public static final Item CALLY_JOINT = registerItem("cally_joint", new Item(new Item.Settings().food(ModFoodComponents.joint)));
    public static final Item WILD_CANNABIS_SEEDS = registerItem("wild_cannabis_seeds",
            new AliasedBlockItem(ModBlocks.WILD_CANNABIS_PLANT_CROP_BLOCK, new Item.Settings()));
    public static final Item CALLY_CANNABIS_SEEDS = registerItem("cally_cannabis_seeds",
            new AliasedBlockItem(ModBlocks.CALLY_CANNABIS_PLANT_CROP_BLOCK, new Item.Settings()));

    private static Item registerItem(String name, Item item) {
        return Registry.register(Registries.ITEM, Identifier.of(Drugsmod.MOD_ID, name), item);
    }

    public static void registerModItems() {
        Drugsmod.LOGGER.info("Registering Mod items for " + Drugsmod.MOD_ID);

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.FOOD_AND_DRINK).register(entries -> {
            entries.add(WILD_WEED);
            entries.add(WILD_JOINT);
            entries.add(CALLY_JOINT);
        });

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.NATURAL).register(entries -> {
            entries.add(WILD_CANNABIS_SEEDS);
            entries.add(CALLY_CANNABIS_SEEDS);
        });
    }
}
