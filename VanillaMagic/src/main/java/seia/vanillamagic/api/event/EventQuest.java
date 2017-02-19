package seia.vanillamagic.api.event;

import net.minecraftforge.fml.common.eventhandler.Event;
import seia.vanillamagic.api.quest.IQuest;

/**
 * Base class for all the Quest-based events.
 */
public class EventQuest extends Event
{
	private IQuest quest;
	
	public EventQuest(IQuest quest)
	{
		this.quest = quest;
	}
	
	public IQuest getQuest()
	{
		return quest;
	}
	
	/**
	 * This Event is fired BEFORE the given {@link IQuest} is added to the list.
	 */
	public static class Add extends EventQuest
	{
		public Add(IQuest quest) 
		{
			super(quest);
		}
	}
}