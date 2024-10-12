package com.deerinforest.trading_block;

import com.deerinforest.trading_block.block.ModBlocks;
import com.deerinforest.trading_block.block.entity.ModBlockEntities;
import com.deerinforest.trading_block.item.ModItemGroup;
import com.deerinforest.trading_block.item.ModItems;
import com.deerinforest.trading_block.screen.ModScreenHandlers;
import com.deerinforest.trading_block.screen.TradingStationScreen;
import net.fabricmc.api.ModInitializer;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TradingBlock implements ModInitializer {
	public static final String MOD_ID = "trading_block";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		// Items, blocks, ...
		ModItemGroup.registerModItemGroup();
		ModItems.registerModItems();
		ModBlocks.registerModBlocks();
		ModBlockEntities.registerBlockEntities();

		// Screens, screen handlers, ...
		ModScreenHandlers.registerScreenHandlers();
		HandledScreens.register(ModScreenHandlers.TRADING_STATION_SCREEN_HANDLER, TradingStationScreen::new);

		// ...
	}
}