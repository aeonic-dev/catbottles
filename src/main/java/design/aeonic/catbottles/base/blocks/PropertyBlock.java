package design.aeonic.catbottles.base.blocks;

import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.Property;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nullable;
import java.util.function.Function;

public abstract class PropertyBlock extends Block {

    protected PropertyBlock(Properties blockProps) {
        super(blockProps);
    }

    protected abstract StateProperty<?, ?>[] getStateProperties();

    /**
     * Shorthand to get the {@link Property} at the given index.
     * Useful in combination with an enum to keep track of properties in a block class.
     */
    @Nullable
    protected Property<?> prop(int index) {
        return getStateProperties().length > index ? getStateProperties()[index].getProperty() : null;
    }

    /**
     * Represents an immutable pair of a blockstate property and its value when placed.
     */
    public static class StateProperty<T extends Comparable<T>, V extends T> {
        private final Pair<Property<T>, StatePropertyApplier<V>> pair;

        protected StateProperty(Property<T> property, StatePropertyApplier<V> placementValue) { this(ImmutablePair.of(property, placementValue)); }
        protected StateProperty(Pair<Property<T>, StatePropertyApplier<V>> pair) { this(ImmutablePair.of(pair)); }
        protected StateProperty(ImmutablePair<Property<T>, StatePropertyApplier<V>> pair) { this.pair = pair; }

        public static <T extends Comparable<T>, V extends T> StateProperty<T, V> of(ImmutablePair<Property<T>, StatePropertyApplier<V>> pair) {
            return new StateProperty<>(pair);
        }
        public static <T extends Comparable<T>, V extends T> StateProperty<T, V> of(Pair<Property<T>, StatePropertyApplier<V>> pair) {
            return new StateProperty<>(ImmutablePair.of(pair));
        }
        public static <T extends Comparable<T>, V extends T> StateProperty<T, V> of(Property<T> property, StatePropertyApplier<V> placementValue) {
            return new StateProperty<>(ImmutablePair.of(property, placementValue));
        }

        public Property<T> getProperty() { return pair.getLeft(); }
        public T getValue(BlockPlaceContext ctx) { return pair.getRight().apply(ctx); }

        public BlockState apply(BlockState state, BlockPlaceContext ctx) {
            return state.setValue(pair.getLeft(), getValue(ctx));
        }

    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        var state = super.getStateForPlacement(pContext);
        if (state == null) return null;
        for (var prop: getStateProperties()) {
            state = prop.apply(state, pContext);
        }
        return state;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        super.createBlockStateDefinition(pBuilder);
        for (var prop: getStateProperties()) {
            pBuilder.add(prop.getProperty());
        }
    }

    /**
     * A functional interface that returns a {@link Property}'s value based on the placement context.
     * Used for {@link Block#getStateForPlacement(BlockPlaceContext)}
     */
    protected interface StatePropertyApplier<T extends Comparable<?>> extends Function<BlockPlaceContext, T> {}
}