package seia.vanillamagic.item.potionedcrystal;

import java.util.List;

import javax.annotation.Nullable;

import org.apache.logging.log4j.Level;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionType;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import seia.vanillamagic.core.VanillaMagic;
import seia.vanillamagic.item.VanillaMagicItems;
import seia.vanillamagic.util.CauldronHelper;

public class PotionedCrystalHelper 
{
	private PotionedCrystalHelper()
	{
	}
	
	public static String getPotionTypeName(PotionType pt)
	{
		return ForgeRegistries.POTION_TYPES.getKey(pt).getResourcePath();
	}
	
	/**
	 * Returns the IPotionedCrystal from ItemPotion.
	 */
	@Nullable
	public static IPotionedCrystal getPotionedCrystalFromCauldron(World world, BlockPos cauldronPos)
	{
		List<EntityItem> itemsInCauldron = CauldronHelper.getItemsInCauldron(world, cauldronPos);
		if(itemsInCauldron == null)
		{
			return null;
		}
		for(EntityItem ei : itemsInCauldron)
		{
			ItemStack stack = ei.getEntityItem();
			if(stack == null)
			{
				return null;
			}
			if(stack.getItem() instanceof ItemPotion)
			{
				PotionType pt = PotionUtils.getPotionFromItem(stack);
				String ptName = getPotionTypeName(pt);
				for(IPotionedCrystal pc : VanillaMagicItems.INSTANCE.potionedCrystals)
				{
					if(pc.getPotionName().equals(ptName))
					{
						return pc;
					}
				}
			}
		}
		return null;
	}
	
	/**
	 * Given ItemStack is a PotionedCrystal - Nether Star.
	 */
	@Nullable
	public static IPotionedCrystal getPotionedCrystal(ItemStack netherStarStack)
	{
		if(netherStarStack == null)
		{
			return null;
		}
		NBTTagCompound stackTag = netherStarStack.getTagCompound();
		if(stackTag == null)
		{
			return null;
		}
		if(stackTag.hasKey(IPotionedCrystal.NBT_POTION_TYPE_NAME))
		{
			String name = stackTag.getString(IPotionedCrystal.NBT_POTION_TYPE_NAME);
			for(IPotionedCrystal pc : VanillaMagicItems.INSTANCE.potionedCrystals)
			{
				if(name.equals(pc.getPotionName()))
				{
					return pc;
				}
			}
		}
		return null;
	}
	
	public static void registerRecipes()
	{
		for(PotionType potionType : ForgeRegistries.POTION_TYPES.getValues())
		{
			VanillaMagicItems.INSTANCE.potionedCrystals.add(new IPotionedCrystal()
			{
				public PotionType getPotionType()
				{
					return potionType;
				}
			});
		}
		VanillaMagic.LOGGER.log(Level.INFO, "Registered Potioned Crystals: " + VanillaMagicItems.INSTANCE.potionedCrystals.size());
	}
}