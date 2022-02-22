package design.aeonic.catbottles.compat.jei;

import design.aeonic.catbottles.CatBottles;
import design.aeonic.catbottles.recipe.virtual.CatBathingRecipe;
import design.aeonic.catbottles.registry.ModItems;
import design.aeonic.catbottles.registry.ModLang;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class CatBathingCategory implements IRecipeCategory<CatBathingRecipe> {

    private final IDrawable cauldron;
    private final IDrawable background;

    public CatBathingCategory(IGuiHelper guiHelper) {

        cauldron = guiHelper.createDrawableIngredient(new ItemStack(Items.CAULDRON));
        background = guiHelper.drawableBuilder(new ResourceLocation(
                CatBottles.MOD_ID, "textures/gui/cat_bathing.png"),
                        0, 0, 101, 41).setTextureSize(101, 41).build();
    }

    @Override
    public ResourceLocation getUid() {
        return new ResourceLocation(CatBottles.MOD_ID, "cat_bathing");
    }

    @Override
    public Class<CatBathingRecipe> getRecipeClass() {
        return CatBathingRecipe.class;
    }

    @Override
    public Component getTitle() {
        return ModLang.CAT_BATHING;
    }

    @Override
    public IDrawable getBackground() {
        return background;
    }

    @Override
    public IDrawable getIcon() {
        return cauldron;
    }

    @Override
    public void setIngredients(CatBathingRecipe recipe, IIngredients ingredients) {
        ingredients.setInputIngredients(CatBathingRecipe.INPUT_INGREDIENTS);
        ingredients.setOutput(VanillaTypes.ITEM, new ItemStack(CatBathingRecipe.OUTPUT_ITEM));
    }

    @Override
    public void setRecipe(IRecipeLayout recipeLayout, CatBathingRecipe recipe, IIngredients ingredients) {
        var stacks = recipeLayout.getItemStacks();

        stacks.init(0, true, 7,  12);
        stacks.init(1, true, 72, 12);
        stacks.init(2, true, 35, 13);

        stacks.set(0, ModItems.ANY_CAT_BOTTLE.asStack());
        stacks.set(1, CatBathingRecipe.OUTPUT_ITEM.getDefaultInstance());
        stacks.set(2, new ItemStack(Items.CAULDRON).setHoverName(new TranslatableComponent("block.minecraft.water_cauldron")));
    }

}
