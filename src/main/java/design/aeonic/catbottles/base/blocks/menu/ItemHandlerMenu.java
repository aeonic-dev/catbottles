package design.aeonic.catbottles.base.blocks.menu;

import design.aeonic.catbottles.base.blocks.entity.ItemHandlerBlockEntity;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

import java.util.ArrayList;
import java.util.List;

public abstract class ItemHandlerMenu<E extends ItemHandlerBlockEntity<E, M>, M extends ItemHandlerMenu<E, M>> extends SimpleMenu<E, M> {
    protected final List<SlotItemHandler> blockSlots = new ArrayList<>();
    protected final List<SlotItemHandler> playerSlots = new ArrayList<>();
    protected final ContainerData dataHolder;

    @SuppressWarnings("unchecked")
    public ItemHandlerMenu(MenuType<M> pMenuType, int pContainerId, Inventory inv, E be) {
        super(pMenuType, pContainerId, inv, be);

        dataHolder = player instanceof ServerPlayer ? be.serverDataHolder() : new SimpleContainerData(be.numDataSlots());
        addDataSlots(dataHolder);

        be.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent((handler -> {
            var struct = be.getSlotStructure();
            var slotPositions = getSlotPositions(struct);
            assert slotPositions.length == struct.length : String.format(
                    "ItemHandlerMenu's slot positions array length %s does not match ItemHandlerBlockEntity's structure length %s!",
                    slotPositions.length, struct.length);

            for (int i = 0; i < struct.length; i++) {
                blockSlots.add((MenuSlot) addSlot(new MenuSlot(
                        handler, i, slotPositions[i].x(), slotPositions[i].y())));
            }
        }));

        for(int i = 0; i < 3; ++i) {
            for(int j = 0; j < 9; ++j) {
                playerSlots.add((SlotItemHandler) addSlot(new SlotItemHandler(playerInventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18)));
            }
        }
        for(int k = 0; k < 9; ++k) {
            playerSlots.add((SlotItemHandler) addSlot(new SlotItemHandler(playerInventory, k, 8 + k * 18, 142)));
        }
    }

    @Override
    public ItemStack quickMoveStack(Player pPlayer, int pIndex) {
        var resultStack = ItemStack.EMPTY;
        var slot = slots.get(pIndex);

        if (slot.hasItem()) {
            var slotStruct = blockEntity.isEmpty() ? new ItemHandlerBlockEntity.SlotType[0] : blockEntity.get().getSlotStructure();
            var repeats = ItemHandlerBlockEntity.getSlotRepeats(slotStruct);
            var sourceStack = slot.getItem();
            resultStack = sourceStack.copy();

            if (isBlockSlot(pIndex)) {
                assert pIndex >= 0 && pIndex < slotStruct.length;
//                if (slotStruct[pIndex].playerCanExtract(blockEntity.get(), pPlayer))
                if (!moveItemStackTo(sourceStack, minPlayerSlot(), maxPlayerSlot() + 1, false))
                    return ItemStack.EMPTY;
            }
            else if (isPlayerSlot(pIndex)) {
                var flag = false;
                for (int i = 0, repeatIndex = 0; i < repeats.size(); i++) {
                    var pair = repeats.get(i);
                    var slotType = pair.getLeft();

                    if (blockEntity.isPresent() && slotType.isValid(blockEntity.get(), sourceStack))
                        if (!moveItemStackTo(sourceStack, repeatIndex, repeatIndex += pair.getRight(), false))
                            flag = true;
                }

                if (isHotbarSlot(pIndex))
                    if (!moveItemStackTo(sourceStack, minPlayerSlot(), maxPlayerSlot() - 8, false))
                        return ItemStack.EMPTY;
                else {
                    if (!moveItemStackTo(sourceStack, maxPlayerSlot() - 8, maxPlayerSlot() + 1, false))
                        return ItemStack.EMPTY;
                }
                if (flag) return ItemStack.EMPTY;
            }

            if (sourceStack.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            }
            else {
                slot.setChanged();
            }

            if (sourceStack.getCount() == resultStack.getCount())
                return ItemStack.EMPTY;

            slot.onTake(pPlayer, sourceStack);
        }

        return resultStack;
    }

    public class MenuSlot extends SlotItemHandler {

        public MenuSlot(IItemHandler itemHandler, int index, int xPosition, int yPosition) {
            super(itemHandler, index, xPosition, yPosition);
        }

        @Override
        public void onTake(Player pPlayer, ItemStack pStack) {
            super.onTake(pPlayer, pStack);
            onPlayerTookSlot(getSlotIndex(), pPlayer, pStack);
        }
    }

    protected void onPlayerTookSlot(int index, Player player, ItemStack stack) {
        if (blockEntity.isPresent() && player instanceof ServerPlayer serverPlayer) {
            blockEntity.get().onPlayerTookSlot(index, serverPlayer, stack);
        }
    }

    protected int minBlockSlot() { return blockSlots.size() > 0 ? 0 : -1; }
    protected int maxBlockSlot() { return blockSlots.size() - 1; }
    protected boolean isBlockSlot(int index) { return index >= minBlockSlot() && index <= maxBlockSlot(); }

    protected int minPlayerSlot() { return blockSlots.size(); }
    protected int maxPlayerSlot() { return minPlayerSlot() + playerSlots.size() - 1; }
    protected boolean isPlayerSlot(int index) { return index >= minPlayerSlot() && index <= maxPlayerSlot(); }
    protected boolean isHotbarSlot(int index) { return index >= maxPlayerSlot() - 8 && index <= maxPlayerSlot(); }

    /**
     * Gets the positions of slots defined in this menu's coupled {@link ItemHandlerBlockEntity}.
     * This array must be of the same length as the passed slot structure.<br /><br />
     * I recommended storing the blockentity class's slot structure in a static field and
     * utilizing it here, but the struct is passed anyway for convenience.
     */
    protected abstract SlotPosition[] getSlotPositions(ItemHandlerBlockEntity.SlotType[] structure);

    /**
     * Represents the x and y position of a menu's gui slot.
     */
    public record SlotPosition(int x, int y) {}

}
