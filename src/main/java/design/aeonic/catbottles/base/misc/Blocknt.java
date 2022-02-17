package design.aeonic.catbottles.base.misc;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;

import javax.annotation.Nonnull;

public abstract class Blocknt extends BlockItem {

    public Blocknt(Block block, Properties props) {
        super(block, props);
    }

    @Nonnull
    @Override
    public String getDescriptionId() {
        // Need to override or will conflict with blockitem keys, because registrate doesn't like blockitems
        // that aren't registered under the block entry
        return getOrCreateDescriptionId();
    }
}
