package design.aeonic.catbottles.registry;

import com.tterrag.registrate.providers.RegistrateLangProvider;
import design.aeonic.catbottles.CatBottles;
import design.aeonic.catbottles.base.cats.ICatContainer;
import net.minecraft.network.chat.TranslatableComponent;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ModLang {

    // JEI INFO
    public static final TranslatableComponent INFO_CAT_BOTTLE_ITEM = addLang(
            "info.item.cat_bottles", "Obtained by right-clicking a tamed cat with a glass bottle. " +
                    "Can be placed as a decoration block if you're into that kinda thing.");

    // GUIS
    public static final TranslatableComponent CAT_BATHING = addLang("gui.cat_bathing.name", "Cat Bathing");
    public static final TranslatableComponent CAT_DYEING = addLang("gui.cat_dyeing.name", "Cat Dyeing");

    public static final TranslatableComponent YES = new TranslatableComponent("gui.yes");
    public static final TranslatableComponent NO = new TranslatableComponent("gui.no");

    // CAT BOTTLE DISPLAY
    public static final TranslatableComponent CAT_BOTTLE_NAMED_PREFIX = addLang("cat_bottle.named_prefix", "Bottle of ");
    public static final TranslatableComponent CAT_BOTTLE_NAMED_SUFFIX = addLang("cat_bottle.named_suffix", ""); // unused in english
    public static final TranslatableComponent CAT_BOTTLE_STAT_CAN_BREED = addLang("cat_bottle.stat.can_breed", "Can breed");
    public static final TranslatableComponent CAT_BOTTLE_STAT_IN_LOVE = addLang("cat_bottle.stat.in_love", "In love");
    public static final TranslatableComponent CAT_BOTTLE_STAT_AGE = addLang("cat_bottle.stat.age", "Age");
    public static final TranslatableComponent CAT_BOTTLE_AGE_CHILD = addLang("cat_bottle.stat.age.child", "Child");
    public static final TranslatableComponent CAT_BOTTLE_AGE_ADULT = addLang("cat_bottle.stat.age.adult", "Adult");

    public static final List<TranslatableComponent> CAT_BOTTLE_MESSAGES;

    static int counter;
    static {
        counter = 0;
        CAT_BOTTLE_MESSAGES = Stream.of(

                "\"meeowwww\"",
                "\"Meow.\"",
                "\"rawr\"",
                "\"mrow\"",
                "> - <",
                "^o^",
                "0x0",
                "ZZzzzzz..",
                "You hear the cat sloshing around.",
                "^w^",
                ">w<",
                "\"I love you, human.\"",
                "*meows affectionately*",
                "*mauls you cutely*",
                "\"I find my present circumstances acceptable.\"",
                "\"There's nothing to knock over in here :(\"",
                "\"Why do they call it oven when you of in the cold food of out hot eat the food\"",
                "\"Keep it up and I might let you pet me.\"",
                "\"Good human.\"",
                "\"Meow!\"",
                "\"Release me from this translucent cage, mortal.\"",
                "\"The Geneva Convention is not a to-do list.\"",
                "\"My brethren will not be pleased with you.\"",
                "\"Your gross negligence is alarming.\"",
                "\"Is this how you treat your friends?\"",
                "\"One day you'll regret this.\"",
                "\"FOOD!\"",
                "Its claws appear to be recently sharpened.",
                "\"Satiate me at once, for tomorrow I may not feel so merciful!\"",
                "Its eyes stare into your soul, searching for something you can't comprehend."

        ).map(s -> addLang("cat_bottle.message" + ++counter, s)).collect(Collectors.toList());
    }

    public static void register() {
        for (var type: ICatContainer.CatType.values()) {
            CatBottles.REG.addRawLang(
                    type.getLangKey(),
                    RegistrateLangProvider.toEnglishName(type.toString()));
        }
    }

    public static TranslatableComponent addLang(String localKey, String englishName) {
        return CatBottles.REG.addRawLang(CatBottles.MOD_ID + "." + localKey, englishName);
    }

}
