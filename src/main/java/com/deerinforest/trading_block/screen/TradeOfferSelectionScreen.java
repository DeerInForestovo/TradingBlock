package com.deerinforest.trading_block.screen;

import com.deerinforest.trading_block.TradingBlock;
import com.deerinforest.trading_block.item.custom.TradingAgreement;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.TradeOfferList;

@Environment(EnvType.CLIENT)
public class TradeOfferSelectionScreen extends Screen {

    // Texture and its size
    private static final int PANEL_WIDTH = 246;
    private static final int PANEL_HEIGHT = 164;
    public final Identifier PANEL_TEXTURE = new Identifier(TradingBlock.MOD_ID, "textures/gui/trade_selection.png");

    private int panelX;
    private int panelY;
    private final TradeOfferList tradeOfferList;
    private TradeOfferSelectionScreen.TradeOfferListWidget tradeOfferListWidget;

    public TradeOfferSelectionScreen(TradeOfferList tradeOfferList) {
        super(Text.translatable("screen.trade_offer_selection_screen_title"));
        this.tradeOfferList = tradeOfferList;
    }

    @Override
    protected void init() {
        panelX = (width - PANEL_WIDTH) / 2;
        panelY = (height - PANEL_HEIGHT) / 2;
        this.tradeOfferListWidget = new TradeOfferListWidget(client, tradeOfferList);
        this.addSelectableChild(tradeOfferListWidget);
        this.addDrawableChild(ButtonWidget
                .builder(ScreenTexts.DONE, (button) -> { this.onDone(); })
                .dimensions((width - 150) / 2, height - 38, 150, 20)
                .build());
    }

    void onDone() {
        TradeOfferListWidget.TradeOfferEntry selectedTradeOfferEntry = (TradeOfferListWidget.TradeOfferEntry) this.tradeOfferListWidget.getSelectedOrNull();
        if (selectedTradeOfferEntry != null) {
            PacketByteBuf buf = PacketByteBufs.create();
            buf.writeNbt(selectedTradeOfferEntry.tradeOffer.toNbt());
            // Send back to the server
            ClientPlayNetworking.send(TradingBlock.TRADE_OFFER_SELECTION_CHANNEL, buf);
        }

        this.client.setScreen(null);
    }

    private void renderPanel(DrawContext context) {
        context.drawTexture(PANEL_TEXTURE, panelX, panelY, 0, 0, PANEL_WIDTH, PANEL_HEIGHT);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context);
        this.renderPanel(context);
        this.tradeOfferListWidget.render(context, mouseX, mouseY, delta);
        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    @Environment(EnvType.CLIENT)
    private class TradeOfferListWidget extends AlwaysSelectedEntryListWidget<TradeOfferListWidget.TradeOfferEntry> {
        public TradeOfferListWidget(MinecraftClient client, TradeOfferList tradeOfferList) {
            super(client, PANEL_WIDTH, PANEL_HEIGHT, panelY + 2, panelY + PANEL_HEIGHT, 20);
            this.setLeftPos(panelX);
            this.setRenderBackground(false);
            this.setRenderHeader(false, 0);
            this.setRenderHorizontalShadows(false);
            for (TradeOffer tradeOffer: tradeOfferList) {
                this.addEntry(new TradeOfferEntry(tradeOffer));
            }
        }

        @Environment(EnvType.CLIENT)
        public class TradeOfferEntry extends AlwaysSelectedEntryListWidget.Entry<TradeOfferEntry> {
            private final TradeOffer tradeOffer;

            public TradeOfferEntry(TradeOffer tradeOffer) {
                this.tradeOffer = tradeOffer;
            }

            @Override
            public void render(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
                context.drawText(TradeOfferSelectionScreen.this.textRenderer, TradingAgreement.TradeOfferToString(this.tradeOffer).formatted(Formatting.BLACK), x, y, 0xFFFFFF, hovered);
            }

            @Override
            public boolean mouseClicked(double mouseX, double mouseY, int button) {
                if (button == 0) {
                    TradeOfferListWidget.this.setSelected(this);
                    return true;
                } else {
                    return false;
                }
            }

            @Override
            public Text getNarration() {
                return Text.of("Narration Unset");  // TODO
            }
        }
    }
}