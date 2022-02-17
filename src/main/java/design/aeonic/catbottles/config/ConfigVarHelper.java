package design.aeonic.catbottles.config;

import com.google.gson.JsonSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import design.aeonic.catbottles.CatBottles;
import design.aeonic.catbottles.base.misc.ConfigHelper;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.SerializationTags;
import net.minecraft.tags.Tag;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.*;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

public class ConfigVarHelper {
    static <T> ForgeConfigSpec.ConfigValue<T> configVar(ForgeConfigSpec.Builder builder, String varName, String comment, BiFunction<ForgeConfigSpec.Builder, String, ForgeConfigSpec.ConfigValue<T>> def) {
        return def.apply(builder
                .comment(comment)
                .translation(String.format("%s.config.%s", CatBottles.MOD_ID, varName)), varName);
    }

    static <T> ConfigHelper.ConfigObject<T> configObj(ForgeConfigSpec.Builder builder, String varName, String comment, BiFunction<ForgeConfigSpec.Builder, String, ConfigHelper.ConfigObject<T>> def) {
        return def.apply(builder
                .comment(comment)
                .translation(String.format("%s.config.%s", CatBottles.MOD_ID, varName)), varName);
    }
}
