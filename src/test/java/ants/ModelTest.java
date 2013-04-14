package ants;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.*;

import static org.testng.Assert.assertEquals;

/**
 * @author Roman K.
 */
public class ModelTest
{

	/**
	 * Логгер
	 */
	private static final Logger logger = LoggerFactory.getLogger(ModelTest.class);

	public static final int FRAMES_PER_SLICE = 10;

	private static final int SCREEN_WIDTH = 400;
	private static final int SCREEN_HEIGHT = 400;


	@Test
	public void antShouldMoveToPizzaBorder()
	{
		Screen screen = new Screen(600, 400);
		GameModel model = new GameModel(screen);
		Anthill hill = new Anthill(1, "Yandex", Screen.ScreenSide.BOTTOM);
		Pizza pizza1 = new Pizza("food", 1, new Point(100, 200), 100);
		Pizza pizza2 = new Pizza("good", 2, new Point(300, 200), 100);
		Pizza pizza3 = new Pizza("mood", 3, new Point(500, 200), 100);


		model.setSliceCount(3);

		model.addAnthill(hill);
		model.addPizza(pizza1);
		model.addPizza(pizza2);
		model.addPizza(pizza3);
		model.precalc();


		List<Frame> frames = new ArrayList<Frame>();

		model.newAnt(0 , hill);
		model.newAnt(1 , hill);
		model.newAnt(2 , hill);
		frames.addAll(model.action());

		model.moveAntTo(0, "food");
		model.moveAntTo(1, "good");
		model.moveAntTo(2, "mood");
		frames.addAll(model.action());

		model.moveAntTo(0, "good");
		model.moveAntTo(1, "mood");
		model.away(2);
		frames.addAll(model.action());

		model.moveAntTo(0, "mood");
		model.away(1);
		frames.addAll(model.action());



		for (Frame frame : frames)
		{

			for(AntTO to : frame.ants)
			{
				logger.info("id:{}, [x:{},y:{}] {}˚",
					new Object[]{to.getId(), to.getX(), to.getY(), to.getAngle()});
			}
		}

		Frame f = frames.get(frames.size() - 1);
		for (PizzaTO to : f.targets)
		{
			logger.info("init radius [{}], radius[{}]", to.getInitRadius(), to.getRadius());
		}	}

	@Test
	public void shouldReducePizzaSizeAfterBite()
	{
		//given


		//when


		//then
	}


	@Test
	public void shouldParseFramesCount() throws IOException
	{
		String json = "{\"frameCount\":721}";
		ObjectMapper mapper = new ObjectMapper();

		Map frameCount = mapper.readValue(json, Map.class);
		assertEquals(frameCount.get("frameCount"), 721);

	}

	@Test
	public void shouldParseInit() throws IOException
	{
		String json = "[{\"id\":4,\"total\":518},{\"id\":7,\"total\":551},{\"id\":6,\"total\":584},{\"id\":1," +
			"\"total\":550}," +
			"{\"id\":0,\"total\":565},{\"id\":2,\"total\":526},{\"id\":11,\"total\":552},{\"id\":14,\"total\":526}," +
			"{\"id\":9,\"total\":537},{\"id\":13,\"total\":586},{\"id\":5,\"total\":582},{\"id\":12,\"total\":566}," +
			"{\"id\":3,\"total\":573},{\"id\":8,\"total\":598},{\"id\":15,\"total\":606},{\"id\":10," +
			"\"total\":579}]\n";


		ObjectMapper mapper = new ObjectMapper();
		List<Map> inits = mapper.readValue(json, List.class);
		List<PizzaInit> ret = new ArrayList<PizzaInit>();

		for(Map init : inits)
		{
			ret.add(mapToPizzaInit(init));
		}

	}

	private PizzaInit mapToPizzaInit(Map init)
	{
		PizzaInit ret = new PizzaInit();

		ret.setId( ((Number)init.get("id")).longValue());
		ret.setTotal(((Number) init.get("total")).longValue());
		return ret;
	}

	@Test
	public void shouldParseCommmands() throws IOException
	{

		String json =
			"[{\"event\":0,\"id\":87,\"source\":2},{\"event\":2,\"id\":2338},{\"event\":1,\"category\":0,\"user\":6},{\"event\":0,\"id\":88,\"source\":0},{\"event\":0,\"id\":321," +
			"\"source\":1},{\"event\":0,\"id\":501,\"source\":1},{\"event\":0,\"id\":1180,\"source\":1},{\"event\":0," +
			"\"id\":1540,\"source\":2},{\"event\":0,\"id\":1750,\"source\":2},{\"event\":0,\"id\":1889,\"source\":0}," +
			"{\"event\":0,\"id\":2040,\"source\":2},{\"event\":0,\"id\":2083,\"source\":1},{\"event\":0,\"id\":2167," +
			"\"source\":1},{\"event\":0,\"id\":2181,\"source\":2},{\"event\":0,\"id\":2830,\"source\":2}]";

		ObjectMapper mapper = new ObjectMapper();
		List<Map> rawCommands = mapper.readValue(json, List.class);
		List<Command> ret = new ArrayList<Command>();

		for (Map rawCommand : rawCommands)
		{
			ret.add(mapToCommand(rawCommand));
		}
		logger.info("{}",ret.size());

	}

	private Command mapToCommand(Map rawCommand)
	{
		Command ret = new Command();

		ret.setRawEvent(longOrNull(rawCommand.get("event")));
		ret.setId(longOrNull(rawCommand.get("id")));
		if(ret.getEvent() == Command.Type.NEW)
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
		if(num == null)
			return null;
		return num.longValue();
	}



}

