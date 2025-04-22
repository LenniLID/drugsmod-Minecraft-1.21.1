package com.drugs.block;

import com.drugs.Drugsmod;
import com.drugs.block.custom.CallyCannabisPlantCropBlock;
import com.drugs.block.custom.WildCannabisPlantCropBlock;
import com.drugs.block.custom.GrowBoxBlock;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.block.*;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;

public class ModBlocks {

    public static final Block GROW_LIGHT = registerBlock("grow_light",
            new Block(AbstractBlock.Settings.create().strength(0.3f)
                    .sounds(BlockSoundGroup.AMETHYST_BLOCK)
                    .luminance(state -> 15)));

    public static final Block NUTRIENT_RICH_SOIL = registerBlock("nutrient_rich_soil",
            new Block(AbstractBlock.Settings.create().strength(0.5f)
                    .sounds(BlockSoundGroup.ROOTED_DIRT)));

    public static final Block GROW_BOX = registerBlock("grow_box",
            new GrowBoxBlock(AbstractBlock.Settings.create().strength(0.5f)
                    .sounds(BlockSoundGroup.GLASS)
                    .luminance(state -> 5)));









    private static Block registerBlock(String name, Block block) {
        registerBlockItem(name, block);
        return Registry.register(Registries.BLOCK, Identifier.of(Drugsmod.MOD_ID, name), block);
    }

    private static void registerBlockItem(String name, Block block) {
        Registry.register(Registries.ITEM, Identifier.of(Drugsmod.MOD_ID, name),
                new BlockItem(block, new Item.Settings()));
    }


    public static final WildCannabisPlantCropBlock WILD_CANNABIS_PLANT_CROP_BLOCK = registerBlockWithoutBlockItem("wild_cannabis_crop",
            new WildCannabisPlantCropBlock(AbstractBlock.Settings.create().noCollision()
                    .ticksRandomly().breakInstantly().sounds(BlockSoundGroup.CROP).pistonBehavior(PistonBehavior.DESTROY).mapColor(MapColor.DARK_GREEN)));

    private static WildCannabisPlantCropBlock registerBlockWithoutBlockItem(String name, WildCannabisPlantCropBlock block) {
        return Registry.register(Registries.BLOCK, Identifier.of(Drugsmod.MOD_ID, name), block);
    }

    public static final CallyCannabisPlantCropBlock CALLY_CANNABIS_PLANT_CROP_BLOCK = registerBlockWithoutBlockItem("cally_cannabis_crop",
            new CallyCannabisPlantCropBlock(AbstractBlock.Settings.create().noCollision()
                    .ticksRandomly().breakInstantly().sounds(BlockSoundGroup.CROP).pistonBehavior(PistonBehavior.DESTROY).mapColor(MapColor.DARK_GREEN)));

    private static CallyCannabisPlantCropBlock registerBlockWithoutBlockItem(String name, CallyCannabisPlantCropBlock block) {
        return Registry.register(Registries.BLOCK, Identifier.of(Drugsmod.MOD_ID, name), block);
    }


    public static void registerModBlocks() {
        Drugsmod.LOGGER.info("Registering Mod Blocks for " + Drugsmod.MOD_ID);

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.FUNCTIONAL).register(entries -> {
            entries.add(ModBlocks.GROW_LIGHT);
            entries.add(ModBlocks.NUTRIENT_RICH_SOIL);
            entries.add(ModBlocks.GROW_BOX);
        });

}}

