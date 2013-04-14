package ants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PizzaTO
{
	private int amount;
	private double centerY;
	private double centerX;
	private String name;
	private double radius;
	private double initRadius;

	public double getCenterY()
	{
		return centerY;
	}

	public double getCenterX()
	{
		return centerX;
	}

	public String getName()
	{
		return name;
	}

	public double getRadius()
	{
		return radius;
	}

	public void setAmount(int amount)
	{
		this.amount = amount;
	}

	public int getAmount()
	{
		return amount;
	}

	public void setCenterY(double centerY)
	{
		this.centerY = centerY;
	}

	public void setCenterX(double centerX)
	{
		this.centerX = centerX;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public void setRadius(double radius)
	{
		this.radius = radius;
	}

	public void setInitRadius(double initRadius)
	{
		this.initRadius = initRadius;
	}

	public double getInitRadius()
	{
		return initRadius;
	}
}
