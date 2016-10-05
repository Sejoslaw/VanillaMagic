package seia.vanillamagic.quest;

import java.util.List;

import com.google.gson.JsonObject;

import net.minecraft.block.BlockCauldron;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import seia.vanillamagic.spell.EnumWand;
import seia.vanillamagic.util.AltarChecker;
import seia.vanillamagic.util.SmeltingHelper;

public class QuestSmeltOnAltar extends Quest
{
	// in 200 ticks You will smelt 1 item in Furnace
	public static final int ONE_ITEM_SMELT_TICKS = 200;
	
	protected int requiredAltarTier;
	protected EnumWand requiredMinimalWand;
	
	public void readData(JsonObject jo)
	{
		super.readData(jo);
		this.requiredAltarTier = jo.get("requiredAltarTier").getAsInt();
		this.requiredMinimalWand = EnumWand.getWandByTier(jo.get("wandTier").getAsInt());
	}
	
	public int getRequiredAltarTier()
	{
		return requiredAltarTier;
	}
	
	public EnumWand getRequiredWand()
	{
		return requiredMinimalWand;
	}
	
	@SubscribeEvent
	public void smeltOnAltar(RightClickBlock event)
	{
		EntityPlayer player = event.getEntityPlayer();
		BlockPos cauldronPos = event.getPos();
		// player has got required wand in hand
		if(EnumWand.isWandInMainHandRight(player, requiredMinimalWand.wandTier))
		{
			// check if player has the "fuel" in offHand
			ItemStack fuelOffHand = player.getHeldItemOffhand();
			if(fuelOffHand == null)
			{
				return;
			}
			if(SmeltingHelper.isItemFuel(fuelOffHand))
			{
				World world = player.worldObj;
				// is right-clicking on Cauldron
				if(world.getBlockState(cauldronPos).getBlock() instanceof BlockCauldron)
				{
					// is altair build correct
					if(AltarChecker.checkAltarTier(world, cauldronPos, requiredAltarTier))
					{
						List<EntityItem> itemsToSmelt = SmeltingHelper.getSmeltable(world, cauldronPos);
						if(itemsToSmelt.size() > 0)
						{
							if(canPlayerGetAchievement(player))
							{
								player.addStat(achievement, 1);
							}
							if(player.hasAchievement(achievement))
							{
								SmeltingHelper.countAndSmelt(player, itemsToSmelt, cauldronPos.offset(EnumFacing.UP), this, true);
							}
						}
					}
				}
			}
		}
	}
}