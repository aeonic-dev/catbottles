package design.aeonic.catbottles;

import design.aeonic.catbottles.recipe.brewing.CatBrewingRecipe;
import design.aeonic.catbottles.base.misc.ConfigHelper;
import design.aeonic.catbottles.config.ConfigCommon;
import design.aeonic.catbottles.config.ConfigServer;
import design.aeonic.catbottles.registry.*;
import net.minecraftforge.common.brewing.BrewingRecipeRegistry;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import org.apache.logging.log4j.LogManager;

@Mod(CatBottles.MOD_ID)
@Mod.EventBusSubscriber(modid = CatBottles.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class CatBottles {
	
    public static final String MOD_ID = "catbottles";
    public static final String MOD_NAME = "Cat Bottles";
    
    public static final Registrateish REG = Registrateish.INSTANCE;
    public static final ConfigServer serverConfig = ConfigHelper.register(ModConfig.Type.SERVER, ConfigServer::create);
    public static final ConfigCommon commonConfig = ConfigHelper.register(ModConfig.Type.COMMON, ConfigCommon::create);

    public CatBottles() {
        ModBlocks.register();
        ModItems.register();
        ModEntities.register();
        ModAdvancements.register();
        ModLang.register();
        ModSoundEvents.register();
    }

    @SubscribeEvent
    public static void commonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            ModRecipeTypes.register();
        });
    }

    @SubscribeEvent
    public static void loadComplete(FMLLoadCompleteEvent event) {
        CatBrewingRecipe.wakeUp();
        event.enqueueWork(() -> {
            BrewingRecipeRegistry.addRecipe(CatBrewingRecipe.DYEING);
            BrewingRecipeRegistry.addRecipe(CatBrewingRecipe.AUGMENTING);
        });
    }
}
