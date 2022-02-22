package design.aeonic.catbottles.registry;

import com.tterrag.registrate.providers.ProviderType;
import com.tterrag.registrate.util.entry.ItemEntry;
import design.aeonic.catbottles.CatBottles;
import design.aeonic.catbottles.base.cats.ICatContainer;
import design.aeonic.catbottles.base.misc.Styles;
import design.aeonic.catbottles.content.GuiItem;
import design.aeonic.catbottles.content.bottles.CatBottleItem;
import design.aeonic.catbottles.content.bottles.ThrowableCatBottleItem;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Items;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ModItems {

    public static final Map<CatBottleItem.CatType, ItemEntry<CatBottleItem>> CAT_BOTTLES =
            Stream.of(ICatContainer.CatType.values()).collect(Collectors.toUnmodifiableMap($ -> $, type ->
                    CatBottles.REG.item("cat_bottle_" + type, (p) -> new CatBottleItem(p, type))
                            .properties(p -> p.stacksTo(1).fireResistant())
                            .lang("Bottle of Cat")
                            .tag(ModTags.Items.CAT_BOTTLE)
                            .tag(ModTags.getCatTypeTag(type))
                            .tab(() -> CreativeModeTab.TAB_MATERIALS)
                            .model((ctx, prv) -> prv.generated(ctx, type.getTexture()))
                            .register()));

    public static final Map<ThrowableCatBottleItem.CatType, ItemEntry<ThrowableCatBottleItem>> THROWABLE_CAT_BOTTLES =
            Stream.of(ICatContainer.CatType.values()).collect(Collectors.toUnmodifiableMap($ -> $, type ->
                    CatBottles.REG.item("throwable_cat_bottle_" + type, (p) -> new ThrowableCatBottleItem(p, type))
                            .properties(p -> p.stacksTo(1).fireResistant())
                            .lang("Splash Potion of Cat")
                            .tab(() -> CreativeModeTab.TAB_MATERIALS)
                            .model((ctx, prv) -> prv.generated(ctx, type.getTexture("throwable")))
                            .register()));

    public static final ItemEntry<GuiItem> ANY_CAT_BOTTLE = CatBottles.REG.item("any_cat_bottle",
                    p -> new GuiItem(p, ModLang.ANY_CAT_BOTTLE_NAME.setStyle(Styles.IMPORTANT),
                            ModLang.ANY_CAT_BOTTLE_TOOLTIP.setStyle(Styles.INFO))).register();

    public static void register() {
        CatBottles.REG.addDataGenerator(ProviderType.ITEM_TAGS, p -> {
            var likedByCats = p.tag(ModTags.Items.LIKED_BY_CATS);
            likedByCats.add(Items.STRING);
        });
    }
}
