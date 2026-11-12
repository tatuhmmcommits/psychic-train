package dev.invictus;

import net.minecraft.entity.LivingEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import virtuoel.pehkui.api.ScaleData;
import virtuoel.pehkui.api.ScaleTypes;

/** Everything the Invictus form changes about a player. */
public final class InvictusForm {
    /** "Slightly bigger, like .25 bigger". */
    public static final float SCALE = 1.25f;

    private InvictusForm() {}

    public static boolean isActive(LivingEntity entity) {
        return InvictusMod.FORM_EFFECT != null && entity.hasStatusEffect(InvictusMod.FORM_EFFECT);
    }

    /** 1, 2 or 3 depending on remaining health (above 66%, above 33%, below). */
    public static int phase(LivingEntity entity) {
        float pct = entity.getHealth() / entity.getMaxHealth();
        return pct > 0.66f ? 1 : pct > 0.33f ? 2 : 3;
    }

    public static void enable(ServerPlayerEntity player) {
        ScaleTypes.BASE.getScaleData(player).setScale(SCALE);
        if (!player.getAbilities().allowFlying) {
            player.getAbilities().allowFlying = true;
            player.sendAbilitiesUpdate();
        }
    }

    public static void disable(ServerPlayerEntity player) {
        ScaleData data = ScaleTypes.BASE.getScaleData(player);
        if (Math.abs(data.getBaseScale() - SCALE) < 0.001f) data.setScale(1.0f); // don't clobber other mods' scaling
        if (!player.isCreative() && !player.isSpectator()) {
            player.getAbilities().allowFlying = false;
            player.getAbilities().flying = false;
            player.sendAbilitiesUpdate();
        }
    }

    /** Cheap per-tick safety net (e.g. effect restored from save data without onApplied firing). */
    public static void enforce(ServerPlayerEntity player) {
        if (Math.abs(ScaleTypes.BASE.getScaleData(player).getBaseScale() - SCALE) > 0.001f) enable(player);
        else if (!player.getAbilities().allowFlying) enable(player);
    }
}
