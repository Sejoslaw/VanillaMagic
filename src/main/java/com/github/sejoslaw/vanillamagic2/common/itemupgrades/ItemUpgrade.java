package com.github.sejoslaw.vanillamagic2.common.itemupgrades;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;

/**
 * @author Sejoslaw - https://github.com/Sejoslaw
 */
public abstract class ItemUpgrade {
    /**
     * @return Array with know item types. i.e.: "sword", "pickaxe", "axe", "hoe", etc.
     */
    public abstract BaseItemType[] getBaseItemTypes();

    /**
     * @return Ingredient required to crafting (add to item) this upgrade.
     */
    public abstract ItemStack getIngredient();

    /**
     * @return Globally unique tag for this specific upgrade.
     */
    public abstract String getUniqueTag();

    /**
     * @return True if the specified ItemStack contains this items tag.
     */
    public boolean containsTag(ItemStack stack) {
        CompoundNBT tag = stack.getTag();
        return tag != null && tag.contains(this.getUniqueTag());
    }

    public boolean isValidIngredient(ItemStack stack) {
        ItemStack ingredient = this.getIngredient();
        return stack.getItem() == ingredient.getItem() && stack.getCount() >= ingredient.getCount();
    }
}
