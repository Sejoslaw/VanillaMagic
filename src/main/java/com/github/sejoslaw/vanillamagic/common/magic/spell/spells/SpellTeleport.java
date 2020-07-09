package com.github.sejoslaw.vanillamagic.common.magic.spell.spells;

import com.github.sejoslaw.vanillamagic.api.magic.IWand;
import com.github.sejoslaw.vanillamagic.common.entity.EntitySpell;
import com.github.sejoslaw.vanillamagic.common.entity.EntitySpellTeleport;
import com.github.sejoslaw.vanillamagic.common.magic.spell.Spell;
import com.github.sejoslaw.vanillamagic.core.VMEntities;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

/**
 * @author Sejoslaw - https://github.com/Sejoslaw
 */
public class SpellTeleport extends Spell {
    public SpellTeleport(int spellID, String spellName, String spellUniqueName, IWand wand, ItemStack itemOffHand) {
        super(spellID, spellName, spellUniqueName, wand, itemOffHand);
    }

    public boolean castSpell(PlayerEntity caster, BlockPos pos, Direction face, Vec3d hitVec) {
        if (pos != null) {
            return false;
        }

        World world = caster.world;
        EntitySpell spellTeleport = new EntitySpellTeleport(VMEntities.TELEPORT_ENTITY_TYPE, world).setCastingEntity(caster);
        world.addEntity(spellTeleport);

        return true;
    }
}