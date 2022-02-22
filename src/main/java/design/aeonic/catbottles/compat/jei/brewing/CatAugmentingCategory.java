package design.aeonic.catbottles.compat.jei.brewing;

import design.aeonic.catbottles.CatBottles;
import design.aeonic.catbottles.base.cats.ICatContainer;
import design.aeonic.catbottles.registry.ModItems;
import design.aeonic.catbottles.registry.ModLang;
import mezz.jei.api.helpers.IGuiHelper;
import net.minecraft.resources.ResourceLocation;

public class CatAugmentingCategory extends BaseBrewingCategory {
    public CatAugmentingCategory(IGuiHelper guiHelper) {
        super(guiHelper,
                guiHelper.createDrawableIngredient(ModItems.THROWABLE_CAT_BOTTLES.get(ICatContainer.CatType.WHITE).asStack()),
                ModLang.CAT_AUGMENTING);
    }

    @Override
    public ResourceLocation getUid() {
        return new ResourceLocation(CatBottles.MOD_ID, "cat_augmenting");
    }
}
