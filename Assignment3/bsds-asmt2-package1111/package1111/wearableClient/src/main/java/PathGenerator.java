import java.util.Random;

/**
 * This part is not used for assignment2 , kept for future use.
 */
public class PathGenerator {

	private static final int POPULATION = 100000;
	private static final int AVAILABLE_DAY = 1;
	private static final int AVAILABLE_HOUR = 24;
	private static final int MAX_STEPS = 5000;
	
	public PathGenerator() {}
	
	public static final String[] generatePath() {
		String[] path = new String[2];
		Random random = new Random();
		int type = random.nextInt(3); // hard code, except the range get for assignment 2
		
		switch (type) {
		case 0: 
			path[0] = "POST";
			path[1] = getRandomPostPath();
			break;
		case 1:
			path[0] = "GET";
			path[1] = getRandomCurrentPath();
			break;
		case 2:
			path[0] = "GET";
			path[1] = getRandomSinglePath();
			break;
		case 3:
			path[0] = "GET";
			path[1] = getRandomRangePath();
			break;
		}
		//System.out.println(path[1]);
		return path;
	}
	
	private static String getRandomPostPath() {
		Random random = new Random();
		int userId = random.nextInt(POPULATION) + 1;
		int day = 1;
		int hour = random.nextInt(AVAILABLE_HOUR);
		int steps = random.nextInt(MAX_STEPS);
		String path = "/POST"+ "/" + userId + "/" + day + "/" + hour + "/" + steps;
		return path;
	}
	
	private static String getRandomCurrentPath() {
		Random random = new Random();
		int userId = random.nextInt(POPULATION) + 1;
		String path = "/GET"+ "/current" + "/" + userId;
		return path;
	}
	
	private static String getRandomSinglePath() {
		Random random = new Random();
		int userId = random.nextInt(POPULATION) + 1;
		int day = random.nextInt(AVAILABLE_DAY) + 1;
		String path = "/GET"+ "/single" + "/" + userId + "/" + day;
		return path;
	}
	
	private static String getRandomRangePath() {
		Random random = new Random();
		int userId = random.nextInt(POPULATION) + 1;
		int startDay = random.nextInt(AVAILABLE_DAY) + 1;
		int numDays = 1;
		String path = "/GET"+ "/range" + "/" + userId + "/" + startDay + "/" + numDays;
		return path;
	}
}
