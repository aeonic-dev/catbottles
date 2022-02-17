package design.aeonic.catbottles.registry;

import com.tterrag.registrate.util.entry.ItemEntry;
import design.aeonic.catbottles.CatBottles;
import design.aeonic.catbottles.content.bottles.CatBottleItem;
import design.aeonic.catbottles.base.cats.ICatContainer;
import net.minecraft.world.item.CreativeModeTab;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ModItems {

    public static final Map<CatBottleItem.CatType, ItemEntry<CatBottleItem>> CAT_BOTTLES =
            Stream.of(ICatContainer.CatType.values()).collect(Collectors.toUnmodifiableMap($ -> $, type ->
                    CatBottles.REG.item("cat_bottle_" + type, (p) -> new CatBottleItem(p, type))
                            .properties(p -> p.stacksTo(1))
                            .lang("Bottle of Cat")
                            .tag(ModTags.Items.CAT_BOTTLE)
                            .tab(() -> CreativeModeTab.TAB_MATERIALS)
                            .model((ctx, prv) -> prv.generated(ctx, type.getTexture()))
                            .register()));

    public static void register() {}

}
