package com.deerinforest.trading_block.item.custom;

import com.deerinforest.trading_block.item.ModItems;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.TradeOfferList;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class TradingAgreement extends Item {
    private static String ItemWithCount(ItemStack item) {
        return item.getCount() + "x " + item.getName().getString();
    }
    private static final String SELECTED_INDEX_NBT = "selected_index";

    public static String TradeOfferToString(TradeOffer tradeOffer) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(ItemWithCount(tradeOffer.getAdjustedFirstBuyItem()));
        if (tradeOffer.getSecondBuyItem() != null && tradeOffer.getSecondBuyItem().getCount() != 0) {
            stringBuilder.append(" + ");
            stringBuilder.append(ItemWithCount(tradeOffer.getSecondBuyItem()));
        }
        stringBuilder.append(" = ");
        stringBuilder.append(ItemWithCount(tradeOffer.getSellItem()));
        return stringBuilder.toString();
    }

    public static void SignWithVillager(ItemStack stack, TradeOfferList tradeOfferList) {
        NbtCompound nbt = tradeOfferList.toNbt();
        nbt.putInt(SELECTED_INDEX_NBT, 0);
        stack.setNbt(nbt);
    }

    public TradingAgreement(Settings settings) {
        super(settings);
    }

    @Override
    public boolean hasGlint(ItemStack stack) {
        return stack.hasNbt();
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        if (stack.hasNbt()) {
            TradeOfferList tradeOfferList = getTradeOfferList(stack);
            if (context.isAdvanced()) {
                tooltip.add(Text.translatable("item.trading_block.trading_agreement.full_list"));
                for (TradeOffer tradeOffer: tradeOfferList) {
                    tooltip.add(Text.literal(TradeOfferToString(tradeOffer)));
                }
            } else {
                int selectedIndex = getSelectedIndex(stack), length = tradeOfferList.size();
                tooltip.add(Text.translatable("item.trading_block.trading_agreement.shift_right_click").formatted(Formatting.RED, Formatting.ITALIC)
                        .append(Text.literal(TradeOfferToString(tradeOfferList.get((selectedIndex + length - 1) % length))).formatted(Formatting.DARK_GRAY, Formatting.ITALIC)));
                tooltip.add(Text.literal(TradeOfferToString(tradeOfferList.get(selectedIndex))));
                tooltip.add(Text.translatable("item.trading_block.trading_agreement.right_click").formatted(Formatting.GREEN, Formatting.ITALIC)
                        .append(Text.literal(TradeOfferToString(tradeOfferList.get((selectedIndex + 1) % length))).formatted(Formatting.DARK_GRAY, Formatting.ITALIC)));
            }
        }
    }

    private void informClientCurrentOffer(ItemStack stack) {
        if (stack.hasNbt()) {
            MinecraftClient client = MinecraftClient.getInstance();
            if (client != null && client.player != null) {
                client.inGameHud.setOverlayMessage(
                        Text.translatable("item.trading_block.trading_agreement.current").formatted(Formatting.YELLOW)
                                .append(Text.literal(TradeOfferToString(getTradeOffer(stack))).formatted(Formatting.WHITE)), false);
            }
        }
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);
        if (!world.isClient) {
            if (stack.getItem() == ModItems.TRADING_AGREEMENT && stack.hasNbt()) {
                NbtCompound nbt = stack.getNbt();
                TradeOfferList tradeOfferList = getTradeOfferList(stack);
                int selectedIndex = getSelectedIndex(stack), length = tradeOfferList.size();
                if (user.isSneaking()) nbt.putInt(SELECTED_INDEX_NBT, (selectedIndex + length - 1) % length);
                    else nbt.putInt(SELECTED_INDEX_NBT, (selectedIndex + 1) % length);
                stack.setNbt(nbt);
                informClientCurrentOffer(stack);
            }
        }
        return super.use(world, user, hand);
    }

    public static TradeOfferList getTradeOfferList(ItemStack stack) {
        if (stack.hasNbt()) return new TradeOfferList(stack.getNbt());
            else return null;
    }

    public static int getSelectedIndex(ItemStack stack) {
        if (stack.hasNbt()) return stack.getNbt().getInt(SELECTED_INDEX_NBT);
            else return 0;
    }

    public static TradeOffer getTradeOffer(ItemStack stack) {
        if (stack.hasNbt()) return getTradeOfferList(stack).get(getSelectedIndex(stack));
            else return null;
    }
}
