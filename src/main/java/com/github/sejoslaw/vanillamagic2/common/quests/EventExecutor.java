package com.github.sejoslaw.vanillamagic2.common.quests;

import com.github.sejoslaw.vanillamagic2.common.functions.*;
import com.github.sejoslaw.vanillamagic2.common.handlers.EventHandler;
import com.github.sejoslaw.vanillamagic2.common.recipes.AltarRecipe;
import com.github.sejoslaw.vanillamagic2.common.registries.PlayerQuestProgressRegistry;
import com.github.sejoslaw.vanillamagic2.common.tileentities.machines.VMTileMachine;
import com.github.sejoslaw.vanillamagic2.common.utils.*;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @author Sejoslaw - https://github.com/Sejoslaw
 */
public final class EventExecutor<TQuest extends Quest> extends EventHandler {
    private final EventCaller<TQuest> caller;

    public EventExecutor(EventCaller<TQuest> caller) {
        this.caller = caller;
    }

    public TQuest click(Block block, IWorld world, BlockPos pos, Supplier<TQuest> action) {
        return world.getBlockState(pos).getBlock() == block ? action.get() : null;
    }

    public void forQuestWithCheck(Function<TQuest, Boolean> check,
                                  Consumer<TQuest> action) {
        this.caller.quests
                .stream()
                .filter(check::apply)
                .forEach(action);
    }

    public void onAttackEntityNoHandsCheck(AttackEntityEvent event,
                               Function3<PlayerEntity, IWorld, Entity, TQuest> check,
                               Consumer4<PlayerEntity, IWorld, Entity, TQuest> consumer) {
        PlayerEntity player = event.getPlayer();
        IWorld world = player.getEntityWorld();
        Entity target = event.getTarget();

        performCheckNoHandsCheck(player,
                () -> check.apply(player, world, target),
                (quest) -> consumer.accept(player, world, target, quest));
    }

    public void onBlockEvent(BlockEvent event, PlayerEntity player,
                             Function3<IWorld, BlockPos, BlockState, TQuest> check,
                             Consumer4<IWorld, BlockPos, BlockState, TQuest> consumer) {
        IWorld world = event.getWorld();
        BlockPos pos = event.getPos();
        BlockState state = event.getState();

        performCheck(player,
                () -> check.apply(world, pos, state),
                (quest) -> consumer.accept(world, pos, state, quest));
    }

    public void onBlockEventNoHandsCheck(BlockEvent event, PlayerEntity player,
                             Function3<IWorld, BlockPos, BlockState, TQuest> check,
                             Consumer4<IWorld, BlockPos, BlockState, TQuest> consumer) {
        IWorld world = event.getWorld();
        BlockPos pos = event.getPos();
        BlockState state = event.getState();

        performCheckNoHandsCheck(player,
                () -> check.apply(world, pos, state),
                (quest) -> consumer.accept(world, pos, state, quest));
    }

    public void onBlockBreak(BlockEvent.BreakEvent event,
                             Function4<PlayerEntity, IWorld, BlockPos, BlockState, TQuest> check,
                             Consumer4<PlayerEntity, IWorld, BlockPos, BlockState> consumer) {
        PlayerEntity player = event.getPlayer();

        onBlockEvent(event, player,
                (world, pos, state) -> check.apply(player, world, pos, state),
                (world, pos, state, quest) -> consumer.accept(player, world, pos, state));
    }

    public void onBlockBreakNoHandsCheck(BlockEvent.BreakEvent event,
                             Function4<PlayerEntity, IWorld, BlockPos, BlockState, TQuest> check,
                             Consumer5<PlayerEntity, IWorld, BlockPos, BlockState, TQuest> consumer) {
        PlayerEntity player = event.getPlayer();

        onBlockEventNoHandsCheck(event, player,
                (world, pos, state) -> check.apply(player, world, pos, state),
                (world, pos, state, quest) -> consumer.accept(player, world, pos, state, quest));
    }

    public void onPlayerInteract(PlayerInteractEvent event,
                                 Consumer4<PlayerEntity, IWorld, BlockPos, Direction> consumer) {
        super.onPlayerInteract(event, (player, world, pos, direction) ->
                performCheck(player, (quest) ->
                        consumer.accept(player, world, pos, direction)));
    }

    public void onPlayerInteractNoStackSizeCheck(PlayerInteractEvent event,
                                 Consumer4<PlayerEntity, IWorld, BlockPos, Direction> consumer) {
        PlayerEntity player = event.getPlayer();

        performCheckNoStackSizeCheck(player, (quest) -> consumer.accept(player, player.world, event.getPos(), event.getFace()));
    }

    public void onPlayerInteract(PlayerInteractEvent event,
                                 Function4<PlayerEntity, IWorld, BlockPos, Direction, TQuest> check,
                                 Consumer5<PlayerEntity, IWorld, BlockPos, Direction, TQuest> consumer) {
        PlayerEntity player = event.getPlayer();
        IWorld world = event.getWorld();
        BlockPos pos = event.getPos();
        Direction dir = event.getFace();

        performCheck(player,
                () -> check.apply(player, world, pos, dir),
                (quest) -> consumer.accept(player, world, pos, dir, quest));
    }

    public void onEntityPlace(BlockEvent.EntityPlaceEvent event,
                              Function4<PlayerEntity, IWorld, BlockState, BlockPos, TQuest> check,
                              Consumer4<PlayerEntity, IWorld, BlockState, BlockPos> consumer) {
        Entity entity = event.getEntity();
        PlayerEntity player = entity instanceof PlayerEntity ? (PlayerEntity) entity : null;

        onBlockEvent(event, player,
                (world, pos, state) -> check.apply(player, world, state, pos),
                (world, pos, state, quest) -> consumer.accept(player, world, state, pos));
    }

    public void onEntityInteract(PlayerInteractEvent.EntityInteract event,
                                 Function4<PlayerEntity, Entity, IWorld, BlockPos, TQuest> check,
                                 Consumer4<PlayerEntity, Entity, IWorld, BlockPos> consumer) {
        Entity target = event.getTarget();

        onPlayerInteract(event,
                (player, world, pos, direction) -> check.apply(player, target, world, pos),
                (player, world, pos, direction, quest) -> consumer.accept(player, target, world, pos));
    }

    public void onItemCrafted(PlayerEvent.ItemCraftedEvent event,
                              Function3<PlayerEntity, ItemStack, IInventory, TQuest> check,
                              Consumer3<PlayerEntity, ItemStack, IInventory> consumer) {
        PlayerEntity player = event.getPlayer();
        ItemStack result = event.getCrafting();
        IInventory inv = event.getInventory();

        performCheck(player,
                () -> check.apply(player, result, inv),
                (quest) -> consumer.accept(player, result, inv));
    }

    public void onPlayerUpdate(LivingEvent.LivingUpdateEvent event,
                               Function<PlayerEntity, TQuest> check,
                               Consumer2<PlayerEntity, TQuest> consumer) {
        LivingEntity entity = event.getEntityLiving();

        if (!(entity instanceof PlayerEntity)) {
            return;
        }

        PlayerEntity player = (PlayerEntity) entity;

        performCheck(player, () -> check.apply(player), (quest) -> consumer.accept(player, quest));
    }

    public void onItemPickup(PlayerEvent.ItemPickupEvent event,
                             Function4<PlayerEntity, IWorld, ItemEntity, ItemStack, TQuest> check,
                             Consumer5<PlayerEntity, IWorld, ItemEntity, ItemStack, TQuest> consumer) {
        PlayerEntity player = event.getPlayer();
        ItemEntity originalEntity = event.getOriginalEntity();
        ItemStack pickedStack = event.getStack();
        IWorld world = player.world;

        performCheck(player,
                () -> check.apply(player, world, originalEntity, pickedStack),
                (quest) -> consumer.accept(player, world, originalEntity, pickedStack, quest));
    }

    public void onPlayerTick(TickEvent.PlayerTickEvent event,
                             Consumer3<PlayerEntity, IWorld, TQuest> consumer) {
        PlayerEntity player = event.player;
        IWorld world = player.world;

        performCheck(player, quest -> consumer.accept(player, world, quest));
    }

    public void onPlayerTick(TickEvent.PlayerTickEvent event,
                             Function2<PlayerEntity, IWorld, TQuest> check,
                             Consumer3<PlayerEntity, IWorld, TQuest> consumer) {
        PlayerEntity player = event.player;
        IWorld world = player.world;

        performCheck(player, () -> check.apply(player, world), quest -> consumer.accept(player, world, quest));
    }

    public void onPlayerTickNoHandsCheck(TickEvent.PlayerTickEvent event,
                                         Function2<PlayerEntity, IWorld, TQuest> check,
                                         Consumer3<PlayerEntity, IWorld, TQuest> consumer) {
        PlayerEntity player = event.player;
        IWorld world = player.world;

        performCheckNoHandsCheck(player, () -> check.apply(player, world), quest -> consumer.accept(player, world, quest));
    }

    public void craftOnAltar(PlayerInteractEvent event,
                             List<AltarRecipe> recipes,
                             Function2<ItemStack, ItemStack, Boolean> optionalCheck) {
        final List<ItemEntity>[] ingredientsInCauldron = new List[1];
        final AltarRecipe[] chosenRecipe = new AltarRecipe[1];

        this.onPlayerInteract(event,
                (player, world, pos, direction) ->
                        this.click(Blocks.CAULDRON, world, pos, () -> {
                            if (WorldUtils.getIsRemote(world)) {
                                return null;
                            }

                            ingredientsInCauldron[0] = WorldUtils.getItems(world, pos);

                            if (ingredientsInCauldron[0].size() == 0) {
                                return null;
                            }

                            for (AltarRecipe recipe : recipes) {
                                if (ingredientsInCauldron[0].size() != recipe.ingredients.size() ||
                                    !AltarUtils.canCraftOnAltar(recipe.ingredients, ingredientsInCauldron[0], optionalCheck)) {
                                    continue;
                                }

                                chosenRecipe[0] = recipe;
                                return (TQuest)chosenRecipe[0].quest;
                            }

                            return null;
                        }),
                (player, world, pos, direction, quest) -> {
                    if (WorldUtils.getIsRemote(world)) {
                        return;
                    }

                    ingredientsInCauldron[0].forEach(Entity::remove);
                    BlockPos newItemPos = new BlockPos(pos.getX(), pos.getY() + 1, pos.getZ());
                    chosenRecipe[0].results.forEach(stack -> Block.spawnAsEntity(WorldUtils.asWorld(world), newItemPos, stack.copy()));
                });
    }

    public void useVMItem(PlayerEntity player,
                          String vmItemUniqueKey,
                          Consumer<ItemStack> consumer) {
        this.withHands(player, (leftHandStack, rightHandStack) -> {
            CompoundNBT nbt = rightHandStack.getTag();

            if (nbt == null) {
                return;
            }

            String key = NbtUtils.NBT_VM_ITEM_UNIQUE_NAME;

            if (nbt.contains(key) && nbt.getString(key).equals(vmItemUniqueKey)) {
                consumer.accept(rightHandStack);
                return;
            }

            nbt = leftHandStack.getTag();

            if (nbt != null && nbt.contains(key) && nbt.getString(key).equals(vmItemUniqueKey)) {
                consumer.accept(leftHandStack);
            }
        });
    }

    public void addVMTileMachine(PlayerInteractEvent.RightClickBlock event, String moduleKey) {
        this.onPlayerInteract(event,
                (player, world, pos, direction) -> this.click(Blocks.CAULDRON, world, pos, () -> this.caller.quests.get(0)),
                (player, world, pos, direction, quest) -> WorldUtils.spawnVMTile(player, world, pos, new VMTileMachine(),
                        (tile) -> tile.setModuleKey(moduleKey)));
    }

    public boolean areHeldItemsCorrect(PlayerEntity player, TQuest quest, boolean checkStackSize) {
        if (quest.leftHandStack != null && !ItemStackUtils.areEqual(player.getHeldItemOffhand(), quest.leftHandStack, checkStackSize)) {
            return false;
        }

        return quest.rightHandStack == null || ItemStackUtils.areEqual(player.getHeldItemMainhand(), quest.rightHandStack, checkStackSize);
    }

    /*
     * -----====== Private Methods =====-----
     */

    private void checkRegisteredQuestsCount(Action action) {
        if (this.caller.quests.size() <= 0) {
            return;
        }

        action.execute();
    }

    private void performCheck(PlayerEntity player,
                              Consumer<TQuest> consumer) {
        checkRegisteredQuestsCount(() -> checkItemsInHands(player, (quest) -> checkQuestProgress(player, quest, consumer), true));
    }

    private void performCheckNoStackSizeCheck(PlayerEntity player,
                                              Consumer<TQuest> consumer) {
        checkRegisteredQuestsCount(() -> checkItemsInHands(player, (quest) -> checkQuestProgress(player, quest, consumer), false));
    }

    private void performCheck(PlayerEntity player,
                              Supplier<TQuest> check,
                              Consumer<TQuest> consumer) {

        checkRegisteredQuestsCount(() ->
                checkItemsInHands(player, (skippedQuest) -> {
                    TQuest quest = check.get();

                    if (quest == null || !this.areHeldItemsCorrect(player, quest, true)) {
                        return;
                    }

                    checkQuestProgress(player, quest, consumer);
                }, true));
    }

    private void performCheckNoHandsCheck(PlayerEntity player,
                                          Supplier<TQuest> check,
                                          Consumer<TQuest> consumer) {
        checkRegisteredQuestsCount(() -> {
            TQuest quest = check.get();

            if (quest == null) {
                return;
            }

            checkQuestProgress(player, quest, consumer);
        });
    }

    private void checkItemsInHands(PlayerEntity player,
                                   Consumer<TQuest> consumer,
                                   boolean checkStackSize) {
        for (TQuest quest : this.caller.quests) {
            if (!this.areHeldItemsCorrect(player, quest, checkStackSize)) {
                continue;
            }

            consumer.accept(quest);
            return;
        }
    }

    private void checkQuestProgress(PlayerEntity player,
                                    TQuest quest,
                                    Consumer<TQuest> consumer) {
        String questUniqueName = quest.uniqueName;

        if (!PlayerQuestProgressRegistry.hasPlayerGotQuest(player, questUniqueName)) {
            if (PlayerQuestProgressRegistry.canPlayerGetQuest(player, questUniqueName)) {
                PlayerQuestProgressUtils.givePlayerQuest(player, quest);
            } else {
                return;
            }
        }

        consumer.accept(quest);
    }
}
