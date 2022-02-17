package design.aeonic.catbottles.registry;

import com.tterrag.registrate.util.entry.EntityEntry;
import design.aeonic.catbottles.CatBottles;
import design.aeonic.catbottles.content.bottles.ThrownCatBottle;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.world.entity.MobCategory;

public class ModEntities {

    public static EntityEntry<ThrownCatBottle> CAT_BOTTLE = CatBottles.REG.entity("cat_bottle", ThrownCatBottle::new, MobCategory.MISC)
            .properties(b -> b.sized(0.25F, 0.25F).clientTrackingRange(8).updateInterval(10).setShouldReceiveVelocityUpdates(true))
            .renderer(() -> ThrownItemRenderer::new)
            .register();

    public static void register() {}
}
