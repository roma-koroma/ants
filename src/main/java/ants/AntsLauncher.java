package ants;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Roman K.
 */
public class AntsLauncher
{

	/**
	 * Логгер
	 */
	private static final Logger logger = LoggerFactory.getLogger(AntsLauncher.class);

	GameModel model;

	private static final int SCREEN_WIDTH = 1920;
	private static final int SCREEN_HEIGHT = 1080;

	private ObjectMapper mapper;
	private PrintWriter writer;

	List<Point> centers;
	public AntsLauncher(File file, File outFile) throws IOException
	{
		centers = initCenters();

		mapper = new ObjectMapper();

		model = new GameModel();

		BufferedReader reader = new BufferedReader( new InputStreamReader(new FileInputStream(file)));

		writer = new PrintWriter(new BufferedWriter( new OutputStreamWriter(new FileOutputStream(outFile))));

		makeAnthills(model);

		long frameCount = parseFrameCount(reader.readLine());

		model.setSliceCount(frameCount);
		List<PizzaInit> pizzaInits = parsePizzaInits(reader.readLine());


		for(int i = 0; i < pizzaInits.size() ; i++)
		{
			PizzaInit init = pizzaInits.get(i);
			Pizza pizza = new Pizza(String.valueOf(init.getId()), init.getTotal().intValue(), centers.get(i), 150);
			model.addPizza(pizza);
		}

		model.precalc();

		String json = null;
		while((json = reader.readLine()) != null)
		{
			List<Command> commands = parseCommands(json);
			mapCommandsToModel(commands, model);
			List<Frame> frames = model.action();
			writeOutput(frames);
		}

	}

	private List<Point> initCenters()
	{
		List<Point> ret = new ArrayList<Point>();

		int x0 = 310;
		int y0 = 310;
		int x = 155;
		int y = 155;


		for(int i = 0; i < 15 ;i++)
		{
			ret.add(new Point(x, y));
			x = (x + x0) % SCREEN_WIDTH;
			y = (y + y0) % SCREEN_HEIGHT;
		}

		return ret;

	}

	private void makeAnthills(GameModel model)
	{
		model.addAnthill(new Anthill(0, "direct", Screen.ScreenSide.LEFT));
		model.addAnthill(new Anthill(1, "yandex", Screen.ScreenSide.BOTTOM));
		model.addAnthill(new Anthill(2, "google", Screen.ScreenSide.RIGHT));
	}

	private void mapCommandsToModel(List<Command> commands, GameModel model)
	{
		for(Command command :commands)
		{
			switch(command.getEvent())
			{
				case NEW:
					model.newAnt( command.getId(),model.hillById(command.getSource()));
					break;
				case MOVE:
					model.moveAntTo(command.getId(), String.valueOf(command.getCategory()));
					break;
				case KILL:
					model.away(command.getId());
					break;
			}
		}
	}

	private List<Command> parseCommands(String json) throws IOException
	{
		List<Map> rawCommands = mapper.readValue(json, List.class);
		List<Command> ret = new ArrayList<Command>();

		for (Map rawCommand : rawCommands)
		{
			ret.add(mapToCommand(rawCommand));
		}

		return ret;
	}

	private Command mapToCommand(Map rawCommand)
	{
		Command ret = new Command();

		ret.setRawEvent(longOrNull(rawCommand.get("event")));
		ret.setId(longOrNull(rawCommand.get("id")));
		if (ret.getEvent() == Command.Type.NEW)
			ret.setSource(longOrNull(rawCommand.get("source")));

		if (ret.getEvent() == Command.Type.MOVE)
		{
			ret.setCategory(longOrNull(rawCommand.get("category")));
			ret.setId(longOrNull(rawCommand.get("user")));
		}
		return ret;
	}

	private Long longOrNull(Object s)
	{
		Number num = (Number) s;
		if (num == null)
			return null;
		return num.longValue();
	}

	private List<PizzaInit> parsePizzaInits(String s) throws IOException
	{
		List<Map> inits = mapper.readValue(s, List.class);
		List<PizzaInit> ret = new ArrayList<PizzaInit>();

		for (Map init : inits)
		{
			ret.add(mapToPizzaInit(init));
		}

		return ret;
	}

	private PizzaInit mapToPizzaInit(Map init)
	{
		PizzaInit ret = new PizzaInit();

		ret.setId(((Number) init.get("id")).longValue());
		ret.setTotal(((Number) init.get("total")).longValue());
		return ret;
	}

	private long parseFrameCount(String s) throws IOException
	{
		Map frameCount = mapper.readValue(s, Map.class);

		return ((Number) frameCount.get("frameCount")).longValue();
	}

	private void writeOutput(List<Frame> frames) throws JsonProcessingException
	{
		for(Frame frame : frames)
		{
			String frameString = frameToString(frame);
			writer.println(frameString);
		}

	}

	private String frameToString(Frame frame)
	{
		StringBuilder builder = new StringBuilder();
		builder.append("{\"ants\":[");

		for(AntTO to: frame.ants)
		{
			builder.append("{");
			builder.append("\"from\": \"").append(to.getFrom()).append("\",");
			if(to.getX().equalsIgnoreCase("nan"))
				logger.info("shit");
			builder.append("\"x\": ").append(to.getX()).append(",");
			builder.append("\"y\": ").append(to.getY()).append("");
			builder.append("},");
		}
		builder.deleteCharAt(builder.length() - 1);
		builder.append("],");

		builder.append("\"pizzas\":[");
		for(PizzaTO to: frame.targets)
		{
			builder.append("{");
			builder.append("\"initRadius\": ").append(to.getInitRadius()).append(",");
			builder.append("\"name\": \"").append(to.getName()).append("\",");
			builder.append("\"radius\": ").append(to.getRadius()).append(",");
			builder.append("\"x\": ").append(to.getCenterX()).append(",");
			builder.append("\"y\": ").append(to.getCenterY()).append("");
			builder.append("},");
		}
		builder.deleteCharAt(builder.length() - 1);

		builder.append("],");
		builder.append("\"time\": \"").append(frame.timeOffset).append("\"");
		builder.append("}");

		return builder.toString();
	}

	/*
	{

    "pizzas": [
        {
            "initRadius": 150,
            "name": "food",
            "radius": 120,
            "x": 100,
            "y": 100
        },
        {
            "initRadius": 150,
            "name": "food",
            "radius": 120,
            "x": 100,
            "y": 100
        }
    ],
    "time": 19020
}
	 */

	public static void main(String[] args) throws IOException
	{
		if(args.length > 0)
		{
			String in = args[0];
			File inFile = new File(in);

			String out = args[1];
			File outFile = new File(out);
			new AntsLauncher(inFile, outFile);

		}
	}
}
