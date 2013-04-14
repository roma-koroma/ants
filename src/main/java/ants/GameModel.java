package ants;

import java.util.*;

import static java.lang.Math.atan2;
import static java.lang.Math.toDegrees;
import static java.lang.Math.toRadians;

public class GameModel
{

	/**
	 * кадров за слайд
	 */
	private int framesPerSlice = 10;

	/**
	 * отступ по количеству времени, от полуночи в мс.
	 */
	private long timeOffset;

	/**
	 * количесто мс реального времени за один фрейм.
	 */
	private long delta = 1 * 60 * 1000;


	//TODO переделать на Map<Subj, Action>
	private Map<Ant, Move> movedAnts;
	private Map<Ant, Kill> killedAnts;

	private Map<Pizza, Decrease> decreasedPizza;

	Map<Anthill, Point> hillPos;

	Random rand;
	private Map<Long, Ant> idToAnt;

	private List<Pizza> targets;


	private Map<String, Pizza> nameToPizza;

	private Screen screen;
	private long sliceCount;

	/**
	 * Сколько отщипывать от радиуса за один укус.
	 */

	public GameModel()
	{
		this.movedAnts = new HashMap<Ant, Move>();
		this.killedAnts = new HashMap<Ant, Kill>();
		this.decreasedPizza = new HashMap<Pizza, Decrease>();
		timeOffset = 0;
		hillPos = new HashMap<Anthill, Point>();
		rand = new Random();
		this.idToAnt = new HashMap<Long, Ant>();
		this.nameToPizza = new HashMap<String, Pizza>();
		this.targets = new ArrayList<Pizza>();
	}

	public GameModel(Screen screen)
	{
		this();
		this.screen = screen;
	}

	/**
	 * Считаем дельту изменения радиуса. Предполагаем, что все пиццы одинакового радиуса.
	 * И сколько реального времени пройдет за один фрейм.
	 */
	public void precalc()
	{
		int max = 0;
		for(Pizza pizza : targets)
		{
			if(pizza.initCapacity > max)
			{
				max = pizza.initCapacity;
			}
		}

		/**
		 * 1000 150
		 * 2000 150
		 * 3000 150
		 */

		/*
		самая популярная категория должна исчезнуть.
		 */
		Collections.sort(targets, new Comparator<Pizza>()
		{

			@Override
			public int compare(Pizza p1, Pizza p2)
			{
				if(p1.getInitCapacity() > p2.getInitCapacity())
					return -1;
				else return 1;
			}
		});


		double delta = targets.get(0).getInitRadius();

		for(Pizza pizza : targets)
		{
			pizza.setBiteDelta(delta / sliceCount);
			delta -= delta / targets.size();
		}
	}

	public void addPizza(Pizza pizza)
	{
		this.targets.add(pizza);
		this.nameToPizza.put(pizza.name, pizza);
	}

	public void addAnthill(Anthill anthill)
	{
		Point pos = null;
		switch (anthill.side)
		{
			case BOTTOM:
				pos = new Point(screen.getWidth() / 2, 0);
				break;
			case LEFT:
				pos = new Point(0, screen.getHeight() / 2);
				break;
			case RIGHT:
				pos = new Point(screen.getWidth(), screen.getHeight() / 2);
				break;
		}
		this.hillPos.put(anthill, pos);
	}

	public void moveAntTo(long antId, String pizzaName)
	{
		Ant ant = idToAnt.get(antId);
		Pizza pizza = nameToPizza.get(pizzaName);

		Point targetPoint = calcTarget(ant, pizza);

		ant.angle = toDegrees(calcAngleInRad(ant.pos, targetPoint));
		this.movedAnts.put(ant, new Move(ant, targetPoint));
		this.decreasedPizza.put(pizza, new Decrease(pizza, pizza.getBiteDelta()));
	}

	private Point calcTarget(Ant ant, Pizza pizza)
	{
		double rad = calcAngleInRad(ant.pos, pizza.center);

		//покрываем борт и немного заходим внутрь
		double x = (Math.cos(rad+randomAngle()) * (pizza.radius - randomRadius())) + pizza.center.x;
		double y = (Math.sin(rad+randomAngle()) * (pizza.radius - randomRadius())) + pizza.center.y;
//		double x = pizza.center.x;
//		double y = pizza.center.y;

		return new Point(x, y);
	}

	private double calcAngleInRad(Point p1, Point p2)
	{
		return atan2(p2.y - p1.y, p2.x - p1.x);
	}

	private double randomRadius()
	{
		return rand.nextInt() % 3;
	}


	private double randomAngle()
	{
		//генерируем случайный угол в градусах и переводим в радианы.
		double ret = rand.nextInt() % 60;
		ret -= (ret/2);
		return toRadians(ret);
	}

	public void away(long antId)
	{
		Ant ant = idToAnt.get(antId);
		killedAnts.put(ant, new Kill(ant));
		Anthill hill = hillByName(ant.from);
		ant.angle = toDegrees(calcAngleInRad(ant.pos, hillPos.get(hill)));
	}

	public void setFramesPerSlice(int framesPerSlice)
	{
		this.framesPerSlice = framesPerSlice;
	}

	public List<Frame> action()
	{
		List<Frame> frames = new ArrayList<Frame>();

		for(int i = 0; i < framesPerSlice ; i++)
		{
			timeOffset += delta;
			frames.add(new Frame(timeOffset));
		}

		//обрабатываем всех и проверяем, идут ли они
		List<Long> toDelete = new ArrayList<Long>();
		for( Ant ant : idToAnt.values())
		{
			Move moved = movedAnts.get(ant);
			Kill killed = killedAnts.get(ant);
			if(moved != null)
			{
				//знаем его текущую позицию;
				Point pos = moved.ant.pos;

				//знаем цель
				Point target = moved.target;

				//можем узнать дельту по осям x и y.
				double deltaX = (target.x - pos.x) / frames.size();
				double deltaY = (target.y - pos.y) / frames.size();

				//сдвигаем муравья и клонируем его во фрейм
				for (Frame frame : frames)
				{
					ant.pos.setX(ant.pos.getX() + deltaX);
					ant.pos.setY(ant.pos.getY() + deltaY);

					frame.addAnt(ant);
				}

			}
			else if(killed != null)
			{
				Anthill hill = hillByName(ant.from);

				Point pos = ant.pos;

				//знаем цель

				Point target = hillPos.get(hill);

				//можем узнать дельту по осям x и y.
				double deltaX = (target.x - pos.x) / (frames.size() - 1 );
				double deltaY = (target.y - pos.y) / (frames.size() - 1 );

				//возвращаем муравьев в свой муравейник, а на последнем кадре они уже исчезают.
				for (int i = 0; i < frames.size() - 1; ++i)
				{
					ant.pos.setX(ant.pos.getX() + deltaX);
					ant.pos.setY(ant.pos.getY() + deltaY);
					frames.get(i).addAnt(ant);
				}
				toDelete.add(ant.getId());
			}
			//если муравей стоит в муравейнике, то не пишем его в кадр.
			else if(ant.pos.equals(hillByName(ant.from).pos))
			{
				continue;
			}
			//если муравей стоит около пиццы, то пишем.
			else
			{
				for (Frame frame : frames)
				{
					frame.addAnt(ant);
				}
			}
		}

		for (Long id : toDelete)
		{
			idToAnt.remove(id);
		}

		movedAnts.clear();
		killedAnts.clear();

		Frame lastFrame = frames.get(frames.size() - 1);
		for(Pizza pizza : targets)
		{
			for (int i = 0; i < frames.size() - 1; ++i)
			{
				frames.get(i).addPizza(pizza);
			}

			Decrease dec = decreasedPizza.get(pizza);
			if(dec != null)
			{
				double newRad = pizza.getRadius() > dec.amount ? pizza.getRadius() - dec.amount : 0;
				pizza.setRadius(newRad);
			}

			lastFrame.addPizza(pizza);
		}

		decreasedPizza.clear();

		return frames;
	}

	public Ant newAnt(long id, Anthill hill)
	{
		Ant ant = new Ant(id, hill);
		ant.setPosition(hillPos.get(hill));
		ant.angle = toDegrees(calcAngleInRad(ant.pos, screen.center()));
		this.idToAnt.put(ant.getId(), ant);

		return ant;
	}


	public Anthill hillByName(String from)
	{
		for (Anthill hill : hillPos.keySet())
		{
			if (hill.name.equals(from))
			{
				return hill;
			}
		}
		return null;
	}

	public Anthill hillById(Long source)
	{
		for (Anthill hill : hillPos.keySet())
		{
			if (hill.id == source)
			{
				return hill;
			}
		}
		return null;
	}

	public void setSliceCount(long sliceCount)
	{
		this.sliceCount = sliceCount;
	}

	public long getSliceCount()
	{
		return sliceCount;
	}

	private class Move
	{
		public Ant ant;
		public Point target;

		private Move(Ant ant, Point target)
		{
			this.ant = ant;
			this.ant.setIsMoved(true);

			this.target = target;
		}

	}

	private class Kill
	{
		public Ant ant;

		private Kill(Ant ant)
		{
			this.ant = ant;
		}
	}

	private class Decrease
	{
		public Pizza pizza;
		public double amount;

		private Decrease(Pizza pizza, double amount)
		{
			this.pizza = pizza;
			this.amount = amount;
		}
	}
}
