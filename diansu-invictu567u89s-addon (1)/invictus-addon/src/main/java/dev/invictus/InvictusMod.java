package dev.invictus;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class InvictusMod implements ModInitializer {
    public static final String MOD_ID = "invictus";

    /** The status effect that IS the Invictus form (infinite duration, toggled by the Engine). */
    public static StatusEffect FORM_EFFECT;

    public static Identifier id(String path) {
        return new Identifier(MOD_ID, path);
    }

    @Override
    public void onInitialize() {
        FORM_EFFECT = Registry.register(Registries.STATUS_EFFECT, id("invictus_form"), new InvictusEffect());
        InvictusItems.register();

        ServerTickEvents.END_SERVER_TICK.register(server -> {
            InvictusBossBars.tick(server);
            AmpeTracker.tick(server);
        });

        // After respawn / end-portal return: keep the form only if the effect survived.
        ServerPlayerEvents.AFTER_RESPAWN.register((oldPlayer, newPlayer, alive) -> {
            if (newPlayer.hasStatusEffect(FORM_EFFECT)) InvictusForm.enable(newPlayer);
            else InvictusForm.disable(newPlayer);
        });

        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> InvictusBossBars.remove(handler.getPlayer()));
    }
}
