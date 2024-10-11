package com.deerinforest.trading_block.item;

import com.deerinforest.trading_block.TradingBlock;
import com.deerinforest.trading_block.item.custom.TradingAgreement;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModItems {

    public static final Item TRADING_AGREEMENT = registerItems("trading_agreement", new TradingAgreement(new FabricItemSettings()));

    private static Item registerItems(String name, Item item) {
        return Registry.register(Registries.ITEM, new Identifier(TradingBlock.MOD_ID, name), item);
    }

    public static void initializeItems() {
        TradingAgreement.initializeItem();
    }
}
