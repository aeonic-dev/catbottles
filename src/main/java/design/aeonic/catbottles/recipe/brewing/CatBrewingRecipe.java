package design.aeonic.catbottles.recipe.brewing;

import design.aeonic.catbottles.base.cats.ICatContainer;
import design.aeonic.catbottles.registry.ModItems;
import net.minecraft.tags.Tag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.brewing.IBrewingRecipe;
import org.apache.logging.log4j.LogManager;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CatBrewingRecipe implements IBrewingRecipe {

    public static void wakeUp() {}

    public static final CatBrewingRecipe DYEING = new CatBrewingRecipe(Map.of(
            Tags.Items.DYES_BROWN, Map.of(
                    ICatContainer.CatType.PERSIAN.tag(),            ICatContainer.CatType.TABBY.baseItem(),
                    ICatContainer.CatType.WHITE.tag(),              ICatContainer.CatType.SIAMESE.baseItem(),
                    ICatContainer.CatType.RAGDOLL.tag(),            ICatContainer.CatType.SIAMESE.baseItem(),
                    ICatContainer.CatType.RED.tag(),                ICatContainer.CatType.CALICO.baseItem()),

            Tags.Items.DYES_ORANGE, Map.of(
                    ICatContainer.CatType.WHITE.tag(),              ICatContainer.CatType.RED.baseItem(),
                    ICatContainer.CatType.SIAMESE.tag(),            ICatContainer.CatType.CALICO.baseItem()),

            Tags.Items.DYES_WHITE, Map.of(
                    ICatContainer.CatType.RED.tag(),                ICatContainer.CatType.RED.baseItem(),
                    ICatContainer.CatType.ALL_BLACK.tag(),          ICatContainer.CatType.BLACK.baseItem(),
                    ICatContainer.CatType.BLACK.tag(),              ICatContainer.CatType.JELLIE.baseItem(),
                    ICatContainer.CatType.JELLIE.tag(),             ICatContainer.CatType.RAGDOLL.baseItem(),
                    ICatContainer.CatType.BRITISH_SHORTHAIR.tag(),  ICatContainer.CatType.RAGDOLL.baseItem()),

            Tags.Items.DYES_GRAY, Map.of(
                    ICatContainer.CatType.WHITE.tag(),              ICatContainer.CatType.JELLIE.baseItem()),

            Tags.Items.DYES_LIGHT_GRAY, Map.of(
                    ICatContainer.CatType.WHITE.tag(),              ICatContainer.CatType.BRITISH_SHORTHAIR.baseItem(),
                    ICatContainer.CatType.RAGDOLL.tag(),            ICatContainer.CatType.BRITISH_SHORTHAIR.baseItem()),

            Tags.Items.DYES_BLACK, Map.of(
                    ICatContainer.CatType.WHITE.tag(),              ICatContainer.CatType.BLACK.baseItem(),
                    ICatContainer.CatType.BLACK.tag(),              ICatContainer.CatType.ALL_BLACK.baseItem())
    ));

    private static final Map<Tag<Item>, Map<Tag<Item>, ICatContainer.CatItem>> AUGMENTING_MAP = new HashMap<>(Map.of(
            Tags.Items.GUNPOWDER, new HashMap<>()
    ));
    static {
        for (var type: ICatContainer.CatType.values()) {
            AUGMENTING_MAP.get(Tags.Items.GUNPOWDER).put(type.tag(), ModItems.THROWABLE_CAT_BOTTLES.get(type).get());
        }
    }
    public static final CatBrewingRecipe AUGMENTING = new CatBrewingRecipe(AUGMENTING_MAP);

    private final Map<Tag<Item>, Map<Tag<Item>, ICatContainer.CatItem>> recipeMap;
    private final List<Tag<Item>> validInputs;
    private final List<Tag<Item>> validIngredients;

    public CatBrewingRecipe(Map<Tag<Item>, Map<Tag<Item>, ICatContainer.CatItem>> map) {
        this.recipeMap = map;
        validInputs = this.recipeMap.values().stream().map(Map::keySet).flatMap(Set::stream).toList();
        validIngredients = this.recipeMap.keySet().stream().toList();
    }

    public List<DisplayBrewingRecipe> getDisplayRecipes() {
        return getRecipeMap().keySet().stream().map(ingredient -> {
            var val = getRecipeMap().get(ingredient);
            return val.keySet().stream().map(input ->
                new DisplayBrewingRecipe(
                        ingredient.getValues().stream().map(ItemStack::new).toList(),
                        input.getValues().stream().map(ItemStack::new).toList(),
                        val.get(input).asItem().getDefaultInstance()
                )).toList();
        }).flatMap(List::stream).toList();
    }

    @Override
    public boolean isInput(ItemStack input) {
        for (var tag: getValidInputs()) {
            if (tag.contains(input.getItem())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isIngredient(ItemStack ingredient) {
        for (var tag: getValidIngredients()) {
            if (tag.contains(ingredient.getItem())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public ItemStack getOutput(ItemStack input, ItemStack ingredient) {
        LogManager.getLogger().info(getRecipeMap());
        var type = getOutput(input.getItem(), ingredient.getItem());
        if (type != null && input.getItem() instanceof ICatContainer.CatItem inputContainer) {
            var stack = new ItemStack(type.asItem());
            var instance = type.catInstance(stack);
            instance.merge(new ICatContainer.CatWrapper<>(inputContainer, input));
            LogManager.getLogger().info("instance {} item {}", instance, instance.get());
            return instance.get();
        }
        LogManager.getLogger().info("empty, type {}", type);
        return ItemStack.EMPTY;
    }

    @Nullable
    public ICatContainer.CatItem getOutput(Item input, Item ingredient) {
        for (var key: getRecipeMap().keySet()) {
            if (key.contains(ingredient)) {
                var val = getRecipeMap().get(key);
                for (var inp: val.keySet()) {
                    if (inp.contains(input)) {
                        return val.get(inp);
                    }
                }
            }
        }
        return null;
    }

    public List<Tag<Item>> getValidInputs() {
        return validInputs;
    }

    public List<Tag<Item>> getValidIngredients() {
        return validIngredients;
    }

    @Nonnull
    public Map<Tag<Item>, Map<Tag<Item>, ICatContainer.CatItem>> getRecipeMap() { return recipeMap; }

    public record DisplayBrewingRecipe(List<ItemStack> ingredients, List<ItemStack> inputs, ItemStack output) {}

}
