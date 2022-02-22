package design.aeonic.catbottles.registry;

import com.tterrag.registrate.util.entry.EntityEntry;
import design.aeonic.catbottles.CatBottles;
import design.aeonic.catbottles.content.AssholeItemEntity;
import design.aeonic.catbottles.content.bottles.ThrownCatBottle;
import net.minecraft.client.renderer.entity.ItemEntityRenderer;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.world.entity.MobCategory;

public class ModEntities {

    public static EntityEntry<ThrownCatBottle> CAT_BOTTLE = CatBottles.REG.entity("cat_bottle", ThrownCatBottle::new, MobCategory.MISC)
            .properties(b -> b.sized(0.25f, 0.25f).clientTrackingRange(8).updateInterval(10).setShouldReceiveVelocityUpdates(true))
            .renderer(() -> ThrownItemRenderer::new)
            .register();

    public static EntityEntry<AssholeItemEntity> ASSHOLE_ITEM = CatBottles.REG.entity("asshole_item", AssholeItemEntity::new, MobCategory.MISC)
            .properties(b -> b.sized(.25f, .25f).clientTrackingRange(8).updateInterval(15).setShouldReceiveVelocityUpdates(true))
            .renderer(() -> ItemEntityRenderer::new)
            .register();

    public static void register() {}
}
