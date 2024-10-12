package com.deerinforest.trading_block.block.entity;

import com.deerinforest.trading_block.item.custom.TradingAgreement;
import com.deerinforest.trading_block.screen.TradingStationScreenHandler;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.village.TradeOffer;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class TradingStationEntity extends BlockEntity implements ImplementedInventory, ExtendedScreenHandlerFactory {
    private final DefaultedList<ItemStack> inventory = DefaultedList.ofSize(4, ItemStack.EMPTY);

    private static final int TA_SLOT = 0;
    private static final int BUY_SLOT_1 = 1;
    private static final int BUY_SLOT_2 = 2;
    private static final int SELL_SLOT = 3;

    protected final PropertyDelegate propertyDelegate;
    private int progress = 0;
    private int maxProgress = 72;

    public TradingStationEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.TRADING_STATION_BE, pos, state);
        this.propertyDelegate = new PropertyDelegate() {
            @Override
            public int get(int index) {
                return switch (index) {
                    case 0 -> TradingStationEntity.this.progress;
                    case 1 -> TradingStationEntity.this.maxProgress;
                    default -> 0;
                };
            }

            @Override
            public void set(int index, int value) {
                switch (index) {
                    case 0 -> TradingStationEntity.this.progress = value;
                    case 1 -> TradingStationEntity.this.maxProgress = value;
                }
            }

            @Override
            public int size() {
                return 2;
            }
        };
    }

    @Override
    public void writeScreenOpeningData(ServerPlayerEntity player, PacketByteBuf buf) {
        buf.writeBlockPos(this.pos);
    }

    @Override
    public DefaultedList<ItemStack> getItems() {
        return inventory;
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        Inventories.writeNbt(nbt, inventory);
        nbt.putInt("trading_station.progress", progress);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        Inventories.readNbt(nbt, inventory);
        progress = nbt.getInt("trading_station.progress");
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        return new TradingStationScreenHandler(syncId, playerInventory, this, this.propertyDelegate);
    }

    public void tick(World world, BlockPos pos, BlockState state) {
        if (world.isClient()) return;
        if (this.isOutputSlotEmptyOrReceivable()) {
            if(this.isTASatisfied()) {
                this.increaseCraftProgress();
                markDirty(world, pos, state);
                if(this.hasCraftingFinished()) {
                    this.craftItem();
                    this.resetProgress();
                }
            } else {
                this.resetProgress();
            }
        } else {
            this.resetProgress();
            markDirty(world, pos, state);
        }
    }

    private void resetProgress() {
        this.progress = 0;
    }

    private void craftItem() {
        TradeOffer tradeOffer = TradingAgreement.getTradeOffer(getStack(TA_SLOT));
        ItemStack firstBuyItem = tradeOffer.getAdjustedFirstBuyItem(), secondBuyItem = tradeOffer.getSecondBuyItem(),
                sellItem = tradeOffer.getSellItem();
        this.removeStack(BUY_SLOT_1, firstBuyItem.getCount());
        this.removeStack(BUY_SLOT_2, secondBuyItem.getCount());
        this.setStack(SELL_SLOT, new ItemStack(sellItem.getItem(), getStack(SELL_SLOT).getCount() + sellItem.getCount()));
    }

    private boolean hasCraftingFinished() {
        return progress >= maxProgress;
    }

    private void increaseCraftProgress() {
        progress++;
    }

    private boolean isTASatisfied() {
        if (!(getStack(TA_SLOT).getItem() instanceof TradingAgreement)) return false;
        if (!getStack(TA_SLOT).hasNbt()) return false;  // Unsigned TA
        TradeOffer tradeOffer = TradingAgreement.getTradeOffer(getStack(TA_SLOT));
        ItemStack firstBuyItem = tradeOffer.getAdjustedFirstBuyItem(), secondBuyItem = tradeOffer.getSecondBuyItem(),
                sellItem = tradeOffer.getSellItem();
        boolean firstBuyItemSatisfied =
                getStack(BUY_SLOT_1).getItem() == firstBuyItem.getItem()
                        && getStack(BUY_SLOT_1).getCount() >= firstBuyItem.getCount();
        boolean secondBuyItemSatisfied =
                secondBuyItem.getCount() == 0
                || (getStack(BUY_SLOT_2).getItem() == secondBuyItem.getItem()
                        && getStack(BUY_SLOT_2).getCount() >= secondBuyItem.getCount());
        return firstBuyItemSatisfied && secondBuyItemSatisfied && canInsertItemIntoOutputSlot(sellItem);
    }

    private boolean canInsertItemIntoOutputSlot(ItemStack stack) {
        return this.getStack(SELL_SLOT).isEmpty()
                || (this.getStack(SELL_SLOT).getItem() == stack.getItem()
                && this.getStack(SELL_SLOT).getCount() + stack.getCount() <= getStack(SELL_SLOT).getMaxCount());
    }

    private boolean isOutputSlotEmptyOrReceivable() {
        return this.getStack(SELL_SLOT).isEmpty() || this.getStack(SELL_SLOT).getCount() < this.getStack(SELL_SLOT).getMaxCount();
    }

    @Override
    public Text getDisplayName() {
        return Text.translatable("block.trading_block.trading_station.display_name");
    }
}
