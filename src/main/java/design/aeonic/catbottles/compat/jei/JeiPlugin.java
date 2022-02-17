package design.aeonic.catbottles.compat.jei;

import design.aeonic.catbottles.CatBottles;
import design.aeonic.catbottles.base.misc.ILog;
import design.aeonic.catbottles.compat.jei.brewing.CatDyeingCategory;
import design.aeonic.catbottles.recipe.brewing.ModBrewingRecipe;
import design.aeonic.catbottles.recipe.virtual.CatBathingRecipe;
import design.aeonic.catbottles.recipe.brewing.BrewingRecipes;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.registration.*;
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
public class JeiPlugin implements IModPlugin, ILog {

	private static final ResourceLocation UID = new ResourceLocation(CatBottles.MOD_ID, "jei_plugin");

	private CatBathingCategory catBathingCategory;
	private CatDyeingCategory catDyeingCategory;

	@Override
	public void registerCategories(IRecipeCategoryRegistration reg) {
		IJeiHelpers jeiHelpers = reg.getJeiHelpers();
		IGuiHelper guiHelper = jeiHelpers.getGuiHelper();

		reg.addRecipeCategories(catBathingCategory = new CatBathingCategory(guiHelper));
		reg.addRecipeCategories(catDyeingCategory = new CatDyeingCategory(guiHelper));
	}

	@Override
	public void registerRecipes(@Nonnull IRecipeRegistration reg) {
		var factory = reg.getVanillaRecipeFactory();

		ItemInfo.addItemInfo(reg);
		reg.addRecipes(BrewingRecipes.RECIPES, catDyeingCategory.getUid());
		reg.addRecipes(List.of(CatBathingRecipe.THE_ONLY_ONE), catBathingCategory.getUid());
	}

	@Override
	public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
		registration.addRecipeCatalyst(new ItemStack(Blocks.CAULDRON), catBathingCategory.getUid());
		registration.addRecipeCatalyst(new ItemStack(Blocks.BREWING_STAND), catDyeingCategory.getUid());
	}

	@Nonnull
	@Override
	public ResourceLocation getPluginUid() {
		return UID;
	}

}
