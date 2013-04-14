package ants;

public class Pizza
{

//	private List<Point> hull;
	public final Point center;

	public double radius;

	public double initRadius;

	public int capacity;

	public int initCapacity;

	public final String name;

	public double biteDelta;

	public Pizza(String name, int initCapacity, Point center, double radius)
	{
		this.radius = radius;
		this.name = name;
		this.initCapacity = initCapacity;
		this.center = center;
		this.initRadius = radius;
	}


	public int getInitCapacity()
	{
		return initCapacity;
	}

	public void setInitCapacity(int initCapacity)
	{
		this.initCapacity = initCapacity;
	}

	public void setRadius(double radius)
	{
		this.radius = radius;
	}

	public double getBiteDelta()
	{
		return biteDelta;
	}

	public void setBiteDelta(double biteDelta)
	{
		this.biteDelta = biteDelta;
	}

	public int getCapacity()
	{
		return capacity;
	}

	public void setCapacity(int capacity)
	{
		this.capacity = capacity;
	}

	public double getInitRadius()
	{
		return initRadius;
	}

	public void setInitRadius(double initRadius)
	{
		this.initRadius = initRadius;
	}

	public double getRadius()
	{
		return this.radius;
	}
}
