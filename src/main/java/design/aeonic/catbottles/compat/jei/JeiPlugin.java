package design.aeonic.catbottles.compat.jei;

import design.aeonic.catbottles.CatBottles;
import design.aeonic.catbottles.recipe.brewing.CatBrewingRecipe;
import design.aeonic.catbottles.compat.jei.brewing.CatAugmentingCategory;
import design.aeonic.catbottles.compat.jei.brewing.CatDyeingCategory;
import design.aeonic.catbottles.recipe.virtual.CatBathingRecipe;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * A class for the mod's JEI integration.
 * @author aeonic-dev
 */
@mezz.jei.api.JeiPlugin
public class JeiPlugin implements IModPlugin {

	private static final ResourceLocation UID = new ResourceLocation(CatBottles.MOD_ID, "jei_plugin");

	private CatBathingCategory catBathingCategory;
	private CatDyeingCategory catDyeingCategory;
	private CatAugmentingCategory catAugmentingCategory;

	@Override
	public void registerCategories(IRecipeCategoryRegistration reg) {
		IJeiHelpers jeiHelpers = reg.getJeiHelpers();
		IGuiHelper guiHelper = jeiHelpers.getGuiHelper();

		reg.addRecipeCategories(catBathingCategory = new CatBathingCategory(guiHelper));
		reg.addRecipeCategories(catDyeingCategory = new CatDyeingCategory(guiHelper));
		reg.addRecipeCategories(catAugmentingCategory = new CatAugmentingCategory(guiHelper));
	}

	@Override
	public void registerRecipes(@Nonnull IRecipeRegistration reg) {
		var dyeing = CatBrewingRecipe.DYEING.getDisplayRecipes();
		var augmenting = CatBrewingRecipe.AUGMENTING.getDisplayRecipes();

		reg.addRecipes(dyeing, catDyeingCategory.getUid());
		reg.addRecipes(augmenting, catAugmentingCategory.getUid());

		reg.addRecipes(List.of(CatBathingRecipe.THE_ONLY_ONE), catBathingCategory.getUid());
		ItemInfo.addItemInfo(reg);
	}

	@Override
	public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
		registration.addRecipeCatalyst(new ItemStack(Blocks.CAULDRON), catBathingCategory.getUid());
		registration.addRecipeCatalyst(new ItemStack(Blocks.BREWING_STAND), catDyeingCategory.getUid());
		registration.addRecipeCatalyst(new ItemStack(Blocks.BREWING_STAND), catAugmentingCategory.getUid());
	}

	@Nonnull
	@Override
	public ResourceLocation getPluginUid() {
		return UID;
	}

}
