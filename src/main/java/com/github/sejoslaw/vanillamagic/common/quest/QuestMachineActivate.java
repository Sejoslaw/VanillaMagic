package com.github.sejoslaw.vanillamagic.common.quest;

import com.github.sejoslaw.vanillamagic.api.quest.IQuest;
import com.github.sejoslaw.vanillamagic.common.util.ItemStackUtil;
import com.google.gson.JsonObject;
import net.minecraft.block.CauldronBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;

/**
 * @author Sejoslaw - https://github.com/Sejoslaw
 */
public abstract class QuestMachineActivate extends Quest {
    protected ItemStack mustHaveOffHand;
    protected ItemStack mustHaveMainHand;

    public void readData(JsonObject jo) {
        super.readData(jo);

        if (jo.has("mustHaveOffHand")) {
            this.mustHaveOffHand = ItemStackUtil.getItemStackFromJSON(jo.get("mustHaveOffHand").getAsJsonObject());
        }

        if (jo.has("mustHaveMainHand")) {
            this.mustHaveMainHand = ItemStackUtil.getItemStackFromJSON(jo.get("mustHaveMainHand").getAsJsonObject());
        }
    }

    public boolean canActivate(PlayerEntity player) {
        ItemStack offHand = player.getHeldItemOffhand();
        ItemStack mainHand = player.getHeldItemMainhand();

        if (ItemStackUtil.isNullStack(offHand) || ItemStackUtil.isNullStack(mainHand)) {
            return false;
        }

        return ItemStack.areItemsEqual(offHand, mustHaveOffHand)
                && (ItemStackUtil.getStackSize(offHand) >= ItemStackUtil.getStackSize(mustHaveOffHand))
                && ItemStack.areItemsEqual(mainHand, mustHaveMainHand)
                && (ItemStackUtil.getStackSize(mainHand) >= ItemStackUtil.getStackSize(mustHaveMainHand));
    }

    public boolean startWorkWithCauldron(PlayerEntity player, BlockPos cauldronPos, IQuest quest) {
        if (!player.isSneaking() || !(player.world.getBlockState(cauldronPos).getBlock() instanceof CauldronBlock) || !canActivate(player)) {
            return false;
        }

        checkQuestProgress(player);

        return hasQuest(player);
    }
}