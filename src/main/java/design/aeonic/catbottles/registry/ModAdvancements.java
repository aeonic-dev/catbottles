package design.aeonic.catbottles.registry;

import com.tterrag.registrate.providers.ProviderType;
import com.tterrag.registrate.providers.RegistrateLangProvider;
import design.aeonic.catbottles.CatBottles;
import design.aeonic.catbottles.base.cats.ICatContainer;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.FrameType;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;
import java.util.function.Supplier;

@SuppressWarnings("SameParameterValue")
public class ModAdvancements {

    public static final ResourceLocation MAIN_GROUP_BACKGROUND = new ResourceLocation(
            "minecraft:textures/block/sculk_sensor_bottom.png");

    public static AdvancementEntry CAT_BOTTLES = new AdvancementEntry(() ->
        Advancement.Builder.advancement()
                .display(ModItems.CAT_BOTTLES.get(ICatContainer.CatType.getDefault()).get(),
                        getNameKey("unpawful_confinement"),
                        getDescKey("unpawful_confinement", "Find a more convenient place for your cat"),
                        MAIN_GROUP_BACKGROUND, FrameType.TASK, true, true, false)
                .addCriterion("has_cat_bottle",InventoryChangeTrigger.TriggerInstance.hasItems(
                        ItemPredicate.Builder.item().of(ModTags.Items.CAT_BOTTLE).build()))
//                                .of(ModItems.CAT_BOTTLES.values().stream().map(ItemEntry::get).toArray(Item[]::new)).build()))
                .build(new ResourceLocation(CatBottles.MOD_ID, "main/cat_bottles")));

//    public static AdvancementEntry UNPAWFUL_CONFINEMENT = new AdvancementEntry(() ->
//            Advancement.Builder.advancement()
//                    .parent(CAT_BOTTLES.get())
//                    .display(
//                            ModItems.CAT_BOTTLES.get(CatBottleItem.CatType.WHITE).get(),
//                            getNameKey("unpawful_confinement"),
//                            getDescKey("unpawful_confinement", "Find a more convenient place for your cat"),
//                            null, FrameType.TASK, true, true, false)
//                    .addCriterion("has_cat_bottle", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().of(ModItems.CAT_BOTTLES.values().stream().map(ItemEntry::get).toArray(Item[]::new)).build()))
//                    .build(new ResourceLocation(CatBottles.MOD_ID, "main/unpawful_confinement")));

    public static void register() {
        CatBottles.REG.addDataGenerator(ProviderType.ADVANCEMENT, prv -> {
            prv.accept(CAT_BOTTLES.get());
//            prv.accept(UNPAWFUL_CONFINEMENT.get());
        });
    }

    private static TranslatableComponent getNameKey(String name) {
        return getNameKey(name, null);
    }

    private static TranslatableComponent getNameKey(String name, @Nullable String englishName) {
        return getLocalized(name, "title", englishName);
    }

    private static TranslatableComponent getDescKey(String name, String englishDescription) {
        return getLocalized(name, "description", englishDescription);
    }

    private static TranslatableComponent getLocalized(String name, String suffix, @Nullable String englishName) {
        var s = getLocalizationKey("main", name, suffix);
        CatBottles.REG.addRawLang(s, englishName == null ? RegistrateLangProvider.toEnglishName(name) : englishName);
        return new TranslatableComponent(s);
    }

    private static String getLocalizationKey(String category, String name, String suffix) {
        return String.format("advancements.%s.%s.%s.%s", CatBottles.MOD_ID, category, name, suffix);
    }

    /**
     * An over-engineered advancement supplier to allow static initialization before items are registered
     */
    public static class AdvancementEntry {
        private final Supplier<Advancement> supplier;
        private Advancement advancement;

        public AdvancementEntry(Supplier<Advancement> supplier) {
            this.supplier = supplier;
        }

        public Advancement get() {
            if (advancement == null)
                return advancement = supplier.get();
            return advancement;
        }
    }
}
