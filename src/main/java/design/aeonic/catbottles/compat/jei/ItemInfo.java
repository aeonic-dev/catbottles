package design.aeonic.catbottles.compat.jei;

import design.aeonic.catbottles.content.bottles.CatBottleItem;
import design.aeonic.catbottles.registry.ModLang;
import design.aeonic.catbottles.registry.ModTags;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.stream.Collectors;

public class ItemInfo {

    public static void addItemInfo(IRecipeRegistration reg) {
//        reg.addIngredientInfo(key.getLeft(), VanillaTypes.ITEM, key.getRight());
        reg.addIngredientInfo(ModTags.Items.CAT_BOTTLE.getValues().stream().map(Item::getDefaultInstance).toList(),
                VanillaTypes.ITEM, ModLang.INFO_CAT_BOTTLE_ITEM);
    }
}
