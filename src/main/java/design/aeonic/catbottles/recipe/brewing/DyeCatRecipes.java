package design.aeonic.catbottles.recipe.brewing;

import design.aeonic.catbottles.content.bottles.CatBottleItem;
import design.aeonic.catbottles.base.cats.ICatContainer.CatType;
import design.aeonic.catbottles.base.cats.ICatContainer;
import design.aeonic.catbottles.registry.ModItems;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.brewing.BrewingRecipe;
import net.minecraftforge.common.brewing.IBrewingRecipe;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DyeCatRecipes implements IBrewingRecipe {

    public static final DyeCatRecipes INSTANCE = new DyeCatRecipes();

    public static final Map<Tags.IOptionalNamedTag<Item>, Map<CatType, CatType>> DYE_MAP = Map.of(
            Tags.Items.DYES_BROWN, Map.of(
                    ICatContainer.CatType.PERSIAN,            ICatContainer.CatType.TABBY,
                    ICatContainer.CatType.WHITE,              ICatContainer.CatType.SIAMESE,
                    ICatContainer.CatType.RAGDOLL,            ICatContainer.CatType.SIAMESE,
                    ICatContainer.CatType.RED,                ICatContainer.CatType.CALICO
            ),
            Tags.Items.DYES_ORANGE, Map.of(
                    ICatContainer.CatType.WHITE,              ICatContainer.CatType.RED,
                    ICatContainer.CatType.SIAMESE,            ICatContainer.CatType.CALICO
            ),
            Tags.Items.DYES_WHITE, Map.of(
                    ICatContainer.CatType.RED,                ICatContainer.CatType.PERSIAN,
                    ICatContainer.CatType.ALL_BLACK,          ICatContainer.CatType.BLACK,
                    ICatContainer.CatType.BLACK,              ICatContainer.CatType.JELLIE,
                    ICatContainer.CatType.JELLIE,             ICatContainer.CatType.RAGDOLL,
                    ICatContainer.CatType.BRITISH_SHORTHAIR,  ICatContainer.CatType.RAGDOLL
            ),
            Tags.Items.DYES_GRAY, Map.of(
                    ICatContainer.CatType.WHITE,              ICatContainer.CatType.JELLIE
            ),
            Tags.Items.DYES_LIGHT_GRAY, Map.of(
                    ICatContainer.CatType.WHITE,              ICatContainer.CatType.BRITISH_SHORTHAIR,
                    ICatContainer.CatType.RAGDOLL,            ICatContainer.CatType.BRITISH_SHORTHAIR
            ),
            Tags.Items.DYES_BLACK, Map.of(
                    ICatContainer.CatType.WHITE,              ICatContainer.CatType.BLACK,
                    ICatContainer.CatType.BLACK,              ICatContainer.CatType.ALL_BLACK
            )
    );

    @Override
    public boolean isInput(ItemStack stack) {
        return stack.getItem() instanceof CatBottleItem;
    }

    @Override
    public boolean isIngredient(ItemStack ingredient) {
        if (ingredient.getItem().getTags().contains(Tags.Items.DYES.getName())) {
            for (var tag: DYE_MAP.keySet()) {
                if (ingredient.is(tag)) return true;
            }
        }
        return false;
    }

    @Nonnull
    @Override
    public ItemStack getOutput(ItemStack stack, @Nonnull ItemStack ingredient) {
        var item = (CatBottleItem) stack.getItem();
        for (var tag: DYE_MAP.keySet()) {
            if (ingredient.is(tag)) {
                var oldType = item.getCatType(stack);
                var oldInstance = item.catInstance(stack);

                if (DYE_MAP.get(tag).containsKey(oldType)) {
                    CatType type = DYE_MAP.get(tag).get(oldType);
                    var newItem = ModItems.CAT_BOTTLES.get(type).get();
                    var instance = newItem.catInstance(new ItemStack(newItem));
                    instance.merge(oldInstance);
                    return instance.get();
                }
                return stack;
            }
        }
        return stack;
    }
}
