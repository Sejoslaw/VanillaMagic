package com.github.sejoslaw.vanillamagic2.common.quests.eventCallers;

import com.github.sejoslaw.vanillamagic2.common.quests.EventCaller;
import com.github.sejoslaw.vanillamagic2.common.quests.types.QuestJumper;
import com.github.sejoslaw.vanillamagic2.common.utils.EntityUtils;
import com.github.sejoslaw.vanillamagic2.common.utils.NbtUtils;
import com.github.sejoslaw.vanillamagic2.common.utils.TextUtils;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

/**
 * @author Sejoslaw - https://github.com/Sejoslaw
 */
public class EventCallerJumper extends EventCaller<QuestJumper> {
    @SubscribeEvent
    public void saveBlockPosToBook(PlayerInteractEvent.RightClickBlock event) {
        this.executor.onPlayerInteract(event, (player, world, pos, direction) ->
                this.executor.withHands(player, (leftHandStack, rightHandStack) -> {
                    BlockPos savePos = pos.offset(direction);

                    ItemStack stack = new ItemStack(Items.ENCHANTED_BOOK);
                    stack.setDisplayName(TextUtils.combine(TextUtils.translate("quest.jumper.bookTitle"), TextUtils.getPosition(world, savePos)));
                    stack.setTagInfo(NbtUtils.NBT_POSITION, NbtUtils.toNbt(world, savePos));

                    player.setHeldItem(Hand.OFF_HAND, stack);
                }));
    }

    @SubscribeEvent
    public void teleport(PlayerInteractEvent.RightClickItem event) {
        this.executor.onPlayerInteract(event,
                (player, world, pos, direction) -> player.getHeldItemMainhand().getOrCreateTag().contains(NbtUtils.NBT_POSITION) ? this.quests.get(0) : null,
                (player, world, pos, direction, quest) ->
                    this.executor.withHands(player, (leftHandStack, rightHandStack) -> {
                        CompoundNBT nbt = rightHandStack.getOrCreateTag().getCompound(NbtUtils.NBT_POSITION);
                        BlockPos savedPos = NbtUtils.getPos(nbt);
                        World savedWorld = NbtUtils.getWorld(player.getServer(), nbt);
                        EntityUtils.teleport(player, savedPos, savedWorld);
                    }));
    }
}
