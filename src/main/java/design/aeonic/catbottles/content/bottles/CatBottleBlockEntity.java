package design.aeonic.catbottles.content.bottles;

import design.aeonic.catbottles.base.block.entity.SimpleBlockEntity;
import design.aeonic.catbottles.base.cats.ICatContainer;
import design.aeonic.catbottles.registry.ModItems;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class CatBottleBlockEntity extends SimpleBlockEntity {

    private CompoundTag itemData;
    private CompoundTag entityData;
    private boolean destroyedByPlayerFall = false;

    public CatBottleBlockEntity(BlockEntityType<? extends SimpleBlockEntity> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public CompoundTag getItemData() {
        return itemData.copy();
    }

    public CompoundTag getEntityData() {
        return entityData.copy();
    }

    public ItemStack makeStack(CatBottleItem.CatType type) {
        var stack = ModItems.CAT_BOTTLES.get(type).asStack();
        if (level instanceof ServerLevel) {
            var tag = stack.getOrCreateTag();
            tag.put(ICatContainer.TAG_ITEM, itemData.copy());
            tag.put(ICatContainer.TAG_ENTITY, entityData.copy());
        }
        return stack;
    }

    public void loadFromStack(ItemStack stack) {
        itemData = stack.getOrCreateTagElement(ICatContainer.TAG_ITEM).copy();
        entityData = stack.getOrCreateTagElement(ICatContainer.TAG_ENTITY).copy();
        setChanged();
    }

    /**
     * Called when relevant data changes for the sibling block's state
     */
    private void updateBlockState() {
        if (getLevel() instanceof ServerLevel serverLevel)
            ((CatBottleBlock) getBlockState().getBlock()).onPropertyDataChange(
                    getBlockState(), serverLevel, getBlockPos(), this);
    }

    public void setShouldReleaseEntity() {
        destroyedByPlayerFall = true;
    }

    public boolean shouldReleaseEntity() {
        return destroyedByPlayerFall;
    }

    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);

        itemData = pTag.getCompound(ICatContainer.TAG_ITEM).copy();
        entityData = pTag.getCompound(ICatContainer.TAG_ENTITY).copy();
        destroyedByPlayerFall = pTag.getBoolean("DestroyedByPlayerFall");
        updateBlockState();
    }

    @Override
    protected void saveAdditional(CompoundTag pTag) {
        super.saveAdditional(pTag);
        if (level instanceof ClientLevel) return;

        pTag.put(ICatContainer.TAG_ITEM, itemData.copy());
        pTag.put(ICatContainer.TAG_ENTITY, entityData.copy());
        pTag.putBoolean("DestroyedByPlayerFall", destroyedByPlayerFall);
    }
}
