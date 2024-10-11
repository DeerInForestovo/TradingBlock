package com.deerinforest.trading_block;

import com.deerinforest.trading_block.block.ModBlocks;
import com.deerinforest.trading_block.block.entity.ModBlockEntities;
import com.deerinforest.trading_block.item.ModItemGroup;
import com.deerinforest.trading_block.item.ModItems;
import com.deerinforest.trading_block.screen.ModScreenHandlers;
import com.deerinforest.trading_block.screen.TradingStationScreen;
import net.fabricmc.api.ModInitializer;

import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TradingBlock implements ModInitializer {
	public static final String MOD_ID = "trading_block";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static final Identifier TRADE_OFFER_LIST_CHANNEL = new Identifier(MOD_ID, "trade_offer_list_channel");
	public static final Identifier TRADE_OFFER_SELECTION_CHANNEL = new Identifier(MOD_ID, "trade_offer_selection_channel");

	@Override
	public void onInitialize() {
		ModItemGroup.registerModItemGroup();
		ModItems.initializeItems();
		ModBlocks.registerModBlocks();
		ModBlockEntities.registerBlockEntities();
		ModScreenHandlers.registerScreenHandlers();
		HandledScreens.register(ModScreenHandlers.TRADING_STATION_SCREEN_HANDLER, TradingStationScreen::new);
	}
}