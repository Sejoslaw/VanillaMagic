package seia.vanillamagic.utils;

import java.util.List;
import java.util.Random;

import javax.annotation.Nullable;

import com.google.gson.JsonObject;

import net.minecraft.block.Block;

public class ListHelper 
{
	private ListHelper()
	{
	}
	
	@Nullable
	public static <T> T getRandomObjectFromList(List<T> list)
	{
		int randIndex = new Random().nextInt(list.size() - 1);
		return list.get(randIndex);
	}
	
	@Nullable
	public static <T> T getRandomObjectFromTab(T[] tab)
	{
		int randIndex = new Random().nextInt(tab.length - 1);
		return tab[randIndex];
	}
	
	@Nullable
	public static int getRandomObjectFromTab(int[] tab)
	{
		int randIndex = new Random().nextInt(tab.length - 1);
		return tab[randIndex];
	}

	public static List<Block> getList(String className, String field) 
	{
		try
		{
			return (List<Block>) ClassUtils.getFieldFromStaticInstance(className, field);
		}
		catch(Exception e)
		{
			return null;
		}
	}
}