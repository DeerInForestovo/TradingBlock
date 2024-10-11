package com.deerinforest.trading_block.item.custom;

import com.deerinforest.trading_block.TradingBlock;
import com.deerinforest.trading_block.screen.TradeOfferSelectionScreen;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.TradeOfferList;
import net.minecraft.village.VillagerProfession;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

public class TradingAgreement extends Item {
    private static String ItemWithCount(ItemStack item) {
        return item.getCount() + "x " + item.getName().getString();
    }

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

    public static void initializeItem() {
        /*
         * Use a TradingAgreement to record the TradeOfferList of the villager and send it to the client.
         */
        UseEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            if (!world.isClient && !player.isSpectator()
                    && player.getStackInHand(hand).getItem() instanceof TradingAgreement tradingAgreement
                    && entity instanceof VillagerEntity villager) {
                if (villager.getVillagerData().getProfession() != VillagerProfession.NONE
                        && villager.getVillagerData().getProfession() != VillagerProfession.NITWIT) {
                    TradeOfferList tradeOfferList = villager.getOffers();
                    PacketByteBuf buf = PacketByteBufs.create();
                    tradeOfferList.toPacket(buf);
                    ServerPlayNetworking.send((ServerPlayerEntity) player, TradingBlock.TRADE_OFFER_LIST_CHANNEL, buf);
                    return ActionResult.SUCCESS;
                } else {
                    // TODO: tell the user this villager has no profession
                    return ActionResult.FAIL;
                }
            }
            return ActionResult.PASS;
        });

        /*
         * Open a screen on the client side
         * Next see: item/screens/TradeOfferSelectionScreen.java
         */
        ClientPlayNetworking.registerGlobalReceiver(TradingBlock.TRADE_OFFER_LIST_CHANNEL, (client, handler, buf, responseSender) -> {
            TradeOfferSelectionScreen screen = new TradeOfferSelectionScreen(TradeOfferList.fromPacket(buf));
            MinecraftClient.getInstance().execute(() -> MinecraftClient.getInstance().setScreen(screen));
        });

        /*
        * The client has made a decision on the client side, and it is sent to the server side now
        */
        ServerPlayNetworking.registerGlobalReceiver(TradingBlock.TRADE_OFFER_SELECTION_CHANNEL, (server, player, handler, buf, responseSender) -> {
            TradeOffer tradeOffer = new TradeOffer(Objects.requireNonNull(buf.readNbt()));
            ItemStack mainHandItemStack = player.getMainHandStack();
            if (mainHandItemStack.getItem() instanceof TradingAgreement) {
                mainHandItemStack.setNbt(tradeOffer.toNbt());
                player.getMainHandStack().setCustomName(Text.translatable("item.trading_block.trading_agreement_signed").formatted(Formatting.ITALIC));
            }
        });
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
            TradeOffer tradeOffer = new TradeOffer(stack.getNbt());
            tooltip.add(Text.of(TradeOfferToString(tradeOffer)));
        }
    }

    public static TradeOffer getTradeOffer(ItemStack stack) {
        if (stack.hasNbt()) return new TradeOffer(stack.getNbt());
            else return null;
    }
}
