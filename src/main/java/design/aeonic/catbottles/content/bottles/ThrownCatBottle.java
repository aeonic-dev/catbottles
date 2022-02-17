package design.aeonic.catbottles.content.bottles;

import design.aeonic.catbottles.base.cats.ICatContainer;
import design.aeonic.catbottles.registry.ModEntities;
import design.aeonic.catbottles.registry.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.entity.IEntityAdditionalSpawnData;
import net.minecraftforge.network.NetworkHooks;

public class ThrownCatBottle extends ThrowableItemProjectile implements IEntityAdditionalSpawnData {

    public ThrownCatBottle(EntityType<ThrownCatBottle> type, Level level) {
        super(type, level);
    }

    protected ThrownCatBottle(Player player, Level level) {
        super(ModEntities.CAT_BOTTLE.get(), player, level);
    }

    @Override
    protected void onHit(HitResult pResult) {
        super.onHit(pResult);
        if (getLevel() instanceof ServerLevel level && getItem().getItem() instanceof CatBottleItem bottleItem) {
            bottleItem.spawnFrom(getItem(), level, pResult.getLocation());
            this.discard();
        }
        getLevel().playSound(null, new BlockPos(pResult.getLocation()), SoundEvents.SPLASH_POTION_BREAK, SoundSource.PLAYERS, 1, 1);
    }

    @Override
    protected float getGravity() {
        return .07f;
    }

    @Override
    protected Item getDefaultItem() {
        return ModItems.CAT_BOTTLES.get(ICatContainer.CatType.getDefault()).get();
    }

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
