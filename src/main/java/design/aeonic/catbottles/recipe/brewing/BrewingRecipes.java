package design.aeonic.catbottles.recipe.brewing;

import design.aeonic.catbottles.registry.ModItems;
import net.minecraft.world.item.ItemStack;
import org.apache.logging.log4j.LogManager;

import java.util.ArrayList;
import java.util.List;

public class BrewingRecipes {

    public static final List<ModBrewingRecipe> RECIPES = new ArrayList<>();

    static {
        DyeCatRecipes.DYE_MAP.forEach((tag, map) -> {
            var ingredients = tag.getValues().stream().map(ItemStack::new).toList();
            map.forEach((input, output) -> RECIPES.add(new ModBrewingRecipe(ingredients,
                    List.of(ModItems.CAT_BOTTLES.get(input).get().getDefaultInstance()),
                    ModItems.CAT_BOTTLES.get(output).get().getDefaultInstance())));
        });
    }
}
