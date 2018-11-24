import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CountDownLatch;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

public class RequestMaker implements Runnable {

	private int iterations;
	private int hours;
	private CountDownLatch latch;
	private Client client;
	private WebTarget webTarget;
	private List<long[]> latencyPairs;
	private int successfulRequsts;
	private final int phaseId;
	private final Random random = new Random();
	private static final int POPULATION = 100000;
	private static final int AVAILABLE_DAY = 1;
	private static final int MAX_STEPS = 5000;
	private static final int RANDOM_SIZE = 3;
	private static final int[] PHASE_START_TIME = {0,3,8,19};
	
	public RequestMaker(int iterations, int hours, CountDownLatch latch, Client client, WebTarget webTarget, int phaseId) {
		this.iterations = iterations;
		this.hours = hours; 
		this.latch = latch;
		this.client = client;
		this.webTarget = webTarget;
		this.latencyPairs = new LinkedList<long[]>();
		this.phaseId = phaseId;
		this.successfulRequsts = 0;
	}
	
	/**
	 * Implements run() of Runnable
	 */
	public void run() {
		for (int i = 0 ; i < hours ; i++) {
			for (int j = 0 ; j < iterations ; j++) {
				int[] users = this.getRandomUsers();
				int[] clockTimes = this.getRandomHours();
				int[] steps = this.getRandomSteps();
				String[] pathes = new String[5];
				pathes[0] = "/POST"+ "/" + users[0] + "/" + AVAILABLE_DAY + "/" + clockTimes[0] + "/" + steps[0];
				pathes[1] = "/POST"+ "/" + users[1] + "/" + AVAILABLE_DAY + "/" + clockTimes[1] + "/" + steps[1];
				pathes[2] = "/GET"+ "/current" + "/" + users[0];
				pathes[3] = "/GET"+ "/single" + "/" + users[1] + "/" + AVAILABLE_DAY;
				pathes[4] = "/POST"+ "/" + users[2] + "/" + AVAILABLE_DAY + "/" + clockTimes[2] + "/" + steps[2];
				for (int k = 0 ; k < 5 ; k++) {
					WebTarget requestEndpoint = webTarget.path(pathes[k]);
					Invocation.Builder invocationBuilder = requestEndpoint.request(MediaType.TEXT_PLAIN);
					Response response = null;
					long start = System.currentTimeMillis();
					if (k == 2 || k == 3) {
						response = invocationBuilder.get();
					} else {
						response = invocationBuilder.post(null);
					}
					long end = System.currentTimeMillis();
					long[] latencyPair = new long[2];
					latencyPair[0] = start;
					latencyPair[1] = end;
					this.latencyPairs.add(latencyPair);
					if (response.getStatus() == 200) {
						this.successfulRequsts++;
					}
					response.close();
				}
			}
		}
		WearableClientApplication.appendLatencies(this.latencyPairs,this.phaseId);
		WearableClientApplication.globalSuccessfulRequestCount.addAndGet(this.successfulRequsts);
		System.out.println("CurrentThread: " + latch.getCount());
		client.close();
		latch.countDown();
	}
	
	private int[] getRandomUsers() {
		int[] users = new int[RANDOM_SIZE];
		for (int i = 0 ; i < RANDOM_SIZE ; i++) {
			users[i] = random.nextInt(POPULATION) + 1; 
		}
		return users;
	}
	
	private int[] getRandomHours() {
		int[] clockTimes = new int[RANDOM_SIZE];
		for (int i = 0 ; i < RANDOM_SIZE ; i++) {
			clockTimes[i] = random.nextInt(this.hours) + PHASE_START_TIME[this.phaseId]; 
		}
		return clockTimes;
	}
	
	private int[] getRandomSteps() {
		int[] steps = new int[RANDOM_SIZE];
		for (int i = 0 ; i < RANDOM_SIZE ; i++) {
			steps[i] = random.nextInt(MAX_STEPS); 
		}
		return steps;
	}

}
