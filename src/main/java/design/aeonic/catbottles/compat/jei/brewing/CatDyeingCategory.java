package design.aeonic.catbottles.compat.jei.brewing;

import design.aeonic.catbottles.CatBottles;
import design.aeonic.catbottles.registry.ModItems;
import design.aeonic.catbottles.registry.ModLang;
import mezz.jei.api.helpers.IGuiHelper;
import net.minecraft.resources.ResourceLocation;

public class CatDyeingCategory extends BaseBrewingCategory {
    public CatDyeingCategory(IGuiHelper guiHelper) {
        super(guiHelper,
                guiHelper.createDrawableIngredient(ModItems.ANY_CAT_BOTTLE.asStack()),
                ModLang.CAT_DYEING);
    }

    @Override
    public ResourceLocation getUid() {
        return new ResourceLocation(CatBottles.MOD_ID, "cat_dyeing");
    }
}
