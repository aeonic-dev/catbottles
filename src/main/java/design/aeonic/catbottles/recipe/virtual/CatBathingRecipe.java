package design.aeonic.catbottles.recipe.virtual;

import design.aeonic.catbottles.CatBottles;
import design.aeonic.catbottles.base.cats.ICatContainer;
import design.aeonic.catbottles.content.bottles.CatBottleItem;
import design.aeonic.catbottles.registry.ModItems;
import design.aeonic.catbottles.registry.ModRecipeTypes;
import design.aeonic.catbottles.registry.ModTags;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public record CatBathingRecipe(ResourceLocation id) implements Recipe<Container> {

    public static final CatBathingRecipe THE_ONLY_ONE =
            new CatBathingRecipe(new ResourceLocation(CatBottles.MOD_ID, "bathe_cat_bottle"));

    public static final Set<Item> INPUT_ITEMS = ModTags.Items.CAT_BOTTLE.getValues().stream()
            .filter(i -> ((CatBottleItem) i).getType() != ICatContainer.CatType.WHITE).collect(Collectors.toSet());
    public static final List<ItemStack> INPUT_STACKS = INPUT_ITEMS.stream().map(Item::getDefaultInstance).toList();
    public static final List<Ingredient> INPUT_INGREDIENTS = INPUT_ITEMS.stream().map(Ingredient::of).toList();

    public static final CatBottleItem OUTPUT_ITEM = ModItems.CAT_BOTTLES.get(ICatContainer.CatType.WHITE).get();

    @Override
    public boolean matches(Container pContainer, Level pLevel) {
        return pContainer.hasAnyOf(INPUT_ITEMS);
    }

    @Override
    public ItemStack assemble(Container pContainer) {
        if (pContainer.getContainerSize() < 1) return ItemStack.EMPTY;

        var stack = OUTPUT_ITEM.getDefaultInstance();
        stack.setTag(pContainer.getItem(0).getOrCreateTag());
        return stack;
    }

    @Override
    public boolean canCraftInDimensions(int pWidth, int pHeight) {
        return true;
    }

    @Override
    public ItemStack getResultItem() {
        return ItemStack.EMPTY;
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return new SimpleRecipeSerializer<>($ -> THE_ONLY_ONE);
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipeTypes.CAT_BATHING;
    }
}
