package com.github.sejoslaw.vanillamagic2.common.spells;

import com.github.sejoslaw.vanillamagic2.common.functions.Function3;
import com.github.sejoslaw.vanillamagic2.common.utils.EntityUtils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.DamagingProjectileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.IWorld;

/**
 * @author Sejoslaw - https://github.com/Sejoslaw
 */
public abstract class Spell {
    /**
     * Casts current spell.
     */
    public abstract void cast(PlayerEntity player, IWorld world, BlockPos pos, Direction face);

    /**
     * Shoots specified Projectile Entity.
     */
    protected void shootEntitySpell(IWorld world, PlayerEntity player, Function3<Double, Double, Double, DamagingProjectileEntity> projectileFactory) {
        Vector3d lookingAt = player.getLookVec();

        double accelX = lookingAt.getX();
        double accelY = lookingAt.getY();
        double accelZ = lookingAt.getZ();

        DamagingProjectileEntity projectileEntity = projectileFactory.apply(accelX, accelY, accelZ);

        projectileEntity.setLocationAndAngles(
                player.getPosX() + accelX,
                player.getPosY() + accelY + 1.5D,
                player.getPosZ() + accelZ,
                player.rotationYaw,
                player.rotationPitch);

        projectileEntity.setMotion(Vector3d.ZERO);
        EntityUtils.setupAcceleration(projectileEntity, accelX, accelY, accelZ);

        world.addEntity(projectileEntity);
    }
}
