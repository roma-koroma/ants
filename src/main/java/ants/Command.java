package ants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Roman K.
 */
public class Command
{

	/**
	 * Логгер
	 */
	private static final Logger logger = LoggerFactory.getLogger(Command.class);
	private Type event;
	private Long id;
	private Long category;
	private Long source;

	public Type getEvent()
	{
		return event;
	}

	public void setId(Long id)
	{
		this.id = id;
	}

	public Long getId()
	{
		return id;
	}

	public void setRawEvent(Long event)
	{
		if (event == 0)
		{
			this.event = Type.NEW;

		} else if (event == 1)
		{
			this.event = Type.MOVE;

		} else if (event == 2)
		{
			this.event = Type.KILL;
		}
	}

	public void setCategory(Long category)
	{
		this.category = category;
	}

	public Long getCategory()
	{
		return category;
	}

	public void setSource(Long source)
	{
		this.source = source;
	}

	public Long getSource()
	{
		return source;
	}

	public enum Type
	{
		NEW(0),
		MOVE(1),
		KILL(2);

		private final int type;

		Type(int type)
		{
			this.type = type;
		}

		public int getType()
		{
			return type;
		}
	}
}
