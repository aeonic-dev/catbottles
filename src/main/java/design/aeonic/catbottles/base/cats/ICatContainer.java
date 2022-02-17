package design.aeonic.catbottles.base.cats;

import design.aeonic.catbottles.CatBottles;
import design.aeonic.catbottles.content.bottles.CatBottleItem;
import design.aeonic.catbottles.registry.ModLang;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.animal.Cat;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeHooks;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Locale;
import java.util.Random;
import java.util.UUID;
import java.util.function.Consumer;

@SuppressWarnings({"UnusedReturnValue", "unused"})
public interface ICatContainer<T> {

    // Tags transferred from Cat entity
    String                  ENTITY_ROOT = "EntityData";
    TagData<UUID>           ENTITY_UUID = new TagData<>(TagData.TagType.UUID, "UUID");

    // Bottled cat data
    String                  ITEM_ROOT = "ItemData";
    TagData<Long>           ITEM_LAST_DAY_TICK = new TagData<>(TagData.TagType.LONG, "LastDayTick");
    TagData<Integer>        ITEM_ALIVE_FOR = new TagData<>(TagData.TagType.INT, "AliveFor");
    TagData<Boolean>        ITEM_IS_BIG = new TagData<>(TagData.TagType.BOOLEAN, "Adult");
    TagData<Integer>        ITEM_BREEDING_COOLDOWN = new TagData<>(TagData.TagType.INT, "BreedingCooldown");
    TagData<Integer>        ITEM_MESSAGE_INDEX = new TagData<>(TagData.TagType.INT, "MessageIndex");
    TagData<String>         ITEM_NAME = new TagData<>(TagData.TagType.STRING, "Name");

    /**
     * Wraps a cat object for data manip & trait system
     */
    class CatWrapper<T> {

        private final ICatContainer<T> outer;
        private final T instance;
        private final CompoundTag itemTag;
        private final CompoundTag entityTag;

        public CatWrapper(ICatContainer<T> outer, T instance) {
            this.outer = outer;
            this.instance = instance;
            itemTag = outer.getItemData(instance);
            entityTag = outer.getEntityData(instance);
        }

        public static <T> CatWrapper<T> captureOrCreate(ICatContainer<T> outer, T instance, Cat cat) {
            var forgeData = cat.getPersistentData();
            if (forgeData.contains(ITEM_ROOT)) {
                var wrapper = new CatWrapper<>(outer, instance);
                wrapper.itemTag.merge(forgeData.getCompound(ITEM_ROOT));
                cat.saveWithoutId(wrapper.entityTag);
                forgeData.remove(ITEM_ROOT);
                return wrapper;
            }
            return createNew(outer, instance, cat);
        }

        public static <T> CatWrapper<T> createNew(ICatContainer<T> outer, T instance, Cat cat) {
            var obj = createNew(outer, instance);

            obj.setLastDayTick(cat.getLevel().getGameTime());
            obj.setIsBig(cat.getAge() >= 0);

            cat.saveWithoutId(obj.entityTag);

            return obj;
        }

        public static <T> CatWrapper<T> createNew(ICatContainer<T> outer, T instance) {
            var obj = new CatWrapper<>(outer, instance);

            obj.setBreedingCooldown(BREEDING_COOLDOWN_PERIOD);
            obj.randomizeMessageIndex();

            return obj;
        }

        public T get() {
            return instance;
        }

        public void merge(CatWrapper<T> other) {
            itemTag.merge(other.getItemTag());
            entityTag.merge(other.getEntityTag());
        }

        public void mergeWithItemTag(CompoundTag tag) {
            itemTag.merge(tag);
        }

        public void mergeWithEntityTag(CompoundTag tag) {
            entityTag.merge(tag);
        }

        public CompoundTag getItemTag() {
            return itemTag.copy();
        }
        public CompoundTag getEntityTag() {
            return entityTag.copy();
        }

        public CatType getType() {
            return outer.getCatType(instance);
        }
        public @Nullable UUID getUUID() { return ENTITY_UUID.get(entityTag); }
        public boolean hasCustomName() { return ITEM_NAME.exists(itemTag); }
        public String getCustomName() { return ITEM_NAME.get(itemTag); }
        public void setCustomName(String s) { ITEM_NAME.set(itemTag, s); }
        public long getLastDayTick() { return ITEM_LAST_DAY_TICK.get(itemTag); }
        public void setLastDayTick(long s) { ITEM_LAST_DAY_TICK.set(itemTag, s); }
        public int getAliveFor() { return ITEM_ALIVE_FOR.get(itemTag); }
        public void setAliveFor(int s) { ITEM_ALIVE_FOR.set(itemTag, s); }
        public boolean getIsBig() { return ITEM_IS_BIG.get(itemTag); }
        public void setIsBig(boolean s) { ITEM_IS_BIG.set(itemTag, s); }
        public int getBreedingCooldown() { return ITEM_BREEDING_COOLDOWN.get(itemTag); }
        public void setBreedingCooldown(int s) { ITEM_BREEDING_COOLDOWN.set(itemTag, s);}
        public int getMessageIndex() { return ITEM_MESSAGE_INDEX.get(itemTag); }
        public void setMessageIndex(int s) { ITEM_MESSAGE_INDEX.set(itemTag, s); }

        public void randomizeMessageIndex() {
            setMessageIndex(new Random().nextInt(ModLang.CAT_BOTTLE_MESSAGES.size()));
        }

        public void doTick(long gameTime) {
            long lastTick = getLastDayTick();
            if (lastTick <= gameTime - DAY_TICKS || lastTick <= 0 || lastTick > gameTime) {
                int aliveFor = getAliveFor() + 1;

                setLastDayTick(gameTime);
                setAliveFor(aliveFor);
                if (getIsBig()) setBreedingCooldown(Math.max(getBreedingCooldown() - 1, 0));

                doDayTick(aliveFor);
            }
        }

        private void doDayTick(int aliveFor) {
            randomizeMessageIndex();
        }

        public boolean sameCat(CatWrapper<?> other) {
            return getUUID() != null && getUUID().equals(other.getUUID());
        }

        public boolean sameType(CatWrapper<?> other) {
            return getType().equals(other.getType());
        }
    }

    // Constants
    int DAY_TICKS = 8000;
    int BREEDING_COOLDOWN_PERIOD = 24000;

    String TAG_ITEM = "ItemData";
    String TAG_ENTITY = "EntityData";
    // Base data
    String TAG_LAST_TICK = "LastTick";
    String TAG_ALIVE_FOR = "AliveFor";
    // Stats + breeding
    String TAG_IS_BIG = "Adult";
    String TAG_BREEDING_COOLDOWN = "BreedingCooldown";
    // Display
    String TAG_MESSAGE_INDEX = "MessageIndex";
    String TAG_NAME = "Name";

    CatBottleItem.CatType getCatType(T t);
    CompoundTag getItemData(T t);
    CompoundTag getEntityData(T t);

    default CatWrapper<T> catInstance(T t) {
        return new CatWrapper<>(this, t);
    }

    default Cat spawnFrom(T t, ServerLevel level, Vec3 pos) {
        return spawnFrom(t, level, pos, $ -> {});
    }

    default Cat spawnFrom(T t, ServerLevel level, Vec3 pos, Consumer<Cat> extras) {
        return spawnFrom(t, level, new BlockPos(pos), extras);
    }

    default Cat spawnFrom(T t, ServerLevel level, BlockPos pos) {
        return spawnFrom(t, level, new BlockPos(pos), $ -> {});
    }

    @Nullable
    default Cat spawnFrom(T t, ServerLevel level, BlockPos pos, Consumer<Cat> extras) {
        var cat = EntityType.CAT.create(level);
        if (cat == null) return null;

        var instance = catInstance(t);
        double x = pos.getX();
        double y = pos.getY();
        double z = pos.getZ();

        cat.moveTo(x, y + 1, z);
        cat.readAdditionalSaveData(instance.getEntityTag());
        if (instance.hasCustomName())
            cat.setCustomName(new TextComponent(instance.getCustomName()));

        if (ForgeHooks.canEntitySpawn(cat, level, x, y, z, null, MobSpawnType.BUCKET) != -1) {
            cat.finalizeSpawn(level, level.getCurrentDifficultyAt(pos), MobSpawnType.BUCKET, null, null);
            cat.setCatType((int) instance.getType().value);

            cat.setOrderedToSit(false);
            cat.setInSittingPose(false);
            extras.accept(cat);

            cat.getPersistentData().put(ITEM_ROOT, instance.getItemTag());

            level.addFreshEntity(cat);
            return cat;
        }
        return null;
    }

    enum CatType implements StringRepresentable {
        TABBY(0),
        BLACK(1),
        RED(2),
        SIAMESE(3),
        BRITISH_SHORTHAIR(4),
        CALICO(5),
        PERSIAN(6),
        RAGDOLL(7),
        WHITE(8),
        JELLIE(9),
        ALL_BLACK(10);

        public final float value;

        CatType(float value) {
            this.value = value;
        }

        public static CatType getDefault() {
            return WHITE;
        }

        public static CatType byValue(int value) {
            return value >= 0 && value < CatType.values().length ? CatType.values()[value] : getDefault();
        }

        public String toString() {
            return name().toLowerCase(Locale.ROOT);
        }

        public ResourceLocation getTexture() {
            return new ResourceLocation(CatBottles.MOD_ID, "item/cat_bottle_" + this);
        }

        public String getLangKey() {
            return CatBottles.MOD_ID + ".cat_bottle.type." + this;
        }

        @Nonnull
        @Override
        public String getSerializedName() {
            return toString();
        }
    }
}
