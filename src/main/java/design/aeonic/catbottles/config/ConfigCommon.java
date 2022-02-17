package design.aeonic.catbottles.config;

import design.aeonic.catbottles.base.misc.ConfigHelper;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.ForgeConfigSpec;

public record ConfigCommon(

){

    @SuppressWarnings("all") // remove when config options are actually added
    public static ConfigCommon create(ForgeConfigSpec.Builder builder) {

//        builder.comment("Iota content additions").push("content");
//            var enableKiln = IotaConfigHelper.configVar(builder,
//                    "enableKiln",
//                    "Whether to enable the Kiln",
//                    (b, s) -> b.define(s, true)
//            );
//            var enableCatBottles = IotaConfigHelper.configVar(builder,
//                    "enableCatBottles",
//                    "Whether to enable Bottles of Cat and their related recipes",
//                    (b, s) -> b.define(s, true)
//            );
//        builder.pop();
//
//        builder.comment("Iota tweaks").push("content");
//        var mobBucketItems = IotaConfigHelper.configObj(builder,
//                "mobBucketItems",
//                "A list of mob buckets dispensers can fill cauldrons with if the server config option is enabled",
//                (b, s) -> ConfigHelper.defineObject(b, s, IotaConfigHelper.ItemList.CODEC, new IotaConfigHelper.ItemList(
//                        Items.PUFFERFISH_BUCKET,
//                        Items.SALMON_BUCKET,
//                        Items.COD_BUCKET,
//                        Items.TROPICAL_FISH_BUCKET,
//                        Items.AXOLOTL_BUCKET
//                )));
//        builder.pop();

        return new ConfigCommon(

        );
    }

}
