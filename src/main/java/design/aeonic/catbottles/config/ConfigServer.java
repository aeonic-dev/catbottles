package design.aeonic.catbottles.config;

import net.minecraftforge.common.ForgeConfigSpec;

public record ConfigServer(

) {

    @SuppressWarnings("all") // remove when config options are actually added
    public static ConfigServer create(ForgeConfigSpec.Builder builder) {



        return new ConfigServer(

        );
    }

}
