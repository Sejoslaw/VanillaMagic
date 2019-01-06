package seia.vanillamagic.util;

import java.util.Random;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.CraftingHelper.ShapedPrimer;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.registries.GameData;

/**
 * Various method for handling crafting since Minecraft 1.12
 * 
 * @author Sejoslaw - https://github.com/Sejoslaw
 */
public final class CraftingUtil {
	private CraftingUtil() {
	}

	/**
	 * Adds a basic shaped recipe
	 *
	 * @param output The stack that should be produced
	 */
	public static void addShapedRecipe(ItemStack output, Object... params) {
		ResourceLocation location = getNameForRecipe(output);
		ShapedPrimer primer = CraftingHelper.parseShaped(params);
		ShapedRecipes recipe = new ShapedRecipes(output.getItem().getRegistryName().toString(), primer.width,
				primer.height, primer.input, output);
		recipe.setRegistryName(location);
		GameData.register_impl(recipe);
	}

	/**
	 * Adds a basic shapeless recipe
	 *
	 * @param output The stack that should be produced
	 */
	public static void addShapelessRecipe(ItemStack output, Object... input) {
		ResourceLocation location = getNameForRecipe(output);
		ShapelessRecipes recipe = new ShapelessRecipes(location.getResourceDomain(), output, buildInput(input));
		recipe.setRegistryName(location);
		GameData.register_impl(recipe);
	}

	/**
	 * Genereates a unique name based of the active mod, and the itemstack that the
	 * recipe outputs
	 *
	 * @param output an itemstack, usualy the one the the recipe produces
	 * @return a unique ResourceLocation based off the item item
	 */
	public static ResourceLocation getNameForRecipe(ItemStack output) {
		ModContainer activeContainer = Loader.instance().activeModContainer();
		ResourceLocation baseLoc = new ResourceLocation(activeContainer.getModId(),
				output.getItem().getRegistryName().getResourcePath());
		ResourceLocation recipeLoc = baseLoc;

		while (CraftingManager.REGISTRY.containsKey(recipeLoc)) {
			recipeLoc = new ResourceLocation(activeContainer.getModId(),
					baseLoc.getResourcePath() + "_" + new Random().nextInt());
		}

		return recipeLoc;
	}

	/**
	 * Converts an object array into a NonNullList of Ingredients
	 */
	private static NonNullList<Ingredient> buildInput(Object[] input) {
		NonNullList<Ingredient> list = NonNullList.create();

		for (Object obj : input) {
			if (obj instanceof Ingredient) {
				list.add((Ingredient) obj);
			} else {
				Ingredient ingredient = CraftingHelper.getIngredient(obj);

				if (ingredient == null) {
					ingredient = Ingredient.EMPTY;
				}

				list.add(ingredient);
			}
		}

		return list;
	}
}