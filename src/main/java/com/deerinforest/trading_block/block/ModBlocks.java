package com.deerinforest.trading_block.block;

import com.deerinforest.trading_block.TradingBlock;
import com.deerinforest.trading_block.block.custom.TradingStation;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModBlocks {
    public static final Block TRADING_STATION = registerBlocks("trading_station",
            new TradingStation(FabricBlockSettings.copyOf(Blocks.STONE)));

    private static Block registerBlocks(String name, Block block) {
        registerBlockItem(name, block);
        return Registry.register(Registries.BLOCK, new Identifier(TradingBlock.MOD_ID, name), block);
    }

    private static Item registerBlockItem(String name, Block block) {
        return Registry.register(Registries.ITEM, new Identifier(TradingBlock.MOD_ID, name),
                new BlockItem(block, new FabricItemSettings()));
    }

    public static void registerModBlocks() {}
}
