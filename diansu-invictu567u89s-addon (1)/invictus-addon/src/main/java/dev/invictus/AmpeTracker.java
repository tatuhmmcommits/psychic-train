package dev.invictus;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

/** Tracks AMPE-launched entities and detonates them: small blasts while falling, a big one on landing. */
public final class AmpeTracker {
    private static final float PULSE_DAMAGE = 4.0f;
    private static final float LANDING_DAMAGE = 10.0f;
    private static final int PULSE_INTERVAL = 7;
    private static final int MAX_AGE = 240;

    private static final class Mark {
        final UUID owner;
        final long start;
        long lastPulse;
        Mark(UUID owner, long start) { this.owner = owner; this.start = start; this.lastPulse = start; }
    }

    private static final Map<UUID, Mark> MARKS = new HashMap<>();

    private AmpeTracker() {}

    public static void mark(LivingEntity target, net.minecraft.entity.player.PlayerEntity owner, long worldTime) {
        MARKS.put(target.getUuid(), new Mark(owner.getUuid(), worldTime));
    }

    public static void tick(MinecraftServer server) {
        if (MARKS.isEmpty()) return;
        Iterator<Map.Entry<UUID, Mark>> it = MARKS.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<UUID, Mark> entry = it.next();
            Mark mark = entry.getValue();
            Entity found = find(server, entry.getKey());
            if (!(found instanceof LivingEntity target) || !target.isAlive()) { it.remove(); continue; }

            ServerWorld world = (ServerWorld) target.getWorld();
            long now = world.getTime();
            long age = now - mark.start;
            if (age > MAX_AGE) { it.remove(); continue; }

            ServerPlayerEntity owner = server.getPlayerManager().getPlayer(mark.owner);
            if (age >= 10 && target.isOnGround()) {
                blast(world, target, owner, LANDING_DAMAGE, true);
                it.remove();
            } else if (age >= 6 && target.getVelocity().y < 0 && now - mark.lastPulse >= PULSE_INTERVAL) {
                blast(world, target, owner, PULSE_DAMAGE, false);
                mark.lastPulse = now;
            }
        }
    }

    private static Entity find(MinecraftServer server, UUID uuid) {
        for (ServerWorld world : server.getWorlds()) {
            Entity e = world.getEntity(uuid);
            if (e != null) return e;
        }
        return null;
    }

    private static void blast(ServerWorld world, LivingEntity target, ServerPlayerEntity owner, float damage, boolean landing) {
        DamageSource source = world.getDamageSources().explosion(owner, owner);
        target.timeUntilRegen = 0; // let every blast land
        target.damage(source, damage);
        double x = target.getX(), y = target.getBodyY(0.5), z = target.getZ();
        world.spawnParticles(landing ? ParticleTypes.EXPLOSION_EMITTER : ParticleTypes.EXPLOSION, x, y, z, 1, 0, 0, 0, 0);
        world.spawnParticles(ParticleTypes.ELECTRIC_SPARK, x, y, z, landing ? 40 : 15, 0.5, 0.6, 0.5, 0.3);
        world.playSound(null, target.getBlockPos(), SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.PLAYERS, landing ? 1.2f : 0.7f, landing ? 0.9f : 1.2f);
    }
}
