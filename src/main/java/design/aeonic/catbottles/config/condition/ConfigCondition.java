package design.aeonic.catbottles.config.condition;

import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.common.crafting.conditions.IConditionSerializer;

import java.util.function.Function;

public abstract class ConfigCondition<T> implements ICondition {

    protected abstract ForgeConfigSpec.ConfigValue<T> getConfigField();

    abstract boolean checkValue(T value);
    public abstract ResourceLocation getID();
    protected abstract ConfigCondition<T> create();

    public Serializer serializer() { return new Serializer(); }

    public <TT> ConfigCondition<TT> create(ForgeConfigSpec.ConfigValue<TT> field, Function<TT, Boolean> checkFunction, ResourceLocation id) {
        return new ConfigCondition<>() {
            protected ConfigCondition<TT> create() {
                return ConfigCondition.this.create(field, checkFunction, id);
            }

            protected ForgeConfigSpec.ConfigValue<TT> getConfigField() {
                return field;
            }

            boolean checkValue(TT value) {
                return checkFunction.apply(value);
            }

            public ResourceLocation getID() {
                return id;
            }
        };
    }

    @Override
    public boolean test() {
        return getConfigField() != null && checkValue(getConfigField().get());
    }

    public class Serializer implements IConditionSerializer<ConfigCondition<?>> {
        @Override
        public void write(JsonObject json, ConfigCondition<?> value) {}

        @Override
        public ConfigCondition<?> read(JsonObject json) {
            return ConfigCondition.this.create();
        }

        @Override
        public ResourceLocation getID() {
            return ConfigCondition.this.getID();
        }
    }
}
