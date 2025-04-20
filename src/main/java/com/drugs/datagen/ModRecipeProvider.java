package com.drugs.datagen;

import com.drugs.Drugsmod;
import com.drugs.block.ModBlocks;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import com.drugs.item.ModItems;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.TintedGlassBlock;
import net.minecraft.data.server.recipe.RecipeExporter;
import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder;
import net.minecraft.data.server.recipe.ShapelessRecipeJsonBuilder;
import net.minecraft.item.Items;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.Identifier;

import java.util.concurrent.CompletableFuture;

public class ModRecipeProvider extends FabricRecipeProvider {
    public ModRecipeProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    public void generate(RecipeExporter exporter) {

        ShapedRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, ModBlocks.GROW_BOX)
                .pattern("tgt")
                .pattern("t t")
                .pattern("tdt")
                .input('t', Blocks.TINTED_GLASS)
                .input('g', ModBlocks.GROW_LIGHT)
                .input('d', ModBlocks.NUTRIENT_RICH_SOIL)
                .criterion(FabricRecipeProvider.hasItem(Blocks.TINTED_GLASS),
                        FabricRecipeProvider.conditionsFromItem(Blocks.TINTED_GLASS))
                .criterion(FabricRecipeProvider.hasItem(ModBlocks.GROW_LIGHT),
                        FabricRecipeProvider.conditionsFromItem(ModBlocks.GROW_LIGHT))
                .criterion(FabricRecipeProvider.hasItem(ModBlocks.NUTRIENT_RICH_SOIL),
                        FabricRecipeProvider.conditionsFromItem(ModBlocks.NUTRIENT_RICH_SOIL))
                .offerTo(exporter);

        ShapelessRecipeJsonBuilder.create(RecipeCategory.FOOD, ModItems.JOINT)
                .input(Items.PAPER)
                .input(ModItems.WEED)
                .criterion(FabricRecipeProvider.hasItem(ModItems.WEED), FabricRecipeProvider.conditionsFromItem(ModItems.WEED))
                .offerTo(exporter);

        ShapelessRecipeJsonBuilder.create(RecipeCategory.REDSTONE, ModBlocks.GROW_LIGHT.asItem())
                .input(Items.AMETHYST_SHARD)
                .input(Items.GLOWSTONE)
                .criterion(FabricRecipeProvider.hasItem(Items.GLOWSTONE), FabricRecipeProvider.conditionsFromItem(Items.GLOWSTONE))
                .offerTo(exporter);

        ShapelessRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, ModBlocks.NUTRIENT_RICH_SOIL.asItem())
                .input(Items.DIRT)
                .input(Items.BONE_MEAL)
                .input(Items.ROTTEN_FLESH)
                .criterion(FabricRecipeProvider.hasItem(Items.DIRT), FabricRecipeProvider.conditionsFromItem(Items.DIRT))
                .offerTo(exporter);
    }
}