package com.deerinforest.trading_block.item;

import com.deerinforest.trading_block.TradingBlock;
import com.deerinforest.trading_block.block.ModBlocks;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class ModItemGroup {
    public static final ItemGroup TRADING_BLOCK_ITEM_GROUP = Registry.register(
            Registries.ITEM_GROUP,
            new Identifier(TradingBlock.MOD_ID,  "trading_block_item_group"),
            FabricItemGroup.builder()
                    .displayName(Text.translatable("itemGroup.trading_block_item_group"))
                    .icon(() -> new ItemStack(ModItems.TRADING_AGREEMENT))
                    .entries((displayContext, entries) -> {
                        entries.add(ModItems.TRADING_AGREEMENT);
                        entries.add(ModBlocks.TRADING_STATION);
                    })
                    .build()
    );
    public static void registerModItemGroup() {}
}
