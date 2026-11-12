package dev.invictus;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.server.network.ServerPlayerEntity;

/** Marker effect for the Invictus form. Applying/removing it enables/disables the form. */
public class InvictusEffect extends StatusEffect {
    public InvictusEffect() {
        super(StatusEffectCategory.BENEFICIAL, 0x3EBC98); // Elysium teal
    }

    @Override
    public void onApplied(LivingEntity entity, AttributeContainer attributes, int amplifier) {
        super.onApplied(entity, attributes, amplifier);
        if (entity instanceof ServerPlayerEntity player) InvictusForm.enable(player);
    }

    @Override
    public void onRemoved(LivingEntity entity, AttributeContainer attributes, int amplifier) {
        super.onRemoved(entity, attributes, amplifier);
        if (entity instanceof ServerPlayerEntity player) InvictusForm.disable(player);
    }
}
