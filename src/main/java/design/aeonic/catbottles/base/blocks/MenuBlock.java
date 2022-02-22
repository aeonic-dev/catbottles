package design.aeonic.catbottles.base.blocks;

import design.aeonic.catbottles.base.blocks.entity.MenuBlockEntity;
import design.aeonic.catbottles.base.blocks.menu.SimpleMenu;
import design.aeonic.catbottles.registry.Registrateish;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.network.NetworkHooks;

import java.util.function.Supplier;

public abstract class MenuBlock<E extends MenuBlockEntity<E, M>, M extends SimpleMenu<E, M>> extends SimpleEntityBlock<E> {

    /**
     * A constructor for use within {@link Registrateish#bazinga}. All arguments are passed from the registry; do not manually set them.
     */
    public MenuBlock(Properties blockProps, Supplier<BlockEntityType<E>> blockEntityType, MenuBlockEntity.MenuBlockEntityFactory<E, M> entityFactory, Supplier<MenuType<M>> menuType, SimpleMenu.ServerMenuFactory<E, M> menuFactory) {
        super(blockProps, blockEntityType, (t, p, s) -> entityFactory.create(t, p, s, menuType, menuFactory));
    }

    @Override
    @SuppressWarnings("deprecation")
    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        var entity = pLevel.getBlockEntity(pPos);
        if (entity != null && entity.getType() == blockEntityType.get()) {
            if (!pLevel.isClientSide()) {
                NetworkHooks.openGui((ServerPlayer) pPlayer, (MenuProvider) entity, (buf) -> {
                    buf.writeBlockPos(entity.getBlockPos());
                    buf.writeNbt(entity.getUpdateTag());
                });
                return InteractionResult.CONSUME;
            }
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    @FunctionalInterface
    public interface MenuBlockFactory<B extends MenuBlock<E, M>, E extends MenuBlockEntity<E, M>, M extends SimpleMenu<E, M>> {
        B create(Properties blockProps, Supplier<BlockEntityType<E>> blockEntityType, MenuBlockEntity.MenuBlockEntityFactory<E, M> entityFactory, Supplier<MenuType<M>> menuType, SimpleMenu.ServerMenuFactory<E, M> menuFactory);
    }
}
