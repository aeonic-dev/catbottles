package design.aeonic.catbottles.registry;

import design.aeonic.catbottles.CatBottles;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.Tag;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public final class ModTags {
    public static final class Blocks {

    }
    public static final class Items {
        public static final Tag.Named<Item> CAT_BOTTLE = ItemTags.bind(CatBottles.MOD_ID + ":cat_bottle");
    }
}
