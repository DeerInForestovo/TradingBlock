package com.deerinforest.trading_block.block.entity;

import com.deerinforest.trading_block.TradingBlock;
import com.deerinforest.trading_block.block.ModBlocks;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModBlockEntities {
    public static final BlockEntityType<TradingStationEntity> TRADING_STATION_BE =
            Registry.register(Registries.BLOCK_ENTITY_TYPE, new Identifier(TradingBlock.MOD_ID, "trading_station_be"),
                    FabricBlockEntityTypeBuilder.create(TradingStationEntity::new,
                            ModBlocks.TRADING_STATION).build());

    public static void registerBlockEntities() {}
}