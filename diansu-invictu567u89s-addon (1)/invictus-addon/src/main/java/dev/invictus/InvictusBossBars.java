package dev.invictus;

import net.minecraft.entity.boss.BossBar;
import net.minecraft.entity.boss.ServerBossBar;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;

import java.util.*;

/** Shows every Invictus player's health as a boss bar to nearby players. */
public final class InvictusBossBars {
    private static final double VIEW_RANGE_SQ = 64.0 * 64.0;
    private static final String[] ROMAN = {"", "I", "II", "III"};
    private static final Map<UUID, ServerBossBar> BARS = new HashMap<>();

    private InvictusBossBars() {}

    public static void tick(MinecraftServer server) {
        Set<UUID> active = new HashSet<>();
        for (ServerPlayerEntity boss : server.getPlayerManager().getPlayerList()) {
            if (!InvictusForm.isActive(boss)) continue;
            InvictusForm.enforce(boss);
            active.add(boss.getUuid());

            ServerBossBar bar = BARS.computeIfAbsent(boss.getUuid(),
                    u -> new ServerBossBar(Text.empty(), BossBar.Color.GREEN, BossBar.Style.PROGRESS));
            int phase = InvictusForm.phase(boss);
            bar.setPercent(MathHelper.clamp(boss.getHealth() / boss.getMaxHealth(), 0f, 1f));
            bar.setColor(phase == 1 ? BossBar.Color.GREEN : phase == 2 ? BossBar.Color.YELLOW : BossBar.Color.RED);
            bar.setName(Text.translatable("bossbar.invictus.title", boss.getDisplayName(), ROMAN[phase]));

            for (ServerPlayerEntity other : server.getPlayerManager().getPlayerList()) {
                if (other == boss) continue;
                boolean inRange = other.getWorld() == boss.getWorld() && other.squaredDistanceTo(boss) <= VIEW_RANGE_SQ;
                if (inRange) bar.addPlayer(other); else bar.removePlayer(other);
            }
        }
        Iterator<Map.Entry<UUID, ServerBossBar>> it = BARS.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<UUID, ServerBossBar> e = it.next();
            if (!active.contains(e.getKey())) {
                e.getValue().clearPlayers();
                it.remove();
            }
        }
    }

    public static void remove(ServerPlayerEntity player) {
        ServerBossBar bar = BARS.remove(player.getUuid());
        if (bar != null) bar.clearPlayers();
    }
}
