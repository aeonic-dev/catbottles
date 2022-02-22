package design.aeonic.catbottles.content;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Dummy item for gui rendering
 */
public class GuiItem extends Item {

    private final @Nullable Component name;
    private final @Nullable List<Component> tooltip;

    public GuiItem(Properties props) {
        this(props, null, (List<Component>) null);
    }

    public GuiItem(Properties props, @Nullable Component name) {
        this(props, name, (List<Component>) null);
    }

    public GuiItem(Properties props, @Nullable Component name, Component tooltip) {
        this(props, name, List.of(tooltip));
    }

    public GuiItem(Properties pProperties, @Nullable Component name, @Nullable List<Component> tooltip) {
        super(pProperties);
        this.name = name;
        this.tooltip = tooltip;
    }

    @Override
    public Component getName(ItemStack pStack) {
        return name == null ? super.getName(pStack) : name;
    }

    @Override
    public void appendHoverText(ItemStack pStack, @org.jetbrains.annotations.Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
        super.appendHoverText(pStack, pLevel, pTooltipComponents, pIsAdvanced);
        if (tooltip != null)
            pTooltipComponents.addAll(tooltip);
    }
}
