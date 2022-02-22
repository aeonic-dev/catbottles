package design.aeonic.catbottles.content.bottles;

import design.aeonic.catbottles.base.cats.ICatContainer;
import design.aeonic.catbottles.base.misc.Blocknt;
import design.aeonic.catbottles.base.misc.Styles;
import design.aeonic.catbottles.content.AssholeItemEntity;
import design.aeonic.catbottles.registry.ModBlocks;
import design.aeonic.catbottles.registry.ModItems;
import design.aeonic.catbottles.registry.ModLang;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.Cat;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Random;

public class CatBottleItem extends Blocknt implements ICatContainer.CatItem {

    private final CatType type;
    private final MutableComponent typeComponent;

    public CatBottleItem(Properties props, CatType type) {
        super(ModBlocks.CAT_BOTTLES.get(type).get(), props);
        this.type = type;
        typeComponent = new TranslatableComponent(type.getLangKey()).setStyle(Styles.IMPORTANT);
    }

    @Override
    public boolean hasCustomEntity(ItemStack stack) {
        return true;
    }

    @Nullable
    @Override
    public Entity createEntity(Level level, Entity location, ItemStack itemstack) {
        var random = new Random();
        var entity = AssholeItemEntity.create(level, location.getX(), location.getY(), location.getZ(), itemstack);
        float f7 = 0.3F;
        float f8 = Mth.sin(location.getXRot() * ((float)Math.PI / 180F));
        float f2 = Mth.cos(location.getXRot() * ((float)Math.PI / 180F));
        float f3 = Mth.sin(location.getYRot() * ((float)Math.PI / 180F));
        float f4 = Mth.cos(location.getYRot() * ((float)Math.PI / 180F));
        float f5 = random.nextFloat() * ((float)Math.PI * 2F);
        float f6 = 0.02F * random.nextFloat();
        entity.setDeltaMovement(
                (double)(-f3 * f2 * 0.3F) + Math.cos((double)f5) * (double)f6,
                (double)(-f8 * 0.3F + 0.1F + (random.nextFloat() - random.nextFloat()) * 0.1F),
                (double)(f4 * f2 * 0.3F) + Math.sin((double)f5) * (double)f6);
        return entity;
    }

    public CatType getType() {
        return type;
    }

    @Override
    public  CatType getCatType(ItemStack stack) {
        if (stack.getItem() instanceof CatBottleItem item) return item.getType();
        return CatType.getDefault();
    }

    @Override
    public InteractionResult useOn(UseOnContext pContext) {
        var level = pContext.getLevel();
        var pos = pContext.getClickedPos();
        var state = level.getBlockState(pos);
        if (state.getBlock() == Blocks.WATER_CAULDRON && type != CatType.WHITE) {
            int cauldronLevel = state.getValue(LayeredCauldronBlock.LEVEL);
            if (cauldronLevel > 1)
                level.setBlock(pos, state.setValue(LayeredCauldronBlock.LEVEL, state.getValue(LayeredCauldronBlock.LEVEL) - 1), 1);
            else
                level.setBlock(pos, Blocks.CAULDRON.defaultBlockState(), 1);

            var player = pContext.getPlayer();
            var oldStack = pContext.getItemInHand();
            var newStack = new ItemStack(ModItems.CAT_BOTTLES.get(CatType.WHITE).get());

            if (!level.isClientSide)
                newStack.setTag(oldStack.getOrCreateTag());

            if (player != null) {
                player.setItemInHand(pContext.getHand(), newStack);
                player.playSound(SoundEvents.BOTTLE_EMPTY, 1, 1);
                player.playSound(SoundEvents.CAT_HURT, 1, 1);
            }

            return InteractionResult.sidedSuccess(level.isClientSide);
        }
        return super.useOn(pContext);
    }

    @Override
    protected boolean updateCustomBlockEntityTag(BlockPos pPos, Level pLevel, @Nullable Player pPlayer, ItemStack pStack, BlockState pState) {
        if (pLevel instanceof ServerLevel && pLevel.getBlockEntity(pPos) instanceof CatBottleBlockEntity entity) {
            entity.loadFromStack(pStack);
            return true;
        }
        return super.updateCustomBlockEntityTag(pPos, pLevel, pPlayer, pStack, pState);
    }

    @Override
    public void fillItemCategory(CreativeModeTab pGroup, NonNullList<ItemStack> pItems) {
        if (this.allowdedIn(pGroup)) {
            pItems.add(getDefaultInstance());
        }
    }

    @Nonnull
    @Override
    public ItemStack getDefaultInstance() {
        var instance = CatWrapper.createNew(this, new ItemStack(this));
        instance.setIsBig(new Random().nextBoolean());
        return instance.get();
    }

    public static ItemStack createStack(Cat cat, Player player) {
        var item = ModItems.CAT_BOTTLES.get(CatType.byValue(cat.getCatType()));

        if (player instanceof ServerPlayer) {
            return CatWrapper.captureOrCreate(item.get(), item.asStack(), cat).get();
        }
        return item.asStack();
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return shouldCauseBlockBreakReset(oldStack, newStack);
    }

    @Override
    public boolean shouldCauseBlockBreakReset(ItemStack oldStack, ItemStack newStack) {
        if (!oldStack.is(newStack.getItem())) return true;

        var oldId = catInstance(oldStack).getUUID();
        var newId = catInstance(newStack).getUUID();

        return oldId == null || !oldId.equals(newId);
    }

    @Override
    public void inventoryTick(ItemStack pStack, Level pLevel, Entity pEntity, int slotId, boolean isSelected) {
        if (!(pEntity instanceof ServerPlayer player)) return;

        long currentTick = player.getLevel().getGameTime();
        CatWrapper<ItemStack> instance = catInstance(pStack);

        instance.doTick(currentTick);
    }

    @Override
    public Component getName(ItemStack pStack) {
        CatWrapper<ItemStack> instance = catInstance(pStack);
        if (instance.hasCustomName())
            return ModLang.CAT_BOTTLE_NAMED_PREFIX.copy().append(instance.getCustomName())
                    .append(ModLang.CAT_BOTTLE_NAMED_SUFFIX.copy());

        return super.getName(pStack);
    }

    // Tooltip
    public static final MutableComponent STAT_AGE_CHILD = ModLang.CAT_BOTTLE_STAT_AGE.copy().setStyle(Styles.BORING).append(": ").append(ModLang.CAT_BOTTLE_AGE_CHILD.copy().setStyle(Styles.STAT));
    public static final MutableComponent STAT_AGE_ADULT = ModLang.CAT_BOTTLE_STAT_AGE.copy().setStyle(Styles.BORING).append(": ").append(ModLang.CAT_BOTTLE_AGE_ADULT.copy().setStyle(Styles.STAT));
    public static final MutableComponent STAT_CAN_BREED_YES = ModLang.CAT_BOTTLE_STAT_CAN_BREED.copy().setStyle(Styles.BORING).append(": ").append(ModLang.YES.copy().setStyle(Styles.STAT));
    public static final MutableComponent STAT_CAN_BREED_NO = ModLang.CAT_BOTTLE_STAT_CAN_BREED.copy().setStyle(Styles.BORING).append(": ").append(ModLang.NO.copy().setStyle(Styles.STAT));

    @Override
    public void appendHoverText(ItemStack pStack, @org.jetbrains.annotations.Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
        if (pLevel instanceof ClientLevel) {
            CatWrapper<ItemStack> instance = catInstance(pStack);

            // Cat type
            pTooltipComponents.add(typeComponent);

            // Cat cosmetic message
            TranslatableComponent message = ModLang.CAT_BOTTLE_MESSAGES.get(instance.getMessageIndex());
            pTooltipComponents.add(message.copy().setStyle(Styles.INFO));

            // Stats
            var isBig = instance.getIsBig();
            var canBreed = instance.getBreedingCooldown() < 1;

            pTooltipComponents.add(isBig ? STAT_AGE_ADULT : STAT_AGE_CHILD);
            pTooltipComponents.add(canBreed ? STAT_CAN_BREED_YES : STAT_CAN_BREED_NO);
        }
        super.appendHoverText(pStack, pLevel, pTooltipComponents, pIsAdvanced);
    }

    @Override
    public boolean hasContainerItem(ItemStack stack) {
        return true;
    }

    @Override
    public ItemStack getContainerItem(ItemStack itemStack) {
        return new ItemStack(Items.GLASS_BOTTLE);
    }

    @Override
    public CompoundTag getItemData(ItemStack itemStack) {
        return itemStack.getOrCreateTagElement(ITEM_ROOT);
    }

    @Override
    public CompoundTag getEntityData(ItemStack itemStack) {
        return itemStack.getOrCreateTagElement(ENTITY_ROOT);
    }
}
