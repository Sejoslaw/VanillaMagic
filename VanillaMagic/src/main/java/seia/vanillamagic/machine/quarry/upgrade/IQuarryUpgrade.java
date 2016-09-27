package seia.vanillamagic.machine.quarry.upgrade;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import seia.vanillamagic.machine.quarry.IQuarry;

/**
 * Every method except getBlock() is fired once a tick.
 */
public interface IQuarryUpgrade
{
	/**
	 * Returns the Block to which this upgrade is connected.
	 */
	public Block getBlock();
	
	/**
	 * Returns the list of the stacks which will be dropped from the given "blockToDig". <br>
	 * Here is where You should do Your stuff like silk-touch, fortune, etc.
	 */
	default public List<ItemStack> getDrops(Block blockToDig, IBlockAccess world, BlockPos workingPos, IBlockState workingPosState)
	{
		return new ArrayList<ItemStack>();
	}
	
	/**
	 * This method is fired before the Quarry starts to count anything. <br>
	 * Here is where You should modify the Quarry itself. <br>
	 * <br>
	 * This method is fired once a tick !!!
	 */
	default public void modifyQuarry(IQuarry quarry)
	{
	}
	
	/**
	 * Returns the upgrade that must be placed BEFORE this upgrade is placed.
	 */
	default public Class<? extends IQuarryUpgrade> requiredUpgrade()
	{
		return null;
	}
}