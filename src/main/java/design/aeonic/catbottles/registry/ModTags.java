package design.aeonic.catbottles.registry;

import design.aeonic.catbottles.CatBottles;
import design.aeonic.catbottles.base.cats.ICatContainer;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.Tag;
import net.minecraft.world.item.Item;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public final class ModTags {
    public static final class Blocks {

    }
    public static final class Items {
        public static final Tag.Named<Item> CAT_BOTTLE = ItemTags.bind(CatBottles.MOD_ID + ":cat_bottles");
        public static final Map<ICatContainer.CatType, Tag.Named<Item>> CAT_BOTTLES = Arrays.stream(ICatContainer.CatType.values())
                .collect(Collectors.toUnmodifiableMap($ -> $, t -> ItemTags.bind(getCatTypeTagName(t))));

        public static final Tag.Named<Item> LIKED_BY_CATS = ItemTags.bind(CatBottles.MOD_ID + ":liked_by_cats");
    }

    public static String getCatTypeTagName(ICatContainer.CatType type) {
        return CatBottles.MOD_ID + ":cat_bottles/" + type.getSerializedName();
    }

    public static Tag.Named<Item> getCatTypeTag(ICatContainer.CatType type) {
        return Items.CAT_BOTTLES.get(type);
    }
}
