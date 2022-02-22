package design.aeonic.catbottles.base.mixins;

import design.aeonic.catbottles.content.bottles.CatBottleItem;
import design.aeonic.catbottles.registry.ModSoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.animal.Cat;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public final class MixinCallbacks {

    public static void createCatBottle(Player pPlayer, InteractionHand pHand, ItemStack sourceStack, Cat cat) {
        if (!pPlayer.isCreative()) sourceStack.shrink(1);
        var newStack = CatBottleItem.createStack(cat, pPlayer);
        pPlayer.getLevel().playSound(null, cat.getOnPos(), ModSoundEvents.CAT_BOTTLE_FILL.get(), SoundSource.PLAYERS, 1, 1);
        cat.discard();

        if (sourceStack.isEmpty()) pPlayer.setItemInHand(pHand, newStack);
        else if (!pPlayer.getInventory().add(newStack)) {
            pPlayer.drop(newStack, false, false);
        }
    }
}
