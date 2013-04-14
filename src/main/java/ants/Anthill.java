package ants;


public class Anthill
{

	public String name;
	public Screen.ScreenSide side;
	public Point pos;
	public long id;

	public Anthill(long id, String name, Screen.ScreenSide side)
	{
		this.id = id;
		this.name = name;
		this.side = side;
	}

}
