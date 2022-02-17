package design.aeonic.catbottles.base.block.entity;

import design.aeonic.catbottles.base.block.menu.ItemHandlerMenu;
import design.aeonic.catbottles.base.block.menu.SimpleMenu;
import design.aeonic.catbottles.base.misc.MultiMap;
import design.aeonic.catbottles.base.misc.QuadFunction;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import org.apache.commons.lang3.function.TriFunction;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.util.TriConsumer;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public abstract class ItemHandlerBlockEntity<E extends ItemHandlerBlockEntity<E, M>, M extends ItemHandlerMenu<E, M>> extends MenuBlockEntity<E, M> {

    protected final ItemStackHandler itemHandler = new ItemStackHandler(getSlotStructure());
    protected final LazyOptional<IItemHandler> itemHandlerCap = LazyOptional.of(() -> itemHandler);

    protected final Map<Direction, Pair<ItemStackHandlerDelegate, LazyOptional<IItemHandler>>> itemHandlerDelegates = new HashMap<>();

    /**
     * A constructor for an item handler blockentity. Implementing classes must also have a {@link MenuBlockEntityFactory}.
     */
    public ItemHandlerBlockEntity(BlockEntityType<E> type, BlockPos pos, BlockState state, Supplier<MenuType<M>> menuType, SimpleMenu.ServerMenuFactory<E, M> menuFactory) {
        super(type, pos, state, menuType, menuFactory);

        var struct = getSlotStructure();
        MultiMap<Direction, Integer> slotMap = new MultiMap<>();
        for (int i = 0; i < struct.length; i++) {
            for (var side: struct[i].automationAccessSides().get()) {
                slotMap.add(side, i);
            }
        }
        for (var side: Direction.values()) {
            var h = new ItemStackHandlerDelegate(itemHandler, side, slotMap.get(side));
            itemHandlerDelegates.put(side, new ImmutablePair<>(h, LazyOptional.of(() -> h)));
        }
    }

    public abstract ContainerData serverDataHolder();
    public abstract int numDataSlots();

    /**
     * Gets this blockentity's virtual slot structure. This should be stored as a static field.
     */
    public abstract SlotType[] getSlotStructure();

    /**
     * Returns {@link #getSlotRepeats(SlotType[])} )} with this blockentity's structure
     */
    public List<ImmutablePair<SlotType, Integer>> getSlotRepeats() {
        return getSlotRepeats(getSlotStructure());
    }

    public void onPlayerTookSlot(int index, ServerPlayer player, ItemStack stack) {
        if (index >= 0 && index < getSlotStructure().length) getSlotStructure()[index].onPlayerTookSlot.accept(this, player, stack);
    }

    /**
     * Run when the contents of the slot at the given index has changed.
     * Probably redundant but here in case you need it for some reason.
     */
    protected void onSlotChanged(int slot) {}

    /*
     * Used for quick stack moving.
     *
     * Each pair contains the SlotType and the number of repeats of that type before the next.
     * If there is a different SlotType between two of the same, they are added as separate pairs.
     */
    public static List<ImmutablePair<SlotType, Integer>> getSlotRepeats(@Nullable SlotType[] struct) {
        if (struct == null || struct.length == 0) return Collections.emptyList();

        var ret = new ArrayList<>(List.of(new MutablePair<>(struct[0], 1)));

        for (int i = 1, counter = 0; i < struct.length; i++) {
            var last = ret.get(counter);
            if (struct[i] == last.getLeft())
                last.setRight(last.getRight() + 1);
            else {
                counter++;
                ret.add(new MutablePair<>(struct[i], 1));
            }
        }

        return ret.stream().map(ImmutablePair::of).collect(Collectors.toList());
    }

    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);
        itemHandler.deserializeNBT(pTag.getCompound("Items"));
    }

    @Override
    protected void saveAdditional(CompoundTag pTag) {
        super.saveAdditional(pTag);
        pTag.put("Items", itemHandler.serializeNBT());
    }

    @NotNull
    @Override
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap) {
        return getCapability(cap, null);
    }

    @NotNull
    @Override
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return side == null ? itemHandlerCap.cast() : itemHandlerDelegates.get(side).getRight().cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void setRemoved() {
        super.setRemoved();
        itemHandlerCap.invalidate();
    }

    /**
     * An item handler that runs slot checks and manipulation functions on the {@link SlotType}s in {@link #slotStructure}.
     */
    protected class ItemStackHandler extends net.minecraftforge.items.ItemStackHandler {

        private final ItemHandlerBlockEntity<E, M> be = ItemHandlerBlockEntity.this;
        private final SlotType[] slotStructure;

        public ItemStackHandler(SlotType[] slotStructure) {
            super(slotStructure.length);
            this.slotStructure = slotStructure;
        }

        @Override
        protected void onContentsChanged(int slot) {
            be.setChanged();
            be.onSlotChanged(slot);
        }

        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            if (slot < slotStructure.length)
                return slotStructure[slot].isItemValidFunc.apply(be, stack);
            return super.isItemValid(slot, stack);
        }

        @NotNull
        @Override
        public ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
            if (slot < slotStructure.length) {
                var s = slotStructure[slot].insertItemFunc.apply(be, stack, null);
                return s == null ? super.insertItem(slot, stack, false) : s;
            }
            return super.insertItem(slot, stack, simulate);
        }

        @NotNull
        @Override
        public ItemStack extractItem(int slot, int amount, boolean simulate) {
            if (slot < slotStructure.length) {
                var s = slotStructure[slot].extractItemFunc.apply(be, getStackInSlot(slot), amount, null);
                return s == null ? super.extractItem(slot, amount, simulate) : s;
            }
            return super.extractItem(slot, amount, simulate);
        }

        // publicized functions for slot handler delegates
        public void validateSlotIndexDelegated(int slot) { validateSlotIndex(slot); }
        public int getStackLimitDelegated(int slot, ItemStack stack) { return getStackLimit(slot, stack); }
        public void onLoadDelegated() { }

        public ItemStack insertItemDelegated(int slot, ItemStack stack, boolean simulate, Direction dir) {
            if (slot < slotStructure.length) {
                var s = slotStructure[slot].insertItemFunc.apply(be, stack, dir);
                return s == null ? super.insertItem(slot, stack, false) : s;
            }
            return super.insertItem(slot, stack, simulate);
        }

        public ItemStack extractItemDelegated(int slot, int amount, boolean simulate, Direction dir) {
            if (slot < slotStructure.length) {
                var s = slotStructure[slot].extractItemFunc.apply(be, getStackInSlot(slot), amount, dir);
                return s == null ? super.extractItem(slot, amount, simulate) : s;
            }
            return super.extractItem(slot, amount, simulate);
        }
    }

    /**
     * A delegate handler for separate slots; passes function calls to the canonical item handler.
     */
    protected class ItemStackHandlerDelegate extends net.minecraftforge.items.ItemStackHandler {

        private final ItemStackHandler parent;
        private final List<Integer> indices;
        private final Direction direction;

        public ItemStackHandlerDelegate(ItemStackHandler parent, Direction direction, List<Integer> indices) {
            super(indices.size());
            this.parent = parent;
            this.indices = indices;
            this.direction = direction;
        }

        @Override
        protected void onContentsChanged(int slot) {
            parent.onContentsChanged(mapSlot(slot));
        }

        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            return parent.isItemValid(mapSlot(slot), stack);
        }

        @NotNull
        @Override
        public ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
            return parent.insertItemDelegated(mapSlot(slot), stack, simulate, direction);
        }

        @NotNull
        @Override
        public ItemStack extractItem(int slot, int amount, boolean simulate) {
            return parent.extractItemDelegated(mapSlot(slot), amount, simulate, direction);
        }

        @NotNull
        @Override
        public ItemStack getStackInSlot(int slot) {
            return parent.getStackInSlot(mapSlot(slot));
        }

        @Override
        public int getSlotLimit(int slot) {
            return parent.getSlotLimit(mapSlot(slot));
        }

        @Override
        public int getSlots() {
            return indices.size();
        }

        @Override
        public void setStackInSlot(int slot, @NotNull ItemStack stack) {
            parent.setStackInSlot(mapSlot(slot), stack);
        }

        @Override
        public void setSize(int size) {
            parent.setSize(parent.getSlots() - indices.size() + size);
        }

        @Override
        protected int getStackLimit(int slot, @NotNull ItemStack stack) {
            return parent.getStackLimitDelegated(mapSlot(slot), stack);
        }

        @Override
        protected void validateSlotIndex(int slot) {
            parent.validateSlotIndexDelegated(mapSlot(slot));
        }

        @Override
        protected void onLoad() {
            parent.onLoadDelegated();
        }

        @Override
        public CompoundTag serializeNBT() {
            return parent.serializeNBT();
        }

        @Override
        public void deserializeNBT(CompoundTag nbt) {
            parent.deserializeNBT(nbt);
        }

        protected int mapSlot(int slot) {
            return indices.get(slot);
        }
    }

    /**
     * Represents the type of a virtual slot, and data describing how it can be utilized.
     */
    public record SlotType(
            Sides automationAccessSides,
            BiFunction<ItemHandlerBlockEntity<?, ?>, ItemStack, Boolean> isItemValidFunc,
            TriFunction<ItemHandlerBlockEntity<?, ?>, ItemStack, Direction, ItemStack> insertItemFunc,
            QuadFunction<ItemHandlerBlockEntity<?, ?>, ItemStack, Integer, Direction, ItemStack> extractItemFunc,
            TriConsumer<ItemHandlerBlockEntity<?, ?>, ServerPlayer, ItemStack> onPlayerTookSlot) {

        public static final SlotType STORAGE = new SlotType( // General storage, always valid everywhere
                Sides.ALL,
                (e, s) -> true,
                (be, stack, dir) -> null,
                (be, stack, amt, dir) -> null,
                (be, player, stack) -> {});

        public static final SlotType INPUT = new SlotType( // Recipe inputs, no automated extraction
                Sides.ALL,
                STORAGE.isItemValidFunc,
                STORAGE.insertItemFunc,
                (be, stack, amt, dir) -> dir == null ? null : ItemStack.EMPTY,
                STORAGE.onPlayerTookSlot);

        public static final SlotType INPUT_TOP = new SlotType( // Recipe inputs, only insertable from top (ex. furnace)
                Sides.TOP,
                INPUT.isItemValidFunc,
                INPUT.insertItemFunc,
                INPUT.extractItemFunc,
                INPUT.onPlayerTookSlot);

        public static final SlotType RESULT = new SlotType( // Recipe result slot, only extract from any side
                Sides.ALL,
                (e, s) -> true,
                (be, stack, dir) -> stack,
                (be, stack, amt, dir) -> null,
                (be, player, stack) -> {});

        public static final SlotType RESULT_DOWN = new SlotType( // Recipe result slot, only extract from bottom (ex. furnace)
                Sides.BOTTOM,
                RESULT.isItemValidFunc,
                RESULT.insertItemFunc,
                RESULT.extractItemFunc,
                RESULT.onPlayerTookSlot);

        public static final SlotType FUEL = new SlotType( // Fuel slot, only allows furnace fuels
                Sides.ALL,
                (e, s) -> ForgeHooks.getBurnTime(s, RecipeType.SMELTING) > 0,
                (be, stack, dir) -> ForgeHooks.getBurnTime(stack, RecipeType.SMELTING) > 0 ? null : stack,
                (be, stack, amt, dir) -> stack.is(Items.BUCKET) || stack.is(Items.WATER_BUCKET) ? null : ItemStack.EMPTY,
                (be, player, stack) -> {});

        public static final SlotType FUEL_SIDES = new SlotType( // Recipe inputs, only insertable from sides (ex. furnace)
                Sides.NOT_UP, // exposed to all sides but only certain items can be extracted
                FUEL.isItemValidFunc,
                (be, stack, dir) -> Sides.SIDES.has(dir) ? SlotType.FUEL.insertItemFunc().apply(be, stack, dir) : stack,
                (be, stack, amt, dir) -> dir == Direction.DOWN ? SlotType.FUEL.extractItemFunc().apply(be, stack, amt, dir) : (dir == null ? null : ItemStack.EMPTY),
                FUEL.onPlayerTookSlot);

        public boolean isValid(ItemHandlerBlockEntity<?, ?> be, ItemStack stack) {
            return this.isItemValidFunc.apply(be, stack);
        }

    }

    /**
     * Represents valid sides for slot access
     */
    public enum Sides {

        TOP(Set.of(Direction.UP)),
        BOTTOM(Set.of(Direction.DOWN)),
        NORTH(Set.of(Direction.NORTH)),
        EAST(Set.of(Direction.EAST)),
        SOUTH(Set.of(Direction.SOUTH)),
        WEST(Set.of(Direction.WEST)),

        NONE(Set.of()),
        ALL(Set.of(
                Direction.UP, Direction.DOWN, Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST)),
        SIDES(Set.of(
                Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST)),
        VERTICAL(Set.of(
                Direction.UP, Direction.DOWN)),

        NOT_UP(TOP.inverse()),
        NOT_DOWN(BOTTOM.inverse()),
        NOT_NORTH(NORTH.inverse()),
        NOT_EAST(EAST.inverse()),
        NOT_SOUTH(SOUTH.inverse()),
        NOT_WEST(WEST.inverse());

        public final Set<Direction> sides;

        public Set<Direction> get() { return this.sides; }
        public Set<Direction> inverse() { var set = new HashSet<>(ALL.get()); set.removeAll(get()); return set; }

        public boolean has(Direction dir) { return this.sides.contains(dir); }
        public boolean hasAny(Sides sides) { return hasAny(sides.sides); }
        public boolean hasAny(Set<Direction> sides) {
            return !Collections.disjoint(this.sides, sides);
        }

        Sides(Set<Direction> sides) {
            this.sides = sides;
        }
    }
}
