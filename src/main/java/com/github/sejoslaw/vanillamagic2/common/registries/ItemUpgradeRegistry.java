package com.github.sejoslaw.vanillamagic2.common.registries;

import com.github.sejoslaw.vanillamagic2.common.itemupgrades.BaseItemType;
import com.github.sejoslaw.vanillamagic2.common.itemupgrades.ItemUpgrade;
import com.github.sejoslaw.vanillamagic2.common.itemupgrades.ItemUpgradeProcessor;
import com.github.sejoslaw.vanillamagic2.common.itemupgrades.eventcallers.ItemUpgradeEventCallerAutosmelt;
import com.github.sejoslaw.vanillamagic2.common.itemupgrades.eventcallers.ItemUpgradeEventCallerLifesteal;
import com.github.sejoslaw.vanillamagic2.common.itemupgrades.eventcallers.ItemUpgradeEventCallerThor;
import com.github.sejoslaw.vanillamagic2.common.itemupgrades.eventcallers.ItemUpgradeEventCallerWither;
import com.github.sejoslaw.vanillamagic2.common.itemupgrades.types.AutosmeltUpgrade;
import com.github.sejoslaw.vanillamagic2.common.itemupgrades.types.LifestealUpgrade;
import com.github.sejoslaw.vanillamagic2.common.itemupgrades.types.ThorUpgrade;
import com.github.sejoslaw.vanillamagic2.common.itemupgrades.types.WitherUpgrade;
import net.minecraft.item.ItemStack;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Sejoslaw - https://github.com/Sejoslaw
 */
public final class ItemUpgradeRegistry {
    public static final Set<ItemUpgradeProcessor> UPGRADES = new HashSet<>();

    public static void initialize() {
        UPGRADES.add(new ItemUpgradeProcessor(ItemUpgradeEventCallerAutosmelt.class, AutosmeltUpgrade.class).register());
        UPGRADES.add(new ItemUpgradeProcessor(ItemUpgradeEventCallerLifesteal.class, LifestealUpgrade.class).register());
        UPGRADES.add(new ItemUpgradeProcessor(ItemUpgradeEventCallerThor.class, ThorUpgrade.class).register());
        UPGRADES.add(new ItemUpgradeProcessor(ItemUpgradeEventCallerWither.class, WitherUpgrade.class).register());
    }

    public static boolean isBase(ItemStack stack) {
        return BaseItemType.TYPES.stream().anyMatch(type -> stack.getItem().getRegistryName().toString().toLowerCase().contains(type.itemType.toLowerCase()));
    }

    public static boolean isIngredient(ItemStack stack) {
        return UPGRADES.stream().anyMatch(proc -> proc.getItemUpgradeEventCaller().itemUpgrade.isValidIngredient(stack));
    }

    public static BaseItemType getBaseItemType(ItemStack stack) {
        return BaseItemType.TYPES
                .stream()
                .filter(type -> stack.getItem().getRegistryName().toString().toLowerCase().contains(type.itemType.toLowerCase()))
                .findFirst()
                .orElse(new BaseItemType(stack.getTranslationKey()));
    }

    public static List<ItemUpgrade> getUpgrades(BaseItemType type) {
        return UPGRADES
                .stream()
                .filter(proc ->
                        Arrays.stream(proc.getItemUpgradeEventCaller().itemUpgrade.getBaseItemTypes())
                        .anyMatch(procType -> procType.itemType.equals(type.itemType)))
                .map(proc -> proc.getItemUpgradeEventCaller().itemUpgrade)
                .collect(Collectors.toList());
    }
}
