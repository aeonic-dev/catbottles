package design.aeonic.catbottles.base.blocks;

import com.tterrag.registrate.builders.BlockEntityBuilder;
import design.aeonic.catbottles.base.blocks.entity.SimpleBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public abstract class SimpleEntityBlock<E extends SimpleBlockEntity> extends PropertyBlock implements EntityBlock {
    protected final BlockEntityBuilder.BlockEntityFactory<E> entityFactory;
    protected final Supplier<BlockEntityType<E>> blockEntityType;

    protected SimpleEntityBlock(Properties blockProps, Supplier<BlockEntityType<E>> blockEntityType, BlockEntityBuilder.BlockEntityFactory<E> entityFactory) {
        super(blockProps);
        this.entityFactory = entityFactory;
        this.blockEntityType = blockEntityType;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return entityFactory.create(blockEntityType.get(), pPos, pState);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
        return pBlockEntityType == blockEntityType.get() ? SimpleBlockEntity::tick : null;
    }
}
