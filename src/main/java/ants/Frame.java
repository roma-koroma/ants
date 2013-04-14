package ants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Roman K.
 */
public class Frame
{

	/**
	 * Логгер
	 */
	private static final Logger logger = LoggerFactory.getLogger(Frame.class);

	List<AntTO> ants;

	List<PizzaTO> targets;

	long timeOffset;

	public Frame(long timeOffset)
	{
		this.timeOffset = timeOffset;
		ants = new ArrayList<AntTO>();
		targets = new ArrayList<PizzaTO>();
	}

	public void addAnt(Ant ant)
	{
		AntTO newAnt = new AntTO();
		newAnt.setId(ant.getId());
		newAnt.setX(ant.pos.x);
		newAnt.setY(ant.pos.y);
		newAnt.setFrom(ant.from);
		newAnt.setAngle(ant.angle);
		ants.add(newAnt);
	}

	public void addPizza(Pizza pizza)
	{
		PizzaTO newPizza = new PizzaTO();

		newPizza.setInitRadius(pizza.initRadius);
		newPizza.setRadius(pizza.radius);
		newPizza.setCenterX(pizza.center.x);
		newPizza.setCenterY(pizza.center.y);
		newPizza.setAmount(pizza.initCapacity);
		newPizza.setName(pizza.name);
		targets.add(newPizza);
	}

}

