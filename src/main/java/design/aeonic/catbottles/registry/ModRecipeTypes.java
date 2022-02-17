package design.aeonic.catbottles.registry;

import design.aeonic.catbottles.CatBottles;
import design.aeonic.catbottles.recipe.virtual.CatBathingRecipe;
import net.minecraft.world.item.crafting.RecipeType;

public class ModRecipeTypes {

//    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS = Registrateish.deferredRegister(ForgeRegistries.RECIPE_SERIALIZERS, FMLJavaModLoadingContext.get().getModEventBus());
//    public static final RegistryObject<SimpleRecipeSerializer<CatBathingRecipe>> CAT_BATHING_SERIALIZER = SERIALIZERS.register(
//            "cat_bathing", () -> new SimpleRecipeSerializer<>(CatBathingRecipe::new));

    public static final RecipeType<CatBathingRecipe> CAT_BATHING = RecipeType.register(CatBottles.MOD_ID + ":cat_bathing");

    public static void register() {}

}
