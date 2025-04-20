package com.drugs.datagen;

import com.drugs.block.ModBlocks;
import com.drugs.item.ModItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.Items;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.condition.BlockStatePropertyLootCondition;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.function.*;
import net.minecraft.loot.provider.number.*;
import net.minecraft.predicate.StatePredicate;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import com.drugs.block.custom.CannabisPlantCropBlock;


import java.util.concurrent.CompletableFuture;

public class ModLootTableProvider extends FabricBlockLootTableProvider {
    public ModLootTableProvider(FabricDataOutput dataOutput, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
        super(dataOutput, registryLookup);
    }

    @Override
    public void generate() {

        addDrop(ModBlocks.NUTRIENT_RICH_SOIL);
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

        BlockStatePropertyLootCondition.Builder builder2 = BlockStatePropertyLootCondition.builder(ModBlocks.CANNABIS_CROP)
                .properties(StatePredicate.Builder.create().exactMatch(CannabisPlantCropBlock.AGE, CannabisPlantCropBlock.MAX_AGE));

        this.addDrop(ModBlocks.CANNABIS_CROP,
                this.applyExplosionDecay(
                    ModBlocks.CANNABIS_CROP,
                    LootTable.builder()
                            .pool(
                                    LootPool.builder()
                                            .with(ItemEntry.builder(ModItems.WEED)
                                                    .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(3, 5)))
                                                    .conditionally(builder2)
                                                    .alternatively(ItemEntry.builder(ModItems.CANNABIS_SEEDS))))
                            .pool(
                                    LootPool.builder()
                                            .conditionally(builder2)
                                            .with(ItemEntry.builder(ModItems.CANNABIS_SEEDS))
                                            .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(2, 3)))
                            )
        ));
    }}
