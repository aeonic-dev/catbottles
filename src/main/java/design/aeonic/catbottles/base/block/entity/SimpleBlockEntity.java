package design.aeonic.catbottles.base.block.entity;

import design.aeonic.catbottles.base.block.SimpleEntityBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

/**
 * A simple abstracted block entity with a coupled {@link SimpleEntityBlock}.
 */
public abstract class SimpleBlockEntity extends BlockEntity {

    protected SimpleBlockEntity(BlockEntityType<? extends SimpleBlockEntity> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public static <T extends BlockEntity> void tick(Level level, BlockPos pos, BlockState state, T t) {
        if (t instanceof SimpleBlockEntity be)
            be.tick();
    }

    public void tick() {}
}
