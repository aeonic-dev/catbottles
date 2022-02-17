package design.aeonic.catbottles.recipe.brewing;

import mezz.jei.api.recipe.vanilla.IJeiBrewingRecipe;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public record ModBrewingRecipe(List<ItemStack> ingredients, List<ItemStack> inputs, ItemStack output) implements IJeiBrewingRecipe {

    public static ModBrewingRecipe create(List<ItemStack> ingredients, List<ItemStack> inputs, ItemStack output) {
        return new ModBrewingRecipe(ingredients, inputs, output);
    }

    @Override
    public int getBrewingSteps() {
        return 1;
    }
}
