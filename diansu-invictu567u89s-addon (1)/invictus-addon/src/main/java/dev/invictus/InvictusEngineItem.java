package dev.invictus;

import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

/** Right-click to engage / disengage the Invictus form. */
public class InvictusEngineItem extends Item {
    public InvictusEngineItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        ItemStack stack = player.getStackInHand(hand);
        if (!world.isClient) {
            if (InvictusForm.isActive(player)) {
                player.removeStatusEffect(InvictusMod.FORM_EFFECT);
                player.sendMessage(Text.translatable("message.invictus.disengaged"), true);
                world.playSound(null, player.getBlockPos(), SoundEvents.BLOCK_BEACON_DEACTIVATE, SoundCategory.PLAYERS, 1f, 1f);
            } else {
                // duration -1 = infinite
                player.addStatusEffect(new StatusEffectInstance(InvictusMod.FORM_EFFECT, -1, 0, false, false, true));
                player.sendMessage(Text.translatable("message.invictus.engaged"), true);
                world.playSound(null, player.getBlockPos(), SoundEvents.BLOCK_BEACON_ACTIVATE, SoundCategory.PLAYERS, 1f, 1f);
            }
        }
        player.getItemCooldownManager().set(this, 40);
        return TypedActionResult.success(stack, world.isClient());
    }
}
