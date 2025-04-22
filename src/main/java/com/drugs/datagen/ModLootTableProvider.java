package com.drugs.datagen;

import com.drugs.block.ModBlocks;
import com.drugs.block.custom.CallyCannabisPlantCropBlock;
import com.drugs.item.ModItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;
import net.minecraft.block.CropBlock;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.Items;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.condition.BlockStatePropertyLootCondition;
import net.minecraft.loot.condition.InvertedLootCondition;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.function.*;
import net.minecraft.loot.provider.number.*;
import net.minecraft.predicate.StatePredicate;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import com.drugs.block.custom.WildCannabisPlantCropBlock;


import java.util.concurrent.CompletableFuture;

public class ModLootTableProvider extends FabricBlockLootTableProvider {
    public ModLootTableProvider(FabricDataOutput dataOutput, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
        super(dataOutput, registryLookup);
    }

    @Override
    public void generate() {

        addDrop(ModBlocks.NUTRIENT_RICH_SOIL);
        addDrop(ModBlocks.GROW_BOX);
        addDrop(ModBlocks.GROW_LIGHT,
                LootTable.builder()
                        .pool(LootPool.builder()
                                .rolls(ConstantLootNumberProvider.create(1))
                                .with(ItemEntry.builder(Items.GLOWSTONE_DUST)
                                        .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0f, 4.0f)))
                                )
                        )
        );


        RegistryWrapper.Impl<Enchantment> impl = this.registryLookup.getWrapperOrThrow(RegistryKeys.ENCHANTMENT);

        BlockStatePropertyLootCondition.Builder builder2 = BlockStatePropertyLootCondition.builder(ModBlocks.WILD_CANNABIS_PLANT_CROP_BLOCK)
                .properties(StatePredicate.Builder.create().exactMatch(WildCannabisPlantCropBlock.AGE, WildCannabisPlantCropBlock.MAX_AGE));

        this.addDrop(ModBlocks.WILD_CANNABIS_PLANT_CROP_BLOCK,
                this.applyExplosionDecay(
                    ModBlocks.WILD_CANNABIS_PLANT_CROP_BLOCK,
                    LootTable.builder()
                            .pool(
                                    LootPool.builder()
                                            .with(ItemEntry.builder(ModItems.WILD_WEED)
                                                    .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(3, 5)))
                                                    .conditionally(builder2)
                                                    .alternatively(ItemEntry.builder(ModItems.WILD_CANNABIS_SEEDS))))
                            .pool(
                                    LootPool.builder()
                                            .conditionally(builder2)
                                            .apply(ApplyBonusLootFunction.uniformBonusCount(impl.getOrThrow(Enchantments.FORTUNE)))
                                            .with(ItemEntry.builder(ModItems.WILD_CANNABIS_SEEDS))
                                            .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(2, 3)))
                            )
        ));



        LootTable.Builder callyCannabisLoot = LootTable.builder()
                // Pool 1: 3-5 Weed, wenn AGE == MAX_AGE (Pflanze reif)
                .pool(LootPool.builder()
                        .rolls(ConstantLootNumberProvider.create(1))  // jeder Pool wird einmal ausgeführt
                        .apply(ApplyBonusLootFunction.uniformBonusCount(impl.getOrThrow(Enchantments.FORTUNE)))
                        .conditionally(BlockStatePropertyLootCondition.builder(ModBlocks.CALLY_CANNABIS_PLANT_CROP_BLOCK)
                                .properties(StatePredicate.Builder.create()
                                        .exactMatch(CropBlock.AGE, CropBlock.MAX_AGE)))  // Bedingung: volle Reife
                        .with(ItemEntry.builder(ModItems.CALLY_WEED)
                                .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(3.0f, 5.0f))))
                )
                // Pool 2: 2-3 Seeds, wenn AGE == MAX_AGE (Pflanze reif)
                .pool(LootPool.builder()
                        .rolls(ConstantLootNumberProvider.create(1))
                        .conditionally(BlockStatePropertyLootCondition.builder(ModBlocks.CALLY_CANNABIS_PLANT_CROP_BLOCK)
                                .properties(StatePredicate.Builder.create()
                                        .exactMatch(CropBlock.AGE, CropBlock.MAX_AGE)))  // gleiche Bedingung wie oben
                        .with(ItemEntry.builder(ModItems.WILD_CANNABIS_SEEDS)
                                .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(2.0f, 3.0f))))
                )
                // Pool 3: 1 Seed, wenn Pflanze NICHT reif (Invertierte Bedingung)
                .pool(LootPool.builder()
                        .rolls(ConstantLootNumberProvider.create(1))
                        .conditionally(InvertedLootCondition.builder(  // invertiert die Reife-Bedingung
                                BlockStatePropertyLootCondition.builder(ModBlocks.CALLY_CANNABIS_PLANT_CROP_BLOCK)
                                        .properties(StatePredicate.Builder.create()
                                                .exactMatch(CropBlock.AGE, CropBlock.MAX_AGE))))
                        .with(ItemEntry.builder(ModItems.CALLY_CANNABIS_SEEDS))
                );

        // Registrierung der LootTable für den neuen Crop-Block
        addDrop(ModBlocks.CALLY_CANNABIS_PLANT_CROP_BLOCK, callyCannabisLoot);
    }}


