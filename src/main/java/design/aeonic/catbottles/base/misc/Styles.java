package design.aeonic.catbottles.base.misc;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Style;

public final class Styles {
    public static final Style IMPORTANT = Style.EMPTY.withColor(ChatFormatting.LIGHT_PURPLE);
    public static final Style INFO = Style.EMPTY.withColor(ChatFormatting.GRAY).withItalic(true);
    public static final Style BORING = Style.EMPTY.withColor(ChatFormatting.GRAY);
    public static final Style STAT = Style.EMPTY.withColor(ChatFormatting.AQUA);
}
