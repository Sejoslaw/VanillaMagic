package com.github.sejoslaw.vanillamagic.common.quest.upgrade.itemupgrade.upgrades;

import com.github.sejoslaw.vanillamagic.common.itemupgrade.ItemUpgradeBase;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

/**
 * Class which describes basic Sowrd upgrade.
 *
 * @author Sejoslaw - https://github.com/Sejoslaw
 */
public abstract class UpgradeSword extends ItemUpgradeBase {
    /**
     * Method which is run after checking all conditions in main event method.
     */
    public abstract void onAttack(PlayerEntity player, Entity target);

    /**
     * Check conditions for event to start. Also check if sword has got required
     * Tag.
     */
    @SubscribeEvent
    public void doEvent(AttackEntityEvent event) {
        PlayerEntity player = event.getPlayer();
        ItemStack playerMainHandStack = player.getHeldItemMainhand();

        if (!containsTag(playerMainHandStack)) {
            return;
        }

        onAttack(player, event.getTarget());
    }
}