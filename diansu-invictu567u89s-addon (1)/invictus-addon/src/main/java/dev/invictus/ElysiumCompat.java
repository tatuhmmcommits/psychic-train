package dev.invictus;

import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.registry.Registries;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

/** Looks Elysium content up by registry id so the addon needs no compile-time dependency on it. */
public final class ElysiumCompat {
    private ElysiumCompat() {}

    /** Elysium's "Voltage" effect, or null if it isn't registered. */
    public static StatusEffect voltage() {
        return Registries.STATUS_EFFECT.get(new Identifier("elysium", "elysium_vulnerability"));
    }

    /** The Cheirosiphon's magnetic blast sound, or null. */
    public static SoundEvent blastSound() {
        return Registries.SOUND_EVENT.get(new Identifier("elysium", "item.cheirosiphon.blast"));
    }
}
