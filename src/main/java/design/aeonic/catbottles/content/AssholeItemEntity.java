package design.aeonic.catbottles.content;

import design.aeonic.catbottles.registry.ModEntities;
import design.aeonic.catbottles.registry.ModTags;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemEntityRenderer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.entity.IEntityAdditionalSpawnData;
import net.minecraftforge.network.NetworkHooks;

import javax.annotation.Nonnull;

/**
 * For when the default item entity is too agreeable
 */
public class AssholeItemEntity extends ItemEntity implements IEntityAdditionalSpawnData {

    public static float CONTINUE_BEING_ASSHOLE_CHANCE = 1/2f;
    public static int CONTINUE_BEING_ASSHOLE_DURATION = 60;

    public AssholeItemEntity(EntityType<AssholeItemEntity> type, Level level) {
        super(type, level);
    }

    public static AssholeItemEntity create(Level level, double x, double y, double z, ItemStack stack) {
        var entity = new AssholeItemEntity(ModEntities.ASSHOLE_ITEM.get(), level);

        entity.setPos(x, y, z);
        entity.setItem(stack);
        entity.setUnlimitedLifetime();
        entity.setDefaultPickUpDelay();

        return entity;
    }

    @Override
    public void playerTouch(Player pEntity) {
        boolean flag = false;

        super.playerTouch(pEntity);
        if (likesItem(pEntity.getMainHandItem()) || likesItem(pEntity.getOffhandItem())) {
            pickupDelay = 0;
            flag = true;
            Vec3 vec3 = new Vec3(pEntity.getX() - this.getX(), pEntity.getY() - this.getY(), pEntity.getZ() - this.getZ());
            double d0 = vec3.lengthSqr();
            if (d0 < 64.0D) {
                double d1 = 1.0D - Math.sqrt(d0) / 8.0D;
                this.setDeltaMovement(this.getDeltaMovement().add(vec3.normalize().scale(d1 * d1 * 0.1D)));
            }
        }
        else {
            Vec3 vec3 = new Vec3(pEntity.getX() - this.getX(), pEntity.getY() - this.getY(), pEntity.getZ() - this.getZ());
            double d0 = vec3.lengthSqr();
            if (d0 < 64.0D) {
                double d1 = 1.0D - Math.sqrt(d0) / 8.0D;
                this.setDeltaMovement(this.getDeltaMovement().add(vec3.normalize().scale(d1 * d1 * 0.1D * -1)));
            }
        }

        if (!flag && pickupDelay <= 1 && random.nextFloat() <= CONTINUE_BEING_ASSHOLE_CHANCE) {
            if (random.nextBoolean())
                playSound(SoundEvents.CAT_STRAY_AMBIENT, .6f, 1 + random.nextFloat());
            pickupDelay = CONTINUE_BEING_ASSHOLE_DURATION;
        }
    }

    public static boolean likesItem(ItemStack stack) {
        return ItemTags.FISHES.contains(stack.getItem()) || ModTags.Items.LIKED_BY_CATS.contains(stack.getItem());
    }

    @Override
    public void setDefaultPickUpDelay() {
        pickupDelay = CONTINUE_BEING_ASSHOLE_DURATION;
    }

    @Override
    public boolean isMergable() {
        return false;
    }

    @Nonnull
    @Override
    public Packet<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @Override
    public void writeSpawnData(FriendlyByteBuf buf) {
        buf.writeItem(getItem());
    }

    @Override
    public void readSpawnData(FriendlyByteBuf buf) {
        setItem(buf.readItem());
    }
}
