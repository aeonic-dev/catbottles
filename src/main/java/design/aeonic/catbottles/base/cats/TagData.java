package design.aeonic.catbottles.base.cats;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import org.apache.logging.log4j.util.TriConsumer;

import java.util.function.BiFunction;

public record TagData<T> (TagType<T> type, String key) {

    public T get(CompoundTag tag) {
        return type.get(tag, key);
    }

    public boolean exists(CompoundTag tag) {
        return type.exists(tag, key);
    }

    public void set(CompoundTag tag, T value) {
        type.set(tag, key, value);
    }

    public record TagType<T>
            (BiFunction<CompoundTag, String, T> getter, TriConsumer<CompoundTag, String, T> setter) {

        public static final TagType<CompoundTag> COMPOUND = new TagType<>(CompoundTag::getCompound, CompoundTag::put);
        public static final TagType<java.util.UUID> UUID = new TagType<>((t, s) -> t.hasUUID(s) ? t.getUUID(s) : null, CompoundTag::putUUID);
        public static final TagType<Byte> BYTE = new TagType<>(CompoundTag::getByte, CompoundTag::putByte);
        public static final TagType<byte[]> BYTEARRAY = new TagType<>(CompoundTag::getByteArray, CompoundTag::putByteArray);
        public static final TagType<Boolean> BOOLEAN = new TagType<>(CompoundTag::getBoolean, CompoundTag::putBoolean);
        public static final TagType<Short> SHORT = new TagType<>(CompoundTag::getShort, CompoundTag::putShort);
        public static final TagType<Integer> INT = new TagType<>(CompoundTag::getInt, CompoundTag::putInt);
        public static final TagType<int[]> INTARRAY = new TagType<>(CompoundTag::getIntArray, CompoundTag::putIntArray);
        public static final TagType<Long> LONG = new TagType<>(CompoundTag::getLong, CompoundTag::putLong);
        public static final TagType<long[]> LONGARRAY = new TagType<>(CompoundTag::getLongArray, CompoundTag::putLongArray);
        public static final TagType<Float> FLOAT = new TagType<>(CompoundTag::getFloat, CompoundTag::putFloat);
        public static final TagType<Double> DOUBLE = new TagType<>(CompoundTag::getDouble, CompoundTag::putDouble);
        public static final TagType<String> STRING = new TagType<>(CompoundTag::getString, CompoundTag::putString);

        public T get(CompoundTag tag, String key) {
            return getter.apply(tag, key);
        }

        public boolean exists(CompoundTag tag, String key) { return tag.contains(key); }

        public void set(CompoundTag tag, String key, T value) {
            setter.accept(tag, key, value);
        }
    }

}
