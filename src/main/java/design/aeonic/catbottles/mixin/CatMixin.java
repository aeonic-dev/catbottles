package design.aeonic.catbottles.mixin;

import design.aeonic.catbottles.base.mixins.MixinCallbacks;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.animal.Cat;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Cat.class)
public abstract class CatMixin extends TamableAnimal {

    protected CatMixin(EntityType<? extends TamableAnimal> type, Level level) {
        super(type, level);
    }

    @SuppressWarnings("all") // "this" cast, remove if modifying callback
    @Inject(method = "mobInteract",
            cancellable = true,
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/TamableAnimal;mobInteract(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/InteractionHand;)Lnet/minecraft/world/InteractionResult;",
                    ordinal = 0))
    public void mobInteractCallback(Player pPlayer, InteractionHand pHand, CallbackInfoReturnable<InteractionResult> ci) {
        ItemStack stack = pPlayer.getItemInHand(pHand);
        if (stack.is(Items.GLASS_BOTTLE)) {
            MixinCallbacks.createCatBottle(pPlayer, pHand, stack, (Cat) ((Object) this));
            ci.setReturnValue(InteractionResult.sidedSuccess(pPlayer instanceof ServerPlayer));
        }
    }
}
