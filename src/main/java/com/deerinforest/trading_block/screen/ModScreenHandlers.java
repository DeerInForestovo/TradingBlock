package com.deerinforest.trading_block.screen;

import com.deerinforest.trading_block.TradingBlock;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;

public class ModScreenHandlers {
    public static final ScreenHandlerType<TradingStationScreenHandler> TRADING_STATION_SCREEN_HANDLER =
            Registry.register(Registries.SCREEN_HANDLER, new Identifier(TradingBlock.MOD_ID, "trading_station_gui"),
                    new ExtendedScreenHandlerType<>(TradingStationScreenHandler::new));

    public static void registerScreenHandlers() {}
}