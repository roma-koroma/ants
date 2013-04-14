package ants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AntTO
{
	private String id;
	private String from;
	private String y;
	private String x;
	private String angle;

	public String getId()
	{
		return id;
	}

	public void setId(long id)
	{
		this.id = String.valueOf(id);
	}

	public String getFrom()
	{
		return from;
	}

	public String getY()
	{
		return y;
	}

	public String getX()
	{
		return x;
	}

	public void setFrom(String from)
	{
		this.from = from;
	}

	public void setY(double y)
	{
		this.y = substring(y);
	}

	public void setX(double x)
	{
		this.x = substring(x);
	}

	public String getAngle()
	{
		return angle;
	}

	public void setAngle(double angle)
	{
		this.angle = substring(angle);
	}

	private String substring(double s)
	{
		return String.format("%1$,.2f", s);
	}
}
