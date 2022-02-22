package design.aeonic.catbottles.content.bottles;

import design.aeonic.catbottles.base.misc.Styles;
import design.aeonic.catbottles.registry.ModLang;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ThrowableCatBottleItem extends CatBottleItem {
    public ThrowableCatBottleItem(Properties props, CatType type) {
        super(props, type);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {
        if (!pLevel.isClientSide) {
            var entity = new ThrownCatBottle(pPlayer, pLevel);
            entity.setItem(pPlayer.getItemInHand(pUsedHand));
            entity.shootFromRotation(pPlayer, pPlayer.getXRot(), pPlayer.getYRot(), -20.0f, .6f, 1f);
            pLevel.addFreshEntity(entity);
            pLevel.playSound(null, pPlayer.getOnPos(), SoundEvents.SPLASH_POTION_THROW, SoundSource.PLAYERS, 1, 1);
            return InteractionResultHolder.consume(ItemStack.EMPTY);
        }
        return super.use(pLevel, pPlayer, pUsedHand);
    }

    @Override
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
        pTooltipComponents.add(ModLang.CAT_BOTTLE_THROW_TOOLTIP.setStyle(Styles.BORING));
        super.appendHoverText(pStack, pLevel, pTooltipComponents, pIsAdvanced);
    }
}
