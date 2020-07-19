package com.github.sejoslaw.vanillamagic2.common.utils;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.AbstractCookingRecipe;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.network.play.client.CPlayerDiggingPacket;
import net.minecraft.network.play.server.SChangeBlockPacket;
import net.minecraft.tileentity.AbstractFurnaceTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Sejoslaw - https://github.com/Sejoslaw
 */
public final class BlockUtils {
    /**
     * Breaks extra block on World. Each broken block affects the ItemStack durability.
     */
    public static void breakBlock(ItemStack stack, World world, PlayerEntity player, BlockPos pos) {
        if (world.isAirBlock(pos)) {
            return;
        }

        BlockState state = world.getBlockState(pos);
        Block block = state.getBlock();
        float strength = block.getExplosionResistance();

        if (!ForgeHooks.canHarvestBlock(state, player, world, pos) || strength > 10f) {
            return;
        }

        if (player.isCreative()) {
            block.onBlockHarvested(world, pos, state, player);

            if (block.removedByPlayer(state, world, pos, player, false, null)) {
                block.onPlayerDestroy(world, pos, state);
            }

            if (!world.isRemote) {
                ((ServerPlayerEntity) player).connection.sendPacket(new SChangeBlockPacket(world, pos));
            }

            return;
        }

        stack.onBlockDestroyed(world, state, pos, player);

        if (!world.isRemote) {
            int xp = ForgeHooks.onBlockBreakEvent(world, ((ServerPlayerEntity) player).interactionManager.getGameType(), (ServerPlayerEntity) player, pos);

            if (xp == -1) {
                return;
            }

            TileEntity tileEntity = world.getTileEntity(pos);

            if (block.removedByPlayer(state, world, pos, player, true, null)) {
                block.onPlayerDestroy(world, pos, state);
                block.harvestBlock(world, player, pos, state, tileEntity, stack);
                block.dropXpOnBlockBreak(world, pos, xp);
            }

            ServerPlayerEntity mpPlayer = (ServerPlayerEntity) player;
            mpPlayer.connection.sendPacket(new SChangeBlockPacket(world, pos));
        } else {
            world.playBroadcastSound(2001, pos, Block.getStateId(state));

            if (block.removedByPlayer(state, world, pos, player, true, null)) {
                block.onPlayerDestroy(world, pos, state);
            }

            stack.onBlockDestroyed(world, state, pos, player);

            Minecraft.getInstance().getConnection().sendPacket(new CPlayerDiggingPacket(CPlayerDiggingPacket.Action.STOP_DESTROY_BLOCK, pos, Direction.getFacingDirections(player)[0]));
        }
    }

    /**
     * @return All ItemEntities on specified position.
     */
    public static List<ItemEntity> getItems(World world, BlockPos pos) {
        AxisAlignedBB aabb = new AxisAlignedBB(
                pos.getX() - 0.5D,
                pos.getY() - 0.5D,
                pos.getZ() - 0.5D,
                pos.getX() + 0.5D,
                pos.getY() + 0.5D,
                pos.getZ() + 0.5D);
        return new ArrayList<>(world.getEntitiesWithinAABB(ItemEntity.class, aabb));
    }

    /**
     * @return All Ores on specified position.
     */
    public static List<ItemEntity> getOres(World world, BlockPos pos) {
        return getItems(world, pos)
                .stream()
                .filter(entity -> entity.getItem().getItem().getRegistryName().toString().toLowerCase().contains("ore"))
                .collect(Collectors.toList());
    }

    /**
     * @return ItemStacks with smelting result based on given input.
     */
    public static List<ItemStack> smeltItems(PlayerEntity player, List<ItemEntity> stacksToSmelt, int smeltingCost) {
        ItemStack leftHandStack = player.getHeldItemOffhand();
        World world = player.world;
        final int[] ticks = {0};

        return stacksToSmelt
                .stream()
                .map(entity -> {
                    ItemStack stack = entity.getItem();
                    int stackSize = stack.getCount();
                    int ticksToSmeltStack = stackSize * smeltingCost;

                    while (leftHandStack.getCount() > 0 && ticks[0] < ticksToSmeltStack) {
                        ticks[0] += AbstractFurnaceTileEntity.getBurnTimes().getOrDefault(stack.getItem(), 0);
                        stack.grow(-1);
                    }

                    ItemStack smeltingResult = getSmeltingResultAsNewStack(stack, world);

                    if (ticks[0] >= ticksToSmeltStack) {
                        smeltingResult.setCount(stack.getCount());
                        entity.remove();
                    } else if (ticks[0] >= smeltingCost) {
                        int howManyCanSmelt = ticks[0] / smeltingCost;
                        stack.grow(-howManyCanSmelt);
                        smeltingResult.setCount(howManyCanSmelt);
                    } else {
                        return ItemStack.EMPTY;
                    }

                    ticks[0] -= ticksToSmeltStack;
                    player.experience += getExperienceFromStack(stack, world);

                    return smeltingResult;
                })
                .filter(stack -> stack != ItemStack.EMPTY)
                .collect(Collectors.toList());
    }

    /**
     * @return Experience value from the given ItemStack.
     */
    public static float getExperienceFromStack(ItemStack stack, World world) {
        AbstractCookingRecipe cookingRecipe = (AbstractCookingRecipe) world.getRecipeManager().getRecipes()
                .stream()
                .filter(recipe -> (recipe.getType() == IRecipeType.SMELTING) && (recipe instanceof AbstractCookingRecipe) && ItemStack.areItemStacksEqual(recipe.getRecipeOutput(), stack))
                .findFirst()
                .orElse(null);
        return cookingRecipe == null ? 0 : cookingRecipe.getExperience();
    }

    /**
     * @return Smelting result based on given ItemStack.
     */
    public static ItemStack getSmeltingResultAsNewStack(ItemStack stackToSmelt, World world) {
        IRecipe<?> recipe = world.getRecipeManager().getRecipes()
                .stream()
                .filter(checkingRecipe -> (checkingRecipe.getType() == IRecipeType.SMELTING) && checkingRecipe.getIngredients().get(0).test(stackToSmelt))
                .findFirst()
                .orElse(null);

        return recipe == null ? ItemStack.EMPTY : recipe.getRecipeOutput();
    }
}
