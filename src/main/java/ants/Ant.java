package ants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Ant
{

	private final long id;
	public Point pos;

	public double angle;

	public boolean isMoved;

	public String from;

	public Ant(long id, Anthill from)
	{
		this.id = id;
		this.from = from.name;
	}

	public long getId()
	{
		return id;
	}

	public void setIsMoved(boolean moved)
	{
		isMoved = moved;
	}

	public void setPosition(Point position)
	{
		this.pos = new Point(position);
	}
}
