package design.aeonic.catbottles.base.block.menu;

import design.aeonic.catbottles.base.block.entity.MenuBlockEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;

import javax.annotation.CheckForNull;
import java.util.Optional;

public abstract class SimpleMenu<E extends MenuBlockEntity<E, M>, M extends SimpleMenu<E, M>> extends AbstractContainerMenu {

    @SuppressWarnings("all")
    protected final Optional<E> blockEntity;
    protected final Player player;
    protected final IItemHandler playerInventory;

    @SuppressWarnings("unchecked") // blockentity will match type
    public SimpleMenu(MenuType<M> pMenuType, int pContainerId, Inventory inv, @CheckForNull BlockEntity be) {
        // Common Constructor
        super(pMenuType, pContainerId);

        blockEntity = Optional.ofNullable((E) be);
        player = inv.player;
        playerInventory = new InvWrapper(inv);
    }

    @Override
    public boolean stillValid(Player pPlayer) {
        if (blockEntity.isEmpty()) return false;
        var be = blockEntity.get();
        return be.getLevel() != null && stillValid(ContainerLevelAccess.create(be.getLevel(), be.getBlockPos()), player, be.getBlockState().getBlock());
    }

    public interface ServerMenuFactory<E extends MenuBlockEntity<E, M>, M extends SimpleMenu<E, M>> {
        M create(MenuType<M> menuType, int containerId, Inventory inventory, BlockEntity be);
    }
}
