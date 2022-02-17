package design.aeonic.catbottles.registry;

import design.aeonic.catbottles.CatBottles;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModSoundEvents {

    public static DeferredRegister<SoundEvent> SOUNDS = Registrateish.deferredRegister(ForgeRegistries.SOUND_EVENTS, FMLJavaModLoadingContext.get().getModEventBus());

    public static final RegistryObject<SoundEvent> CAT_BOTTLE_FILL = createSoundEventDeferred("item.cat_bottle.fill");

    public static RegistryObject<SoundEvent> createSoundEventDeferred(String name) {
        return SOUNDS.register(name, () -> new SoundEvent(new ResourceLocation(CatBottles.MOD_ID, name)));
    }

    public static void register() {}
}
