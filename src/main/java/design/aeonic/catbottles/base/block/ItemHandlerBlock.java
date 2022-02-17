package design.aeonic.catbottles.base.block;

import design.aeonic.catbottles.base.block.entity.ItemHandlerBlockEntity;
import design.aeonic.catbottles.base.block.entity.MenuBlockEntity;
import design.aeonic.catbottles.base.block.menu.ItemHandlerMenu;
import design.aeonic.catbottles.base.block.menu.SimpleMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Containers;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.items.CapabilityItemHandler;

import java.util.function.Supplier;

public abstract class ItemHandlerBlock<E extends ItemHandlerBlockEntity<E, M>, M extends ItemHandlerMenu<E, M>> extends MenuBlock<E, M> {

    public ItemHandlerBlock(Properties blockProps, Supplier<BlockEntityType<E>> blockEntityType, MenuBlockEntity.MenuBlockEntityFactory<E, M> entityFactory, Supplier<MenuType<M>> menuType, SimpleMenu.ServerMenuFactory<E, M> menuFactory) {
        super(blockProps, blockEntityType, entityFactory, menuType, menuFactory);
    }

    /**
     * Whether the item handler should drop its items when broken.
     */
    private boolean keepItemsOnDestroy(BlockState state) {
        // TODO: Implement data modifiers so this actually works lol
        return false;
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pIsMoving) {
        if (!pState.is(pNewState.getBlock())) {
            var be = pLevel.getBlockEntity(pPos);
            if (be != null && pLevel instanceof ServerLevel) {
                if (!keepItemsOnDestroy(pState)) {
                    be.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(inv -> {
                        double x, y, z;
                        x = pPos.getX();
                        y = pPos.getY();
                        z = pPos.getZ();

                        for (int i = 0; i < inv.getSlots(); i++) {
                            Containers.dropItemStack(pLevel, x, y, z, inv.getStackInSlot(i));
                        }
                    });
                    pLevel.updateNeighbourForOutputSignal(pPos, pState.getBlock());
                }
            }
        }

        super.onRemove(pState, pLevel, pPos, pNewState, pIsMoving);
    }
}
