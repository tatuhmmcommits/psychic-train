package dev.invictus;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolMaterials;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.Map;
import java.util.WeakHashMap;

/** Condensed energy rapier: a fast sword whose right-click is a dash you can chain 3 times (Invictus form only). */
public class RapierItem extends SwordItem {
    public static final int MAX_DASHES = 3;
    private static final double DASH_SPEED = 1.8;
    /** [charges, lastDashTick] per player; resets fully after the recharge window. */
    private static final Map<PlayerEntity, long[]> DASH_STATE = new WeakHashMap<>();

    public RapierItem(Settings settings) {
        super(ToolMaterials.NETHERITE, 2, -1.6f, settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        ItemStack stack = player.getStackInHand(hand);
        if (world.isClient) return TypedActionResult.success(stack, true);

        if (!InvictusForm.isActive(player)) {
            player.sendMessage(Text.translatable("message.invictus.requires_form"), true);
            return TypedActionResult.fail(stack);
        }

        long now = world.getTime();
        int recharge = 60 - 15 * (InvictusForm.phase(player) - 1); // later phases recharge faster
        long[] state = DASH_STATE.computeIfAbsent(player, p -> new long[]{MAX_DASHES, 0});
        if (now - state[1] >= recharge) state[0] = MAX_DASHES;
        if (state[0] <= 0) return TypedActionResult.fail(stack);

        state[0]--;
        state[1] = now;

        Vec3d look = player.getRotationVec(1.0f);
        player.setVelocity(look.multiply(DASH_SPEED));
        player.velocityModified = true;
        player.fallDistance = 0;

        ServerWorld sw = (ServerWorld) world;
        sw.spawnParticles(ParticleTypes.ELECTRIC_SPARK, player.getX(), player.getBodyY(0.5), player.getZ(), 20, 0.4, 0.5, 0.4, 0.15);
        sw.playSound(null, player.getBlockPos(), SoundEvents.ITEM_TRIDENT_RIPTIDE_1, SoundCategory.PLAYERS, 1f, 1.3f);
        player.sendMessage(Text.translatable("message.invictus.dashes", (int) state[0], MAX_DASHES), true);
        return TypedActionResult.success(stack, false);
    }

    @Override
    public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        boolean result = super.postHit(stack, target, attacker);
        StatusEffect voltage = ElysiumCompat.voltage(); // optional Elysium synergy
        if (voltage != null && InvictusForm.isActive(attacker)) {
            target.addStatusEffect(new StatusEffectInstance(voltage, 60, 0), attacker);
        }
        return result;
    }
}
