package dev.invictus;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

/** AMPE: magnetic blast that launches whatever you aim at into the clouds; it explodes on the way down. */
public class AmpeItem extends Item {
    private static final double RANGE = 24.0;
    private static final double LAUNCH_SPEED = 2.0;

    public AmpeItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        ItemStack stack = player.getStackInHand(hand);
        if (world.isClient) return TypedActionResult.success(stack, true);

        if (!InvictusForm.isActive(player)) {
            player.sendMessage(Text.translatable("message.invictus.requires_form"), true);
            return TypedActionResult.fail(stack);
        }

        LivingEntity target = findTarget(world, player);
        if (target == null) return TypedActionResult.fail(stack);

        target.setVelocity(0, LAUNCH_SPEED, 0);
        target.velocityModified = true;
        AmpeTracker.mark(target, player, world.getTime());

        ServerWorld sw = (ServerWorld) world;
        sw.spawnParticles(ParticleTypes.END_ROD, target.getX(), target.getBodyY(0.5), target.getZ(), 25, 0.3, 0.6, 0.3, 0.2);
        sw.spawnParticles(ParticleTypes.ELECTRIC_SPARK, target.getX(), target.getBodyY(0.5), target.getZ(), 25, 0.4, 0.6, 0.4, 0.3);
        SoundEvent blast = ElysiumCompat.blastSound();
        sw.playSound(null, player.getBlockPos(), blast != null ? blast : SoundEvents.ITEM_TRIDENT_THUNDER, SoundCategory.PLAYERS, 1.2f, 0.9f);

        int cooldown = 60 - 15 * (InvictusForm.phase(player) - 1); // later phases fire faster
        player.getItemCooldownManager().set(this, cooldown);
        return TypedActionResult.success(stack, false);
    }

    private static LivingEntity findTarget(World world, PlayerEntity player) {
        Vec3d eye = player.getEyePos();
        Vec3d look = player.getRotationVec(1.0f);
        Box box = player.getBoundingBox().stretch(look.multiply(RANGE)).expand(4.0);
        LivingEntity best = null;
        double bestDot = 0.95;
        for (LivingEntity e : world.getEntitiesByClass(LivingEntity.class, box, x -> x != player && x.isAlive() && !x.isSpectator())) {
            Vec3d to = e.getBoundingBox().getCenter().subtract(eye);
            double dist = to.length();
            if (dist > RANGE || dist < 0.01) continue;
            double dot = to.multiply(1.0 / dist).dotProduct(look);
            if (dot > bestDot && player.canSee(e)) {
                best = e;
                bestDot = dot;
            }
        }
        return best;
    }
}
