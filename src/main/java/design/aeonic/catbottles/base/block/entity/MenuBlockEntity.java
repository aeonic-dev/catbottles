package design.aeonic.catbottles.base.block.entity;

import design.aeonic.catbottles.CatBottles;
import design.aeonic.catbottles.base.block.menu.SimpleMenu;
import design.aeonic.catbottles.registry.Registrateish;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.Objects;
import java.util.function.Supplier;

public abstract class MenuBlockEntity<E extends MenuBlockEntity<E, M>, M extends SimpleMenu<E, M>> extends SimpleBlockEntity implements MenuProvider {

    private final Supplier<MenuType<M>> menuType;
    private final SimpleMenu.ServerMenuFactory<E, M> menuFactory;

    /**
     * A constructor for use within {@link Registrateish#bazinga}. All arguments are passed from the factory; do not manually set them.
     */
    public MenuBlockEntity(BlockEntityType<E> type, BlockPos pos, BlockState state, Supplier<MenuType<M>> menuType, SimpleMenu.ServerMenuFactory<E, M> menuFactory) {
        super(type, pos, state);
        this.menuType = menuType;
        this.menuFactory = menuFactory;
    }

    @Nonnull
    @Override
    public Component getDisplayName() {
        return getDisplayName(getBlockState().getBlock());
    }

    public static Component getDisplayName(Block block) {
        return new TranslatableComponent("container." + CatBottles.MOD_ID + "." + Objects.requireNonNull(block.getRegistryName()).getPath());
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pInventory, Player pPlayer) {
        // Server menu
        return menuFactory.create(menuType.get(), pContainerId, pInventory, this);
    }

    @FunctionalInterface
    public interface MenuBlockEntityFactory<E extends MenuBlockEntity<E, M>, M extends SimpleMenu<E, M>> {
        E create(BlockEntityType<E> type, BlockPos pos, BlockState state, Supplier<MenuType<M>> menuType, SimpleMenu.ServerMenuFactory<E, M> menuFactory);
    }
}
