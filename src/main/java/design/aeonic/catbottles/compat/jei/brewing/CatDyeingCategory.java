package design.aeonic.catbottles.compat.jei.brewing;

import design.aeonic.catbottles.CatBottles;
import design.aeonic.catbottles.registry.ModLang;
import mezz.jei.api.helpers.IGuiHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class CatDyeingCategory extends BaseBrewingCategory {
    public CatDyeingCategory(IGuiHelper guiHelper) {
        super(guiHelper,
                guiHelper.createDrawableIngredient(new ItemStack(Items.WHITE_DYE)),
                ModLang.CAT_DYEING);
    }

    @Override
    public ResourceLocation getUid() {
        return new ResourceLocation(CatBottles.MOD_ID, "cat_dyeing");
    }
}
