package com.github.sejoslaw.vanillamagic2.common.itemupgrades.eventcallers;

import com.github.sejoslaw.vanillamagic2.common.itemupgrades.ItemUpgradeEventCaller;
import com.github.sejoslaw.vanillamagic2.common.utils.EntityUtils;
import com.github.sejoslaw.vanillamagic2.common.utils.WorldUtils;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.effect.LightningBoltEntity;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

/**
 * @author Sejoslaw - https://github.com/Sejoslaw
 */
public class ItemUpgradeEventCallerThor extends ItemUpgradeEventCaller {
    @SubscribeEvent
    public void onAttack(AttackEntityEvent event) {
        this.eventCaller.executor.onAttackEntityNoHandsCheck(event,
                (player, world, entity) -> this.getQuest(player),
                (player, world, entity, quest) ->
                        this.execute(player, () -> {
                            LightningBoltEntity lightningBoltEntity = EntityType.LIGHTNING_BOLT.create(WorldUtils.asWorld(world));
                            lightningBoltEntity.setPosition(entity.getPosX(), entity.getPosY(), entity.getPosZ());
                            EntityUtils.spawnLightningBolt(world, lightningBoltEntity);
                        }));
    }
}
