package design.aeonic.catbottles.compat.jei.brewing;

import com.mojang.blaze3d.vertex.PoseStack;
import design.aeonic.catbottles.recipe.brewing.CatBrewingRecipe;
import mezz.jei.api.constants.ModIds;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.ITickTimer;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.gui.ingredient.IGuiItemStackGroup;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

/**
 * Lots of copy pasting from the JEI vanilla plugin
 */
public abstract class BaseBrewingCategory implements IRecipeCategory<CatBrewingRecipe.DisplayBrewingRecipe> {

    public static final ResourceLocation BREWING_GUI = new ResourceLocation(ModIds.JEI_ID, "textures/gui/gui_vanilla.png");

    private static final int brewPotionSlot1 = 0;
    private static final int brewPotionSlot2 = 1;
    private static final int brewPotionSlot3 = 2;
    private static final int brewIngredientSlot = 3;
    private static final int outputSlot = 4; // for display only

    private final IDrawable background;
    private final IDrawable icon;
    private final IDrawable slotDrawable;
    private final Component localizedName;
    private final IDrawableAnimated arrow;
    private final IDrawableAnimated bubbles;
    private final IDrawableStatic blazeHeat;

    public BaseBrewingCategory(IGuiHelper guiHelper, IDrawable icon, TranslatableComponent name) {
        ResourceLocation location = BREWING_GUI;
        background = guiHelper.drawableBuilder(location, 0, 0, 64, 60)
                .addPadding(1, 0, 0, 50)
                .build();
        this.icon = icon;
        localizedName = name;

        arrow = guiHelper.drawableBuilder(location, 64, 0, 9, 28)
                .buildAnimated(400, IDrawableAnimated.StartDirection.TOP, false);

        ITickTimer bubblesTickTimer = new BrewingBubblesTickTimer(guiHelper);
        bubbles = guiHelper.drawableBuilder(location, 73, 0, 12, 29)
                .buildAnimated(bubblesTickTimer, IDrawableAnimated.StartDirection.BOTTOM);

        blazeHeat = guiHelper.createDrawable(location, 64, 29, 18, 4);

        slotDrawable = guiHelper.getSlotDrawable();
    }

    @Override
    public Class<CatBrewingRecipe.DisplayBrewingRecipe> getRecipeClass() {
        return CatBrewingRecipe.DisplayBrewingRecipe.class;
    }

    @Override
    public Component getTitle() {
        return localizedName;
    }

    @Override
    public IDrawable getBackground() {
        return background;
    }

    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    public void setIngredients(CatBrewingRecipe.DisplayBrewingRecipe recipe, IIngredients ingredients) {
        ingredients.setInputLists(VanillaTypes.ITEM, List.of(
                recipe.inputs(),
                recipe.inputs(),
                recipe.inputs(),
                recipe.ingredients()));

        ingredients.setOutput(VanillaTypes.ITEM, recipe.output());
    }

    @Override
    public void draw(CatBrewingRecipe.DisplayBrewingRecipe recipe, PoseStack poseStack, double mouseX, double mouseY) {
        blazeHeat.draw(poseStack, 5, 30);
        bubbles.draw(poseStack, 8, 0);
        arrow.draw(poseStack, 42, 2);

        TranslatableComponent steps = new TranslatableComponent("gui.jei.category.brewing.steps", 1);
        Minecraft minecraft = Minecraft.getInstance();
        minecraft.font.draw(poseStack, steps, 70, 28, 0xFF808080);
    }

    @Override
    public void setRecipe(IRecipeLayout recipeLayout, CatBrewingRecipe.DisplayBrewingRecipe recipe, IIngredients ingredients) {
        IGuiItemStackGroup itemStacks = recipeLayout.getItemStacks();

        itemStacks.init(brewPotionSlot1, true, 0, 36);
        itemStacks.init(brewPotionSlot2, true, 23, 43);
        itemStacks.init(brewPotionSlot3, true, 46, 36);
        itemStacks.init(brewIngredientSlot, true, 23, 2);
        itemStacks.init(outputSlot, false, 80, 2);

        itemStacks.setBackground(outputSlot, slotDrawable);

        itemStacks.set(ingredients);
    }

    private static class BrewingBubblesTickTimer implements ITickTimer {
        private static final int[] BUBBLE_LENGTHS = new int[]{29, 23, 18, 13, 9, 5, 0};
        private final ITickTimer internalTimer;

        public BrewingBubblesTickTimer(IGuiHelper guiHelper) {
            this.internalTimer = guiHelper.createTickTimer(14, BUBBLE_LENGTHS.length - 1, false);
        }

        @Override
        public int getValue() {
            int timerValue = this.internalTimer.getValue();
            return BUBBLE_LENGTHS[timerValue];
        }

        @Override
        public int getMaxValue() {
            return BUBBLE_LENGTHS[0];
        }
    }

}
