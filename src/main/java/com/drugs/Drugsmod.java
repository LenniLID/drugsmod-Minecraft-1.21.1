package com.drugs;

import com.drugs.block.ModBlocks;
import com.drugs.item.ModItems;
import com.drugs.util.ModLootTableModifiers;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.object.builder.v1.trade.TradeOfferHelper;
import net.fabricmc.fabric.api.registry.CompostingChanceRegistry;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.TradedItem;
import net.minecraft.village.VillagerProfession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Drugsmod implements ModInitializer {
	public static final String MOD_ID = "drugsmod";

	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		ModItems.registerModItems();
		ModBlocks.registerModBlocks();
		ModLootTableModifiers.modifyLootTables();
		CompostingChanceRegistry.INSTANCE.add(ModItems.WILD_WEED, 0.5f);
		CompostingChanceRegistry.INSTANCE.add(ModItems.CALLY_WEED, 0.5f);
		CompostingChanceRegistry.INSTANCE.add(ModItems.WILD_CANNABIS_SEEDS, 0.25f);
		BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.WILD_CANNABIS_PLANT_CROP_BLOCK, RenderLayer.getCutout());
		BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.CALLY_CANNABIS_PLANT_CROP_BLOCK, RenderLayer.getCutout());


		TradeOfferHelper.registerVillagerOffers(VillagerProfession.FARMER, 1, factories -> {
			factories.add((entity, random) -> new TradeOffer(
					new TradedItem(ModItems.WILD_WEED, 1),
					new ItemStack(Items.EMERALD, 2), 15, -1, 0.04f));
		});

		TradeOfferHelper.registerVillagerOffers(VillagerProfession.CLERIC, 1, factories -> {
			factories.add((entity, random) -> new TradeOffer(
					new TradedItem(ModItems.WILD_JOINT, 1),
					new ItemStack(Items.EMERALD, 3), 15, -1, 0.04f));
		});

		TradeOfferHelper.registerWanderingTraderOffers(1, factories -> {
			factories.add((entity, random) -> new TradeOffer(
					new TradedItem(Items.EMERALD, 10),
					new ItemStack(ModItems.WILD_CANNABIS_SEEDS, 1), 4, 7, 0.04f));

			factories.add((entity, random) -> new TradeOffer(
					new TradedItem(Items.EMERALD, 10),
					new ItemStack(ModItems.WILD_CANNABIS_SEEDS, 1), 4, 7, 0.04f));

			factories.add((entity, random) -> new TradeOffer(
					new TradedItem(Items.EMERALD, 10),
					new ItemStack(ModItems.WILD_CANNABIS_SEEDS, 1), 4, 7, 0.04f));

			factories.add((entity, random) -> new TradeOffer(
					new TradedItem(Items.EMERALD, 10),
					new ItemStack(ModItems.WILD_CANNABIS_SEEDS, 1), 4, 7, 0.04f));

			factories.add((entity, random) -> new TradeOffer(
					new TradedItem(Items.EMERALD, 10),
					new ItemStack(ModItems.WILD_CANNABIS_SEEDS, 1), 4, 7, 0.04f));
		});
	}
}